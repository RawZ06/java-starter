import { Component, signal, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TranslateModule } from '@ngx-translate/core';

@Component({
  selector: 'app-dashboard',
  imports: [TranslateModule],
  templateUrl: './dashboard.html'
})
export class Dashboard implements OnInit {
  stats = signal<any>(null);
  loading = signal(true);

  constructor(private http: HttpClient) {}

  ngOnInit() {
    this.http.get('/api/admin/dashboard').subscribe({
      next: (data) => {
        this.stats.set(data);
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }
}
