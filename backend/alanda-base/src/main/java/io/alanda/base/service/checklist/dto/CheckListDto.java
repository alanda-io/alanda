package io.alanda.base.service.checklist.dto;

import io.alanda.base.entity.checklist.CheckListItem;

import java.util.List;

public class CheckListDto {
    private Long id;

    private CheckListTemplateDto checkListTemplate;

    private String userTask;

    private List<CheckListItemDefinitionDto> itemDefinitions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CheckListTemplateDto getCheckListTemplate() {
        return checkListTemplate;
    }

    public void setCheckListTemplate(CheckListTemplateDto checkListTemplate) {
        this.checkListTemplate = checkListTemplate;
    }

    public String getUserTask() {
        return userTask;
    }

    public void setUserTask(String userTask) {
        this.userTask = userTask;
    }

    public List<CheckListItemDefinitionDto> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(List<CheckListItemDefinitionDto> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }
}
