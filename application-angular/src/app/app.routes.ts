import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { Home } from './home/home';
import { Layout } from './admin/layout/layout';
import { Dashboard } from './admin/dashboard/dashboard';
import { authGuard } from './guards/auth-guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: LoginComponent },
  {
    path: 'dashboard',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: '', component: Dashboard }
    ]
  },
  {
    path: 'admin',
    component: Layout,
    canActivate: [authGuard],
    children: [
      { path: 'users', component: Dashboard }, // TODO: create UsersComponent
      { path: 'settings', component: Dashboard } // TODO: create SettingsComponent
    ]
  },
  { path: '**', redirectTo: '' }
];
