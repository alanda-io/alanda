
import { Component, OnInit, Input, EventEmitter, Output } from "@angular/core";
import { MessageService } from "primeng/api";
import { FormGroup, FormControl } from "@angular/forms";
import { PropertyService } from "../../../services/rest/property.service";

@Component({
    selector: 'datepicker-variable',
    templateUrl: './datepicker.component.html',
    styleUrls: [],
  })
export class DatepickerComponent implements OnInit {

    @Input() key: string;
    @Input() label: string;
    @Input() project: any;

    @Output() formEvent = new EventEmitter<FormGroup>();
    dateForm: FormGroup;

    constructor(private messageService: MessageService, private propertyService: PropertyService){}

    ngOnInit(){
      this.initFormGroup();
      this.registerForm();
      this.load();

    }

    initFormGroup() {
      this.dateForm = new FormGroup({
        date: new FormControl(null),
      });
    }

    save() {
      let d = new Date(this.dateForm.get('date').value);
      let utcDate = new Date(Date.UTC(d.getFullYear(), d.getMonth(), d.getDate()));
      this.propertyService.setString(null, null, this.project.guid, this.key, utcDate.toISOString().substring(0,10)).subscribe();
    }


    registerForm(){
      this.formEvent.emit(this.dateForm);
    }

    load() {
      this.propertyService.get(null, null, this.project.guid, this.key).subscribe(
        res => {
          if(res.value) {
            let date = new Date(res.value);
            this.dateForm.get('date').setValue(date);
          }
        }
      );
    }    
  }