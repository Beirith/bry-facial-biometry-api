import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common'
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {MatButtonModule} from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {cpfFieldValidator} from '../../../shared/validator';

@Component({
  selector: 'app-user-create',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './user-create.html',
  styleUrl: './user-create.css'
})
export class UserCreate {
  private fb = inject(FormBuilder);

  userForm: FormGroup = this.fb.group({
    name: ['', [Validators.required]],
    cpf: ['', [Validators.required, cpfFieldValidator()]],
  });

  selectedFile: File | null = null;
  error = signal<string | null>(null);
  submitting = signal(false);

  constructor(
    private userService: UserService,
    private router: Router) {
  }

  onPictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.userForm.invalid) {
      this.error.set('Preencha todos os campos.');
      return;
    }

    if (!this.selectedFile) {
      this.error.set('Anexe uma foto.');
      return;
    }

    const formData = new FormData();
    formData.append('name', this.userForm.value.name!);
    formData.append('cpf', this.userForm.value.cpf!);
    formData.append('picture', this.selectedFile);

    this.submitting.set(true);
    this.error.set(null);

    this.userService.create(formData).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(err.error?.message || 'Erro ao cadastrar usuário.');
      }
    });
  }
}
