import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AlandaPhaseTabComponent } from './phase-tab.component';
import { MenuModule } from 'primeng/menu';
import { TemplateModule } from '@rx-angular/template';
import { AlandaProjectApiService, APP_CONFIG } from '../../..';
import { HttpClient, HttpHandler } from '@angular/common/http';
import { TooltipModule } from 'primeng/tooltip';
import { TabViewModule } from 'primeng/tabview';

const appConfig = {
  API_ENDPOINT: '/test-api',
  DATE_FORMAT: 'dd.MM.yyyy',
};

describe('AlandaPhaseTabComponent', () => {
  let component: AlandaPhaseTabComponent;
  let fixture: ComponentFixture<AlandaPhaseTabComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AlandaPhaseTabComponent],
        imports: [MenuModule, TemplateModule, TabViewModule, TooltipModule],
        providers: [
          AlandaProjectApiService,
          HttpClient,
          HttpHandler,
          {
            provide: APP_CONFIG,
            useValue: appConfig,
          },
        ],
      }).compileComponents();
    }),
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaPhaseTabComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
