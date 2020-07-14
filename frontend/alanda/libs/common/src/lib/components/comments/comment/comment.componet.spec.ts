import { async, ComponentFixture, TestBed } from '@angular/core/testing';
import { AlandaCommentComponent } from './comment.component';
import { AlandaCommentTagComponent } from '../comment-tag/comment-tag.component';
import { ButtonModule } from 'primeng/button';
import { TemplateModule } from '@rx-angular/template';
import { AlandaComment, APP_CONFIG } from '@alanda/common';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

const appConfig = {
  AVATAR_BASE_PATH: 'test/foo',
  AVATAR_EXT: 'png',
};

describe('AlandaCommentComponent', () => {
  let component: AlandaCommentComponent;
  let fixture: ComponentFixture<AlandaCommentComponent>;

  const testComment: AlandaComment = {
    guid: 1,
    replies: [],
    text: 'test comment',
    fulltext: 'Test comment',
  };

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [AlandaCommentComponent, AlandaCommentTagComponent],
      imports: [ButtonModule, TemplateModule, ReactiveFormsModule, FormsModule],
      providers: [
        {
          provide: APP_CONFIG,
          useValue: appConfig,
        },
      ],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaCommentComponent);
    component = fixture.componentInstance;
    component.comment = testComment;
    component.type = 'comment';
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
