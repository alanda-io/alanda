import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';

@Component({
  selector: 'alanda-permissions-demo',
  templateUrl: './permissions-demo.component.html',
  styles: [
    `
      .disabled,
      [disabled] {
        border: 1px solid red;
      }
    `,
  ],
})
export class PermissionsDemoComponent implements OnInit {
  demoForm = this.fb.group({
    guid: [],
    projectName: [],
    title: [],
    status: [],
    phases: [],
    running: [],
  });

  constructor(private readonly fb: FormBuilder) {}

  ngOnInit() {}

  onSubmit() {}
}
