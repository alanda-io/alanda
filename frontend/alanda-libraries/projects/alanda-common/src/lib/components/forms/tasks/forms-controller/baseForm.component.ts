import { OnInit } from "@angular/core";
import { FormGroup, FormBuilder } from "@angular/forms";
import { PmcTask } from "../../../../models/pmcTask";
import { Project } from "../../../../models/project.model";
import { TaskServiceNg } from "../../../../services/rest/task.service";
import { MessageService } from "primeng/api";
import { Router } from "@angular/router";

  export abstract class BaseFormComponent implements OnInit {

    project: Project;
    task: PmcTask;
    formGroup: FormGroup;

    constructor(fb: FormBuilder, private taskService: TaskServiceNg, private messageService: MessageService, private router: Router) {
        this.formGroup = fb.group({});
    }

    ngOnInit() {
    }

    submitTask() {
        if(this.formGroup.valid){
            this.taskService.complete(this.task.task_id).subscribe(
                res => {
                  this.messageService.add({severity:'success', summary:'Task completed', detail: `Task ${this.task.task_name} has been completed`})
                  this.router.navigate(['tasks/list']);
                },
                error => {
                  this.messageService.add({severity:'error', summary:'Cannot complete task', detail: error.message})
                }
              );
        } else {
            console.log("formGroup is not valid", this.formGroup);
            Object.keys(this.formGroup.controls).forEach(key => {
              const nestedForm = this.formGroup.get(key) as FormGroup;
              Object.keys(nestedForm.controls).forEach(key => {
                nestedForm.get(key).updateValueAndValidity();
                if(!nestedForm.get(key).valid) {
                  nestedForm.get(key).markAsTouched();
                } 
              })
            });
        }
    }
  }