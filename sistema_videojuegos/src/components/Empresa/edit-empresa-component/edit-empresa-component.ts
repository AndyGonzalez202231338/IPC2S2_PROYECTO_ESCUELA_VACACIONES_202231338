import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Empresa, EmpresaService } from '../../../services/Empresa/empresa.service';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Footer } from '../../footer/footer';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-edit-empresa-component',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './edit-empresa-component.html',
  styleUrl: './edit-empresa-component.css',
})
export class EditEmpresaComponent implements OnInit {
  empresaForm: FormGroup;
  empresaId!: number;
  empresaOriginal: Empresa | null = null;
  isLoading: boolean = false;
  loadingData: boolean = false;
  operationDone: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private empresaService: EmpresaService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.empresaForm = this.createForm();
  }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      this.empresaId = +params['id'];
      this.loadEmpresaData();
    });
  }

  private createForm(): FormGroup {
    return this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.maxLength(500)]]
    });
  }

  private loadEmpresaData(): void {
    this.loadingData = true;
    this.errorMessage = '';
    
    this.empresaService.getEmpresaById(this.empresaId).subscribe({
      next: (empresa) => {
        this.empresaOriginal = empresa;
        this.empresaForm.patchValue({
          nombre: empresa.nombre,
          descripcion: empresa.descripcion
        });
        this.loadingData = false;
        this.cdr.detectChanges();
        console.log('Datos de empresa cargados:', empresa);
      },
      error: (error) => {
        console.error('Error cargando empresa:', error);
        this.errorMessage = 'No se pudo cargar la información de la empresa.';
        this.loadingData = false;
        this.cdr.detectChanges();
        // Redirigir después de 3 segundos si hay error
        setTimeout(() => {
          this.router.navigate(['/empresas']);
        }, 3000);
      }
    });
  }

  hasChanges(): boolean {
    if (!this.empresaOriginal) return true;
    
    const formValues = this.empresaForm.getRawValue();
    return (
      formValues.nombre !== this.empresaOriginal.nombre ||
      formValues.descripcion !== this.empresaOriginal.descripcion
    );
  }

  onSubmit(): void {
    if (this.empresaForm.valid && this.hasChanges()) {
      this.updateEmpresa();
    } else if (!this.hasChanges()) {
      this.errorMessage = 'No hay cambios para guardar.';
    } else {
      this.markAllFieldsAsTouched();
      this.errorMessage = 'Por favor complete todos los campos requeridos correctamente.';
    }
  }

  private updateEmpresa(): void {
    const formData = this.empresaForm.getRawValue();
    
    const updateData = {
      nombre: formData.nombre.trim(),
      descripcion: formData.descripcion.trim()
    };

    console.log('Actualizando empresa ID:', this.empresaId, 'Datos:', updateData);
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.empresaService.updateEmpresa(this.empresaId, updateData).subscribe({
      next: (empresaActualizada) => {
        console.log('Empresa actualizada exitosamente:', empresaActualizada);
        this.isLoading = false;
        this.successMessage = 'Empresa actualizada exitosamente.';
        this.empresaOriginal = empresaActualizada;
        
        // Redirigir después de 2 segundos
        setTimeout(() => {
          this.router.navigate(['/empresas']);
        }, 2000);
      },
      error: (error: any) => {
        this.isLoading = false;
        this.errorMessage = error.message || 'Error al actualizar empresa';
        console.log('Error asignado:', this.errorMessage);
        
        // FORZAR DETECCIÓN DE CAMBIOS
        this.cdr.detectChanges();
        
        // Verificar que se actualizó
        setTimeout(() => {
          console.log('Después de detectChanges:', this.errorMessage);
        }, 0);
      }
    });
  }

  cancelEdit(): void {
    // Preguntar si hay cambios sin guardar
    if (this.hasChanges()) {
      if (confirm('¿Desea descartar los cambios no guardados?')) {
        this.router.navigate(['/empresas']);
      }
    } else {
      this.router.navigate(['/empresas']);
    }
  }

  resetForm(): void {
    if (this.empresaOriginal) {
      this.empresaForm.reset({
        nombre: this.empresaOriginal.nombre,
        descripcion: this.empresaOriginal.descripcion
      });
    }
    this.errorMessage = '';
    this.successMessage = '';
  }

  private markAllFieldsAsTouched(): void {
    Object.keys(this.empresaForm.controls).forEach(key => {
      const control = this.empresaForm.get(key);
      control?.markAsTouched();
    });
  }

  // Getters para acceder fácilmente a los controles del formulario
  get nombre() { return this.empresaForm.get('nombre'); }
  get descripcion() { return this.empresaForm.get('descripcion'); }
}
