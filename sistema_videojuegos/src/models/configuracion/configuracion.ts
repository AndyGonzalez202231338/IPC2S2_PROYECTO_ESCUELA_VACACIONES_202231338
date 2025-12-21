export interface Configuracion {
  id_configuracion: number;
  configuracion: string;
  valor: string;
  descripcion: string;
  fecha_inicio: string;
  fecha_final: string | null;
}

export interface UpdateConfiguracionRequest {
  valor: string;
  fecha_final: string | null;
}