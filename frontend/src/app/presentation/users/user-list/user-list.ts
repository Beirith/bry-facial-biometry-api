import {Component, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {NavigationService} from '../../../data/services/navigation.service';
import {User} from '../../../data/models/user';
import {MatButtonModule} from '@angular/material/button';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {SelectionService} from '../../../data/services/selection.service';

@Component({
  selector: 'app-user-list',
  imports: [CommonModule, MatButtonModule, MatCheckboxModule],
  templateUrl: './user-list.html',
  styleUrl: './user-list.css'
})
export class UserList implements OnInit {
  users = signal<User[]>([]);
  loading = signal(true);
  error = signal<string | null>(null);
  selectedIds = signal<Set<number>>(new Set());

  constructor(
    private userService: UserService,
    private navigationService: NavigationService,
    private selectionService: SelectionService,
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

  toggleSelection(id: number): void {
    const current = new Set(this.selectedIds());
    if (current.has(id)) {
      current.delete(id);
    } else {
      current.add(id);
    }
    this.selectedIds.set(current);
  }

  isSelected(id: number): boolean {
    return this.selectedIds().has(id);
  }

  get hasSelection(): boolean {
    return this.selectedIds().size > 0;
  }

  deleteSelected(): void {
    const confirmed = confirm(`Tem certeza que deseja excluir ${this.selectedIds().size} usuário(s)?`);
    if (!confirmed) {
      return;
    }

    const idsToDelete = Array.from(this.selectedIds());
    let completed = 0;

    idsToDelete.forEach(id => {
      this.userService.delete(id).subscribe({
        next: () => {
          completed++;
          this.users.set(this.users().filter(u => u.id !== id));
          if (completed === idsToDelete.length) {
            this.selectedIds.set(new Set());
          }
        },
        error: () => {
          this.error.set('Erro ao excluir um ou mais usuários.');
        }
      });
    });
  }

  updateSelected(): void {
    const selectedUsers = this.users().filter(u => this.selectedIds().has(u.id));
    this.selectionService.setSelection(selectedUsers);
    this.router.navigate(['/users/batch-update']);
  }

  viewUser(id: number): void {
    this.router.navigate(['/users', id]);
  }

  editUser(id: number): void {
    this.router.navigate(['/users/edit', id]);
  }

  goHome(): void {
    this.navigationService.goHome();
  }
}
