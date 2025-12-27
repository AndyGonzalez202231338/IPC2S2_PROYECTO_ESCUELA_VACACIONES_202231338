import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, map } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';

export interface BibliotecaResponse {
  id_biblioteca: number;
  id_usuario: number;
  tipo_adquisicion: 'COMPRA' | 'PRESTAMO';
  compra: CompraMiniResponse;
  videojuego: VideojuegoMiniResponse;
}

export interface CompraMiniResponse {
  id_compra: number;
  monto_pago: number;
  fecha_compra: string;
  comision_aplicada: number;
  id_usuario: number;
}

export interface VideojuegoMiniResponse {
  id_videojuego: number;
  id_empresa: number;
  titulo: string;
  precio: number;
  clasificacion_edad: string;
  fecha_lanzamiento: string;
}

export interface InstalacionJuego {
  id_instalacion: number;
  id_biblioteca: number;
  id_videojuego: number;
  estado: 'INSTALADO' | 'NO_INSTALADO';
  tipo_adquisicion: 'COMPRA' | 'PRESTAMO';
}

export interface VerificacionResponse {
  tieneJuego: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class BibliotecaService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  /**
   * Obtiene la biblioteca completa de un usuario
   */
  obtenerBibliotecaUsuario(idUsuario: number): Observable<BibliotecaResponse[]> {
    const url = `${this.restConstants.getApiURL()}biblioteca/usuario/${idUsuario}`;
    return this.http.get<BibliotecaResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtiene solo los juegos comprados de un usuario
   */
  obtenerJuegosComprados(idUsuario: number): Observable<BibliotecaResponse[]> {
    const url = `${this.restConstants.getApiURL()}biblioteca/usuario/${idUsuario}/compras`;
    return this.http.get<BibliotecaResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtiene solo los juegos prestados de un usuario
   */
  obtenerJuegosPrestados(idUsuario: number): Observable<BibliotecaResponse[]> {
    const url = `${this.restConstants.getApiURL()}biblioteca/usuario/${idUsuario}/prestamos`;
    return this.http.get<BibliotecaResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Busca juegos en la biblioteca por título
   */
  buscarEnBiblioteca(idUsuario: number, titulo: string): Observable<BibliotecaResponse[]> {
    const url = `${this.restConstants.getApiURL()}biblioteca/usuario/${idUsuario}/buscar`;
    const params = { titulo: titulo };
    
    return this.http.get<BibliotecaResponse[]>(url, { params })
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Verifica si un usuario tiene un juego específico en su biblioteca
   */
  verificarJuegoEnBiblioteca(idUsuario: number, idVideojuego: number): Observable<boolean> {
    const url = `${this.restConstants.getApiURL()}biblioteca/usuario/${idUsuario}/verificar/${idVideojuego}`;
    
    return this.http.get<VerificacionResponse>(url)
      .pipe(
        map(response => response.tieneJuego),
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtiene la instalación actual del usuario
   */
  obtenerInstalacionActual(idUsuario: number): Observable<InstalacionJuego | null> {
    const url = `${this.restConstants.getApiURL()}instalaciones/juego/usuario/${idUsuario}/actual`;
    
    return this.http.get<InstalacionJuego>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => {
          // Si es 404 (no tiene instalación), devolver null
          if (error.status === 404) {
            return [null];
          }
          return this.handleError(error);
        })
      );
  }

  /**
   * Instalar un juego
   */
  instalarJuego(idBiblioteca: number, idUsuario: number, tipoAdquisicion: 'COMPRA' | 'PRESTAMO'): Observable<InstalacionJuego> {
    const url = `${this.restConstants.getApiURL()}instalaciones/juego`;
    
    const body = {
      id_biblioteca: idBiblioteca,
      id_usuario: idUsuario,
      tipo_adquisicion: tipoAdquisicion
    };
    
    return this.http.post<InstalacionJuego>(url, body)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Desinstalar el juego actual
   */
  desinstalarJuego(idInstalacion: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}instalaciones/juego/${idInstalacion}`;
    
    return this.http.delete(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Obtener juegos disponibles para préstamo del grupo
   */
  obtenerJuegosDisponiblesPrestamo(idUsuario: number, idGrupo: number): Observable<BibliotecaResponse[]> {
    const url = `${this.restConstants.getApiURL()}biblioteca/grupo/${idGrupo}/disponibles-prestamo/${idUsuario}`;
    
    return this.http.get<BibliotecaResponse[]>(url)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Solicitar préstamo de un juego del grupo
   */
  solicitarPrestamo(idUsuario: number, idVideojuego: number, idUsuarioPropietario: number): Observable<BibliotecaResponse> {
    const url = `${this.restConstants.getApiURL()}biblioteca/prestamo`;
    
    const body = {
      id_usuario_solicitante: idUsuario,
      id_videojuego: idVideojuego,
      id_usuario_propietario: idUsuarioPropietario
    };
    
    return this.http.post<BibliotecaResponse>(url, body)
      .pipe(
        catchError((error: HttpErrorResponse) => this.handleError(error))
      );
  }

  /**
   * Manejo de errores HTTP
   */
  private handleError(error: HttpErrorResponse): Observable<never> {
    console.log('Error en BibliotecaService:', error);
    
    let errorMessage = 'Error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      const serverError = this.extractErrorMessage(error);
      
      if (error.status === 400) {
        errorMessage = serverError || 'Datos inválidos';
      } else if (error.status === 404) {
        errorMessage = serverError || 'Recurso no encontrado';
      } else if (error.status === 409) {
        errorMessage = serverError || 'Conflicto (ya existe)';
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