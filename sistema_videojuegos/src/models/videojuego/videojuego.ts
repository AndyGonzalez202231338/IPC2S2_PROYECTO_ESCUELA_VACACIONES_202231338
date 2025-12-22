export interface Videojuego {
  id_videojuego: number;
  id_empresa: number;
  titulo: string;
  descripcion: string;
  recursos_minimos: string;
  precio: number;
  clasificacion_edad: string;
  fecha_lanzamiento: string;
  comentarios_bloqueados: boolean;
  categorias: Categoria[];
}

export interface Categoria {
  id_categoria: number;
  nombre: string;
  descripcion?: string;
}

export interface NewVideojuegoRequest {
  id_empresa: number;
  titulo: string;
  descripcion: string;
  recursos_minimos: string;
  precio: number;
  clasificacion_edad: string;
  fecha_lanzamiento: string;
  comentarios_bloqueados?: boolean;
}