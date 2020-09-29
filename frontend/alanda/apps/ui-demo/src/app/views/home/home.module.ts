import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home.component';
import { CardModule } from 'primeng/card';
import { AlandaCommonModule } from '@alanda/common';
import { ButtonModule } from 'primeng/button';
import { CheckboxModule } from 'primeng/checkbox';
import { FormsModule } from '@angular/forms';
import { HomeRoutingModule } from './home-routing.module';

@NgModule({
  declarations: [HomeComponent],
  imports: [
    CommonModule,
    AlandaCommonModule,
    CardModule,
    ButtonModule,
    CheckboxModule,
    FormsModule,
    HomeRoutingModule,
  ],
})
export class HomeModule {}
