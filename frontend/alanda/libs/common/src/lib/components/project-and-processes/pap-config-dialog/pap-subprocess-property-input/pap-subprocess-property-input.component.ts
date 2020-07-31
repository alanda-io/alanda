import { Component, OnInit, Input } from '@angular/core';
import { SubprocessPropertyValue } from '../pap-config-dialog.component';
import { AlandaPropertyApiService } from '../../../../api/propertyApi.service';

@Component({
  selector: 'alanda-pap-subprocess-property-input',
  templateUrl: './pap-subprocess-property-input.component.html',
})
export class PapSubprocessPropertyInputComponent implements OnInit {
  @Input() property: SubprocessPropertyValue;
  @Input() projectGuid: number;
  @Input() processGuid: number;

  constructor(private readonly propertyService: AlandaPropertyApiService) {}

  ngOnInit() {
    this.loadProperty();
  }

  saveProperty(property: SubprocessPropertyValue): void {
    let propertyName = property.propertyName;
    if (!property.projectScope) {
      propertyName += '_' + this.processGuid;
    }
    this.propertyService
      .setString(null, null, this.projectGuid, propertyName, property.value)
      .subscribe();
  }

  private loadProperty(): void {
    let propertyName = this.property.propertyName;
    if (!this.property.projectScope) {
      propertyName += '_' + this.processGuid;
    }
    this.property.display = true;
    this.propertyService
      .get(null, null, this.projectGuid, propertyName)
      .subscribe((response) => {
        if (response.value) {
          this.property.value = response.value;
          if (this.property.hideIfAlreadySet) {
            this.property.display = false;
          }
        } else {
          this.property.value = this.property.defaultValue;
        }
      });
  }
}
