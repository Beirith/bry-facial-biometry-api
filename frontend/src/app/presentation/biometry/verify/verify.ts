import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {FacialTemplateService} from '../../../data/services/facial-template.service';
import {VerificationResult} from '../../../data/models/verification-result';
import {NavigationService} from '../../../data/services/navigation.service';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {cpfFieldValidator} from '../../../shared/validator';
import {ActivatedRoute} from '@angular/router';
import {MatSliderModule} from '@angular/material/slider';

@Component({
  selector: 'app-verify',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule, MatSliderModule],
  templateUrl: './verify.html',
  styleUrl: './verify.css'
})
export class Verify {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);

  verifyForm: FormGroup = this.fb.group({
    cpf: ['', [Validators.required, cpfFieldValidator()]],
    threshold: [0.85],
  });

  selectedFile: File | null = null;
  result = signal<VerificationResult | null>(null);
  error = signal<string | null>(null);
  submitting = signal(false);

  constructor(
    private facialTemplateService: FacialTemplateService,
    private navigationService: NavigationService) {
  }

  ngOnInit(): void {
    const cpfFromQuery = this.route.snapshot.queryParamMap.get('cpf');
    if (cpfFromQuery) {
      this.verifyForm.patchValue({cpf: cpfFromQuery});
    }
  }

  onPictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.verifyForm.invalid || !this.selectedFile) {
      this.error.set('Informe o CPF e selecione uma foto.');
      return;
    }

    const formData = new FormData();
    formData.append('cpf', this.verifyForm.value.cpf!);
    formData.append('picture', this.selectedFile);

    const threshold = this.verifyForm.value.threshold;
    if (threshold) {
      formData.append('threshold', threshold);
    }

    this.submitting.set(true);
    this.error.set(null);
    this.result.set(null);

    this.facialTemplateService.verify(formData).subscribe({
      next: (data) => {
        this.result.set(data);
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao verificar identidade.');
        this.submitting.set(false);
      }
    });
  }

  goBack(): void {
    this.navigationService.goBack();
  }
}
