/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.services;

import db.EmpresaDB;
import empresa.dtos.NewEmpresaRequest;
import empresa.models.Empresa;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import exceptions.EmpresaDataInvalidException;
import user.models.Usuario;
import user.services.UsersCrudService;

/**
 *
 * @author andy
 */
public class EmpresaCreator {
    
    public Empresa createEmpresa(NewEmpresaRequest empresaRequest) throws EmpresaDataInvalidException, EntityNotFoundException, EntityAlreadyExistsException {
        System.out.println("nombre en empresacreator"+ empresaRequest.getNombre());
    if (empresaRequest.getNombre() == null || empresaRequest.getNombre().trim().isEmpty()) {
        System.out.println("ERROR: Nombre está vacío o null");
        throw new EmpresaDataInvalidException("El nombre de la empresa es requerido");
    }
    
    EmpresaCrudService empresaService = new EmpresaCrudService();
    empresaService.validarNombreUnicoParaCreacion(empresaRequest.getNombre());
    
    
    UsersCrudService usersService = new UsersCrudService();
    Usuario administrador = usersService.getUserById(empresaRequest.getId_administrador());
    
    if (administrador.getId_rol() != 2) {
        throw new EmpresaDataInvalidException("Usuario no es administrador");
    }
    
    if (administrador.getId_empresa() > 0) {
        throw new EmpresaDataInvalidException("Administrador ya tiene empresa");
    }
    
    //Crear empresa
    EmpresaDB empresaDB = new EmpresaDB();
    Empresa nuevaEmpresa = new Empresa(
        0,
        empresaRequest.getNombre(),
        empresaRequest.getDescripcion()
    );
    
    Empresa empresaCreada = empresaDB.createEmpresa(nuevaEmpresa);
    
    //Asignar empresa al administrador (solo actualiza usuario.id_empresa)
    usersService.asignarEmpresaAUsuario(
        administrador.getIdUsuario(), 
        empresaCreada.getId_empresa()
    );
    
    
    return empresaCreada;
}
}

