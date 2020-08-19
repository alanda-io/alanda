package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "AL_CHECKLIST_TEMPLATE")
public class CheckListTemplate extends AbstractAuditEntity {
    @Column(name = "NAME", nullable = false)
    private String name;

    @Column(name = "ITEM_BACKEND", nullable = false)
    @Enumerated(EnumType.STRING)
    private CheckListItemBackend itemBackend;

    //@OneToMany(fetch = FetchType.EAGER)
    //@JoinColumn(name = "TEMPLATE")
    @OneToMany(mappedBy = "checkListTemplate", fetch = FetchType.EAGER)
    private List<CheckListItemDefinition> itemDefinitions;

    @OneToMany(mappedBy = "checkListTemplate", fetch = FetchType.EAGER)
    //@JoinColumn(name = "TEMPLATE")
    private List<CheckListTemplateTaskAssociation> taskAssociations;

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

    public List<CheckListItemDefinition> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(List<CheckListItemDefinition> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }

    public List<CheckListTemplateTaskAssociation> getTaskAssociations() {
        return taskAssociations;
    }

    public void setTaskAssociations(List<CheckListTemplateTaskAssociation> taskAssociations) {
        this.taskAssociations = taskAssociations;
    }
}
