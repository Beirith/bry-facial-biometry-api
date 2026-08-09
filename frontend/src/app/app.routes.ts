import { Routes } from '@angular/router';
import { UserList } from './presentation/users/user-list/user-list';
import { UserCreate } from './presentation/users/user-create/user-create';
import { UserEdit } from './presentation/users/user-edit/user-edit';

export const routes: Routes = [
  { path: 'users', component: UserList },
  { path: 'users/create', component: UserCreate },
  { path: 'users/edit/:id', component: UserEdit },
  { path: 'users/:id', component: UserDetails },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
