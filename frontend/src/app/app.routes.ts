import {Routes} from '@angular/router';
import {UserList} from './presentation/users/user-list/user-list';
import {UserCreate} from './presentation/users/user-create/user-create';
import {UserEdit} from './presentation/users/user-edit/user-edit';
import {UserDetails} from './presentation/users/user-details/user-details';
import {Verify} from './presentation/biometry/verify/verify';
import {Identify} from './presentation/biometry/identify/identify';
import {UserBatchCreate} from './presentation/users/user-batch-create/user-batch-create';
import {UserBatchUpdate} from './presentation/users/user-batch-update/user-batch-update';

export const routes: Routes = [
  {path: '', component: UserList},
  {path: 'users/create', component: UserCreate},
  {path: 'users/batch-create', component: UserBatchCreate},
  {path: 'users/edit/:id', component: UserEdit},
  {path: 'users/batch-update', component: UserBatchUpdate},
  {path: 'users/:id', component: UserDetails},
  {path: 'verify', component: Verify},
  {path: 'identify', component: Identify},
];
