package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;

import javax.persistence.*;

@Entity
@Table(name = "AL_CHECKLIST_ITEM")
public class CheckListItem extends AbstractAuditEntity {
    @ManyToOne
    @JoinColumn(name = "DEFINITION")
    private CheckListItemDefinition definition;

    @Column(name = "STATUS", nullable = false)
    private Boolean status;

    public CheckListItemDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(CheckListItemDefinition definition) {
        this.definition = definition;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
