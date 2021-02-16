import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PageHeaderBarComponent } from './page-header-bar.component';

describe('PageHeaderBarComponent', () => {
  let component: PageHeaderBarComponent;
  let fixture: ComponentFixture<PageHeaderBarComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PageHeaderBarComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PageHeaderBarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
