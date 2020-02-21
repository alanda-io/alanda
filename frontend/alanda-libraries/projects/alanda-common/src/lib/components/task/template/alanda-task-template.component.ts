import { Component, OnInit } from '@angular/core';
import { AlandaProject } from '../../../api/models/alandaProject';
import { AlandaTask } from '../../../api/models/alandaTask';
import { AlandaFormsRegisterService } from '../../../services/alandaFormsRegister.service';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { switchMap, tap } from 'rxjs/operators';
import { AlandaTaskService } from '../../../api/alandaTask.service';
import { AlandaProjectService } from '../../../api/alandaProject.service';

@Component({
    providers: [AlandaFormsRegisterService],
    template: '',
})
export class AlandaTaskTemplateComponent implements OnInit {

    constructor(public formsRegisterService: AlandaFormsRegisterService, private route: ActivatedRoute,
                private taskService: AlandaTaskService, private projectService: AlandaProjectService) {}

    project: AlandaProject;
    task: AlandaTask;


    ngOnInit() {
     /*  this.route.paramMap.pipe(
        switchMap((params: ParamMap) => {
          this.taskService.getTask(params.get('taskId'))
        }
    ).subscribe(task => {
        this.task = task;
        this.projectService.getProjectByGuid(task.pmcProjectGuid).subscribe(project => {
            this.project = project;
        });
      )} */
      this.route.paramMap.pipe(
        switchMap((params: ParamMap) => {
          return this.taskService.getTask(params.get('taskId'));
        }),
        tap(task => {
          this.task = task;
          return this.projectService.getProjectByGuid(task.pmcProjectGuid);
        })).subscribe(project => this.project = project);
      }

    submitTask() {
        this.formsRegisterService.submit(this.task);
    }



}
