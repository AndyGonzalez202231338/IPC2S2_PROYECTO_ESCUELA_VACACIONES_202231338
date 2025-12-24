import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { Categoria, NewCategoriaRequest, VerificarNombreResponse } from '../../models/videojuego/categoria';

@Injectable({
  providedIn: 'root'
})
export class CategoriaService {
  private apiUrl: string;

  constructor(private http: HttpClient) {
    const restConstants = new RestConstants();
    this.apiUrl = restConstants.getApiURL();
  }

  /**
   * Obtiene todas las categorías
   */
  getAllCategorias(): Observable<Categoria[]> {
    const url = `${this.apiUrl}categorias`;
    console.log('URL para obtener categorías:', url);
    
    return this.http.get<Categoria[]>(url).pipe(
      catchError(this.handleError)
    );
  }

  //categorias de un videojuego por id
  getCategoriasByVideojuegoId(videojuegoId: number): Observable<Categoria[]> {
    const url = `${this.apiUrl}videojuegos/${videojuegoId}/categorias`;
    console.log('URL para obtener categorías por videojuego ID:', url);
    
    return this.http.get<Categoria[]>(url).pipe(
      catchError(this.handleError)
    );
  } 

  /**
   * Obtiene una categoría por ID
   */
  getCategoriaById(id: number): Observable<Categoria> {
    const url = `${this.apiUrl}categorias/${id}`;
    console.log('URL para obtener categoría:', url);
    
    return this.http.get<Categoria>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Crea una nueva categoría
   */
  createCategoria(categoriaData: NewCategoriaRequest): Observable<Categoria> {
    const url = `${this.apiUrl}categorias`;
    console.log('URL para crear categoría:', url);
    console.log('Datos a enviar:', categoriaData);
    
    return this.http.post<Categoria>(url, categoriaData).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Actualiza una categoría existente
   */
  updateCategoria(id: number, categoriaData: NewCategoriaRequest): Observable<Categoria> {
    const url = `${this.apiUrl}categorias/${id}`;
    console.log('URL para actualizar categoría:', url);
    console.log('Datos a enviar:', categoriaData);
    
    return this.http.put<Categoria>(url, categoriaData).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Elimina una categoría
   */
  deleteCategoria(id: number): Observable<any> {
    const url = `${this.apiUrl}categorias/${id}`;
    console.log('URL para eliminar categoría:', url);
    
    return this.http.delete<any>(url).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Verifica si un nombre de categoría está disponible
   */
  verificarNombreDisponible(nombre: string): Observable<VerificarNombreResponse> {
    const url = `${this.apiUrl}categorias/verificar-nombre`;
    console.log('URL para verificar nombre:', url);
    
    const params = new HttpParams().set('nombre', nombre);
    
    return this.http.get<VerificarNombreResponse>(url, { params }).pipe(
      catchError(this.handleError)
    );
  }

  /**
   * Manejo de errores personalizado
   */
  private handleError(error: HttpErrorResponse) {
    console.error('Error en CategoriaService:', error);
    
    let errorMessage = 'Ocurrió un error desconocido';
    
    if (error.error instanceof ErrorEvent) {
      errorMessage = `Error: ${error.error.message}`;
    } else {
      
      if (error.error && typeof error.error === 'string') {
        try {
          const errorObj = JSON.parse(error.error);
          errorMessage = errorObj.error || 'Error del servidor';
        } catch (e) {
          errorMessage = error.error;
        }
      } else if (error.error && error.error.error) {
        errorMessage = error.error.error;
      } else {
        
        if (error.status === 0) {
          errorMessage = 'No se pudo conectar con el servidor.';
        } else if (error.status === 400) {
          errorMessage = 'Datos inválidos enviados al servidor.';
        } else if (error.status === 404) {
          errorMessage = 'Recurso no encontrado.';
        } else if (error.status === 409) {
          errorMessage = 'El recurso ya existe.';
        } else if (error.status === 500) {
          errorMessage = 'Error interno del servidor. Por favor intente más tarde.';
        } else {
          errorMessage = `Error ${error.status}: ${error.statusText}`;
        }
      }
    }
    
    console.log('Mensaje de error generado:', errorMessage);
    return throwError(() => new Error(errorMessage));
  }
}