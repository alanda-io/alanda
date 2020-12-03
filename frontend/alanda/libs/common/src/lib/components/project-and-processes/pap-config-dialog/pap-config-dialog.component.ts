import {
  Component,
  OnInit,
  ComponentFactoryResolver,
  ViewChild,
  AfterViewInit,
  ChangeDetectorRef,
} from '@angular/core';
import { DynamicDialogConfig, DynamicDialogRef } from 'primeng/dynamicdialog';
import { AlandaProcess } from '../../../api/models/process';
import { AlandaProject } from '../../../api/models/project';
import { ProcessConfigDirective } from '../../../directives/process.config.directive';
import { AlandaProcessConfigModalService } from '../../../services/process-config-modal.service';

interface ProjectTypeProcessConfig {
  guiHiddenRoles?: string;
  obligatoryDueDate?: boolean;
  processDefsToHide?: string[];
  processDefsToHideAfterCompletion?: string[];
  processDefsToHideButShowTasks?: string[];
  startSubprocessMessageName?: string;
  startSubprocessVariable?: string;
  subprocessProperties?: SubprocessProperty[];
}

interface SubprocessProperty {
  processDefinitionKey?: string;
  properties: SubprocessPropertyValue[];
  propertiesTemplate: string;
}

export interface SubprocessPropertyValue {
  defaultValue?: string;
  value?: { value: string; displayName: string };
  displayName?: string;
  propertyName?: string;
  typ?: string;
  values?: { value: string; displayName: string }[];
  display?: boolean;
  projectScope?: boolean;
  hideIfAlreadySet?: boolean;
  description?: string;
}

@Component({
  templateUrl: './pap-config-dialog.component.html',
  styleUrls: ['./pap-config-dialog.component.scss'],
})
export class PapConfigDialogComponent implements OnInit, AfterViewInit {
  process: AlandaProcess;
  project: AlandaProject;
  configuration: ProjectTypeProcessConfig;
  startProcess = false;
  properties: SubprocessPropertyValue[];
  template: string;

  @ViewChild(ProcessConfigDirective) propertiesHost: ProcessConfigDirective;

  constructor(
    public dynamicDialogConfig: DynamicDialogConfig,
    public dynamicDialogRef: DynamicDialogRef,
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    private readonly cdRef: ChangeDetectorRef,
    private readonly templateService: AlandaProcessConfigModalService,
  ) {}

  ngOnInit() {
    this.process = this.dynamicDialogConfig.data.process;
    this.configuration = this.dynamicDialogConfig.data.configuration;
    this.project = this.dynamicDialogConfig.data.project;
    this.startProcess = this.dynamicDialogConfig.data.startProcess;

    this.configuration.subprocessProperties.forEach((prop) => {
      if (
        prop.processDefinitionKey === this.process['processKeyWithoutPhase']
      ) {
        this.properties = prop.properties;
        this.template = prop.processDefinitionKey;
      }
    });
  }

  ngAfterViewInit(): void {
    if (this.template) {
      this.loadTemplate();
      this.cdRef.detectChanges();
    }
  }

  private loadTemplate(): void {
    if (this.templateService.getTemplate(this.template) === undefined) {
      return;
    }
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(
      this.templateService.getTemplate(this.template),
    );
    const viewContainerRef = this.propertiesHost.viewContainerRef;
    viewContainerRef.clear();
    const componentRef = viewContainerRef.createComponent(componentFactory);
    (componentRef.instance as any).project = this.project;
    (componentRef.instance as any).processGuid = this.process?.guid;
  }
}
