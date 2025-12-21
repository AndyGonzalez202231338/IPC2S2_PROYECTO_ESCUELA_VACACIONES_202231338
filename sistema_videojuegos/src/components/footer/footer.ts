import { LoginService } from './../../services/Login/login.services';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  imports: [CommonModule],
  templateUrl: './footer.html',
  styleUrl: './footer.css'
})
export class Footer {
  constructor(
    private loginService: LoginService,
    private router: Router
  ) {}

  // Verificar si hay usuario logueado
  isLoggedIn(): boolean {
    return this.loginService.isAuthenticated();
  }

  // Cerrar sesión
  logout(): void {
    this.loginService.logout();
    this.router.navigate(['/login']);
  }

  // Obtener información del usuario para mostrar en el footer
  getCurrentUser() {
    return this.loginService.getCurrentUser();
  }
}
