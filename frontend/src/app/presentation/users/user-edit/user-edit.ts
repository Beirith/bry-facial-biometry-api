import {Component, inject, signal, OnInit} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormBuilder, FormGroup, Validators, ReactiveFormsModule} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {NavigationService} from '../../../data/services/navigation.service';
import {MatButtonModule} from '@angular/material/button';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';

@Component({
  selector: 'app-user-edit',
  imports: [CommonModule, ReactiveFormsModule, MatButtonModule, MatFormFieldModule, MatInputModule],
  templateUrl: './user-edit.html',
  styleUrl: './user-edit.css'
})
export class UserEdit implements OnInit {
  private fb = inject(FormBuilder);
  private route = inject(ActivatedRoute);

  userId!: number;
  selectedFile: File | null = null;
  error = signal<string | null>(null);
  loading = signal(true);
  submitting = signal(false);

  updateForm: FormGroup = this.fb.group({
    name: ['', [Validators.required]],
  });

  constructor(
    private userService: UserService,
    private router: Router,
    private navigationService: NavigationService,
  ) {
  }

  ngOnInit(): void {
    this.userId = Number(this.route.snapshot.paramMap.get('id'));

    this.userService.findById(this.userId).subscribe({
      next: (user) => {
        this.updateForm.patchValue({name: user.name});
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Usuário não encontrado.');
        this.loading.set(false);
      }
    });
  }

  onPictureSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedFile = input.files[0];
    }
  }

  onSubmit(): void {
    if (this.updateForm.invalid) {
      this.error.set('Preencha o campo de nome.');
      return;
    }

    const formData = new FormData();
    formData.append('name', this.updateForm.value.name!);
    if (this.selectedFile) {
      formData.append('picture', this.selectedFile);
    }

    this.submitting.set(true);
    this.error.set(null);

    this.userService.update(this.userId, formData).subscribe({
      next: () => {
        this.submitting.set(false);
        this.router.navigate(['/']);
      },
      error: (err) => {
        this.submitting.set(false);
        this.error.set(err.error?.message || 'Erro ao atualizar usuário.');
      }
    });
  }


  goBack(): void {
    this.navigationService.goBack();
  }
}
