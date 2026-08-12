import {Component, inject, signal, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormArray, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {UserService} from '../../../data/services/user.service';
import {BatchResult} from '../../../data/models/batch-result';
import {cpfFieldValidator} from '../../../shared/validator';
import {SelectionService} from '../../../data/services/selection.service';

@Component({
  selector: 'app-user-batch-update',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './user-batch-update.html',
  styleUrl: './user-batch-update.css'
})
export class UserBatchUpdate implements OnInit {
  private fb = inject(FormBuilder);
  private selectionService = inject(SelectionService);

  updateBatchForm: FormGroup = this.fb.group({
    users: this.fb.array([])
  });

  selectedFiles: (File | null)[] = [];
  submitted = signal(false);

  results = signal<BatchResult[] | null>(null);
  error = signal<string | null>(null);
  submitting = signal(false);

  constructor(private userService: UserService) {
  }

  ngOnInit(): void {
    const preSelected = this.selectionService.selectedUsers();

    if (preSelected.length > 0) {
      preSelected.forEach(user => {
        this.usersFormArray.push(this.createUserGroup(user.cpf, user.name));
        this.selectedFiles.push(null);
      });
      this.selectionService.clear();
    } else {
      this.usersFormArray.push(this.createUserGroup());
      this.selectedFiles.push(null);
    }
  }

  private createUserGroup(cpf: string = '', name: string = ''): FormGroup {
    const isPreFilled = cpf !== '';

    return this.fb.group({
      cpf: [{ value: cpf, disabled: isPreFilled }, [Validators.required, cpfFieldValidator()]],
      name: [name, [Validators.required]],
    });
  }

  get usersFormArray(): FormArray {
    return this.updateBatchForm.get('users') as FormArray;
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
      this.error.set('Preencha CPF e nome de todos os usuários corretamente.');
      return;
    }

    // Necessário quando o campo é desabilitado (seleção lista de usuários)
    const usersData = this.usersFormArray.getRawValue();

    const formData = new FormData();
    formData.append('users', JSON.stringify(usersData));
    this.selectedFiles.forEach(file => {
      formData.append('pictures', file ?? new Blob([]), file?.name ?? '');
    });

    this.submitting.set(true);
    this.error.set(null);
    this.results.set(null);

    this.userService.updateBatch(formData).subscribe({
      next: (data) => {
        this.results.set(data);
        this.submitting.set(false);
      },
      error: (err) => {
        this.error.set(err.error?.message || 'Erro ao atualizar usuários.');
        this.submitting.set(false);
      }
    });
  }
}
