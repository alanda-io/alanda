package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "AL_CHECKLIST_TEMPLATE_CHECKLIST")
public class CheckListTemplateTaskAssociation extends AbstractAuditEntity {
    @Column(name = "USER_TASK_DEF_KEY")
    private String userTaskDefKey;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "template")
    private CheckListTemplate checkListTemplate;

    @OneToMany(mappedBy = "templateTaskAssociation")
    private List<CheckList> taskInstanceAssociations;

    public String getUserTaskDefKey() {
        return userTaskDefKey;
    }

    public void setUserTaskDefKey(String userTask) {
        this.userTaskDefKey = userTask;
    }

    public CheckListTemplate getCheckListTemplate() {
        return checkListTemplate;
    }

    public void setTaskListTemplate(CheckListTemplate checkListTemplate) {
        this.checkListTemplate = checkListTemplate;
    }

    public List<CheckList> getTaskInstanceAssociations() {
        return taskInstanceAssociations;
    }

    public void setTaskInstanceAssociations(List<CheckList> taskInstanceItemDefinitions) {
        this.taskInstanceAssociations = taskInstanceItemDefinitions;
    }
}
