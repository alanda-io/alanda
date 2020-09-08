import { Component } from '@angular/core';
import { DynamicDialogRef, DynamicDialogConfig } from 'primeng/dynamicdialog';

@Component({
  templateUrl: './pap-reason-dialog.component.html',
})
export class PapReasonDialogComponent {

  reason = '';

  constructor(
    public dynamicDialogRef: DynamicDialogRef,
    public config: DynamicDialogConfig,
  ) {}
}
