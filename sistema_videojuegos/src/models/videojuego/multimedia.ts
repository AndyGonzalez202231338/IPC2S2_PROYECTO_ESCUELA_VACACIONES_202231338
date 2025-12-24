export interface NewMultimediaRequest {
  id_videojuego: number;
  imagenBase64: string;
}

export interface MultimediaResponse {
  id_multimedia: number;
  id_videojuego: number;
  imagenBase64: string;
  fecha_creacion: string;
}