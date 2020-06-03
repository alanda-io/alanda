import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PermissionsDemoComponent } from './permissions-demo.component';

describe('PermissionsDemoComponent', () => {
  let component: PermissionsDemoComponent;
  let fixture: ComponentFixture<PermissionsDemoComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PermissionsDemoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PermissionsDemoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
