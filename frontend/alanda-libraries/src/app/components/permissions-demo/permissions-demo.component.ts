import {Component, OnInit} from '@angular/core';
import {FormBuilder} from '@angular/forms';

@Component({
  selector: 'app-permissions-demo',
  templateUrl: './permissions-demo.component.html'
})
export class PermissionsDemoComponent implements OnInit {

  demoForm = this.fb.group({
    guid: [],
    projectName: [],
    title: [],
    status: [],
    phases: [],
    running: []
  });

  constructor(private fb: FormBuilder) {
  }

  ngOnInit() {

  }

  onSubmit() {

  }

}
