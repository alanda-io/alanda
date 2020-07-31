package io.alanda.base.service.checklist;

import io.alanda.base.entity.checklist.CheckListItemBackend;
import io.alanda.base.entity.checklist.CheckListItemDefinition;
import io.alanda.base.entity.checklist.CheckListTemplateTaskAssociation;
import io.alanda.base.service.checklist.dto.CheckListDto;
import io.alanda.base.service.checklist.dto.CheckListItemDefinitionDto;
import io.alanda.base.service.checklist.dto.CheckListItemDto;
import io.alanda.base.service.checklist.dto.CheckListTemplateDto;
import io.alanda.base.service.checklist.statusresolver.TaskListItemService;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

public class CheckListService {
    private final Map<CheckListItemBackend, TaskListItemService<Object>> itemBackendServices = new HashMap<>();
    private final CheckListMapper checkListMapper = new CheckListMapper();

    @PostConstruct
    public void registerBackends(List<TaskListItemService<Object>> itemBackends) {
        for (TaskListItemService<Object> itemService : itemBackends) {
            itemBackendServices.put(itemService.getTaskBackend(), itemService);
        }
    }

    public List<CheckListItemDefinitionDto> getTaskListDefinitions(String taskId) {
        final CheckListTemplateDto template = getTaskListTemplateForTask(taskId);
        final CheckListTemplateTaskAssociation templateAssociation = null;

        final List<CheckListItemDefinitionDto> itemDefinitionDtos = new ArrayList<>(template.getItemDefinitions());
        //itemDefinitionDtos.addAll(checkListMapper.mapTaskListItemDefinitionToDto(templateAssociation.getTaskInstanceAssociations()));

        itemDefinitionDtos.sort(Comparator.comparingInt(CheckListItemDefinitionDto::getSortOrder));
        return itemDefinitionDtos;
    }

    public List<CheckListItemDto> getTaskListItems(String taskId) {
        final CheckListTemplateDto templateDto = getTaskListTemplateForTask(taskId);
        final List<CheckListItemDefinitionDto> definitionDtos = getTaskListDefinitions(taskId);

        return definitionDtos.stream().map(def -> getItemBackend(templateDto).getTaskListItem(def)).collect(Collectors.toList());
    }

    public CheckListItemDefinitionDto getTaskListItemDefinition(Long id) {
        return null;
    }

    public CheckListTemplateDto getTaskListTemplate(Long id) {
        return null;
    }

    public CheckListTemplateDto getTaskListTemplateForTask(String taskId) {
        return null;
    }

    public List<CheckListTemplateDto> getTaskListTemplates() {
        return Collections.emptyList();
    }

    public List<CheckListTemplateDto> getTaskListTemplates(String nameQuery) {
        return Collections.emptyList();
    }

    public void setTaskListTemplateItemDefinitions(CheckListTemplateDto templateDto, List<CheckListItemDefinitionDto> definitionDtos) {

    }

    public void setTaskListTemplateAssociation(String taskId, CheckListTemplateDto templateDto, List<CheckListItemDefinitionDto> additionalItemDefinitions) {

    }

    public void setTaskListItemStatus(Long taskListDefinitionId, Boolean status) {
        final CheckListItemDefinitionDto definitionDto = getTaskListItemDefinition(taskListDefinitionId);
        final TaskListItemService<Object> itemBackend = getItemBackend(definitionDto);

        itemBackend.setTaskListItemStatus(itemBackend.getTaskListItem(definitionDto), status);
    }

    public void setTaskListItemStatus(CheckListItemDto itemDto, Boolean status) {
        getItemBackend(itemDto).setTaskListItemStatus(itemDto.getId(), status);
    }

    public Boolean getTaskListItemStatus(CheckListItemDto itemDto) {
        return getItemBackend(itemDto).getTaskListItemStatus(itemDto.getId());
    }

    private TaskListItemService<Object> getItemBackend(CheckListItemDto itemDto) {
        return null;// itemBackendServices.get(itemDto.getBackend());
    }

    private TaskListItemService<Object> getItemBackend(CheckListTemplateDto templateDto) {
        return itemBackendServices.get(templateDto.getItemBackend());
    }

    private TaskListItemService<Object> getItemBackend(CheckListItemDefinitionDto itemDefinitionDto) {
        return null;
    }

    public CheckListTemplateDto createTaskListTemplate(CheckListTemplateDto templateDto) {
        return null;
    }

    public CheckListTemplateDto updateTaskListTemplate(CheckListTemplateDto templateDto) {
        return null;
    }

    public void deleteTaskListTemplate(CheckListTemplateDto templateDto) {

    }

    // **********************

    public List<CheckListTemplateDto> getAllCheckListTemplates() {
        return Collections.emptyList();
    }


    public Optional<CheckListTemplateDto> getCheckListTemplate(Long templateId) {
        return Optional.empty();
    }

    public CheckListTemplateDto saveCheckListTemplate(CheckListTemplateDto template) {
        return null;
    }

    public List<CheckListDto> getCheckListsForUserTaskInstance(String taskInstanceGuid) {
        return null;
    }

    public Optional<CheckListDto> getCheckList(Long checkListId) {
        return Optional.empty();
    }

    public void updateCheckListItemStatus(Long checkListId, String key, Boolean status) {
    }

    public CheckListDto saveCheckList(Long checkListId, CheckListDto updateCheckList) {
        return null;
    }

    class CheckListMapper {
        CheckListItemDefinitionDto mapTaskListItemDefinitionToDto(CheckListItemDefinition itemDefinition) {
            return null;
        }

        List<CheckListItemDefinitionDto> mapTaskListItemDefinitionToDto(List<CheckListItemDefinition> itemDefinitions) {
            return itemDefinitions.stream().map(this::mapTaskListItemDefinitionToDto).collect(Collectors.toList());
        }
    }
}
