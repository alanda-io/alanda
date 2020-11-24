import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CompleteTaskComponent } from './complete-task.component';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';

@NgModule({
  declarations: [CompleteTaskComponent],
  imports: [CommonModule, CardModule, ButtonModule],
  exports: [CompleteTaskComponent],
})
export class CompleteTaskModule {}
