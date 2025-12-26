import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { GrupoService, NewGrupoRequest, GrupoResponse, IntegranteResponse } from '../../../services/Grupo/grupo.service';
import { LoginService } from '../../../services/Login/login.services';
import { Header } from '../../Header/header/header';
import { Footer } from '../../footer/footer';

@Component({
  selector: 'app-grupos',
  imports: [CommonModule, ReactiveFormsModule, Header, Footer],
  templateUrl: './grupos-component.html',
  styleUrl: './grupos-component.css'
})
export class GruposComponent implements OnInit {
  currentUser: any = null;
  grupos: GrupoResponse[] = [];
  grupoSeleccionado: GrupoResponse | null = null;
  integrantes: IntegranteResponse[] = [];
  
  // Estados
  isLoading = false;
  isCreating = false;
  isAddingParticipant = false;
  isLoadingIntegrantes = false;
  errorMessage = '';
  successMessage = '';
  
  // Formularios
  crearGrupoForm: FormGroup;
  agregarParticipanteForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private grupoService: GrupoService,
    private loginService: LoginService,
    private cdr: ChangeDetectorRef
  ) {
    this.crearGrupoForm = this.fb.group({
      nombre: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]]
    });
    
    this.agregarParticipanteForm = this.fb.group({
      idUsuario: ['', [Validators.required, Validators.min(1)]]
    });
  }

  ngOnInit(): void {
    this.currentUser = this.loginService.getCurrentUser();
    
    if (!this.currentUser) {
      // Redirigir a login si no hay usuario
      return;
    }
    
    this.cargarGrupos();
    this.cdr.detectChanges();
  }

  cargarGrupos(): void {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.grupoService.obtenerGruposDeUsuario(this.currentUser.id_usuario || this.currentUser.idUsuario)
      .subscribe({
        next: (grupos) => {
          this.grupos = grupos;
          this.isLoading = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al cargar grupos';
          this.isLoading = false;
          this.cdr.detectChanges();
        }
      });
  }

  seleccionarGrupo(grupo: GrupoResponse): void {
    this.grupoSeleccionado = grupo;
    this.cargarIntegrantes(grupo.id_grupo);
    this.cargarEspaciosDisponibles(grupo.id_grupo);
    this.cdr.detectChanges();
  }

  cargarIntegrantes(idGrupo: number): void {
    this.isLoadingIntegrantes = true;
    
    this.grupoService.obtenerIntegrantes(idGrupo)
      .subscribe({
        next: (integrantes) => {
          this.integrantes = integrantes;
          this.isLoadingIntegrantes = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = `Error al cargar integrantes: ${error.message}`;
          this.isLoadingIntegrantes = false;
          this.cdr.detectChanges();
        }
      });
  }

  cargarEspaciosDisponibles(idGrupo: number): void {
    this.grupoService.obtenerEspaciosDisponibles(idGrupo)
      .subscribe({
        next: (response) => {
          if (this.grupoSeleccionado) {
            // Puedes mostrar esta información en la vista
            console.log('Espacios disponibles:', response.espacios_disponibles);
            this.cdr.detectChanges();
          }
        },
        error: (error) => {
          console.error('Error al cargar espacios disponibles:', error);
          this.cdr.detectChanges();
        }
      });
  }

  crearGrupo(): void {
    if (this.crearGrupoForm.invalid) {
      this.marcarCamposComoSucios(this.crearGrupoForm);
      return;
    }
    
    this.isCreating = true;
    this.errorMessage = '';
    this.successMessage = '';
    
    const grupoRequest: NewGrupoRequest = {
      id_creador: this.currentUser.id_usuario || this.currentUser.idUsuario,
      nombre: this.crearGrupoForm.value.nombre
    };
    
    this.grupoService.crearGrupo(grupoRequest)
      .subscribe({
        next: (grupoCreado) => {
          this.successMessage = `Grupo "${grupoCreado.nombre}" creado exitosamente.`;
          this.grupos.push(grupoCreado);
          this.crearGrupoForm.reset();
          this.isCreating = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al crear grupo';
          this.isCreating = false;
          this.cdr.detectChanges();
        }
      });
  }

  agregarParticipante(): void {
    if (!this.grupoSeleccionado || this.agregarParticipanteForm.invalid) {
      this.marcarCamposComoSucios(this.agregarParticipanteForm);
      return;
    }
    
    this.isAddingParticipant = true;
    this.errorMessage = '';
    
    const idUsuario = this.agregarParticipanteForm.value.idUsuario;
    
    this.grupoService.agregarParticipante(this.grupoSeleccionado.id_grupo, idUsuario)
      .subscribe({
        next: (response) => {
          this.successMessage = 'Participante agregado exitosamente.';
          this.agregarParticipanteForm.reset();
          this.cargarIntegrantes(this.grupoSeleccionado!.id_grupo);
          this.isAddingParticipant = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = error.message || 'Error al agregar participante';
          this.isAddingParticipant = false;
          this.cdr.detectChanges();
        }
      });
  }

  eliminarParticipante(idUsuario: number): void {
    if (!this.grupoSeleccionado || !this.currentUser) return;
    
    if (!confirm('¿Estás seguro de eliminar a este participante del grupo?')) {
      return;
    }
    
    this.grupoService.eliminarParticipante(
      this.grupoSeleccionado.id_grupo,
      idUsuario,
      this.currentUser.id_usuario || this.currentUser.idUsuario
    ).subscribe({
      next: () => {
        this.successMessage = 'Participante eliminado exitosamente.';
        this.cargarIntegrantes(this.grupoSeleccionado!.id_grupo);
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al eliminar participante';
        this.cdr.detectChanges();
      }
    });
  }

  eliminarGrupo(): void {
    if (!this.grupoSeleccionado || !this.currentUser) return;
    
    if (!confirm('¿Estás seguro de eliminar este grupo? Esta acción no se puede deshacer.')) {
      return;
    }
    
    this.grupoService.eliminarGrupo(
      this.grupoSeleccionado.id_grupo,
      this.currentUser.id_usuario || this.currentUser.idUsuario
    ).subscribe({
      next: () => {
        this.successMessage = 'Grupo eliminado exitosamente.';
        this.grupoSeleccionado = null;
        this.integrantes = [];
        this.cargarGrupos();
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.errorMessage = error.message || 'Error al eliminar grupo';
        this.cdr.detectChanges();
      }
    });
  }

  private marcarCamposComoSucios(form: FormGroup): void {
    Object.keys(form.controls).forEach(key => {
      const control = form.get(key);
      control?.markAsTouched();
    });
  }

  // Getters para formularios
  get nombre() {
    return this.crearGrupoForm.get('nombre');
  }
  
  get idUsuario() {
    return this.agregarParticipanteForm.get('idUsuario');
  }
}