import { FormBuilder } from "@angular/forms";
import { PmcTask } from "../../../models/pmcTask";
import { Project } from "../../../models/project.model";
import { TaskServiceNg } from "../../../services/rest/task.service";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";
import { FormsRegisterService } from "../../../services/forms-register.service";

  export abstract class BaseFormComponent {

    project: Project;
    task: PmcTask;

    constructor(private fb: FormBuilder, private taskService: TaskServiceNg, private messageService: MessageService, private router: Router,
                private formsRegisterService: FormsRegisterService) {
                  this.formsRegisterService.init();
    }

    submitTask() {
      if(this.formsRegisterService.isValid()){
        this.taskService.complete(this.task.task_id).subscribe(
          res => {
            this.messageService.add({severity:'success', summary:'Task completed', detail: `Task ${this.task.task_name} has been completed`})
            this.router.navigate(['tasks/list']);
          },
          error => {
            this.messageService.add({severity:'error', summary:'Cannot complete task', detail: error.message})
          });
      } else {
        this.messageService.add({severity:'error', summary:'Cannot complete task', detail:'Please fill out all required fields'})
        this.formsRegisterService.touch();
      }
    }
  }