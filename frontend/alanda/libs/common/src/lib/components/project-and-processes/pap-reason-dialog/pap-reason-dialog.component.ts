import { Component } from '@angular/core';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';

@Component({
  templateUrl: './pap-reason-dialog.component.html',
  styleUrls: ['./pap-reason-dialog.component.scss'],
})
export class PapReasonDialogComponent {
  reason = '';

  constructor(
    public dynamicDialogRef: DynamicDialogRef,
    public config: DynamicDialogConfig,
  ) {}
}
