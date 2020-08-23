package io.alanda.base.entity.checklist;

import io.alanda.base.entity.AbstractAuditEntity;
import org.hibernate.annotations.Fetch;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "AL_CHECKLIST_TEMPLATE_CHECKLIST")
public class CheckListTemplateTaskAssociation extends AbstractAuditEntity {
    @Column(name = "USER_TASK_DEF_KEY")
    private String userTaskDefKey;

    @ManyToOne
    @JoinColumn(name = "template")
    private CheckListTemplate checkListTemplate;

    @OneToMany(
            mappedBy = "templateTaskAssociation",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<CheckList> taskInstanceAssociations = new ArrayList<>();

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

    public void addChecklist(CheckList checkList) {
        taskInstanceAssociations.add(checkList);
        checkList.setTemplateTaskAssociation(this);
    }

    public void removeChecklist(CheckList checkList) {
        taskInstanceAssociations.remove(checkList);
        checkList.setTemplateTaskAssociation(null);
    }
}
