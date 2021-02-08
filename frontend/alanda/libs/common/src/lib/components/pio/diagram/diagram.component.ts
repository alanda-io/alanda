import {
  Component,
  OnInit,
  AfterContentInit,
  OnDestroy,
  OnChanges,
  Input,
  ViewChild,
  ElementRef,
  Inject,
  SimpleChanges,
} from '@angular/core';
import { AlandaTask } from '../../../api/models/task';
// import * as BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.production.min.js';
import * as BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.development.js';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { importDiagram } from './importDiagram';
import { throwError } from 'rxjs';
import { AlandaUser } from '../../../api/models/user';
import { Authorizations } from '../../../permissions';

@Component({
  selector: 'alanda-diagram-component',
  templateUrl: './diagram.component.html',
  styleUrls: ['./diagram.component.scss'],
})
export class DiagramComponent
  implements AfterContentInit, OnDestroy, OnChanges {
  @Input() pid: string;
  @Input() task: AlandaTask;
  @Input() user: AlandaUser;

  @ViewChild('ref', { static: true }) private readonly el: ElementRef;
  endpointUrl: string;
  activities: any[] = [];
  canViewCamundaCockpit: boolean;

  private readonly bpmnJS: BpmnJS;

  constructor(
    @Inject(APP_CONFIG) private readonly config: AppSettings,
    private readonly http: HttpClient,
  ) {
    this.endpointUrl = config.API_ENDPOINT + '/pmc-process';
    this.bpmnJS = new BpmnJS({
      keyboard: { bindTo: document },
    });
    this.canViewCamundaCockpit = Authorizations.hasPermission(
      this.user,
      'menu:read:administration',
    );
  }

  loadActivities(canvas) {
    this.http
      .get(`${this.endpointUrl}/${this.pid}/activities`)
      .subscribe((res: any) => {
        const overlays = this.bpmnJS.get('overlays');
        if (res.childActivityInstances instanceof Array) {
          for (const act of res.childActivityInstances) {
            this.addActToMap(act);
          }
        }
        if (res.childTransitionInstances instanceof Array) {
          for (const act of res.childTransitionInstances) {
            this.addToMap(act);
          }
        }
        for (const key in this.activities) {
          if (this.activities[key]) {
            this.addTokenOverlay(canvas, overlays, key, this.activities[key]);
          }
        }
      });
  }

  addTokenOverlay(canvas, overlays, activityId, count) {
    overlays.add(activityId, null, {
      html: '<span class="lol pd-token pd-token-count">' + count + '</span>',
      type: 'badge',
      position: {
        bottom: -1,
        left: -5,
      },
      show: {
        minZoom: 0.2,
        maxZoom: 1.8,
      },
    });
    canvas.addMarker(activityId, 'highlight');
  }

  addActToMap(act: any) {
    if (act.childActivityInstances.length > 0) {
      for (const inst of act.childActivityInstances) {
        this.addToMap(inst.activityId);
      }
    } else {
      this.addToMap(act.activityId);
    }
  }

  addToMap(activityId: string) {
    if (activityId.includes('#')) {
      activityId = activityId.substring(0, activityId.indexOf('#'));
    }
    if (this.activities[activityId]) {
      this.activities[activityId] = this.activities[activityId] + 1;
    } else {
      this.activities[activityId] = 1;
    }
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes.pid) {
      this.loadDiagram();
    }
  }

  ngAfterContentInit(): void {
    this.bpmnJS.attachTo(this.el.nativeElement);
  }

  ngOnDestroy(): void {
    this.bpmnJS.destroy();
  }

  loadDiagram() {
    this.http
      .get(`${this.endpointUrl}/${this.pid}/definition/xml`)
      .pipe(
        catchError((err) => throwError(err)),
        importDiagram(this.bpmnJS),
      )
      .subscribe(
        (warnings) => {
          const canvas = this.bpmnJS.get('canvas');
          // setTimeout(() => { // just a funny quickfix
          canvas.zoom('fit-viewport');
          this.loadActivities(canvas);
          // }, 1000);
        },
        (error) => {},
      );
  }
}
