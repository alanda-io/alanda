package io.alanda.base.service.checklist;

import io.alanda.base.dao.ChecklistRepo;
import io.alanda.base.dao.ChecklistTemplateRepo;
import io.alanda.base.entity.AbstractEntity;
import io.alanda.base.entity.checklist.*;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.base.service.checklist.statusresolver.ICheckListItemService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Transactional
@Singleton
@Startup
public class CheckListService {
    private final Map<CheckListItemBackend, ICheckListItemService> itemBackendServices = new HashMap<>();
    private final CheckListMapper checkListMapper = new CheckListMapper();

    @Inject
    private static final Logger log = LoggerFactory.getLogger(CheckListService.class);

    @Inject
    private ChecklistTemplateRepo checklistTemplateRepo;

    @Any
    @Inject
    private Instance<ICheckListItemService<?>> itemBackends;

    @Inject
    private ChecklistRepo checklistRepo;

    @Inject
    private TaskService taskService;

    @PostConstruct
    public void registerBackends() {
        for (ICheckListItemService<?> itemService : itemBackends) {
            itemBackendServices.put(itemService.getTaskBackend(), itemService);
            log.info("Registered CheckListItemService: {}({})", itemService.getTaskBackend(), itemService.getClass().getName());
        }
    }

    public Iterable<CheckListTemplateDto> getAllCheckListTemplates() {
        return checkListMapper.mapChecklistTemplatesToDto(checklistTemplateRepo.findAll());
    }

    public Optional<CheckListTemplateDto> getCheckListTemplate(Long templateId) {
        return Optional.ofNullable(checklistTemplateRepo.findOne(templateId)).map(checkListMapper::mapChecklistTemplateToDto);
    }

    public CheckListTemplateDto saveCheckListTemplate(CheckListTemplateDto template) {
        checklistTemplateRepo.save(checkListMapper.mapDtoToChecklistTemplate(template));
        return template;
    }

    public CheckListTemplateDto upsertCheckListTemplate(CheckListTemplateDto templateDto) {
        final CheckListTemplate template;
        final CheckListTemplate savedTemplate = checklistTemplateRepo.findOne(templateDto.getId());
        if (savedTemplate != null) {
            template = savedTemplate;
        } else {
            template = checklistTemplateRepo.save(new CheckListTemplate());
            template.setTaskAssociations(templateDto.getUserTasks().stream().map(ut -> {
                final CheckListTemplateTaskAssociation templateTaskAssociation = new CheckListTemplateTaskAssociation();
                templateTaskAssociation.setUserTaskDefKey(ut);
                templateTaskAssociation.setTaskListTemplate(template);
                return templateTaskAssociation;
            }).collect(Collectors.toList()));
        }
        template.setItemBackend(templateDto.getItemBackend());
        template.setName(templateDto.getName());
        return checkListMapper.mapChecklistTemplateToDto(checklistTemplateRepo.save(template));
    }

    public CheckListTemplateDto updateCheckListTemplate(CheckListTemplateDto template) {
        CheckListTemplate existingTemplate = checklistTemplateRepo.findOne(template.getId());
        for (String userTask : template.getUserTasks()) {
            if (!existingTemplate.getTaskAssociations().stream().map(CheckListTemplateTaskAssociation::getUserTaskDefKey).collect(Collectors.toList()).contains(userTask)) {
                CheckListTemplateTaskAssociation mappedAssociation = checkListMapper.mapDtoToCheckListTemplateTaskAssociation(userTask);
                List<Task> tasks = taskService.createTaskQuery().taskDefinitionKey(mappedAssociation.getUserTaskDefKey()).list().stream().collect(Collectors.toList());
                for (Task task : tasks) {
                    log.info("XXX Task: " + task.getProcessInstanceId());
                    CheckList cl = new CheckList();
                    cl.setUserTaskInstance(task.getProcessInstanceId());
                    mappedAssociation.addChecklist(cl);
                    existingTemplate.addCheckListTemplateTaskAssociation(mappedAssociation);
                }
            }
        }
        Iterator<CheckListTemplateTaskAssociation> taskAssociationIterator = existingTemplate.getTaskAssociations().iterator();
        while (taskAssociationIterator.hasNext()) {
            CheckListTemplateTaskAssociation existingAssociation = taskAssociationIterator.next();
            if (!template.getUserTasks().contains(existingAssociation.getUserTaskDefKey())) {
                taskAssociationIterator.remove();
            }
        }
        for (int i = 0; i < template.getItemDefinitions().size(); i++) {
            template.getItemDefinitions().get(i).setSortOrder((long) i);
        }
        for (CheckListItemDefinitionDto itemDefinitionDto : template.getItemDefinitions()) {
            int index = existingTemplate.getItemDefinitions().stream().map(CheckListItemDefinition::getKey).collect(Collectors.toList()).indexOf(itemDefinitionDto.getKey());
            if (index == -1) {
                CheckListItemDefinition itemDefinition = checkListMapper.mapDtoToChecklistItemDefinition(itemDefinitionDto);
                itemBackendServices.get(template.getItemBackend()).createCheckListItem(itemDefinition);
                existingTemplate.addItemDefinition(itemDefinition);
            } else {
                CheckListItemDefinition existingDefinition = existingTemplate.getItemDefinitions().get(index);
                existingDefinition.setSortOrder(itemDefinitionDto.getSortOrder().intValue());
                existingDefinition.setRequired(itemDefinitionDto.getRequired());
                existingDefinition.setDisplayText(itemDefinitionDto.getDisplayText());
            }
        }
        Iterator<CheckListItemDefinition> itemDefIterator = existingTemplate.getItemDefinitions().iterator();
        while (itemDefIterator.hasNext()) {
            CheckListItemDefinition itemDefinition = itemDefIterator.next();
            if (!existingTemplate.getItemDefinitions().stream().map(CheckListItemDefinition::getKey).collect(Collectors.toList()).contains(itemDefinition.getKey())) {
                itemDefIterator.remove();
            }
        }
        existingTemplate.setItemBackend(template.getItemBackend());
        existingTemplate.setName(template.getName());

        return template;
    }

    public void addCheckListItemToChecklist(long checkListId, CheckListItemDefinitionDto checkListItemDefinitionDto) {
        CheckList checklist = checklistRepo.findOne(checkListId);
        checkListItemDefinitionDto.setSortOrder(Long.valueOf(checklist.getItemDefinitions().size()));
        CheckListItemDefinition checkListItemDefinition = checkListMapper.mapDtoToChecklistItemDefinition(checkListItemDefinitionDto);
        CheckListItem checkListItem = new CheckListItem();
        checkListItem.setStatus(false);
        checkListItemDefinition.addCheckListItem(checkListItem);
        checklist.addItemDefinition(checkListItemDefinition);
    }

    public void removeCheckListItemFromChecklist(long checkListId, String itemKey) {
        CheckList checklist = checklistRepo.findOne(checkListId);
        Iterator<CheckListItemDefinition> checkListItemDefinitionIterator = checklist.getItemDefinitions().iterator();
        while(checkListItemDefinitionIterator.hasNext()) {
            CheckListItemDefinition checkListItemDefinition = checkListItemDefinitionIterator.next();
            if (checkListItemDefinition.getKey().equals(itemKey)) {
                checkListItemDefinitionIterator.remove();
            }
        }
    }

    public CheckListItemDto getCheckListItemForDefinition(CheckListItemDefinition itemDefinition) {
        final CheckListItemBackend itemBackend = itemDefinition.getCheckListTemplate().getItemBackend();
        return itemBackendServices.get(itemBackend).getCheckListItem(itemDefinition.getGuid());
    }

    public List<CheckListItemDto> getCheckListItemsForDefinitions(CheckListItemBackend itemBackend, List<Long> itemDefinitionIds) {
        if (itemDefinitionIds.size() == 0) {
            itemDefinitionIds.add(-1L);
        }
        return new ArrayList<>(itemBackendServices.get(itemBackend).getCheckListItems(itemDefinitionIds));
    }

    public List<CheckListItemDto> getCheckListItems(CheckList checkList) {
        final CheckListItemBackend itemBackend = checkList.getTemplateTaskAssociation().getCheckListTemplate().getItemBackend();
        final List<CheckListItemDefinition> templateDefinitions = checkList.getTemplateTaskAssociation().getCheckListTemplate().getItemDefinitions();
        final List<CheckListItemDefinition> checklistDefinitions = checkList.getItemDefinitions();

        final List<CheckListItemDto> allItems = new ArrayList<>(getCheckListItemsForDefinitions(itemBackend,
                templateDefinitions.stream().map(AbstractEntity::getGuid).collect(Collectors.toList())));

        allItems.addAll(getCheckListItemsForDefinitions(itemBackend,
                checklistDefinitions.stream().map(AbstractEntity::getGuid).collect(Collectors.toList())));

        return allItems;
    }

    public List<CheckListItemDto> getCheckListItems(Long checkListGuid) {
        return getCheckList(checkListGuid)
                .map(CheckListDto::getCheckListItems)
                .orElse(Collections.emptyList());
    }

    public List<CheckListDto> getCheckListsForUserTaskInstance(String taskInstanceGuid) {
        final List<CheckList> checklists = checklistRepo.findByUserTaskInstance(taskInstanceGuid);
        return checkListMapper.mapChecklistsToDto(checklists);
    }

    public Optional<CheckListDto> getCheckList(Long checkListId) {
        return Optional.ofNullable(checklistRepo.findOne(checkListId)).map(checkListMapper::mapChecklistToDto);
    }

    public void updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
        getCheckList(checkListId).ifPresent(cl -> {
            log.info("checklist is present: " + cl.getItemBackend());
            getCheckListItems(cl.getId()).stream()
                    .filter(ci -> ci.getItemDefinition().getKey().equals(key))
                    .forEach(ci -> itemBackendServices.get(cl.getItemBackend()).setCheckListItemStatus(ci.getId(), status));
        });
    }

    public CheckListDto saveCheckList(Long checkListId, CheckListDto updateCheckList) {
        // TODO
        return null;
    }

    class CheckListMapper {
        CheckListItemDefinitionDto mapCheckListItemDefinitionToDto(CheckListItemDefinition itemDefinition) {
            CheckListItemDefinitionDto checkListItemDefinitionDto = new CheckListItemDefinitionDto();
            checkListItemDefinitionDto.setCustom(itemDefinition.getCheckListTemplate() != null ? true : false);
            checkListItemDefinitionDto.setDisplayText(itemDefinition.getDisplayText());
            checkListItemDefinitionDto.setKey(itemDefinition.getKey());
            checkListItemDefinitionDto.setRequired(itemDefinition.getRequired());
            checkListItemDefinitionDto.setSortOrder(itemDefinition.getSortOrder().longValue());
            return checkListItemDefinitionDto;
        }

        List<CheckListItemDefinitionDto> mapCheckListItemDefinitionToDto(List<CheckListItemDefinition> itemDefinitions) {
            return itemDefinitions.stream().map(this::mapCheckListItemDefinitionToDto).collect(Collectors.toList());
        }

        CheckListTemplate mapDtoToChecklistTemplate(CheckListTemplateDto checkListTemplateDto) {
            CheckListTemplate template = new CheckListTemplate();
            template.setGuid(checkListTemplateDto.getId());
            template.setName(checkListTemplateDto.getName());
            template.setItemBackend(checkListTemplateDto.getItemBackend());

            checkListTemplateDto.getItemDefinitions().stream().map(this::mapDtoToChecklistItemDefinition).forEach(checkListItemDefinition -> {
                template.addItemDefinition(checkListItemDefinition);
            });
            checkListTemplateDto.getUserTasks().stream().map(this::mapDtoToCheckListTemplateTaskAssociation).forEach(checkListTemplateTaskAssociation -> {
                template.addCheckListTemplateTaskAssociation(checkListTemplateTaskAssociation);
            });
            return template;
        }

        List<CheckListItem> mapDtoToChecklistItemList(List<CheckListItemDto> checkListItemDtos) {
            return checkListItemDtos.stream().map(this::mapDtoToChecklistItem).collect(Collectors.toList());
        }

        CheckListItem mapDtoToChecklistItem(CheckListItemDto checkListItemDto) {
            CheckListItem checkListItem = new CheckListItem();
            checkListItem.setDefinition(mapDtoToChecklistItemDefinition(checkListItemDto.getItemDefinition()));
            checkListItem.setStatus(checkListItemDto.getStatus());
            return checkListItem;
        }

        CheckListItemDefinition mapDtoToChecklistItemDefinition(CheckListItemDefinitionDto checkListItemDefinitionDto) {
            CheckListItemDefinition checkListItemDefinition = new CheckListItemDefinition();
            checkListItemDefinition.setDisplayText(checkListItemDefinitionDto.getDisplayText());
            checkListItemDefinition.setKey(checkListItemDefinitionDto.getKey());
            checkListItemDefinition.setRequired(checkListItemDefinitionDto.getRequired());
            checkListItemDefinition.setSortOrder(checkListItemDefinitionDto.getSortOrder().intValue());
            return checkListItemDefinition;
        }

        CheckListTemplateTaskAssociation mapDtoToCheckListTemplateTaskAssociation(String userTask) {
            CheckListTemplateTaskAssociation checkListTemplateTaskAssociation = new CheckListTemplateTaskAssociation();
            checkListTemplateTaskAssociation.setUserTaskDefKey(userTask);
            return checkListTemplateTaskAssociation;
        }

        CheckListTemplateDto mapChecklistTemplateToDto(CheckListTemplate template) {
            final CheckListTemplateDto templateDto = new CheckListTemplateDto();
            templateDto.setId(template.getGuid());
            templateDto.setItemBackend(template.getItemBackend());
            templateDto.setName(template.getName());
            templateDto.setItemDefinitions(mapCheckListItemDefinitionToDto(template.getItemDefinitions()));
            templateDto.setUserTasks(template.getTaskAssociations().stream()
                    .map(CheckListTemplateTaskAssociation::getUserTaskDefKey).collect(Collectors.toList()));

            return templateDto;
        }

        List<CheckListTemplateDto> mapChecklistTemplatesToDto(Iterable<CheckListTemplate> templates) {
            return StreamSupport.stream(templates.spliterator(), false).map(this::mapChecklistTemplateToDto).collect(Collectors.toList());
        }

        CheckListDto mapChecklistToDto(CheckList checkList) {
            final CheckListDto checkListDto = new CheckListDto();
            checkListDto.setId(checkList.getGuid());
            checkListDto.setCheckListItems(getCheckListItems(checkList));
            final CheckListTemplate checkListTemplate = checkList.getTemplateTaskAssociation().getCheckListTemplate();
            checkListDto.setName(checkListTemplate.getName());
            checkListDto.setItemBackend(checkListTemplate.getItemBackend());
            return checkListDto;
        }

        List<CheckListDto> mapChecklistsToDto(List<CheckList> checkList) {
            return checkList.stream().map(this::mapChecklistToDto).collect(Collectors.toList());
        }
    }
}
