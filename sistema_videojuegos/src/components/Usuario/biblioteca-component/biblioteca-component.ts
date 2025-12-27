import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterModule } from '@angular/router';

import { GrupoService, GrupoResponse } from '../../../services/Grupo/grupo.service';
import { LoginService } from '../../../services/Login/login.services';
import { Header } from '../../Header/header/header';
import { Footer } from '../../footer/footer';
import { BibliotecaResponse, BibliotecaService } from '../../../services/Usuario/biblioteca.service';
import { GrupoPrestable, InstalacionResponse, InstalacionService, JuegoPrestable, JuegosPrestablesAgrupados } from '../../../services/Usuario/instalacion.service';

@Component({
  selector: 'app-biblioteca',
  imports: [CommonModule, FormsModule, RouterModule, Header, Footer],
  templateUrl: './biblioteca-component.html',
  styleUrl: './biblioteca-component.css'
})
export class BibliotecaComponent implements OnInit {
  currentUser: any = null;
  
  // Datos de la biblioteca (SOLO juegos comprados)
  bibliotecaCompleta: BibliotecaResponse[] = [];
  
  // Todas las instalaciones del usuario
  instalacionesUsuario: InstalacionResponse[] = [];
  
  // Juegos disponibles para préstamo (de otros usuarios del grupo)
  juegosPrestables: JuegoPrestable[] = [];
  
  // Estados
  isLoadingBiblioteca = false;
  isLoadingInstalaciones = false;
  isLoadingPrestables = false;
  isInstalando = false;
  isDesinstalando = false;
  errorMessage = '';
  successMessage = '';
  
  // Filtros
  terminoBusqueda: string = '';
  ordenarPor: 'fecha' | 'titulo' | 'precio' = 'fecha';
  ordenAscendente: boolean = false;
  
  // Pestaña activa
  tabActive: 'biblioteca' | 'prestamos' = 'biblioteca';

  constructor(
    private bibliotecaService: BibliotecaService,
    private instalacionService: InstalacionService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    
    if (!this.currentUser) {
      return;
    }
    
    this.cargarBiblioteca();
    this.cargarInstalacionesUsuario();
  }

  cargarBiblioteca(): void {
    this.isLoadingBiblioteca = true;
    this.errorMessage = '';
    
    const userId = this.currentUser.id_usuario || this.currentUser.idUsuario;
    
    // Obtener solo juegos comprados
    this.bibliotecaService.obtenerJuegosComprados(userId).subscribe({
      next: (biblioteca) => {
        this.bibliotecaCompleta = biblioteca;
        this.ordenarJuegos(this.bibliotecaCompleta);
        this.isLoadingBiblioteca = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al cargar biblioteca';
        this.isLoadingBiblioteca = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarInstalacionesUsuario(): void {
    this.isLoadingInstalaciones = true;
    
    const userId = this.currentUser.id_usuario || this.currentUser.idUsuario;
    
    this.instalacionService.obtenerInstalacionesPorUsuario(userId).subscribe({
      next: (instalaciones) => {
        this.instalacionesUsuario = instalaciones || [];
        this.isLoadingInstalaciones = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar instalaciones:', error);
        this.instalacionesUsuario = [];
        this.isLoadingInstalaciones = false;
        this.cdr.detectChanges();
      }
    });
  }

  cargarJuegosPrestables(): void {
    if (!this.currentUser) return;
    
    this.isLoadingPrestables = true;
    this.errorMessage = '';
    
    const userId = this.currentUser.id_usuario || this.currentUser.idUsuario;
    
    this.instalacionService.obtenerJuegosPrestables(userId).subscribe({
      next: (juegos) => {
        this.juegosPrestables = juegos;
        this.isLoadingPrestables = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = `Error al cargar juegos prestables: ${error.message}`;
        this.juegosPrestables = [];
        this.isLoadingPrestables = false;
        this.cdr.detectChanges();
      }
    });
  }

  ordenarJuegos(juegos: BibliotecaResponse[]): void {
    juegos.sort((a, b) => {
      let valorA, valorB;
      
      switch (this.ordenarPor) {
        case 'titulo':
          valorA = a.videojuego.titulo.toLowerCase();
          valorB = b.videojuego.titulo.toLowerCase();
          break;
        case 'precio':
          valorA = a.videojuego.precio;
          valorB = b.videojuego.precio;
          break;
        case 'fecha':
        default:
          valorA = new Date(a.compra.fecha_compra).getTime();
          valorB = new Date(b.compra.fecha_compra).getTime();
          break;
      }
      
      if (this.ordenAscendente) {
        return valorA > valorB ? 1 : -1;
      } else {
        return valorA < valorB ? 1 : -1;
      }
    });
  }

  buscarJuegos(): void {
    if (this.terminoBusqueda.trim()) {
      this.isLoadingBiblioteca = true;
      
      const userId = this.currentUser.id_usuario || this.currentUser.idUsuario;
      const termino = this.terminoBusqueda.toLowerCase();
      
      // Filtrar localmente en lugar de llamar al backend
      this.bibliotecaCompleta = this.bibliotecaCompleta.filter(juego =>
        juego.videojuego.titulo.toLowerCase().includes(termino)
      );
      
      this.isLoadingBiblioteca = false;
      this.cdr.detectChanges();
    } else {
      // Recargar desde el backend si no hay término de búsqueda
      this.cargarBiblioteca();
    }
  }

  cambiarOrden(criterio: 'fecha' | 'titulo' | 'precio'): void {
    if (this.ordenarPor === criterio) {
      this.ordenAscendente = !this.ordenAscendente;
      this.cdr.detectChanges();
    } else {
      this.ordenarPor = criterio;
      this.ordenAscendente = false;
      this.cdr.detectChanges();
    }
    this.ordenarJuegos(this.bibliotecaCompleta);
  }

  cambiarTab(tab: 'biblioteca' | 'prestamos'): void {
    this.tabActive = tab;
    if (tab === 'prestamos') {
      this.cargarJuegosPrestables();
    }
  }

  formatearPrecio(precio: number): string {
    return `Q${precio.toFixed(2)}`;
  }

  formatearFecha(fechaString: string): string {
    if (!fechaString) return 'Sin fecha';
    const fecha = new Date(fechaString);
    return fecha.toLocaleDateString('es-GT', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
  }

  // Verifica si un juego está instalado
  estaInstalado(juego: BibliotecaResponse): boolean {
    return this.instalacionesUsuario.some(
      instalacion => instalacion.id_videojuego === juego.videojuego.id_videojuego
    );
  }

  // Obtiene la instalación de un juego específico
  getInstalacionDeJuego(juego: BibliotecaResponse): InstalacionResponse | undefined {
    return this.instalacionesUsuario.find(
      instalacion => instalacion.id_videojuego === juego.videojuego.id_videojuego
    );
  }

  instalarJuegoComprado(juego: BibliotecaResponse): void {
    if (!this.currentUser || this.isInstalando) return;
    
    const userId = this.currentUser.id_usuario || this.currentUser.idUsuario;
    const videojuegoId = juego.videojuego.id_videojuego;
    
    this.isInstalando = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.instalacionService.instalarJuegoComprado(userId, videojuegoId, userId).subscribe({
      next: (instalacion) => {
        this.successMessage = `¡${juego.videojuego.titulo} instalado exitosamente!`;
        this.instalacionesUsuario.push(instalacion);
        this.isInstalando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al instalar juego';
        this.isInstalando = false;
        this.cdr.detectChanges();
      }
    });
  }

  instalarJuegoPrestado(juegoPrestable: JuegoPrestable): void {
    if (!this.currentUser || this.isInstalando) return;
    
    const userId = juegoPrestable.id_usuario_propietario;
    const videojuegoId = juegoPrestable.id_videojuego;
    
    this.isInstalando = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    this.instalacionService.instalarJuegoPrestado(userId, videojuegoId, this.currentUser.id_usuario || this.currentUser.idUsuario).subscribe({
      next: (instalacion) => {
        this.successMessage = `¡${juegoPrestable.titulo} instalado exitosamente!`;
        this.instalacionesUsuario.push(instalacion);
        this.isInstalando = false;
        this.cdr.detectChanges();
        // Recargar juegos prestables para actualizar estado
        this.cargarJuegosPrestables();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al instalar juego prestado';
        this.isInstalando = false;
        this.cdr.detectChanges();
      }
    });
  }

  desinstalarJuego(juego: BibliotecaResponse): void {
    if (!this.currentUser || this.isDesinstalando) return;
    
    const instalacion = this.getInstalacionDeJuego(juego);
    if (!instalacion) return;
    
    this.isDesinstalando = true;
    
    this.instalacionService.desinstalarJuego(instalacion.id_instalacion).subscribe({
      next: () => {
        this.successMessage = `${juego.videojuego.titulo} desinstalado exitosamente`;
        this.instalacionesUsuario = this.instalacionesUsuario.filter(
          inst => inst.id_instalacion !== instalacion.id_instalacion
        );
        this.isDesinstalando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al desinstalar juego';
        this.isDesinstalando = false;
        this.cdr.detectChanges();
      }
    });
  }

  desinstalarJuegoPorId(idInstalacion: number): void {
    if (!this.currentUser || this.isDesinstalando) return;
    
    this.isDesinstalando = true;
    
    this.instalacionService.desinstalarJuego(idInstalacion).subscribe({
      next: () => {
        this.successMessage = 'Juego desinstalado exitosamente';
        this.instalacionesUsuario = this.instalacionesUsuario.filter(
          inst => inst.id_instalacion !== idInstalacion
        );
        this.isDesinstalando = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al desinstalar juego';
        this.isDesinstalando = false;
        this.cdr.detectChanges();
      }
    });
  }

  // Verifica si un juego prestable ya está instalado
  juegoPrestableEstaInstalado(juegoPrestable: JuegoPrestable): boolean {
    return this.instalacionesUsuario.some(
      instalacion => instalacion.id_videojuego === juegoPrestable.id_videojuego
    );
  }

  get totalInstalaciones(): number {
    return this.instalacionesUsuario.length;
  }

  get instalacionesActivas(): InstalacionResponse[] {
    return this.instalacionesUsuario.filter(inst => inst.estado === 'INSTALADO');
  }

  get juegosFiltrados(): BibliotecaResponse[] {
    if (this.terminoBusqueda.trim()) {
      const termino = this.terminoBusqueda.toLowerCase();
      return this.bibliotecaCompleta.filter(juego =>
        juego.videojuego.titulo.toLowerCase().includes(termino)
      );
    }
    return this.bibliotecaCompleta;
  }

  get juegosPrestablesFiltrados(): JuegoPrestable[] {
    if (this.terminoBusqueda.trim() && this.tabActive === 'prestamos') {
      const termino = this.terminoBusqueda.toLowerCase();
      return this.juegosPrestables.filter(juego =>
        juego.titulo.toLowerCase().includes(termino)
      );
    }
    return this.juegosPrestables;
  }
}