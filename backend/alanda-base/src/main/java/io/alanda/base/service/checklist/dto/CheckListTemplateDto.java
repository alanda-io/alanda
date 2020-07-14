package io.alanda.base.service.checklist.dto;

import io.alanda.base.entity.checklist.CheckListItemBackend;

import java.util.List;

public class CheckListTemplateDto {
    private Long id;
    private String name;
    private CheckListItemBackend taskBackend;
    private List<String> userTasks;
    private List<CheckListItemDefinitionDto> itemDefinitions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CheckListItemBackend getTaskBackend() {
        return taskBackend;
    }

    public void setTaskBackend(CheckListItemBackend taskBackend) {
        this.taskBackend = taskBackend;
    }

    public List<String> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<String> userTasks) {
        this.userTasks = userTasks;
    }

    public List<CheckListItemDefinitionDto> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(List<CheckListItemDefinitionDto> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }
}
