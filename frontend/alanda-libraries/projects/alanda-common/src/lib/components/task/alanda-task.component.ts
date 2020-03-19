import { Component, OnInit } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaFormsRegisterService } from '../../services/formsRegister.service';
import { ActivatedRoute } from '@angular/router';
import { switchMap, tap } from 'rxjs/operators';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTaskApiService } from '../../api/taskApi.service';

@Component({
    providers: [AlandaFormsRegisterService],
    template: '',
})
export class AlandaTaskComponent implements OnInit {

  project: AlandaProject;
  task: AlandaTask;

  constructor(public formsRegisterService: AlandaFormsRegisterService, private route: ActivatedRoute,
              private taskService: AlandaTaskApiService, private projectService: AlandaProjectApiService) {}

  ngOnInit() {

    /* this.route.paramMap.pipe(
      switchMap(params => this.taskService.getTask(params.get('taskId')))
    ).subscribe(task => {
      this.task = task;
      this.projectService.getProjectByGuid(task.pmcProjectGuid)
    }); */

    this.route.paramMap.pipe(
      switchMap(params => this.taskService.getTask(params.get('taskId'))),
      tap(task => {
        this.task = task;
      }),
      switchMap(_=> this.projectService.getProjectByGuid(this.task.pmcProjectGuid))
    ).subscribe(project => this.project = project);

    /* this.route.paramMap.pipe(
      switchMap((params: ParamMap) => {
        this.taskService.getTask(params.get('taskId'))
      }
  ).subscribe(task => {
      this.task = task;
      this.projectService.getProjectByGuid(task.pmcProjectGuid).subscribe(project => {
          this.project = project;
      });
  })); */

  }


  submitTask() {
      this.formsRegisterService.submit(this.task);
  }



}
