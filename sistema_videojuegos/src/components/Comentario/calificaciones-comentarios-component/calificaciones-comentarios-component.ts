import { Component, Input, OnInit, OnDestroy, ChangeDetectorRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { Subject, takeUntil } from 'rxjs';
import { CalificacionService, NotaFinalResponse } from '../../../services/comentario/calificacion.service';
import { ComentarioService } from '../../../services/comentario/comentario.service';
import { LoginService } from '../../../services/Login/login.services';
import { DateUtilsService } from '../../../services/comentario/date-utils.service';



@Component({
  selector: 'app-calificaciones-comentarios',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './calificaciones-comentarios-component.html',
  styleUrls: ['./calificaciones-comentarios-component.css']
})
export class CalificacionesComentariosComponent implements OnInit, OnDestroy {
  @Input() videojuegoId!: number;
  @Input() mostrarTitulo: boolean = true;

  // Datos del backend
  notaFinalData: NotaFinalResponse | null = null;
  
  // Datos para comentarios
  datosCombinados: any[] = [];
  datosFiltrados: any[] = [];
  comentarios: any[] = [];
  calificaciones: any[] = [];
  
  // Estados
  isLoading = false;
  errorMessage = '';
  mostrarCompleto = false;
  mostrarSoloConComentarios = false;
  ordenarPor: 'fecha' | 'calificacion' = 'fecha';
  ordenDescendente = true;

  private destroy$ = new Subject<void>();

  constructor(
    private calificacionService: CalificacionService,
    private comentarioService: ComentarioService,
    private userService: LoginService,
    private dateUtils: DateUtilsService,
    private cdr: ChangeDetectorRef
  ) {}

  ngOnInit(): void {
    if (this.videojuegoId) {
      this.cargarDatos();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  cargarDatos(): void {
    this.isLoading = true;
    
    // Cargar nota final
    this.calificacionService.obtenerNotaFinalVideojuego(this.videojuegoId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (notaFinal) => {
          this.notaFinalData = notaFinal;
          // Si hay nota final, cargar comentarios
          this.cargarComentariosYCalificaciones();
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error cargando nota final:', error);
          this.errorMessage = 'No se pudo cargar la calificación';
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  private cargarComentariosYCalificaciones(): void {
    // Cargar comentarios
    this.comentarioService.obtenerComentariosVideojuego(this.videojuegoId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (comentarios) => {
          this.comentarios = comentarios || [];
          this.cargarCalificaciones();
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error cargando comentarios:', error);
          this.comentarios = [];
          this.cargarCalificaciones();
          this.cdr.detectChanges();
        }
      });
  }

  private cargarCalificaciones(): void {
    // Cargar calificaciones
    this.calificacionService.obtenerCalificacionesVideojuego(this.videojuegoId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (calificaciones) => {
          this.calificaciones = calificaciones || [];
          this.procesarDatos();
          this.cdr.detectChanges();
        },
        error: (error) => {
          console.error('Error cargando calificaciones:', error);
          this.calificaciones = [];
          this.procesarDatos();
          this.cdr.detectChanges();
        }
      });
  }

  private procesarDatos(): void {
    // Combinar comentarios y calificaciones por usuario
    const usuariosMap = new Map<number, any>();
    
    // Agregar calificaciones
    this.calificaciones.forEach(cal => {
      usuariosMap.set(cal.id_usuario, {
        id_usuario: cal.id_usuario,
        calificacion: {
          calificacion: cal.calificacion,
          fecha_hora: cal.fecha_hora
        }
      });
    });
    
    // Agregar comentarios
    this.comentarios.forEach(com => {
      if (usuariosMap.has(com.id_usuario)) {
        const usuario = usuariosMap.get(com.id_usuario);
        usuario.comentario = {
          comentario: com.comentario,
          fecha_hora: com.fecha_hora
        };
      } else {
        usuariosMap.set(com.id_usuario, {
          id_usuario: com.id_usuario,
          comentario: {
            comentario: com.comentario,
            fecha_hora: com.fecha_hora
          }
        });
      }
    });
    
    this.datosCombinados = Array.from(usuariosMap.values());
    this.ordenarDatos();
    this.aplicarFiltros();
    this.isLoading = false;
  }

  // Métodos de utilidad simples
  formatearFecha(fechaInput: any): string {
    return this.dateUtils.formatearFecha(fechaInput, true);
  }

  ordenarDatos(): void {
    this.datosCombinados.sort((a, b) => {
      const fechaA = this.getFecha(a);
      const fechaB = this.getFecha(b);
      return this.ordenDescendente ? fechaB - fechaA : fechaA - fechaB;
    });
  }

  private getFecha(item: any): number {
    if (item.comentario?.fecha_hora) {
      return new Date(this.dateUtils.fechaToISO(item.comentario.fecha_hora)).getTime();
    }
    if (item.calificacion?.fecha_hora) {
      return new Date(this.dateUtils.fechaToISO(item.calificacion.fecha_hora)).getTime();
    }
    return 0;
  }

  aplicarFiltros(): void {
    let datos = [...this.datosCombinados];
    
    if (this.mostrarSoloConComentarios) {
      datos = datos.filter(item => item.comentario);
    }
    
    if (!this.mostrarCompleto) {
      datos = datos.slice(0, 5);
    }
    
    this.datosFiltrados = datos;
  }

  toggleMostrarCompleto(): void {
    this.mostrarCompleto = !this.mostrarCompleto;
    this.aplicarFiltros();
  }

  toggleFiltroComentarios(): void {
    this.mostrarSoloConComentarios = !this.mostrarSoloConComentarios;
    this.aplicarFiltros();
  }

  cambiarOrden(criterio: 'fecha' | 'calificacion'): void {
    if (this.ordenarPor === criterio) {
      this.ordenDescendente = !this.ordenDescendente;
    } else {
      this.ordenarPor = criterio;
      this.ordenDescendente = true;
    }
    this.ordenarDatos();
    this.aplicarFiltros();
  }

  recargar(): void {
    this.cargarDatos();
  }

  get mostrarBotonVerTodos(): boolean {
    return this.datosCombinados.length > 5;
  }

  get mostrarFiltros(): boolean {
    return this.datosCombinados.length > 0;
  }

  get tituloComponente(): string {
    return 'Calificaciones y Comentarios';
  }
}