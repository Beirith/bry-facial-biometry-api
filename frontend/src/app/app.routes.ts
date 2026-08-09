import { Routes } from '@angular/router';
import { UserList } from './presentation/users/user-list/user-list';
import { UserCreate } from './presentation/users/user-create/user-create';

export const routes: Routes = [
  { path: 'users', component: UserList },
  { path: 'users/create', component: UserCreate },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
