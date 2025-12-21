import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

import { RestConstants } from "../../shared/rest-appi/rest-constants";
import { Role, User, NewUserRequest } from "../../models/counts/count";

@Injectable({
    providedIn: 'root'
})
export class CountsService {
    private selectedUser: User | null = null;
    restConstants = new RestConstants();

    constructor(private httpClient: HttpClient) { }

    // Crear nuevo usuario (para registro)
    public createNewUser(usuario: any): Observable<void> {
        console.log("Usuario recibido en servicio:", usuario);
        
        // Transformar los datos para que el backend reciba el formato correcto
        const usuarioTransformado = this.transformarUsuarioParaBackend(usuario);
        console.log("Usuario transformado para backend:", usuarioTransformado);
        
        return this.httpClient.post<void>(`${this.restConstants.getApiURL()}users/create`, usuarioTransformado);
    }

    // Actualizar usuario existente
    public updateUser(correo: string, userToUpdate: any): Observable<User> {
        console.log("Updating user:", correo, userToUpdate);
        return this.httpClient.put<User>(`${this.restConstants.getApiURL()}users/${correo}`, userToUpdate);
    }

    // Obtener todos los usuarios
    public getAllUsers(): Observable<User[]> {
        return this.httpClient.get<User[]>(`${this.restConstants.getApiURL()}users`);
    }

    // Obtener usuario por email
    public getUserByEmail(email: string): Observable<User> {
        return this.httpClient.get<User>(`${this.restConstants.getApiURL()}users/${email}`);
    }

    // Obtener usuario por ID
    public getUserById(idUsuario: number): Observable<User> {
        return this.httpClient.get<User>(`${this.restConstants.getApiURL()}users/id/${idUsuario}`);
    }

    // Eliminar usuario
    public deleteUser(email: string): Observable<void> {
        return this.httpClient.delete<void>(`${this.restConstants.getApiURL()}users/${email}`);
    }

    // Obtener todos los roles disponibles
    public getAvailableRoles(): Observable<Role[]> {
        return this.httpClient.get<Role[]>(`${this.restConstants.getApiURL()}roles`);
    }

    setSelectedUser(user: User): void {
        this.selectedUser = user;
    }

    getSelectedUser(): User | null {
        return this.selectedUser;
    }

    clearSelectedUser(): void {
        this.selectedUser = null;
    }

    private transformarUsuarioParaBackend(usuario: any): any {
    console.log("Transformando usuario:", usuario);
    
    let fechaNacimiento = usuario.fecha_nacimiento;
    if (!fechaNacimiento || fechaNacimiento === '' || fechaNacimiento === 'null') {
        fechaNacimiento = null;
    }
    
    const userForBackend = {
        correo: usuario.correo,
        nombre: usuario.nombre,
        password: usuario.password,
        id_rol: usuario.id_rol,
        id_empresa: null,  // Siempre null para nuevo usuario
        fecha_nacimiento: fechaNacimiento, 
        pais: usuario.pais || null,  // null en lugar de string vacío
        telefono: usuario.telefono || null,  // null en lugar de string vacío
        saldo_cartera: 0.00,
        avatar: null
    };
    
    console.log("Usuario para backend:", userForBackend);
    return userForBackend;
}
}