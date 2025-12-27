import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, map } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface NewInstalacionRequest {
  id_biblioteca: number;
  id_videojuego: number;
  estado: string;
  tipo_adquisicion: string;
  id_usuario: number;
}

export interface InstalacionResponse {
  id_instalacion: number;
  id_biblioteca: number;
  id_videojuego: number;
  estado: 'INSTALADO' | 'NO_INSTALADO';
  tipo_adquisicion: 'COMPRA' | 'PRESTAMO';
}

export interface JuegoPrestable {
  id_videojuego: number;
  titulo: string;
  id_usuario_propietario: number;
  nombre_propietario: string;
  id_grupo: number;
  nombre_grupo: string;
  clasificacion_edad: string;
  descripcion: string;
  ya_prestado: boolean;
}

export interface JuegosPrestablesAgrupados {
  success: boolean;
  usuario_id: number;
  total_juegos: number;
  total_grupos: number;
  total_propietarios: number;
  grupos: GrupoPrestable[];
  resumen_grupos: ResumenGrupo[];
}

export interface GrupoPrestable {
  id_grupo: number;
  nombre_grupo: string;
  juegos: JuegoPrestableInfo[];
}

export interface JuegoPrestableInfo {
  id_videojuego: number;
  titulo: string;
  descripcion: string;
  clasificacion_edad: string;
  propietario: PropietarioInfo;
  ya_prestado: boolean;
  puede_solicitar: boolean;
}

export interface PropietarioInfo {
  id_usuario: number;
  nombre: string;
}

export interface ResumenGrupo {
  id_grupo: number;
  nombre_grupo: string;
  total_juegos: number;
}

@Injectable({
  providedIn: 'root'
})
export class InstalacionService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Obtener la instalación actual del usuario
   */
  obtenerInstalacionActual(idUsuario: number): Observable<InstalacionResponse | null> {
    const url = `${this.restConstants.getApiURL()}instalaciones/usuario/${idUsuario}`;
    
    return this.http.get<InstalacionResponse[]>(url).pipe(
      map(instalaciones => {
        // Buscar la instalación activa (INSTALADO)
        const instalacionActiva = instalaciones?.find(
          inst => inst.estado === 'INSTALADO'
        );
        return instalacionActiva || null;
      }),
      catchError((error: HttpErrorResponse) => {
        if (error.status === 404 || error.status === 500) {
          // Si no hay instalaciones o error, devolver null
          return [null];
        }
        return this.handleError(error);
      })
    );
  }

  /**
   * Instalar juego comprado
   */
  instalarJuegoComprado(idUsuario: number, idVideojuego: number, idUsuarioPropietario: number): Observable<InstalacionResponse> {
    const url = `${this.restConstants.getApiURL()}instalaciones/instalar-comprado/${idUsuario}/${idVideojuego}/${idUsuarioPropietario}`;
    
    return this.http.post<InstalacionResponse>(url, {})
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Instalar juego prestado
   */
  instalarJuegoPrestado(idUsuario: number, idVideojuego: number, idUsuarioPropietario: number): Observable<InstalacionResponse> {
    console.log('InstalacionService: instalarJuegoPrestado llamado con idUsuario=', idUsuario, 'idVideojuego=', idVideojuego);
    const url = `${this.restConstants.getApiURL()}instalaciones/instalar-prestado/${idUsuario}/${idVideojuego}/${idUsuarioPropietario}`;
    
    return this.http.post<InstalacionResponse>(url, {})
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Desinstalar juego
   */
  desinstalarJuego(idInstalacion: number): Observable<InstalacionResponse> {
    const url = `${this.restConstants.getApiURL()}instalaciones/desinstalar/${idInstalacion}`;
    
    return this.http.post<InstalacionResponse>(url, {})
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Verificar si puede instalar juego prestado
   */
  puedeInstalarJuegoPrestado(idUsuario: number): Observable<boolean> {
    const url = `${this.restConstants.getApiURL()}instalaciones/puede-instalar-prestado/${idUsuario}`;
    
    return this.http.get<{puede_instalar: boolean, id_usuario: number}>(url)
      .pipe(
        map(response => response.puede_instalar),
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener juegos prestables (agrupados por grupo)
   */
  obtenerJuegosPrestablesAgrupados(idUsuario: number): Observable<JuegosPrestablesAgrupados> {
    const url = `${this.restConstants.getApiURL()}instalaciones/prestables-agrupados/${idUsuario}`;
    
    return this.http.get<JuegosPrestablesAgrupados>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener juegos prestables (lista simple)
   */
  obtenerJuegosPrestables(idUsuario: number): Observable<JuegoPrestable[]> {
    const url = `${this.restConstants.getApiURL()}instalaciones/prestables/${idUsuario}`;
    
    return this.http.get<JuegoPrestable[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Crear nueva instalación
   */
  crearInstalacion(request: NewInstalacionRequest): Observable<InstalacionResponse> {
    const url = `${this.restConstants.getApiURL()}instalaciones`;
    
    return this.http.post<InstalacionResponse>(url, request)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener todas las instalaciones del usuario
   */
  obtenerInstalacionesPorUsuario(idUsuario: number): Observable<InstalacionResponse[]> {
    const url = `${this.restConstants.getApiURL()}instalaciones/usuario/${idUsuario}`;
    
    return this.http.get<InstalacionResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error en InstalacionService:', error);
    
    let errorMessage = 'Error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 400) {
        errorMessage = serverError || 'Datos de instalación inválidos';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Recurso no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'Restricción de instalación: ' + (serverError || 'Ya tienes un juego instalado');
      } else if (error.status === 500) {
        errorMessage = serverError || 'Error interno del servidor';
      } else {
        errorMessage = serverError || `Error ${error.status}: ${error.message}`;
      }
    }
    
    return throwError(() => new Error(errorMessage));
  }

  /**
   * Extraer mensaje de error del cuerpo de la respuesta
   */
  private extractErrorMessage(error: HttpErrorResponse): string | null {
    try {
      if (typeof error.error === 'string') {
        try {
          const parsedError = JSON.parse(error.error);
          if (parsedError && parsedError.error) {
            return parsedError.error;
          }
        } catch {
          return error.error;
        }
      }
      
      if (error.error && typeof error.error === 'object') {
        if (error.error.error) {
          return error.error.error;
        } else if (error.error.message) {
          return error.error.message;
        }
      }
      
      if (error.message) {
        return error.message;
      }
      
    } catch (e) {
      console.error('Error al extraer mensaje:', e);
    }
    
    return null;
  }
}