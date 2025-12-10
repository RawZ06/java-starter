import { Routes } from '@angular/router';
import { LoginComponent } from './pages/public/login/login.component';
import { Home } from './pages/public/home/home';
import { Layout } from './pages/admin/layout/layout';
import { Dashboard } from './pages/admin/dashboard/dashboard';
import { Users } from './pages/admin/users/users';
import { JobsComponent } from './pages/admin/jobs/jobs';
import { ProfileComponent } from './pages/profile/profile';
import { PrivacyComponent } from './pages/legal/privacy/privacy';
import { TermsComponent } from './pages/legal/terms/terms';
import { authGuard } from './guards/auth-guard';
import { adminGuard } from './guards/admin-guard';

export const routes: Routes = [
  { path: '', component: Home },
  { path: 'login', component: LoginComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'privacy', component: PrivacyComponent },
  { path: 'terms', component: TermsComponent },
  {
    path: 'dashboard',
    component: Layout,
    canActivate: [adminGuard],
    children: [
      { path: '', component: Dashboard }
    ]
  },
  {
    path: 'admin',
    component: Layout,
    canActivate: [adminGuard],
    children: [
      { path: 'users', component: Users },
      { path: 'jobs', component: JobsComponent }
    ]
  },
  { path: '**', redirectTo: '' }
];
