import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlandaSnoozeComponent } from './snooze.component';

describe('AlandaSnoozeComponent', () => {
  let component: AlandaSnoozeComponent;
  let fixture: ComponentFixture<AlandaSnoozeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlandaSnoozeComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaSnoozeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
