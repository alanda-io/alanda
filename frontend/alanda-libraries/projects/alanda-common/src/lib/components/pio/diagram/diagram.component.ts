import { Component, OnInit, AfterContentInit, OnDestroy, OnChanges, Input, ViewChild, ElementRef, Inject, SimpleChanges } from '@angular/core';
import { AlandaTask } from '../../../api/models/task';
// import * as BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.production.min.js';
import * as BpmnJS from 'bpmn-js/dist/bpmn-navigated-viewer.development.js';
import { APP_CONFIG, AppSettings } from '../../../models/appSettings';
import { HttpClient } from '@angular/common/http';
import { catchError } from 'rxjs/operators';
import { importDiagram } from './importDiagram';
import { throwError } from 'rxjs';

@Component({
    selector: 'pio-diagram-component',
    templateUrl: './diagram.component.html',
    styleUrls: [],
})
export class DiagramComponent implements OnInit, AfterContentInit, OnDestroy, OnChanges {

    @Input() pid: string;
    @Input() task: AlandaTask;

    @ViewChild('ref', { static: true }) private el: ElementRef;
    endpointUrl: string;
    private bpmnJS: BpmnJS;
    activities: any[] = [];

    constructor(@Inject(APP_CONFIG) private config: AppSettings, private http: HttpClient) {
        this.endpointUrl = config.API_ENDPOINT + "/pmc-process";
        this.bpmnJS = new BpmnJS({
            keyboard: { bindTo: document }
          });
    };

    loadActivities(canvas) {
        this.http.get(`${this.endpointUrl}/${this.pid}/activities`).subscribe(
            (res: any) => {
                let overlays = this.bpmnJS.get('overlays');
                if(res.childActivityInstances instanceof Array) {
                    for(let act of res.childActivityInstances) {
                        this.addActToMap(act);
                    }
                }
                if(res.childTransitionInstances instanceof Array) {
                    for(let act of res.childTransitionInstances) {
                        this.addToMap(act);
                    }
                }
                for(let key in this.activities) {
                   this.addTokenOverlay(canvas, overlays, key, this.activities[key]);
                }
            }
        );
    }

    addTokenOverlay(canvas, overlays, activityId, count) {
        overlays.add(activityId, null, {
            html: '<span class="lol pd-token pd-token-count">' + count + '</span>',
            type: 'badge',
            position: {
                bottom: -1,
                left: -5
            },
            show: {
                minZoom: 0.2,
                maxZoom: 1.8
            }
        });
        canvas.addMarker(activityId, 'highlight');
    }

    addActToMap(act: any) {
        if(act.childActivityInstances.length > 0) {
            for(let inst of act.childActivityInstances) {
                this.addToMap(inst.activityId);
            }
        } else {
            this.addToMap(act.activityId);
        }
    }

    addToMap(activityId: string) {
        if(activityId.indexOf('#') != -1) {
            activityId = activityId.substring(0, activityId.indexOf('#'));
        }
        if(this.activities[activityId]) {
            this.activities[activityId] = this.activities[activityId] + 1;
        } else {
            this.activities[activityId] = 1;
        }
    }

    ngOnInit(){

    }

    ngOnChanges(changes: SimpleChanges) {
        if(changes.pid) {
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
        this.http.get(`${this.endpointUrl}/${this.pid}/definition/xml`)
        .pipe(
            catchError(err => throwError(err)),
            importDiagram(this.bpmnJS))
        .subscribe(
            (warnings) => {
                let canvas = this.bpmnJS.get('canvas');
                // setTimeout(() => { // just a funny quickfix
                    canvas.zoom('fit-viewport');
                    this.loadActivities(canvas);
                // }, 1000);
            },
            (error) => {

            });
    }
}
