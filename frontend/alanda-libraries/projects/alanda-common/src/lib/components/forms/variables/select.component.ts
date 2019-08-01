
import { Component, OnInit, Input, EventEmitter, Output } from "@angular/core";
import { Router } from "@angular/router";
import { MessageService, SelectItem } from "primeng/api";
import { TaskServiceNg } from "../../../services/rest/task.service";
import { FormGroup, FormControl, FormBuilder, Validators } from "@angular/forms";

@Component({
    selector: 'select-variable',
    templateUrl: './select.component.html',
    styleUrls: [],
  })
export class SelectComponent implements OnInit {

    @Input() items: SelectItem[];
    @Input() variableName: string;
    @Input() task: any;
    @Input() label: string;
    @Input() type?: string;

    selectForm: FormGroup;
    @Input() formGroup: FormGroup;
    
    constructor(private taskService: TaskServiceNg, private fb: FormBuilder){}

    ngOnInit(){
      if(!this.type) {
        this.type = 'string';
      }

      this._initFormGroup();
    }

    private _initFormGroup() {
      this.selectForm = this.fb.group({
        selected: [null, Validators.required]
      });
      this.formGroup.addControl('selectForm', this.selectForm);
      //this.formGroup.get('selected').hasError('')
    }

    save() {
      this.taskService.setVariable(this.task.task_id,this.variableName, {value: this.selectForm.get('selected').value, type: this.type}).subscribe();
    }
    
  }