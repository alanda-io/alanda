import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { MenuModule } from 'primeng/menu';
import { SplitButtonModule } from 'primeng/splitbutton';
import { TableModule } from 'primeng/table';
import { PanelModule } from 'primeng/panel';
import { AlandaTaskTableComponent } from './task-table.component';
import { DialogModule } from 'primeng/dialog';
import { MonitorValuesPipeModule } from '../../pipes/nested-object.pipe';
import { FilterPipeModule } from '../../pipes/filter.pipe';
import { CalendarModule } from 'primeng/calendar';
import { TooltipModule } from 'primeng/tooltip';
import { QuickEditProjectDetailsModule } from '../quick-edit-project-details/quick-edit-project-details.module';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { SlideMenuModule } from 'primeng/slidemenu';
import { PanelMenuModule } from 'primeng/panelmenu';

@NgModule({
  declarations: [AlandaTaskTableComponent],
  imports: [
    CommonModule,
    PanelModule,
    ButtonModule,
    DropdownModule,
    TableModule,
    SplitButtonModule,
    MenuModule,
    InputTextModule,
    DialogModule,
    FormsModule,
    ReactiveFormsModule,
    MonitorValuesPipeModule,
    FilterPipeModule,
    CalendarModule,
    TooltipModule,
    QuickEditProjectDetailsModule,
    OverlayPanelModule,
    SlideMenuModule,
    PanelMenuModule,
  ],
  exports: [AlandaTaskTableComponent],
})
export class TaskTableModule {}
