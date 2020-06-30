import { Component, Input } from '@angular/core';
import { MenuItem } from 'primeng/api';
import { AlandaSimplePhase } from '../../api/models/simplePhase';
import { AlandaProject } from '../../api/models/project';
import { AlandaProjectApiService } from '../../api/projectApi.service';
import { switchMap } from 'rxjs/operators';
import { RxState } from '@rx-angular/state';

export interface AlandaPhaseTabState {
  simplePhases: AlandaSimplePhase[];
  project: AlandaProject
}

@Component({
  selector: 'alanda-phase-tab',
  templateUrl: './phase-tab.component.html',
  styleUrls: ['./phase-tab.component.scss']
})
export class AlandaPhaseTabComponent extends RxState<AlandaPhaseTabState> {
  @Input()
  set project(project: AlandaProject) {
    this.set({ project });
  }

  activeItemIndex = 0;

  menuItems: MenuItem[] = [
    {
      label: 'Enabled',
      command: (event) => this.togglePhaseEnabled(true),
    },
    {
      label: 'Disabled',
      command: (event) => this.togglePhaseEnabled(false)
    }
  ];

  simplePhases$ = this.select('project').pipe(
    switchMap((project: AlandaProject) => {
      return this.projectApiService.getPhasesForProject(project.guid)
    })
  )

  constructor(
    private readonly projectApiService: AlandaProjectApiService
  ) {
    super();
    this.connect('simplePhases', this.simplePhases$);
  }

  setActiveItemIndex(index): void {
    this.activeItemIndex = index;
  }

  togglePhaseEnabled(enabled: boolean): void {
    const projectGuid = this.get().project.guid;
    const phaseDefidName = this.get().simplePhases[this.activeItemIndex].pmcProjectPhaseDefinition.idName;

    this.projectApiService.setPhaseEnabled(projectGuid, phaseDefidName, enabled).subscribe(response => {
      this.set('simplePhases', oldState => {
        const phases = [...oldState.simplePhases]
        phases[this.activeItemIndex].enabled = enabled;
        return phases;
      });
    })
  }
}
