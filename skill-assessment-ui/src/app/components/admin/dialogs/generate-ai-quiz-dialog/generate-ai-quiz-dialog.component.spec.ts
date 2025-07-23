import { ComponentFixture, TestBed } from '@angular/core/testing';

import { GenerateAiQuizDialogComponent } from './generate-ai-quiz-dialog.component';

describe('GenerateAiQuizDialogComponent', () => {
  let component: GenerateAiQuizDialogComponent;
  let fixture: ComponentFixture<GenerateAiQuizDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GenerateAiQuizDialogComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(GenerateAiQuizDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
