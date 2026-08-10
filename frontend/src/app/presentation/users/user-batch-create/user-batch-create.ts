import {Component, inject, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormArray, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {BatchResult} from '../../../data/models/batch-result';
import {NavigationService} from '../../../data/services/navigation.service';
import { MatButtonModule } from '@angular/material/button';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import {cpfFieldValidator} from '../../../shared/validator';

@Component({
  selector: 'app-user-batch-create',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],

  templateUrl: './user-batch-create.html',
  styleUrl: './user-batch-create.css'
})
export class UserBatchCreate {
  private fb = inject(FormBuilder);

  batchForm: FormGroup = this.fb.group({
    users: this.fb.array([this.createUserGroup()])
  });

  selectedFiles: (File | null)[] = [null];

  results = signal<BatchResult[] | null>(null);
  error = signal<string | null>(null);
  submitting = signal(false);
  submitted = signal(false);

  constructor(
    private userService: UserService,
    private navigationService: NavigationService,
    private router: Router
  ) {
  }

  private createUserGroup(): FormGroup {
    return this.fb.group({
      name: ['', [Validators.required]],
      cpf: ['', [Validators.required, cpfFieldValidator()]],
    });
  }

  get usersFormArray(): FormArray {
    return this.batchForm.get('users') as FormArray;
  }

  get userGroups(): FormGroup[] {
    return this.usersFormArray.controls as FormGroup[];
  }

  addUser(): void {
    this.usersFormArray.push(this.createUserGroup());
    this.selectedFiles.push(null);
  }

  removeUser(index: number): void {
    this.usersFormArray.removeAt(index);
    this.selectedFiles.splice(index, 1);
  }

  onFileSelected(index: number, event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFiles[index] = input.files[0];
    }
  }

  onSubmit(): void {
    this.submitted.set(true);

    if (this.usersFormArray.invalid) {
      this.error.set('Preencha nome e CPF de todos os usuários.');
      return;
    }

    if (this.selectedFiles.some(file => file === null)) {
      this.error.set('Anexe uma foto para cada usuário.');
      return;
    }

    const usersData = this.usersFormArray.value;

    const formData = new FormData();
    formData.append('usersData', JSON.stringify(usersData));
    this.selectedFiles.forEach(file => {
      formData.append('pictures', file as File);
    });
    this.submitting.set(true);
    this.error.set(null);
    this.results.set(null);

    this.userService.createBatch(formData).subscribe({
      next: (data) => {
        this.results.set(data);
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao cadastrar usuários.');
        this.submitting.set(false);
      }
    });
  }

  isPictureMissing(index: number): boolean {
    return this.submitted() && this.selectedFiles[index] === null
  }

  goHome(): void {
    this.navigationService.goHome();
  }
}
