import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-terms',
  standalone: true,
  imports: [CommonModule, TranslateModule],
  templateUrl: './terms.html',
  styleUrl: './terms.css'
})
export class TermsComponent {
  constructor(private router: Router) {}

  goBack() {
    this.router.navigate(['/']);
  }
}
