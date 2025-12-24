// services/Multimedia/multimedia.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { RestConstants } from '../../shared/rest-appi/rest-constants';
import { MultimediaResponse, NewMultimediaRequest } from '../../models/videojuego/multimedia';


@Injectable({
  providedIn: 'root'
})
export class MultimediaService {
  restConstants = new RestConstants();

  constructor(private http: HttpClient) { }

  crearMultiplesImagenes(idVideojuego: number, requests: NewMultimediaRequest[]): Observable<any> {
    const url = `${this.restConstants.getApiURL()}multimedia/videojuego/${idVideojuego}/multiple`;
    return this.http.post(url, requests);
  }

    // Obtener imágenes de un videojuego
  getMultimediaByVideojuego(idVideojuego: number): Observable<MultimediaResponse[]> {
    const url = `${this.restConstants.getApiURL()}multimedia/videojuego/${idVideojuego}`;
    return this.http.get<MultimediaResponse[]>(url);
  }

  // Crear imagen individual
  createMultimedia(request: NewMultimediaRequest): Observable<any> {
    const url = `${this.restConstants.getApiURL()}multimedia`;
    return this.http.post(url, request);
  }

  // Eliminar imagen
  deleteMultimedia(id: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}multimedia/${id}`;
    return this.http.delete(url);
  }

  // Eliminar todas las imágenes de un videojuego
  deleteMultimediaByVideojuego(idVideojuego: number): Observable<any> {
    const url = `${this.restConstants.getApiURL()}multimedia/videojuego/${idVideojuego}`;
    return this.http.delete(url);
  }

}