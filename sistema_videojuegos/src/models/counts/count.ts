export interface User {
  idUsuario: number; 
  correo: string;
  rol: Role;
  empresa: Empresa | null;
  nombre: string;
  password: string;
  fecha_nacimiento: string;
  pais: string;
  telefono: string;
  saldo_cartera: number;
  avatar: Uint8Array | null;
}

export interface Empresa {
  id_empresa: number;
  nombre: string;
  descripcion: string;
  administrador?: User | null;
}

export interface Role {
  id_rol: number;
  nombre: string;
  descripcion: string;
}

export interface NewUserRequest {
  correo: string;
  rol: Role;
  empresa: Empresa | null;
  nombre: string;
  password: string;
  fecha_nacimiento?: string;  
  pais?: string;              
  telefono?: string;          
  saldo_cartera: number;
  avatar: Uint8Array | null;
}