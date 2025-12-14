/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package empresa.services;

import db.EmpresaDB;
import empresa.dtos.NewEmpresaRequest;
import empresa.dtos.UpdateEmpresaRequest;
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
    
    /**
     * Actualiza una empresa existente
     * Solo actualiza nombre y descripción, no requiere información de usuario
     * @param id
     * @param empresaRequest
     * @return
     * @throws EntityNotFoundException
     * @throws EmpresaDataInvalidException
     * @throws EntityAlreadyExistsException 
     */
    public Empresa updateEmpresa(int id, UpdateEmpresaRequest empresaRequest) 
            throws EntityNotFoundException, EmpresaDataInvalidException, EntityAlreadyExistsException {
        
        System.out.println("Actualizando empresa ID: " + id + " con nombre: " + empresaRequest.getNombre());
        
        // 1. Verificar que la empresa existe
        Empresa empresaExistente = getEmpresaById(id);
        
        // 2. Validar datos básicos
        if (empresaRequest.getNombre() == null || empresaRequest.getNombre().trim().isEmpty()) {
            throw new EmpresaDataInvalidException("El nombre de la empresa es requerido");
        }
        
        if (empresaRequest.getDescripcion() == null || empresaRequest.getDescripcion().trim().isEmpty()) {
            throw new EmpresaDataInvalidException("La descripción de la empresa es requerida");
        }
        
        // 3. Validar que el nuevo nombre sea único (solo si cambió)
        String nuevoNombre = empresaRequest.getNombre().trim();
        String descripcion = empresaRequest.getDescripcion().trim();
        
        if (!empresaExistente.getNombre().equals(nuevoNombre)) {
            validarNombreUnicoParaActualizacion(nuevoNombre, id);
        }
        
        // 4. Actualizar los datos de la empresa
        empresaExistente.setNombre(nuevoNombre);
        empresaExistente.setDescripcion(descripcion);
        
        // 5. Guardar en la base de datos
        boolean actualizado = empresaDB.updateEmpresa(empresaExistente);
        
        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar la empresa en la base de datos");
        }
        
        System.out.println("Empresa actualizada exitosamente: " + empresaExistente.getNombre());
        return empresaExistente;
    }

    
    /**
     * Valida que el nombre sea único para actualización (excluyendo la empresa actual)
     * @param nombre
     * @param idEmpresaActual
     * @throws EntityAlreadyExistsException 
     */
    private void validarNombreUnicoParaActualizacion(String nombre, int idEmpresaActual) 
            throws EntityAlreadyExistsException {
        if (nombre == null || nombre.trim().isEmpty()) {
            return;
        }
        
        String nombreNormalizado = nombre.trim();
        
        if (empresaDB.existeEmpresaPorNombreExcluyendoId(nombreNormalizado, idEmpresaActual)) {
            throw new EntityAlreadyExistsException(
                "Ya existe otra empresa con el nombre: '" + nombreNormalizado + "'. " +
                "Por favor, elige un nombre diferente."
            );
        }
    }
    
    public void deleteEmpresa(int id) throws EntityNotFoundException {
        // Verificar que la empresa existe
        getEmpresaById(id);
        
        // Eliminar empresa
        //empresaDB.deleteEmpresa(id);
        
    }
}
