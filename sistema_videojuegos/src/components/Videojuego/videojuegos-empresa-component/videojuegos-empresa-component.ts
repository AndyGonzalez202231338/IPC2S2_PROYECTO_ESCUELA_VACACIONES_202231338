import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { Videojuego } from '../../../models/videojuego/videojuego';
import { VideojuegoService } from '../../../services/Videojuego/videojuego.service';
import { LoginService } from '../../../services/Login/login.services';
import { Router, RouterModule } from '@angular/router';
import { HeaderAdminEmpresa } from '../../Header/header-admin-empresa/header-admin-empresa';
import { Footer } from '../../footer/footer';
import { CountsService } from '../../../services/Usuario/counts.service';

@Component({
  selector: 'app-videojuegos-empresa-component',
  imports: [CommonModule, RouterModule, Footer, HeaderAdminEmpresa],
  templateUrl: './videojuegos-empresa-component.html',
  styleUrl: './videojuegos-empresa-component.css',
})
export class VideojuegosEmpresaComponent implements OnInit {
  videojuegos: Videojuego[] = [];
  isLoading = false;
  errorMessage = '';
  empresaId: number | null = null;
  empresaNombre = '';
  estaBloqueando = false; 
  todosComentariosBloqueados = false; 

  constructor(
    private videojuegoService: VideojuegoService,
    private loginService: LoginService,
    private countsService: CountsService,
    private router: Router,
    private cdr: ChangeDetectorRef
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
      this.cdr.detectChanges();
      return;
    }

    this.empresaId = currentUser.empresa.id_empresa;
    this.empresaNombre = currentUser.empresa.nombre || 'Tu Empresa';

    this.videojuegoService.getVideojuegosByEmpresa(this.empresaId).subscribe({
      next: (videojuegos) => {
        console.log('Videojuegos cargados:', videojuegos);
        this.videojuegos = videojuegos;
        this.verificarEstadoComentarios();
        this.isLoading = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error al cargar videojuegos:', error);
        this.errorMessage = 'Error al cargar los videojuegos. Intenta nuevamente.';
        this.isLoading = false;
        this.cdr.detectChanges();
      }
    });
  }

  verificarEstadoComentarios(): void {
    if (this.videojuegos.length > 0) {
      // Verifica si TODOS los videojuegos tienen comentarios bloqueados
      this.todosComentariosBloqueados = this.videojuegos.every(
        videojuego => videojuego.comentarios_bloqueados === true
      );
    } else {
      this.todosComentariosBloqueados = false;
    }
  }

  bloquearTodosComentarios(): void {
    if (!this.empresaId) return;
    
    this.estaBloqueando = true;
    
    this.videojuegoService.bloquearComentariosTodosVideojuegosEmpresa(this.empresaId)
      .subscribe({
        next: (response) => {
          console.log('Comentarios bloqueados:', response);
          
          // Actualizar el estado local de todos los videojuegos
          this.videojuegos.forEach(videojuego => {
            videojuego.comentarios_bloqueados = true;
          });
          
          this.todosComentariosBloqueados = true;
          this.estaBloqueando = false;
          this.cdr.detectChanges();
          
        },
        error: (error) => {
          console.error('Error al bloquear comentarios:', error);
          this.estaBloqueando = false;
          this.cdr.detectChanges();
        }
      });
  }

  desbloquearTodosComentarios(): void {
    if (!this.empresaId) return;
    
    this.estaBloqueando = true;
    
    this.videojuegoService.desbloquearComentariosTodosVideojuegosEmpresa(this.empresaId)
      .subscribe({
        next: (response) => {
          console.log('Comentarios desbloqueados:', response);
          
          // Actualizar el estado local de todos los videojuegos
          this.videojuegos.forEach(videojuego => {
            videojuego.comentarios_bloqueados = false;
          });
          
          this.todosComentariosBloqueados = false;
          this.estaBloqueando = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error al desbloquear comentarios:', error);
          this.estaBloqueando = false;
          this.cdr.detectChanges();
        }
      });
  }
  

  navegarACrearVideojuego(): void {
    this.router.navigate(['/empresa/videojuegos/crear']);
  }

  editarVideojuego(id: number): void {
    this.router.navigate(['/empresa/videojuegos/editar', id]);
  }

  verDetalle(videojuego: Videojuego): void {
    this.router.navigate(['empresa/videojuegos', videojuego.id_videojuego]);
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
