// form-categoria-component.ts
import { ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { debounceTime, distinctUntilChanged, switchMap } from 'rxjs/operators';
import { Observable, of } from 'rxjs';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';
import { NewCategoriaRequest } from '../../../models/videojuego/categoria';
import { CategoriaService } from '../../../services/Videojuego/categoria.service';

@Component({
  selector: 'app-form-categoria-component',
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './form-categoria-component.html',
  styleUrl: './form-categoria-component.css',
})
export class FormCategoriaComponent implements OnInit {
  mode: 'create' | 'edit' = 'create';
  categoriaId?: number;
  categoriaOriginal: any = null;

  categoriaForm: FormGroup;
  isLoading = false;
  loadingData = false;
  errorMessage = '';
  successMessage = '';
  nombreDisponible = true;
  verificandoNombre = false;

  constructor(
    private fb: FormBuilder,
    private categoriaService: CategoriaService,
    private router: Router,
    private route: ActivatedRoute,
    private cdr: ChangeDetectorRef
  ) {
    this.categoriaForm = this.createForm();
  }

  ngOnInit(): void {
    // Verificar si estamos en modo edición
    this.route.params.subscribe(params => {
      if (params['id']) {
        this.mode = 'edit';
        this.categoriaId = +params['id'];
        this.loadCategoriaData();
      }
    });
    
    this.setupNombreValidation();
  }

  private createForm(): FormGroup {
    return this.fb.group({
      nombre: ['', [
        Validators.required,
        Validators.maxLength(50),
        Validators.pattern(/^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑ\s\-_]+$/)
      ]],
      descripcion: ['', Validators.maxLength(500)]
    });
  }

  private loadCategoriaData(): void {
    if (this.categoriaId) {
      this.loadingData = true;
      this.categoriaService.getCategoriaById(this.categoriaId).subscribe({
        next: (categoria) => {
          this.categoriaOriginal = categoria;
          this.categoriaForm.patchValue({
            nombre: categoria.nombre,
            descripcion: categoria.descripcion
          });
          this.loadingData = false;
          this.cdr.detectChanges();
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar la categoría';
          this.loadingData = false;
          setTimeout(() => this.router.navigate(['/categorias']), 3000);
          this.cdr.detectChanges();
        }
      });
    }
  }

  private setupNombreValidation(): void {
    const nombreControl = this.categoriaForm.get('nombre');
    
    if (nombreControl) {
      nombreControl.valueChanges.pipe(
        debounceTime(500),
        distinctUntilChanged(),
        switchMap(nombre => {
          if (nombre && nombre.length >= 2 && nombre !== this.categoriaOriginal?.nombre) {
            this.verificandoNombre = true;
            return this.categoriaService.verificarNombreDisponible(nombre);
          }
          return of({ disponible: true, nombre: '' });
        })
      ).subscribe({
        next: (response) => {
          this.nombreDisponible = response.disponible;
          this.verificandoNombre = false;
          if (!response.disponible) {
            nombreControl.setErrors({ nombreNoDisponible: true });
          }
        },
        error: (error) => {
          this.verificandoNombre = false;
        }
      });
    }
  }

  submitForm(): void {
    if (this.categoriaForm.valid && this.nombreDisponible) {
      const formData = this.categoriaForm.getRawValue();
      const categoriaData: NewCategoriaRequest = {
        nombre: formData.nombre.trim(),
        descripcion: formData.descripcion?.trim() || ''
      };
      
      this.isLoading = true;
      this.errorMessage = '';
      this.cdr.detectChanges();
      
      if (this.mode === 'create') {
        this.crearCategoria(categoriaData);
      } else {
        this.actualizarCategoria(categoriaData);
      }
    } else {
      this.markAllFieldsAsTouched();
      if (!this.nombreDisponible) {
        this.errorMessage = 'El nombre ya está en uso';
      } else {
        this.errorMessage = 'Complete los campos correctamente';
      }
      this.cdr.detectChanges();
    }
  }

  private crearCategoria(categoriaData: NewCategoriaRequest): void {
    this.categoriaService.createCategoria(categoriaData).subscribe({
      next: () => {
        this.successMessage = 'Categoría creada exitosamente';
        setTimeout(() => this.router.navigate(['/categorias']), 2000);
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.message || 'Error al crear categoría';
        this.cdr.detectChanges();
      }
    });
  }

  private actualizarCategoria(categoriaData: NewCategoriaRequest): void {
    if (!this.categoriaId) return;
    
    this.categoriaService.updateCategoria(this.categoriaId, categoriaData).subscribe({
      next: () => {
        this.successMessage = 'Categoría actualizada exitosamente';
        setTimeout(() => this.router.navigate(['/categorias']), 2000);
        this.cdr.detectChanges();
      },
      error: (error) => {
        this.isLoading = false;
        this.errorMessage = error.message || 'Error al actualizar categoría';
        this.cdr.detectChanges();
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/categorias']);
  }

  private markAllFieldsAsTouched(): void {
    Object.keys(this.categoriaForm.controls).forEach(key => {
      const control = this.categoriaForm.get(key);
      control?.markAsTouched();
    });
  }

  get nombre() { return this.categoriaForm.get('nombre'); }
  get descripcion() { return this.categoriaForm.get('descripcion'); }
}