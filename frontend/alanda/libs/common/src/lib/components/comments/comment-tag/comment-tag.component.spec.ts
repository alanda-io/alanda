import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AlandaCommentTagComponent } from './comment-tag.component';
import { ButtonModule } from 'primeng/button';
import { TemplateModule } from '@rx-angular/template';

describe('AlandaCommentTagComponent', () => {
  let component: AlandaCommentTagComponent;
  let fixture: ComponentFixture<AlandaCommentTagComponent>;

  const testTag = {
    name: 'test',
    type: 'test',
    status: true,
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlandaCommentTagComponent],
      imports: [ButtonModule, TemplateModule],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaCommentTagComponent);
    component = fixture.componentInstance;
    component.tag = testTag;
    component.isActive = true;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
