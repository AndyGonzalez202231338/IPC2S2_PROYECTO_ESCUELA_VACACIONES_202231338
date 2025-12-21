import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { Header } from '../../Header/header/header';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { User } from '../../../models/user/user';
import { LoginService } from '../../../services/Login/login.services';
import { Footer } from '../../footer/footer';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    Header, 
    Footer, 
    HeaderAdminSistema
  ],
  templateUrl: './home.html',
  styleUrls: ['./home.css']
})
export class Home implements OnInit {
  protected normalTitle = 'Home';
  currentUser: User | null = null;

  constructor(
    private loginService: LoginService, 
    private router: Router
  ) {}

  ngOnInit(): void {
    // Obtener el usuario actual del servicio
    this.currentUser = this.loginService.getCurrentUser();
    
    // Si no hay usuario, redirigir a login
    if (!this.currentUser) {
      this.router.navigate(['/login']);
    }
    
    console.log('Usuario actual en Home:', this.currentUser);
  }

  isAdminCine(): boolean {
    return this.currentUser?.rol?.nombre === 'ADMINISTRADOR DE CINE';
  }
}