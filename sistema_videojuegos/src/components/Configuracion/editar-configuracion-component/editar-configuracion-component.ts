import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { Configuracion, UpdateConfiguracionRequest } from '../../../models/configuracion/configuracion';
import { ConfiguracionService } from '../../../services/Configuracion/configuracion.service';

@Component({
  selector: 'app-editar-configuracion-component',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './editar-configuracion-component.html',
  styleUrl: './editar-configuracion-component.css',
})
export class EditarConfiguracionComponent implements OnInit {
  configuracionForm: FormGroup;
  configuracionId!: number;
  configuracionOriginal: Configuracion | null = null;
  isLoading: boolean = false;
  loadingData: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private configuracionService: ConfiguracionService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.configuracionForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.configuracionId = +params['id'];
      this.loadConfiguracionData();
    });
  }

  private createForm(): FormGroup {
    return this.fb.group({
      valor: ['', [Validators.required]],
      fecha_final: ['']
    });
  }

  private loadConfiguracionData(): void {
    this.loadingData = true;
    this.errorMessage = '';
    
    this.configuracionService.getAllConfiguraciones().subscribe({
      next: (configs) => {
        const config = configs.find(c => c.id_configuracion === this.configuracionId);
        if (config) {
          this.configuracionOriginal = config;
          this.configuracionForm.patchValue({
            valor: config.valor,
            fecha_final: config.fecha_final || ''
          });
        } else {
          this.errorMessage = 'Configuración no encontrada';
          this.cdr.detectChanges();
        }
        this.loadingData = false;
        this.cdr.detectChanges();
      },
      error: (error) => {
        console.error('Error cargando configuración:', error);
        this.errorMessage = 'No se pudo cargar la información de la configuración.';
        this.loadingData = false;
        this.cdr.detectChanges();
        setTimeout(() => {
          this.router.navigate(['/sistema/configuraciones']);
        }, 3000);
      }
    });
  }

  hasChanges(): boolean {
    if (!this.configuracionOriginal) return true;
    
    const formValues = this.configuracionForm.getRawValue();
    const fechaFinalForm = formValues.fecha_final || null;
    const fechaFinalOriginal = this.configuracionOriginal.fecha_final || null;
    
    return (
      formValues.valor !== this.configuracionOriginal.valor ||
      fechaFinalForm !== fechaFinalOriginal
    );
  }

  onSubmit(): void {
    if (this.configuracionForm.valid && this.hasChanges()) {
      this.updateConfiguracion();
    } else if (!this.hasChanges()) {
      this.errorMessage = 'No hay cambios para guardar.';
    } else {
      this.markAllFieldsAsTouched();
      this.errorMessage = 'Por favor complete los campos correctamente.';
    }
  }

  private updateConfiguracion(): void {
    const formData = this.configuracionForm.getRawValue();
    
    const updateData: UpdateConfiguracionRequest = {
      valor: formData.valor.trim(),
      fecha_final: formData.fecha_final || null
    };

    console.log('Actualizando configuración ID:', this.configuracionId, 'Datos:', updateData);
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.configuracionService.updateConfiguracion(this.configuracionId, updateData).subscribe({
      next: (configActualizada) => {
        console.log('Configuración actualizada exitosamente:', configActualizada);
        this.isLoading = false;
        this.successMessage = 'Configuración actualizada exitosamente.';
        this.configuracionOriginal = configActualizada;
        
        setTimeout(() => {
          this.router.navigate(['/sistema/configuraciones']);
        }, 2000);
      },
      error: (error: Error) => {
        this.isLoading = false;
        console.error('Error actualizando configuración:', error);
        this.errorMessage = error.message;
      }
    });
  }

  cancelEdit(): void {
    if (this.hasChanges()) {
      if (confirm('¿Desea descartar los cambios no guardados?')) {
        this.router.navigate(['/sistema/configuraciones']);
      }
    } else {
      this.router.navigate(['/sistema/configuraciones']);
    }
  }

  resetForm(): void {
    if (this.configuracionOriginal) {
      this.configuracionForm.reset({
        valor: this.configuracionOriginal.valor,
        fecha_final: this.configuracionOriginal.fecha_final || ''
      });
    }
    this.errorMessage = '';
    this.successMessage = '';
  }

  getConfiguracionTipo(): string {
    if (!this.configuracionOriginal) return '';
    
    switch(this.configuracionOriginal.configuracion) {
      case 'COMISION_GLOBAL':
        return 'Comisión Global';
      case 'EDAD_ADOLESCENTES':
        return 'Edad para Adolescentes';
      case 'MAX_MIEMBROS_GRUPO':
        return 'Máximo de Miembros por Grupo';
      default:
        return this.configuracionOriginal.configuracion;
    }
  }

  getConfiguracionIcon(): string {
    if (!this.configuracionOriginal) return 'fa-cog';
    
    switch(this.configuracionOriginal.configuracion) {
      case 'COMISION_GLOBAL':
        return 'fa-percent';
      case 'EDAD_ADOLESCENTES':
        return 'fa-user';
      case 'MAX_MIEMBROS_GRUPO':
        return 'fa-users';
      default:
        return 'fa-cog';
    }
  }

  formatFecha(fecha: string | null): string {
    if (!fecha) return 'Permanente';
    return new Date(fecha).toLocaleDateString('es-ES');
  }

  private markAllFieldsAsTouched(): void {
    Object.keys(this.configuracionForm.controls).forEach(key => {
      const control = this.configuracionForm.get(key);
      control?.markAsTouched();
    });
  }

  get valor() { return this.configuracionForm.get('valor'); }
  get fecha_final() { return this.configuracionForm.get('fecha_final'); }
}
