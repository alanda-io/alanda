import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlandaProjectMonitorComponent } from './project-monitor.component';

describe('AlandaProjectMonitorComponent', () => {
  let component: AlandaProjectMonitorComponent;
  let fixture: ComponentFixture<AlandaProjectMonitorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlandaProjectMonitorComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaProjectMonitorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
