import {Component, inject, OnInit, signal} from '@angular/core';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, Router} from '@angular/router';
import {UserService} from '../../../data/services/user.service';
import {User} from '../../../data/models/user';
import {NavigationService} from '../../../data/services/navigation.service';
import {MatButtonModule} from '@angular/material/button';

@Component({
  selector: 'app-user-details',
  imports: [CommonModule, MatButtonModule],
  templateUrl: './user-details.html',
  styleUrl: './user-details.css'
})
export class UserDetails implements OnInit {
  private route = inject(ActivatedRoute);

  user = signal<User | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);
  pictureUrl = '';

  constructor(
    private userService: UserService,
    private navigationService: NavigationService,
    private router: Router
  ) {
  }

  ngOnInit(): void {
    const id = Number(this.route.snapshot.paramMap.get('id'));
    this.pictureUrl = this.userService.getPictureUrl(id);

    this.userService.findById(id).subscribe({
      next: (data) => {
        this.user.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('Usuário não encontrado.');
        this.loading.set(false);
      }
    });
  }

  editUser(): void {
    this.router.navigate(['/users/edit', this.user()!.id]);
  }

  goBack(): void {
    this.navigationService.goBack();
  }
}
