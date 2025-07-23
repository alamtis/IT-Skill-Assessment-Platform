import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ViewResultDialogComponent } from './view-result-dialog.component';

describe('ViewResultDialogComponent', () => {
  let component: ViewResultDialogComponent;
  let fixture: ComponentFixture<ViewResultDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ViewResultDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ViewResultDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
