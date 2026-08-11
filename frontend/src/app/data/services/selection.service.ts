import {Injectable, signal} from '@angular/core';
import {User} from '../models/user';

@Injectable({providedIn: 'root'})
export class SelectionService {
  selectedUsers = signal<User[]>([]);

  setSelection(users: User[]): void {
    this.selectedUsers.set(users);
  }

  clear(): void {
    this.selectedUsers.set([]);
  }
}
