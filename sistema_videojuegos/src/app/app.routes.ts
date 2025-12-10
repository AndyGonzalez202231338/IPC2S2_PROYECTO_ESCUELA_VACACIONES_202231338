import { Routes } from '@angular/router';
import { LoginForm } from '../components/login/login-form/login-form';

export const routes: Routes = [

    { 
        path: '', 
        redirectTo: 'login', 
        pathMatch: 'full' 
    },
    { 
        path: 'login', 
        component: LoginForm 
    },
];
