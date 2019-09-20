import { OnInit, Component, Input } from "@angular/core";
import { MessageService } from "primeng/api";
import { ProjectServiceNg } from "../../../core/api/project.service";

@Component({
    selector: 'project-control-item',
    templateUrl: './project-control-item.component.html' ,
    styles: []
  })
  export class ProjectControlItemComponent implements OnInit {

    @Input() projectItem: any;

    constructor(private pmcProjectService: ProjectServiceNg, private messageService: MessageService) {

    }

    ngOnInit() {
      console.log("project-control-item INIT");
      console.log("projectItem", this.projectItem);
      //let process = this.getMainProcess(this.pro)
    }

    getMainProcess(processes): any {
      processes.forEach(p => {
        if(p.status == 'MAIN') {
          return p;
        }
      });
      return null;
    }



    /* proc = pmcProjectService.getMainProcessFromProject(scope.project)
      if proc?
        scope.processInstanceId = proc.processInstanceId

      pmcProjectService.getPhasesForProject(scope.project.guid)
        .then((phases) ->
          scope.phaseMap = {}
          for p in phases
            scope.phaseMap[p.pmcProjectPhaseDefinition.idName] = p

          loadProcesses() */
  

  }