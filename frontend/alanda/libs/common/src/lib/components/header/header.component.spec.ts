import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';
import { AlandaHeaderComponent } from './header.component';
import { ToastModule } from 'primeng/toast';
import { APP_CONFIG } from '../../models/appSettings';
import { MenubarModule } from 'primeng/menubar';
import { TemplateModule } from '@rx-angular/template';
import { MessageService } from 'primeng/api';

const appConfig = {
  AVATAR_BASE_PATH: 'test/foo',
  AVATAR_EXT: 'png',
};

describe('AlandaHeaderComponent', () => {
  let component: AlandaHeaderComponent;
  let fixture: ComponentFixture<AlandaHeaderComponent>;

  beforeEach(
    waitForAsync(() => {
      TestBed.configureTestingModule({
        declarations: [AlandaHeaderComponent],
        imports: [ToastModule, MenubarModule, TemplateModule],
        providers: [
          {
            provide: APP_CONFIG,
            useValue: appConfig,
          },
          MessageService,
        ],
      })
        .compileComponents()
        .then(() => {
          fixture = TestBed.createComponent(AlandaHeaderComponent);
          component = fixture.componentInstance;

          fixture.detectChanges();
        });
    }),
  );

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
