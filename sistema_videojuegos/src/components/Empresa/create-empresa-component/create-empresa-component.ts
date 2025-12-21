import { NewEmpresaRequest } from './../../../services/Empresa/empresa.service';
import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { User } from '../../../models/counts/count';
import { EmpresaService } from '../../../services/Empresa/empresa.service';
import { HeaderAdminSistema } from '../../Header/header-admin-sistema/header-admin-sistema';
import { Footer } from '../../footer/footer';

@Component({
  selector: 'app-create-empresa',
  standalone: true,
  imports: [CommonModule, FormsModule, ReactiveFormsModule, RouterModule, HeaderAdminSistema, Footer],
  templateUrl: './create-empresa-component.html'
})
export class CreateEmpresaComponent implements OnInit {
  newEmpresaRequest!: NewEmpresaRequest;
  empresaForm: FormGroup;
  administradoresDisponibles: User[] = [];
  isLoading: boolean = false;
  isLoadingAdmins: boolean = false;
  operationDone: boolean = false;
  errorMessage: string = '';
  successMessage: string = '';
  selectedAdmin: User | null = null;

  constructor(
    private fb: FormBuilder,
    private empresaService: EmpresaService,
    private router: Router
  ) {
    this.empresaForm = this.createForm();
  }

  ngOnInit(): void {
    this.loadAdministradoresDisponibles();
    
    // Escuchar cambios en la selección de administrador
    this.empresaForm.get('id_administrador')?.valueChanges.subscribe(id => {
      this.onAdminSelected(id);
    });
  }

  private createForm(): FormGroup {
    return this.fb.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      descripcion: ['', [Validators.required, Validators.maxLength(500)]],
      id_administrador: [null, Validators.required]
    });
  }

  private loadAdministradoresDisponibles(): void {
    this.isLoadingAdmins = true;
    this.empresaService.getAdministradoresSinEmpresa().subscribe({
      next: (admins) => {
        this.administradoresDisponibles = admins;
        this.isLoadingAdmins = false;
        console.log('Administradores disponibles:', admins);
        
        if (admins.length === 0) {
          this.errorMessage = 'No hay administradores disponibles. Primero debe crear un usuario administrador.';
        }
      },
      error: (error) => {
        console.error('Error cargando administradores:', error);
        this.errorMessage = 'Error al cargar administradores disponibles. Intente nuevamente.';
        this.isLoadingAdmins = false;
      }
    });
  }

  private onAdminSelected(adminId: number): void {
    if (adminId) {
      this.selectedAdmin = this.administradoresDisponibles.find(a => a.idUsuario === adminId) || null;
    } else {
      this.selectedAdmin = null;
    }
  }

  onSubmit(): void {
    if (this.empresaForm.valid) {
      this.createEmpresa();
    } else {
      this.markAllFieldsAsTouched();
      this.errorMessage = 'Por favor complete todos los campos requeridos correctamente.';
    }
  }

  private createEmpresa(): void {
    const formData = this.empresaForm.getRawValue();
    
    this.newEmpresaRequest = {
      nombre: formData.nombre.trim(),
      descripcion: formData.descripcion.trim(),
      id_administrador: formData.id_administrador
    };

    console.log('Enviando datos de empresa:', this.newEmpresaRequest);
    
    this.isLoading = true;
    this.errorMessage = '';
    this.successMessage = '';

    this.empresaService.createNewEmpresa(this.newEmpresaRequest).subscribe({
      next: (response) => {
        console.log('Empresa creada exitosamente:', response);
        this.isLoading = false;
        this.operationDone = true;
        
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error creando empresa:', error);
        
        if (error.status === 409) {
          this.errorMessage = 'El nombre de la empresa ya existe. Por favor use otro nombre.';
        } else if (error.status === 400) {
          this.errorMessage = error.error?.error || 'Datos inválidos. Por favor verifique la información.';
        } else if (error.status === 404) {
          this.errorMessage = 'Administrador no encontrado o ya tiene empresa asignada.';
        } else if (error.status === 500) {
          this.errorMessage = 'Error interno del servidor. Por favor intente más tarde.';
        } else {
          this.errorMessage = 'Error al crear empresa. Por favor intente nuevamente.';
        }
      }
    });
  }

  resetForm(): void {
    this.empresaForm.reset({
      nombre: '',
      descripcion: '',
      id_administrador: null
    });
    this.selectedAdmin = null;
    this.errorMessage = '';
    this.successMessage = '';
    this.operationDone = false;
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
  get id_administrador() { return this.empresaForm.get('id_administrador'); }
}