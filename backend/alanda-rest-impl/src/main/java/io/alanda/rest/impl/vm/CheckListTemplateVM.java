package io.alanda.rest.impl.vm;

import io.alanda.base.entity.checklist.CheckListItemBackend;

import java.util.List;

public class CheckListTemplateVM {
    private Long id;

    private String name;

    private CheckListItemBackend itemBackend;

    private List<String> userTasks;

    private List<CheckListItemDefinitionVM> itemDefinitions;

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

    public CheckListItemBackend getItemBackend() {
        return itemBackend;
    }

    public void setItemBackend(CheckListItemBackend itemBackend) {
        this.itemBackend = itemBackend;
    }

    public List<String> getUserTasks() {
        return userTasks;
    }

    public void setUserTasks(List<String> userTasks) {
        this.userTasks = userTasks;
    }

    public List<CheckListItemDefinitionVM> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(List<CheckListItemDefinitionVM> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }
}
