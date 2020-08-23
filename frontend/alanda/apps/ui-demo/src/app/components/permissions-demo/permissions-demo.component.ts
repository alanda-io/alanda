import { Component, OnInit } from '@angular/core';
import { FormBuilder } from '@angular/forms';
import { UserStoreImpl } from '../../store/user';

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
    fc: {},
  });
  user$ = this.userStore.currentUser$;

  constructor(
    private readonly fb: FormBuilder,
    private userStore: UserStoreImpl,
  ) {}

  ngOnInit() {}

  onSubmit() {}
}
