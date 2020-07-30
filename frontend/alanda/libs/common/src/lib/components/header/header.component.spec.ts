import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AlandaHeaderComponent } from './header.component';

describe('AlandaHeaderComponent', () => {
  let component: AlandaHeaderComponent;
  let fixture: ComponentFixture<AlandaHeaderComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AlandaHeaderComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaHeaderComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
