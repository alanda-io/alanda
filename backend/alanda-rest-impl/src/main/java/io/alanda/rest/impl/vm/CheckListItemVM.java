package io.alanda.rest.impl.vm;

public class CheckListItemVM {
    private Boolean customDefinition;

    private String key;

    private String displayText;

    private Boolean required;

    private Long sortOrder;

    private Boolean status;

    public Boolean getCustomDefinition() {
        return customDefinition;
    }

    public void setCustomDefinition(Boolean customDefinition) {
        this.customDefinition = customDefinition;
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

    public Boolean getRequired() {
        return required;
    }

    public void setRequired(Boolean required) {
        this.required = required;
    }

    public Long getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Long sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
