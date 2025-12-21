
import { Injectable } from '@angular/core';

import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';

import { User, Empresa as EmpresaModel } from '../../models/counts/count';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

// Interfaces específicas para el servicio
export interface NewEmpresaRequest {
  nombre: string;
  descripcion: string;
  id_administrador: number;
}

export interface UpdateEmpresaRequest {
  nombre: string;
  descripcion: string;
}

export interface Empresa {
  id_empresa: number;
  nombre: string;
  descripcion: string;
  administrador?: User;
}

export interface BackendUser {
  id_usuario?: number;
  idUsuario?: number;
  correo: string;
  rol?: any;
  empresa?: any;
  nombre: string;
  password?: string;
  fecha_nacimiento?: string;
  pais?: string;
  telefono?: string;
  saldo_cartera?: number;
  avatar?: any;
}

@Injectable({
  providedIn: 'root'
})
export class EmpresaService {
  private restConstants = new RestConstants();

  constructor(private httpClient: HttpClient) {
  }

  // ========== MÉTODOS PARA EMPRESAS ==========

  public createNewEmpresa(empresa: NewEmpresaRequest): Observable<void> {
    return this.httpClient.post<void>(`${this.restConstants.getApiURL()}empresas`, empresa);
    }

  public getAllEmpresa(): Observable<Empresa[]> {
    return this.httpClient.get<Empresa[]>(`${this.restConstants.getApiURL()}empresas`);
  }

  public deleteEmpresa(id: number): Observable<void> {
    const url = `${this.restConstants.getApiURL()}empresas/${id}`;
    return this.httpClient.delete<void>(url).pipe(
      catchError(this.handleError)
    );
  }

  public getEmpresaById(id: number): Observable<Empresa> {
    const url = `${this.restConstants.getApiURL()}empresas/${id}`;
    return this.httpClient.get<any>(url).pipe(
      map(empresaData => this.adaptEmpresa(empresaData)),
      catchError(this.handleError)
    );
  }

  public updateEmpresa(id: number, empresaData: UpdateEmpresaRequest): Observable<Empresa> {
    const url = `${this.restConstants.getApiURL()}empresas/${id}`;
    return this.httpClient.put<any>(url, empresaData).pipe(
      map(updatedData => this.adaptEmpresa(updatedData)),
      catchError(this.handleError)
    );
  } 

  

  // ========== MÉTODOS PARA ADMINISTRADORES ==========

  /**
   * Obtiene administradores sin empresa asignada
   */
  getAdministradoresSinEmpresa(): Observable<User[]> {
    const url = `${this.restConstants.getApiURL()}empresas/administradores-disponibles`;
    console.log('URL para obtener administradores:', url);

    return this.httpClient.get<BackendUser[]>(url).pipe(
      map(users => users.map(user => this.adaptUser(user))),
      catchError(this.handleError)
    );
  }

  // ========== MÉTODOS DE ADAPTACIÓN ==========

  /**
   * Adapta un usuario del backend al modelo del frontend
   */
  private adaptUser(backendUser: BackendUser): User {
    // Determinar el ID del usuario
    const idUsuario = backendUser.idUsuario || backendUser.id_usuario || 0;

    // Adaptar el rol
    let rol = {
      id_rol: 2, // Por defecto ADMINISTRADOR DE EMPRESA
      nombre: 'ADMINISTRADOR DE EMPRESA',
      descripcion: 'Administrador de empresa'
    };

    if (backendUser.rol) {
      rol = {
        id_rol: backendUser.rol.id_rol || backendUser.rol.idRol || 2,
        nombre: backendUser.rol.nombre || 'ADMINISTRADOR DE EMPRESA',
        descripcion: backendUser.rol.descripcion || 'Administrador de empresa'
      };
    }

    return {
      idUsuario: idUsuario,
      correo: backendUser.correo,
      nombre: backendUser.nombre,
      password: backendUser.password || '',
      fecha_nacimiento: backendUser.fecha_nacimiento || '',
      pais: backendUser.pais || '',
      telefono: backendUser.telefono || '',
      saldo_cartera: backendUser.saldo_cartera || 0,
      avatar: backendUser.avatar || null,
      rol: rol,
      empresa: null // Siempre null para administradores sin empresa
    };
  }

  /**
   * Adapta una empresa del backend al modelo del frontend
   */
  private adaptEmpresa(backendEmpresa: any): Empresa {
    return {
      id_empresa: backendEmpresa.id_empresa || backendEmpresa.idEmpresa || 0,
      nombre: backendEmpresa.nombre || '',
      descripcion: backendEmpresa.descripcion || '',
      administrador: backendEmpresa.administrador ? this.adaptUser(backendEmpresa.administrador) : undefined
    };
  }

  // ========== MANEJO DE ERRORES ==========

  /**
   * Maneja errores HTTP
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Error en EmpresaService:', error);

    let errorMessage = 'Ocurrió un error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      // Error del lado del cliente
      errorMessage = `Error: ${error.error.message}`;
    } else {
      // Error del lado del servidor
      if (error.status === 0) {
        errorMessage = 'No se pudo conectar con el servidor. Verifique su conexión a internet.';
      } else if (error.status === 400) {
        errorMessage = error.error?.error || 'Datos inválidos enviados al servidor.';
      } else if (error.status === 404) {
        errorMessage = 'Recurso no encontrado.';
      } else if (error.status === 409) {
        errorMessage = error.error?.error || 'El recurso ya existe.';
      } else if (error.status === 500) {
        errorMessage = 'Error interno del servidor. Por favor intente más tarde.';
      } else {
        errorMessage = `Error ${error.status}: ${error.statusText}`;
      }
    }

    return throwError(() => new Error(errorMessage));
  }
}