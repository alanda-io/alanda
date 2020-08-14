import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AlandaTaskListComponent } from './task-list.component';

describe('TaskListComponent', () => {
  let component: AlandaTaskListComponent;
  let fixture: ComponentFixture<AlandaTaskListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlandaTaskListComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaTaskListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
