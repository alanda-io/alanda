import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CompleteTaskComponent } from './complete-task.component';
import { CardModule } from 'primeng/card';
import { ButtonModule } from 'primeng/button';
import { RippleModule } from 'primeng/ripple';

@NgModule({
  declarations: [CompleteTaskComponent],
  imports: [CommonModule, CardModule, ButtonModule, RippleModule],
  exports: [CompleteTaskComponent],
})
export class CompleteTaskModule {}
