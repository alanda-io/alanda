import { BrowserModule } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { NgModule, Inject } from '@angular/core';
import { AppComponent } from './app.component';

/**
 * Add here your alanda-common components to test them independently from
 * the client project
 */

import { MessageService } from 'primeng/api';
import { ALANDA_CONFIG } from './app.settings';
import { AppRoutingModule } from './app-routing.module';
import { AppSettings, APP_CONFIG, ProjectDetailsServiceNg, AlandaCommonModule,
         AlandaProjectPropertiesService} from 'projects/alanda-common/src/public_api';
import { VacationModule } from './features/vacation/vacation.module';
import { VacationProjectPropertiesService } from './features/vacation/services/vacation-projectproperties.service';
import { VacationProjectDetailsService } from './features/vacation/services/vacation-projectdetails.service';
import { LayoutModule } from './layout/layout.module';
import { CoreModule } from './core/core.module';
import { ViewsModule } from './views/views.module';


const CURRENT_CONFIG: AppSettings = ALANDA_CONFIG;
@NgModule({
  declarations: [
    AppComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    AppRoutingModule,
    AlandaCommonModule.forRoot(CURRENT_CONFIG),
    LayoutModule,
    CoreModule,
    ViewsModule
  ],
  providers: [
    {provide: APP_CONFIG, useValue: CURRENT_CONFIG},
    {provide: AlandaProjectPropertiesService, useClass: VacationProjectPropertiesService },
    {provide: ProjectDetailsServiceNg, useClass: VacationProjectDetailsService},
    MessageService,
  ],
  bootstrap: [AppComponent]
})
export class AppModule {

  constructor(@Inject(APP_CONFIG) config: AppSettings) {
    console.log('Settings', config);
  }

  ngDoBootstrap() {

  }
}
