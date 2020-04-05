import { Component, OnInit } from '@angular/core';
import { AlandaProject } from '../../api/models/project';
import { AlandaTask } from '../../api/models/task';
import { AlandaFormsRegisterService } from '../../services/formsRegister.service';
import { ActivatedRoute } from '@angular/router';
import { switchMap, map } from 'rxjs/operators';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { AlandaTaskApiService } from '../../api/taskApi.service';

@Component({
    providers: [AlandaFormsRegisterService],
    template: '',
})
export class AlandaTaskFormComponent implements OnInit {

  project: AlandaProject;
  task: AlandaTask;

  constructor(public formsRegisterService: AlandaFormsRegisterService, private route: ActivatedRoute,
              private taskService: AlandaTaskApiService, private projectService: AlandaProjectApiService) {}

  ngOnInit() {
    this.route.paramMap.pipe(
      switchMap(params => {
        return this.taskService.getTask(params.get('taskId')).pipe(
          switchMap(task => this.projectService.getProjectByGuid(task.pmcProjectGuid).pipe(
            map(project => ({task: task, project: project}))
          ))
        );
      })
    ).subscribe(res => {this.task = res.task; this.project = res.project; });
  }

  submitTask() {
    this.formsRegisterService.submit(this.task);
  }



}
