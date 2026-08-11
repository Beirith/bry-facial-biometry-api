import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {NavigationService} from '../../../data/services/navigation.service';
import {User} from '../../../data/models/user';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, MatButtonModule],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css'
})
export class UserList implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);

  constructor(
    private userService: UserService,
    private navigationService: NavigationService,
    private router: Router
  ) {
  }

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

  deleteUser(id: number): void {
    const confirmed = confirm('Tem certeza que deseja excluir este usuário?');
    if (!confirmed) {
      return;
    }

    this.userService.delete(id).subscribe({
      next: () => {
        this.users.set(this.users().filter(u => u.id !== id));
      },
      error: () => {
        this.error.set('Erro ao excluir usuário.');
      }
    });
  }

  editUser(id: number): void {
    this.router.navigate(['/users/edit', id]);
  }

  viewUser(id: number): void {
    this.router.navigate(['/users', id]);
  }

  goBack(): void {
    this.navigationService.goBack();
  }
}
