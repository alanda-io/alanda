import { Component, OnInit, Input } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { AlandaFormsRegisterService } from 'projects/alanda-common/src/lib/services/formsRegister.service';
import { AlandaTaskApiService } from 'projects/alanda-common/src/lib/api/taskApi.service';

@Component({
    selector: 'alanda-simple-select',
    templateUrl: './simple-select.component.html',
    styleUrls: [],
  })
export class AlandaSimpleSelectComponent implements OnInit {

    @Input() items: SelectItem[];
    @Input() variableName: string;
    @Input() task: any;
    @Input() label: string;
    @Input() type?: string;

    selectForm: FormGroup;

    constructor(private taskService: AlandaTaskApiService, private fb: FormBuilder, private formsRegisterService: AlandaFormsRegisterService){}

    ngOnInit(){
      if(!this.type) {
        this.type = 'string';
      }

      this.initFormGroup();
    }

    private initFormGroup() {
      this.selectForm = this.fb.group({
        selected: [null, Validators.required]
      });
      this.formsRegisterService.registerForm(this.selectForm, "selectForm");
    }

    save() {
      this.taskService.setVariable(this.task.task_id,this.variableName, {value: this.selectForm.get('selected').value, type: this.type}).subscribe();
    }

    get selected(): AbstractControl { return this.selectForm.get('selected'); }

  }
