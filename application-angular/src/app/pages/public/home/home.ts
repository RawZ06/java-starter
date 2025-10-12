import { Component } from '@angular/core';
import { TranslateModule } from '@ngx-translate/core';
import { Header } from '../../../components/header/header';

@Component({
  selector: 'app-home',
  imports: [Header, TranslateModule],
  templateUrl: './home.html'
})
export class Home {
  currentYear = new Date().getFullYear();
}
