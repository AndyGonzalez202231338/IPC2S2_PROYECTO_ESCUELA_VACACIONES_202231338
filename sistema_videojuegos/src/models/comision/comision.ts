export interface Comision {
  id_comision: number;
  id_empresa: number;
  porcentaje: number;
  fecha_inicio: string; 
  fecha_final: string | null;
  tipo_comision: 'global' | 'especifica';
}