import { NgModule } from '@angular/core';
import { HomeComponent } from './home/home.component';
import { ProjectMonitorModule } from './project-monitor/project-monitor.module';

@NgModule({
  declarations: [HomeComponent],
  imports: [ProjectMonitorModule],
  providers: [],
})
export class ViewsModule {}
