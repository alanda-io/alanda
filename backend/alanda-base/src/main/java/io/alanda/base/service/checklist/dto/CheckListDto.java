package io.alanda.base.service.checklist.dto;

import io.alanda.base.entity.checklist.CheckListItem;

import java.util.List;

public class CheckListDto {
    private Long id;

    private String name;

    private String userTask;

    private List<CheckListItemDto> checkListItems;


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

    public String getUserTask() {
        return userTask;
    }

    public void setUserTask(String userTask) {
        this.userTask = userTask;
    }

    public List<CheckListItemDto> getCheckListItems() {
        return checkListItems;
    }

    public void setCheckListItems(List<CheckListItemDto> checkListItems) {
        this.checkListItems = checkListItems;
    }
}
