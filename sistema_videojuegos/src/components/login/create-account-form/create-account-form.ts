import { Component, Input, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators, FormsModule, ReactiveFormsModule } from "@angular/forms";
import { KeyValuePipe, NgFor, NgIf } from "@angular/common";
import { Router, RouterLink } from '@angular/router';
import { CountsService } from "../../../services/Usuario/counts.service";
import { NewUserRequest, Role, User } from "../../../models/counts/count";


@Component({
  selector: 'app-user-form',
  imports: [NgFor, NgIf, FormsModule, ReactiveFormsModule, RouterLink],
  templateUrl: './create-account-form.html',
})
export class CreateAccountComponent implements OnInit {

  @Input()
  isEditMode: boolean = false;
  @Input()
  isAdminMode: boolean = false;
  @Input()
  userToUpdate!: User;

  newUserForm!: FormGroup;
  availableRoles: Role[] = [];
  isLoading: boolean = false;
  operationDone: boolean = false;
  
  // Propiedades para controlar qué campos mostrar según el rol
  showFullForm: boolean = true;
  selectedRoleId: number | null = null;

  constructor(
    private formBuilder: FormBuilder,
    private countsService: CountsService,
    private router: Router
  ) {
    this.initializeForm();
  }

  ngOnInit(): void {
    this.loadAvailableRoles();
    
    if (this.isEditMode && this.userToUpdate) {
      this.populateForm();
    } else {
      this.reset();
    }
  }

  private initializeForm(): void {
    this.newUserForm = this.formBuilder.group({
      nombre: ['', [Validators.required, Validators.maxLength(100)]],
      correo: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(3), Validators.maxLength(100)]],
      id_rol: [null, Validators.required],
      fecha_nacimiento: [''],
      pais: [''],
      telefono: ['', Validators.pattern('^[0-9]{8}$')]
    });

    if (this.isEditMode) {
      this.newUserForm.get('correo')?.disable();
    }

    this.newUserForm.get('id_rol')?.valueChanges.subscribe(roleId => {
      this.onRoleChange(roleId);
    });
  }

  private loadAvailableRoles(): void {
    this.availableRoles = [
      { id_rol: 2, nombre: 'ADMINISTRADOR DE EMPRESA', descripcion: 'Administrador de empresa' },
      { id_rol: 3, nombre: 'COMUN', descripcion: 'Usuario común' }
    ];
  }

  private onRoleChange(roleId: number): void {
    this.selectedRoleId = roleId;
    
    if (roleId === 2) {
      this.showFullForm = false;
      this.newUserForm.patchValue({
        pais: '',
        telefono: ''
      });
      
      this.newUserForm.get('fecha_nacimiento')?.setValidators([Validators.required]);
      this.newUserForm.get('fecha_nacimiento')?.updateValueAndValidity();
      
      this.newUserForm.get('pais')?.clearValidators();
      this.newUserForm.get('telefono')?.clearValidators();
    } else {
      this.showFullForm = true;
      this.newUserForm.get('fecha_nacimiento')?.clearValidators();
      this.newUserForm.get('fecha_nacimiento')?.updateValueAndValidity();
      
      this.newUserForm.get('pais')?.clearValidators();
      this.newUserForm.get('telefono')?.setValidators([Validators.pattern('^[0-9]{8}$')]);
    }
    
    this.newUserForm.get('pais')?.updateValueAndValidity();
    this.newUserForm.get('telefono')?.updateValueAndValidity();
  }

  private populateForm(): void {
    this.newUserForm.patchValue({
      nombre: this.userToUpdate.nombre,
      correo: this.userToUpdate.correo,
      password: this.userToUpdate.password,
      id_rol: this.userToUpdate.rol.id_rol,
      fecha_nacimiento: this.userToUpdate.fecha_nacimiento ? 
        this.formatDateForInput(this.userToUpdate.fecha_nacimiento) : '',
      pais: this.userToUpdate.pais || '',
      telefono: this.userToUpdate.telefono || ''
    });
    
    this.onRoleChange(this.userToUpdate.rol.id_rol);
  }

  private formatDateForInput(dateString: string): string {
    if (!dateString) return '';
    try {
      const date = new Date(dateString);
      if (isNaN(date.getTime())) return '';
      return date.toISOString().split('T')[0];
    } catch (error) {
      return '';
    }
  }

  submit(): void {
    if (this.newUserForm.valid) {
      console.log('Formulario válido, procediendo con la operación...');
      
      if (this.isEditMode) {
        this.updateUser();
      } else {
        this.createUser();
      }
    } else {
      this.markAllFieldsAsTouched();
    }
  }

  reset(): void {
    if (this.isEditMode && this.userToUpdate) {
      this.populateForm();
    } else {
      this.newUserForm.reset({
        nombre: '',
        correo: '',
        password: '',
        id_rol: null,
        fecha_nacimiento: '',
        pais: '',
        telefono: ''
      });
      
      this.showFullForm = true;
      this.selectedRoleId = null;
    }
    this.operationDone = false;
  }

  private createUser(): void {
    const formValues = this.newUserForm.getRawValue();
    
    // Buscar el objeto Role completo basado en el id_rol seleccionado
    const selectedRole = this.availableRoles.find(role => role.id_rol === formValues.id_rol);
    
    if (!selectedRole) {
      console.error('Rol no encontrado');
      return;
    }
    
    // Crear el objeto en el formato que espera el backend
    const newUserRequest: any = {
      correo: formValues.correo,
      nombre: formValues.nombre,
      password: formValues.password,
      id_rol: formValues.id_rol, // ENVIAR SOLO EL ID, NO EL OBJETO
      fecha_nacimiento: formValues.fecha_nacimiento || "",
      pais: formValues.pais || "",
      telefono: formValues.telefono || "",
      saldo_cartera: 0.00,
      avatar: null
    };
    
    // Campos específicos según el rol
    if (formValues.id_rol === 2) {
      newUserRequest.fecha_nacimiento = formValues.fecha_nacimiento;
      newUserRequest.pais = "";
      newUserRequest.telefono = "";
    } else {
      newUserRequest.fecha_nacimiento = formValues.fecha_nacimiento || "";
      newUserRequest.pais = formValues.pais || "";
      newUserRequest.telefono = formValues.telefono || "";
    }
    
    console.log('Enviando usuario al backend:', newUserRequest);
    
    this.isLoading = true;
    
    this.countsService.createNewUser(newUserRequest).subscribe({
      next: (response) => {
        this.isLoading = false;
        this.reset();
        this.operationDone = true;
        
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 5000);
      },
      error: (error: any) => {
        this.isLoading = false;
        console.error('Error creando usuario:', error);
        
        // Mostrar mensaje de error al usuario
        if (error.status === 409) {
          alert('El correo electrónico ya está registrado');
        } else if (error.status === 400) {
          alert('Datos inválidos. Por favor verifica la información');
        } else {
          alert('Error al crear usuario. Por favor intenta nuevamente');
        }
      }
    });
  }

  private updateUser(): void {
    // Tu lógica de actualización aquí
  }

  private markAllFieldsAsTouched(): void {
    Object.keys(this.newUserForm.controls).forEach(key => {
      const control = this.newUserForm.get(key);
      control?.markAsTouched();
    });
  }

  getFormTitle(): string {
    return this.isEditMode ? 'Editar Usuario' : 'Crear Nueva Cuenta';
  }

  getSubmitButtonText(): string {
    return this.isEditMode ? 'Actualizar Usuario' : 'Crear Cuenta';
  }
}