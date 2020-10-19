import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AlandaPropCheckboxComponent } from './components/prop-checkbox/prop-checkbox.component';
import { AlandaPropSelectComponent } from './components/prop-select/prop-select.component';
import { AlandaVarCheckboxComponent } from './components/var-checkbox/var-checkbox.component';
import { AlandaVarDatepickerComponent } from './components/var-datepicker/var-datepicker.component';
import { AlandaVarDisplayComponent } from './components/var-display/var-display.component';
import { AlandaVarRoleUserSelectComponent } from './components/var-role-user-select/var-role-user-select.component';
import { AlandaVarSelectComponent } from './components/var-select/var-select.component';
import { AlandaFormsControllerComponent } from './forms-controller/forms-controller.component';
import { CheckboxModule } from 'primeng/checkbox';
import { MessageModule } from 'primeng/message';
import { DropdownModule } from 'primeng/dropdown';
import { CalendarModule } from 'primeng/calendar';
import { ReactiveFormsModule } from '@angular/forms';
import { TabViewModule } from 'primeng/tabview';
import { RouterModule } from '@angular/router';
import { PioModule } from '../components/pio/pio.module';
import { HistoryGridModule } from '../components/history/history-grid.module';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { AutoCompleteModule } from 'primeng/autocomplete';
import { AlandaPropAutocompleteEagerComponent } from './components/prop-autocomplete-eager/prop-autocomplete-eager.component';
import { AlandaVarTextComponent } from './components/var-text/var-text.component';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { AlandaVarTextareaComponent } from './components/var-textarea/var-textarea.component';
import { AlandaPropTextareaComponent } from './components/prop-textarea/prop-textarea.component';
import { LabelModule } from '../components/label/label.module';

@NgModule({
  declarations: [
    AlandaPropCheckboxComponent,
    AlandaPropSelectComponent,
    AlandaVarCheckboxComponent,
    AlandaVarDatepickerComponent,
    AlandaVarDisplayComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarSelectComponent,
    AlandaFormsControllerComponent,
    AlandaPropAutocompleteEagerComponent,
    AlandaVarTextComponent,
    AlandaVarTextareaComponent,
    AlandaPropTextareaComponent,
  ],
  imports: [
    CommonModule,
    CheckboxModule,
    MessageModule,
    DropdownModule,
    AutoCompleteModule,
    CalendarModule,
    ReactiveFormsModule,
    TabViewModule,
    RouterModule,
    ProgressSpinnerModule,
    PioModule,
    HistoryGridModule,
    InputTextareaModule,
    InputTextModule,
    LabelModule,
  ],
  exports: [
    AlandaPropCheckboxComponent,
    AlandaPropSelectComponent,
    AlandaVarCheckboxComponent,
    AlandaVarDatepickerComponent,
    AlandaVarDisplayComponent,
    AlandaVarRoleUserSelectComponent,
    AlandaVarSelectComponent,
    AlandaFormsControllerComponent,
    AlandaPropAutocompleteEagerComponent,
    AlandaVarTextComponent,
    AlandaVarTextareaComponent,
    AlandaPropTextareaComponent,
  ],
})
export class FormModule {}
