package io.alanda.rest.impl.vm;

public class CheckListItemVM {
    private CheckListItemDefinitionVM definition;

    private Boolean status;

    public CheckListItemDefinitionVM getDefinition() {
        return definition;
    }

    public void setDefinition(CheckListItemDefinitionVM definition) {
        this.definition = definition;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
