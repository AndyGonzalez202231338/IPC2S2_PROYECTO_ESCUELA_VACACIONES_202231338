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
import java.util.List;

/**
 *
 * @author andy
 */
public class EmpresaCrudService {
    
    private final EmpresaDB empresaDB;
    
    public EmpresaCrudService() {
        this.empresaDB = new EmpresaDB();
    }
    
    public List<Empresa> getAllEmpresas() {
        return empresaDB.getAllEmpresas();
        
    }
    
    public Empresa getEmpresaById(int id) throws EntityNotFoundException {
        Empresa empresa = empresaDB.getEmpresaById(id);
        if (empresa == null) {
            throw new EntityNotFoundException("Empresa no encontrada con ID: " + id);
        }
        return empresa;
    }
    


    public void validarNombreUnicoParaCreacion(String nombre) throws EntityAlreadyExistsException {
        String nombreNormalizado = nombre.trim();
        
        if (empresaDB.existeEmpresaPorNombre(nombreNormalizado)) {
            throw new EntityAlreadyExistsException(
                "Ya existe una empresa con el nombre: '" + nombreNormalizado + "'. " +
                "Por favor, elige un nombre diferente."
            );
        }
    }
    
    public Empresa updateEmpresa(int id, NewEmpresaRequest empresaRequest) 
            throws EntityNotFoundException, EmpresaDataInvalidException {
        
        // Verificar que la empresa existe
        Empresa empresaExistente = getEmpresaById(id);
        
        if (empresaRequest.getNombre() == null || empresaRequest.getNombre().trim().isEmpty()) {
            throw new EmpresaDataInvalidException("El nombre de la empresa es requerido");
        }
        
        if (empresaRequest.getDescripcion() == null || empresaRequest.getDescripcion().trim().isEmpty()) {
            throw new EmpresaDataInvalidException("La descripción de la empresa es requerida");
        }
        
        // Actualizar empresa
        empresaExistente.setNombre(empresaRequest.getNombre());
        empresaExistente.setDescripcion(empresaRequest.getDescripcion());
        
        ///return empresaDB.updateEmpresa(empresaExistente);
        return null;
    }
    
    public void deleteEmpresa(int id) throws EntityNotFoundException {
        // Verificar que la empresa existe
        getEmpresaById(id);
        
        // Eliminar empresa
        //empresaDB.deleteEmpresa(id);
        
    }
}
