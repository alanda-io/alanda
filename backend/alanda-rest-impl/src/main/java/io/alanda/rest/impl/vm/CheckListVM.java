package io.alanda.rest.impl.vm;

import java.util.List;

public class CheckListVM {
    private Long id;

    private String name;

    private List<CheckListItemVM> checkListItems;

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

    public List<CheckListItemVM> getCheckListItems() {
        return checkListItems;
    }

    public void setCheckListItems(List<CheckListItemVM> checkListItems) {
        this.checkListItems = checkListItems;
    }
}
