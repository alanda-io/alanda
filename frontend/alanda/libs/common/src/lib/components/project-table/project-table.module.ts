import { NgModule } from '@angular/core';
import { AlandaProjectTableComponent } from './project-table.component';
import { TableModule } from 'primeng/table';
import { CommonModule } from '@angular/common';
import { MonitorValuesPipeModule } from '../../pipes/nested-object.pipe';
import { MenuModule } from 'primeng/menu';
import { DropdownModule } from 'primeng/dropdown';
import { InputTextModule } from 'primeng/inputtext';
import { ButtonModule } from 'primeng/button';
import { FormsModule } from '@angular/forms';
import { PageSizeSelectModule } from '../page-size-select/page-size-select.module';
import { CalendarModule } from 'primeng/calendar';
import { DialogModule } from 'primeng/dialog';
import { QuickEditProjectDetailsModule } from '../quick-edit-project-details/quick-edit-project-details.module';
import { OverlayPanelModule } from 'primeng/overlaypanel';
import { RippleModule } from 'primeng/ripple';

@NgModule({
  imports: [
    TableModule,
    CommonModule,
    MonitorValuesPipeModule,
    MenuModule,
    DropdownModule,
    InputTextModule,
    ButtonModule,
    FormsModule,
    PageSizeSelectModule,
    CalendarModule,
    DialogModule,
    QuickEditProjectDetailsModule,
    OverlayPanelModule,
    RippleModule,
  ],
  declarations: [AlandaProjectTableComponent],
  exports: [AlandaProjectTableComponent],
})
export class ProjectTableModule {}
