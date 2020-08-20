package io.alanda.base.service.checklist;

import io.alanda.base.dao.ChecklistItemRepo;
import io.alanda.base.dao.ChecklistRepo;
import io.alanda.base.dao.ChecklistTemplateRepo;
import io.alanda.base.entity.AbstractEntity;
import io.alanda.base.entity.checklist.*;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.base.service.checklist.statusresolver.CheckListItemService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public class CheckListService {
    private final Map<CheckListItemBackend, CheckListItemService<Object>> itemBackendServices = new HashMap<>();
    private final CheckListMapper checkListMapper = new CheckListMapper();

    @Inject
    private static final Logger log = LoggerFactory.getLogger(CheckListService.class);

    @Inject
    private ChecklistTemplateRepo checklistTemplateRepo;

    @Inject
    private ChecklistRepo checklistRepo;

    @PostConstruct
    public void registerBackends(List<CheckListItemService<Object>> itemBackends) {
        for (CheckListItemService<Object> itemService : itemBackends) {
            itemBackendServices.put(itemService.getTaskBackend(), itemService);
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

    @Transactional
    public CheckListTemplateDto updateCheckListTemplate(CheckListTemplateDto template) {
        CheckListTemplate existingTemplate = checklistTemplateRepo.findOne(template.getId());
        CheckListTemplate mappedTemplate = checkListMapper.mapDtoToChecklistTemplate(template);
        existingTemplate.setItemBackend(mappedTemplate.getItemBackend());
        existingTemplate.setName(mappedTemplate.getName());
        Iterator<CheckListItemDefinition> itemDefinitionIterator = existingTemplate.getItemDefinitions().iterator();
        while (itemDefinitionIterator.hasNext()) {
            itemDefinitionIterator.next();
            itemDefinitionIterator.remove();
        }
        Iterator<CheckListTemplateTaskAssociation> taskAssociationIterator = existingTemplate.getTaskAssociations().iterator();
        while (taskAssociationIterator.hasNext()) {
            taskAssociationIterator.next();
            taskAssociationIterator.remove();
        }
        //existingTemplate.getItemDefinitions().forEach(checkListItemDefinition -> existingTemplate.removeItemDefinition(checkListItemDefinition));
        //existingTemplate.getTaskAssociations().forEach(checkListTemplateTaskAssociation -> existingTemplate.removeCheckListTemplateTaskAssociation(checkListTemplateTaskAssociation));
        mappedTemplate.getItemDefinitions().forEach(checkListItemDefinition -> existingTemplate.addItemDefinition(checkListItemDefinition));
        mappedTemplate.getTaskAssociations().forEach(checkListTemplateTaskAssociation -> existingTemplate.addCheckListTemplateTaskAssociation(checkListTemplateTaskAssociation));
        //existingTemplate.setItemDefinitions(mappedTemplate.getItemDefinitions());
        //existingTemplate.setTaskAssociations(mappedTemplate.getTaskAssociations());
        return template;
    }

    public CheckListItemDto getCheckListItemForDefinition(CheckListItemDefinition itemDefinition) {
        final CheckListItemBackend itemBackend = itemDefinition.getCheckListTemplate().getItemBackend();
        return itemBackendServices.get(itemBackend).getCheckListItem(itemDefinition.getGuid());
    }

    public List<CheckListItemDto> getCheckListItemsForDefinitions(CheckListItemBackend itemBackend, Iterable<Long> itemDefinitionIds) {
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
        final Iterable<CheckList> checklists = checklistRepo.findCheckListsByUserTaskInstance(taskInstanceGuid);

        return checkListMapper.mapChecklistsToDto(checklists);
    }

    public Optional<CheckListDto> getCheckList(Long checkListId) {
        return Optional.ofNullable(checklistRepo.findOne(checkListId)).map(checkListMapper::mapChecklistToDto);
    }

    public void updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
        getCheckList(checkListId).ifPresent(cl -> {
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

            return checkListDto;
        }

        List<CheckListDto> mapChecklistsToDto(Iterable<CheckList> checkList) {
            return StreamSupport.stream(checkList.spliterator(), false).map(this::mapChecklistToDto).collect(Collectors.toList());
        }
    }
}
