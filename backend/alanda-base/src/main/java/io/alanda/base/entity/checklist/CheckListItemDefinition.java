package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "AL_CHECKLIST_ITEM_DEFINITION")
public class CheckListItemDefinition extends AbstractAuditEntity {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "TEMPLATE")
    private CheckListTemplate checkListTemplate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "checklist")
    private CheckList checkList;

    @Column(name = "KEY", nullable = false)
    private String key;

    @Column(name = "DISPLAY_TEXT", nullable = false)
    private String displayText;

    @Column(name = "SORT_ORDER")
    private Integer sortOrder;

    @Column(name = "REQUIRED", nullable = false)
    private Boolean required;

    public CheckListTemplate getCheckListTemplate() {
        return checkListTemplate;
    }

    public void setCheckListTemplate(CheckListTemplate definition) {
        if (checkList != null) {
            throw new IllegalStateException("Can't associate with template when user task instance is already set");
        }

        this.checkListTemplate = definition;
    }

    public CheckList getCheckList() {
        return checkList;
    }

    public void setCheckList(CheckList userTaskInstance) {
        if (checkListTemplate != null) {
            throw new IllegalStateException("Can't associate with user task instance when template is already set");
        }

        this.checkList = userTaskInstance;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }
}
