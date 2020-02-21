import { OnInit, Component, Input } from "@angular/core";
import { ProcessRelation } from "../../../enums/processRelation.enum";
import { Router } from "@angular/router";
import { FlattenProjectResult } from "../project-and-processes.component";
import { AlandaProcess } from '../../../api/models/alandaProcess';
import { AlandaTask } from '../../../api/models/alandaTask';

@Component({
    selector: 'project-control-item',
    templateUrl: './project-control-item.component.html' ,
    styles: []
  })
  export class ProjectControlItemComponent implements OnInit {

    @Input() info: FlattenProjectResult;
    processInfoArray: any[] = [];
    showProcessTasks: any[] = [];

    constructor(private router: Router) {}

    ngOnInit() {
      // TODO: projectTypeConfig, subprocessProperties

      // TODO: get main Process (better to pass as an input)
      // TODO: get Phases for Project, create PhaseMap
      this.loadProcesses();
    }

    private loadProcesses() {
      let allowedProcesses: AlandaProcess[] = [];

      for(let aProcess of this.info.processesAndTasks['allowed']) {
        allowedProcesses.push(aProcess);
      }

      let activeProcesses: AlandaProcess[] = this.info.processesAndTasks['active'];
      for(let process of activeProcesses) {
        // TODO: hide or show processes, check auth

        if(!process.businessObject && process.relation == ProcessRelation.MAIN) {
          process.businessObject = this.info.project.refObjectIdName;
        }

        if(!process.label) {
          for(let allowedProcess of allowedProcesses) {
            // TODO: set label, check auth
          }
        }

        if(process.phase && process.phase != 'default') {
          //TODO: set process start
        }

        this.processInfoArray.push(process);
      }

    }

    public openForm(task: AlandaTask) {
      this.router.navigate([`forms/${task.formKey}/${task.task_id}`]);
    }

  }
