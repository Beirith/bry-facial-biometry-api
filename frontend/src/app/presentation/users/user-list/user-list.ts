import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { UserService } from '../../../data/services/user.service';
import { User } from '../../../data/models/user';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css'
})
export class UserList implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(
    private userService: UserService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userService.findAll().subscribe({
      next: (data) => {
        this.users.set(data);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set('Erro ao carregar usuários.');
        this.loading.set(false);
      }
    });
  }

  editUser(id: number): void {
    console.log('Editar clicado, id:', id);
    this.router.navigate(['/users/edit', id]);

  viewUser(id: number): void {
    this.router.navigate(['/users', id]);
  }
}
