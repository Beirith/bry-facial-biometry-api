import {Component, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FacialTemplateService} from '../../../data/services/facial-template.service';
import {IdentificationResult} from '../../../data/models/identification-result';
import {NavigationService} from '../../../data/services/navigation.service';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-identify',
  imports: [CommonModule, MatButtonModule],
  templateUrl: './identify.html',
  styleUrl: './identify.css'
})
export class Identify {
  selectedFile: File | null = null;
  result = signal<IdentificationResult | null>(null);
  error = signal<string | null>(null);
  submitting = signal(false);

  constructor(
    private facialTemplateService: FacialTemplateService,
    private navigationService: NavigationService) {
  }

  onPictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (!this.selectedFile) {
      this.error.set('Selecione uma foto.');
      return;
    }

    const formData = new FormData();
    formData.append('picture', this.selectedFile);

    this.submitting.set(true);
    this.error.set(null);
    this.result.set(null);

    this.facialTemplateService.identify(formData).subscribe({
      next: (data) => {
        this.result.set(data);
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao identificar usuário.');
        this.submitting.set(false);
      }
    });
  }

  goHome(): void {
    this.navigationService.goHome();
  }
}
