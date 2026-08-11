import {Injectable, inject} from '@angular/core';
import {Router} from '@angular/router';
import {Location} from '@angular/common';

@Injectable({providedIn: 'root'})
export class NavigationService {
  private router = inject(Router);
  private location = inject(Location);

  goBack(): void {
    this.location.back();
  }

  goHome(): void {
    this.router.navigate(['/']);
  }
}
