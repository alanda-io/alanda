import { Component, OnInit, Input } from '@angular/core';
import { SelectItem } from 'primeng/api';
import { FormGroup, FormBuilder, Validators, AbstractControl } from '@angular/forms';
import { AlandaTaskApiService } from '../../../../api/taskApi.service';

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

    @Input()
    set rootFormGroup(rootFormGroup: FormGroup) {
      if (rootFormGroup) {
        rootFormGroup.addControl('alanda-simple-select', this.selectForm);
      }
    }

    selectForm = this.fb.group({
      selected: [null, Validators.required]
    });

    constructor(private taskService: AlandaTaskApiService, private fb: FormBuilder){}

    ngOnInit(){
      if(!this.type) {
        this.type = 'string';
      }
    }

    save() {
      this.taskService.setVariable(this.task.task_id,this.variableName, {value: this.selectForm.get('selected').value, type: this.type}).subscribe();
    }

    get selected(): AbstractControl { return this.selectForm.get('selected'); }

  }
