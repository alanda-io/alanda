package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "AL_CHECKLIST")
public class CheckList extends AbstractAuditEntity {
    @ManyToOne
    @JoinColumn(name = "TEMPLATE_CHECKLIST")
    private CheckListTemplateTaskAssociation templateTaskAssociation;

    @Column(name = "USER_TASK")
    private String userTaskInstance;

    @OneToMany(mappedBy = "userTaskInstance")
    private List<CheckListItemDefinition> itemDefinitions;

    public CheckListTemplateTaskAssociation getTemplateTaskAssociation() {
        return templateTaskAssociation;
    }

    public void setTemplateTaskAssociation(CheckListTemplateTaskAssociation templateTaskAssociation) {
        this.templateTaskAssociation = templateTaskAssociation;
    }

    public String getUserTaskInstance() {
        return userTaskInstance;
    }

    public void setUserTaskInstance(String userTaskInstance) {
        this.userTaskInstance = userTaskInstance;
    }

    public List<CheckListItemDefinition> getItemDefinitions() {
        return itemDefinitions;
    }

    public void setItemDefinitions(List<CheckListItemDefinition> itemDefinitions) {
        this.itemDefinitions = itemDefinitions;
    }
}
