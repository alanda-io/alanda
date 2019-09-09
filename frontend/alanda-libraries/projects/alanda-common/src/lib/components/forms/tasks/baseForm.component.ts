import { FormGroup, FormBuilder } from "@angular/forms";
import { PmcTask } from "../../../models/pmcTask";
import { Project } from "../../../models/project.model";
import { TaskServiceNg } from "../../../services/rest/task.service";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";

  export abstract class BaseFormComponent {

    private project: Project;
    private task: PmcTask;
    private baseFormGroup: FormGroup;

    constructor(private fb: FormBuilder, private taskService: TaskServiceNg, private messageService: MessageService, private router: Router) {
        this.baseFormGroup = fb.group({});
    }

    submitTask() {
      console.log(this.baseFormGroup);
      
      if(this.baseFormGroup.valid){
          /* this.taskService.complete(this.task.task_id).subscribe(
            res => {
              this.messageService.add({severity:'success', summary:'Task completed', detail: `Task ${this.task.task_name} has been completed`})
              this.router.navigate(['tasks/list']);
            },
            error => {
              this.messageService.add({severity:'error', summary:'Cannot complete task', detail: error.message})
            }); */
      } else {
          console.log("formGroup is not valid", this.baseFormGroup);
          Object.keys(this.baseFormGroup.controls).forEach(key => {
            const nestedForm = this.baseFormGroup.get(key) as FormGroup;
            Object.keys(nestedForm.controls).forEach(key => {
              nestedForm.get(key).updateValueAndValidity();
              if(!nestedForm.get(key).valid) {
                nestedForm.get(key).setErrors({'notValid': true});
              } 
            })
          });
      }
    }
  }