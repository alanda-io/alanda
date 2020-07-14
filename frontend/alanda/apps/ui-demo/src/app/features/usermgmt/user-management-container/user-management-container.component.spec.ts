import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { UserMnagementContainerComponent } from './user-management-container.component';

describe('UserMnagementContainerComponent', () => {
  let component: UserMnagementContainerComponent;
  let fixture: ComponentFixture<UserMnagementContainerComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ UserMnagementContainerComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(UserMnagementContainerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
