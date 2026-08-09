import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {FacialTemplateService} from '../../../data/services/facial-template.service';
import {VerificationResult} from '../../../data/models/verification-result';
import {NavigationService} from '../../../data/services/navigation.service';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

@Component({
  selector: 'app-verify',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './verify.html',
  styleUrl: './verify.css'
})
export class Verify {
  private fb = inject(FormBuilder);

  verifyForm: FormGroup = this.fb.group({
    cpf: ['', [Validators.required]],
  });

  selectedFile: File | null = null;
  result = signal<VerificationResult | null>(null);
  error = signal<string | null>(null);
  submitting = signal(false);

  constructor
  (private facialTemplateService: FacialTemplateService,
   private navigationService: NavigationService
  ) {
  }

  onPictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.verifyForm.invalid) {
      this.error.set('Preencha todos os campos.');
      return;
    }

    if (!this.selectedFile) {
      this.error.set('Anexe uma foto.');
      return;
    }

    const formData = new FormData();
    formData.append('cpf', this.verifyForm.value.cpf!);
    formData.append('picture', this.selectedFile);

    this.submitting.set(true);
    this.error.set(null);
    this.result.set(null);

    this.facialTemplateService.verify(formData).subscribe({
      next: (data) => {
        this.result.set(data);
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao realizar identificação.');
        this.submitting.set(false);
      }
    });
  }

  goHome(): void {
    this.navigationService.goHome();
  }
}
