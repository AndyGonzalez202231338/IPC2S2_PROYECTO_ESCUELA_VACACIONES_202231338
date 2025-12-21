import { Empresa } from "../empresa/empresa";
import { Role } from "./role";

export interface User {
  idUsuario: number;
  correo: string;
  rol: Role;
  empresa: Empresa | null;
  nombre: string;
  password: string;
  fecha_nacimiento: string;
  pais : string;
  telefono: string;
  saldo_cartera: number;
  avatar: Uint8Array | null;
}
