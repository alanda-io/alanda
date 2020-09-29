import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';
import { HomeComponent } from './home.component';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: '',
        component: HomeComponent,
        data: { title: 'Home' },
      },
    ]),
  ],
  exports: [RouterModule],
})
export class HomeRoutingModule {}
