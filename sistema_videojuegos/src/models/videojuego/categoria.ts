export interface Categoria {
  id_categoria: number;
  nombre: string;
  descripcion: string;
}

export interface NewCategoriaRequest {
  nombre: string;
  descripcion: string;
}

export interface VerificarNombreResponse {
  disponible: boolean;
  nombre: string;
}