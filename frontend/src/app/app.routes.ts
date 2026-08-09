import { Routes } from '@angular/router';
import { UserCreate } from './presentation/users/user-create/user-create';

export const routes: Routes = [
  { path: 'users', component: UserList },
  { path: '', redirectTo: 'users', pathMatch: 'full' }
];
