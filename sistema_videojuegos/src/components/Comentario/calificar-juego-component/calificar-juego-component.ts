import { Component, Input, Output, EventEmitter, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { CalificacionService } from '../../../services/comentario/calificacion.service';
import { ComentarioService } from '../../../services/comentario/comentario.service';


@Component({
  selector: 'app-calificar-juego',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './calificar-juego-component.html',
  styleUrl: './calificar-juego-component.css'
})
export class CalificarJuegoComponent implements OnInit {
  @Input() juegoId!: number;
  @Input() bibliotecaId!: number;
  @Input() tituloJuego!: string;
  @Output() calificacionCreada = new EventEmitter<void>();
  @Output() comentarioCreado = new EventEmitter<void>();
  @Output() cancelar = new EventEmitter<void>();

  // Inicializa con valores por defecto
  calificacionForm: FormGroup;
  comentarioForm: FormGroup;
  
  usuarioActual: any;
  yaCalificado = false;
  yaComentado = false;
  
  calificacionUsuario: number = 0;
  comentarioUsuario: string = '';
  
  isLoading = false;
  mensajeError = '';
  mensajeExito = '';
  
  estrellas = [1, 2, 3, 4, 5];
  estrellaSeleccionada = 0;

  constructor(
    private fb: FormBuilder,
    private calificacionService: CalificacionService,
    private comentarioService: ComentarioService
  ) {
    // Inicializar formularios en el constructor
    this.calificacionForm = this.fb.group({
      calificacion: [0, [Validators.required, Validators.min(1), Validators.max(5)]]
    });
    
    this.comentarioForm = this.fb.group({
      comentario: ['', [Validators.required, Validators.minLength(10), Validators.maxLength(500)]]
    });
  }

  ngOnInit(): void {
    console.log('CalificarJuegoComponent inicializando con:', {
      juegoId: this.juegoId,
      bibliotecaId: this.bibliotecaId,
      tituloJuego: this.tituloJuego
    });
    
    // Obtener usuario actual del localStorage
    const usuarioStr = localStorage.getItem('currentUser');
    if (usuarioStr) {
      try {
        this.usuarioActual = JSON.parse(usuarioStr);
        console.log('Usuario obtenido:', this.usuarioActual);
        
        // Solo verificar estado si tenemos todos los datos necesarios
        if (this.juegoId && this.bibliotecaId && this.usuarioActual) {
          this.verificarEstadoUsuario();
        } else {
          this.mensajeError = 'Faltan datos necesarios para calificar';
        }
      } catch (error) {
        console.error('Error parseando usuario:', error);
        this.mensajeError = 'Error al obtener información del usuario';
      }
    } else {
      this.mensajeError = 'Usuario no autenticado';
      console.warn('No se encontró usuario en localStorage');
    }
  }

  verificarEstadoUsuario(): void {
    if (!this.usuarioActual || !this.bibliotecaId || !this.juegoId) {
      console.warn('Datos incompletos para verificar estado');
      return;
    }

    const idUsuario = this.usuarioActual.id_usuario || this.usuarioActual.idUsuario;
    console.log('Verificando estado para usuario:', idUsuario);

    // Verificar si ya calificó
    this.calificacionService.verificarCalificacion(idUsuario, this.bibliotecaId)
      .subscribe({
        next: (response) => {
          console.log('Respuesta verificación calificación:', response);
          this.yaCalificado = response.yaCalifico || false;
          console.log('Ya calificó?', this.yaCalificado);
          
          if (this.yaCalificado) {
            this.cargarCalificacionExistente(idUsuario);
          }
        },
        error: (error) => {
          console.error('Error verificando calificación:', error);
          this.yaCalificado = false;
          this.mensajeError = 'Error al verificar estado de calificación';
        }
      });

    // Verificar si ya comentó
    this.cargarComentarioExistente(idUsuario);
  }

  cargarCalificacionExistente(idUsuario: number): void {
    if (!this.usuarioActual || !this.bibliotecaId) return;
    
    this.calificacionService.obtenerCalificacionUsuario(idUsuario, this.bibliotecaId)
      .subscribe({
        next: (calificacion) => {
          console.log('Calificación existente:', calificacion);
          this.calificacionUsuario = calificacion.calificacion;
          this.estrellaSeleccionada = calificacion.calificacion;
          this.calificacionForm.patchValue({ calificacion: calificacion.calificacion });
        },
        error: (error) => {
          console.error('Error cargando calificación existente:', error);
          // Si hay error, asumimos que no hay calificación previa
          this.calificacionUsuario = 0;
          this.estrellaSeleccionada = 0;
        }
      });
  }

  cargarComentarioExistente(idUsuario: number): void {
    if (!this.usuarioActual || !this.juegoId) return;
    
    this.comentarioService.obtenerComentariosUsuarioVideojuego(idUsuario, this.juegoId)
      .subscribe({
        next: (comentarios) => {
          console.log('Comentarios existentes:', comentarios);
          if (comentarios && comentarios.length > 0) {
            this.yaComentado = true;
            this.comentarioUsuario = comentarios[0].comentario;
            this.comentarioForm.patchValue({ comentario: comentarios[0].comentario });
          } else {
            this.yaComentado = false;
          }
        },
        error: (error) => {
          console.error('Error cargando comentarios:', error);
          this.yaComentado = false;
        }
      });
  }

  seleccionarEstrella(valor: number): void {
    if (this.yaCalificado) {
      this.mensajeError = 'Ya has calificado este juego. No puedes modificarlo.';
      return;
    }
    
    this.estrellaSeleccionada = valor;
    this.calificacionForm.patchValue({ calificacion: valor });
    this.mensajeError = '';
  }

  enviarCalificacion(): void {
    if (this.yaCalificado) {
      this.mensajeError = 'Ya has calificado este juego';
      return;
    }

    if (this.calificacionForm.invalid) {
      this.mensajeError = 'Por favor selecciona una calificación entre 1 y 5 estrellas';
      return;
    }

    if (!this.usuarioActual || !this.bibliotecaId) {
      this.mensajeError = 'No se pudo identificar al usuario o la biblioteca';
      return;
    }

    this.isLoading = true;
    this.mensajeError = '';
    
    const idUsuario = this.usuarioActual.id_usuario || this.usuarioActual.idUsuario;
    const calificacionData = {
      id_usuario: idUsuario,
      id_biblioteca: this.bibliotecaId,
      calificacion: this.calificacionForm.value.calificacion
    };

    console.log('Enviando calificación:', calificacionData);

    this.calificacionService.crearCalificacion(calificacionData).subscribe({
      next: (response) => {
        console.log('Calificación creada exitosamente:', response);
        this.mensajeExito = '¡Calificación enviada con éxito!';
        this.yaCalificado = true;
        this.calificacionUsuario = calificacionData.calificacion;
        this.isLoading = false;
        
        // Emitir evento
        this.calificacionCreada.emit();
        
        // Limpiar mensaje después de 3 segundos
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error al enviar calificación:', error);
        this.mensajeError = `Error al enviar calificación: ${error.message || 'Error desconocido'}`;
        this.isLoading = false;
      }
    });
  }

  enviarComentario(): void {
    // Verificar si el formulario existe
    if (!this.comentarioForm) {
      this.mensajeError = 'Formulario no disponible';
      return;
    }

    if (this.comentarioForm.invalid) {
      const errors = this.comentarioForm.get('comentario')?.errors;
      if (errors?.['required']) {
        this.mensajeError = 'El comentario es requerido';
      } else if (errors?.['minlength']) {
        this.mensajeError = 'El comentario debe tener al menos 10 caracteres';
      } else if (errors?.['maxlength']) {
        this.mensajeError = 'El comentario no puede exceder los 500 caracteres';
      }
      return;
    }

    if (!this.usuarioActual || !this.bibliotecaId) {
      this.mensajeError = 'No se pudo identificar al usuario o la biblioteca';
      return;
    }

    this.isLoading = true;
    this.mensajeError = '';
    
    const idUsuario = this.usuarioActual.id_usuario || this.usuarioActual.idUsuario;
    const comentarioData = {
      id_usuario: idUsuario,
      id_biblioteca: this.bibliotecaId,
      comentario: this.comentarioForm.value.comentario
    };

    console.log('Enviando comentario:', comentarioData);

    this.comentarioService.crearComentario(comentarioData).subscribe({
      next: (response) => {
        console.log('Comentario creado exitosamente:', response);
        this.mensajeExito = '¡Comentario enviado con éxito!';
        this.yaComentado = true;
        this.comentarioUsuario = comentarioData.comentario;
        this.comentarioCreado.emit();
        this.isLoading = false;
        
        setTimeout(() => {
          this.mensajeExito = '';
        }, 3000);
      },
      error: (error) => {
        console.error('Error al enviar comentario:', error);
        this.mensajeError = `Error al enviar comentario: ${error.message || 'Error desconocido'}`;
        this.isLoading = false;
      }
    });
  }

  onCancelar(): void {
    this.cancelar.emit();
  }

  get caracteresRestantes(): number {
    if (!this.comentarioForm || !this.comentarioForm.get('comentario')) {
      return 500;
    }
    const comentario = this.comentarioForm.get('comentario')?.value || '';
    return 500 - comentario.length;
  }

  get mostrarFormularioCalificacion(): boolean {
    return !this.yaCalificado && !!this.usuarioActual;
  }

  get mostrarFormularioComentario(): boolean {
    return !this.yaComentado && !!this.usuarioActual;
  }

  // Método para validar si los formularios están disponibles
  get formulariosDisponibles(): boolean {
    return !!this.calificacionForm && !!this.comentarioForm;
  }
}