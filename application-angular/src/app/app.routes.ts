import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { HelloComponent } from './hello/hello.component';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'hello', component: HelloComponent },
  { path: '', pathMatch: 'full', redirectTo: 'hello' }
];
