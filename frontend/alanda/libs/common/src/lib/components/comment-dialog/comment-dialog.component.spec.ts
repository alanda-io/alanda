import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AlandaCommentDialogComponent } from './comment-dialog.component';

describe('CommentDialogComponent', () => {
  let component: AlandaCommentDialogComponent;
  let fixture: ComponentFixture<AlandaCommentDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AlandaCommentDialogComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AlandaCommentDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
