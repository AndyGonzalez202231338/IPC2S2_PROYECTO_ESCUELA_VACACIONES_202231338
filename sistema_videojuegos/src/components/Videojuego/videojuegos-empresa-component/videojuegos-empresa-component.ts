import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Videojuego } from '../../../models/videojuego/videojuego';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { LoginService } from '../../../services/Login/login.services';
import { Router } from '@angular/router';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { Footer } from '../../footer/footer';
import { CountsService } from '../../../services/Usuario/counts.service';

@Component({
  selector: 'app-videojuegos-empresa-component',
  imports: [CommonModule, Footer, HeaderAdminEmpresa],
  templateUrl: './videojuegos-empresa-component.html',
  styleUrl: './videojuegos-empresa-component.css',
})
export class VideojuegosEmpresaComponent implements OnInit {
  videojuegos: Videojuego[] = [];
  isLoading = false;
  errorMessage = '';
  empresaId: number | null = null;
  empresaNombre = '';

  constructor(
    private videojuegoService: VideojuegoService,
    private loginService: LoginService,
    private countsService: CountsService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.cargarVideojuegos();
  }

  cargarVideojuegos(): void {
    this.isLoading = true;
    const currentUser = this.loginService.getCurrentUser();
    if (!currentUser || !currentUser.empresa?.id_empresa) {
      this.errorMessage = 'No se encontró la información de la empresa.';
      this.isLoading = false;
      return;
    }

    this.empresaId = currentUser.empresa.id_empresa;
    this.empresaNombre = currentUser.empresa.nombre || 'Tu Empresa';

    this.videojuegoService.getVideojuegosByEmpresa(this.empresaId).subscribe({
      next: (videojuegos) => {
        console.log('Videojuegos cargados:', videojuegos);
        this.videojuegos = videojuegos;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error al cargar videojuegos:', error);
        this.errorMessage = 'Error al cargar los videojuegos. Intenta nuevamente.';
        this.isLoading = false;
      }
    });
  }

  navegarACrearVideojuego(): void {
    this.router.navigate(['/empresa/videojuegos/crear']);
  }

  verDetalle(videojuego: Videojuego): void {
    this.router.navigate(['/empresa/videojuegos', videojuego.id_videojuego]);
  }

  formatearPrecio(precio: number): string {
    return `Q${precio.toFixed(2)}`;
  }

  formatearFecha(fechaString: string): string {
    if (!fechaString) return 'Sin fecha';
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-GT');
  }

  tieneCategorias(videojuego: Videojuego): boolean {
    return videojuego.categorias && videojuego.categorias.length > 0;
  }
}
