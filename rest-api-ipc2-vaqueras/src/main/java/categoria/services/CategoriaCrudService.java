/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package categoria.services;

import categoria.dtos.NewCategoriaRequest;
import categoria.models.Categoria;
import db.CategoriaDB;
import exceptions.CategoriaDataInvalidException;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;
import java.util.List;

/**
 *
 * @author andy
 */
public class CategoriaCrudService {

    private final CategoriaDB categoriaDB;

    public CategoriaCrudService() {
        this.categoriaDB = new CategoriaDB();
    }

    public List<Categoria> getAllCategorias() {
        return categoriaDB.getAllCategorias();
    }

    public Categoria getCategoriaById(int id) throws EntityNotFoundException {
        Categoria categoria = categoriaDB.getCategoriaById(id);
        if (categoria == null) {
            throw new EntityNotFoundException("Categoría no encontrada con ID: " + id);
        }
        return categoria;
    }

    private void validarDatosCategoria(String nombre, String descripcion) throws CategoriaDataInvalidException {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new CategoriaDataInvalidException("El nombre de la categoría es requerido");
        }

        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new CategoriaDataInvalidException("La descripción de la categoría es requerida");
        }

        if (nombre.trim().length() > 50) {
            throw new CategoriaDataInvalidException("El nombre no puede exceder 50 caracteres");
        }
    }

    public Categoria createCategoria(NewCategoriaRequest categoriaRequest)
            throws CategoriaDataInvalidException, EntityAlreadyExistsException {

        // Validar datos de entrada
        validarDatosCategoria(categoriaRequest.getNombre(), categoriaRequest.getDescripcion());

        // Validar que el nombre sea único
        String nombreNormalizado = categoriaRequest.getNombre().trim();
        if (categoriaDB.existeCategoriaPorNombre(nombreNormalizado)) {
            throw new EntityAlreadyExistsException(
                    "Ya existe una categoría con el nombre: '" + nombreNormalizado + "'. "
                    + "Por favor, elige un nombre diferente."
            );
        }

        Categoria nuevaCategoria = new Categoria(
                0,
                nombreNormalizado,
                categoriaRequest.getDescripcion().trim()
        );

        Categoria categoriaCreada = categoriaDB.createCategoria(nuevaCategoria);

        System.out.println("Categoría creada exitosamente con ID: " + categoriaCreada.getId_categoria());
        return categoriaCreada;
    }

    public Categoria updateCategoria(int id, NewCategoriaRequest categoriaRequest)
            throws EntityNotFoundException, CategoriaDataInvalidException, EntityAlreadyExistsException {

        System.out.println("Actualizando categoría ID: " + id + " con nombre: " + categoriaRequest.getNombre());

        // Verificar que la categoría existe
        Categoria categoriaExistente = getCategoriaById(id);

        // Validar datos básicos
        validarDatosCategoria(categoriaRequest.getNombre(), categoriaRequest.getDescripcion());

        // Validar que el nuevo nombre sea único 
        String nuevoNombre = categoriaRequest.getNombre().trim();
        if (!categoriaExistente.getNombre().equals(nuevoNombre)) {
            if (categoriaDB.existeCategoriaPorNombreExcluyendoId(nuevoNombre, id)) {
                throw new EntityAlreadyExistsException(
                        "Ya existe otra categoría con el nombre: '" + nuevoNombre + "'. "
                        + "Por favor, elige un nombre diferente."
                );
            }
        }

        categoriaExistente.setNombre(nuevoNombre);
        categoriaExistente.setDescripcion(categoriaRequest.getDescripcion().trim());

        boolean actualizado = categoriaDB.updateCategoria(categoriaExistente);

        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar la categoría en la base de datos");
        }

        System.out.println("Categoría actualizada exitosamente: " + categoriaExistente.getNombre());
        return categoriaExistente;
    }

    public void deleteCategoria(int id) throws EntityNotFoundException {
        System.out.println("Eliminando categoría ID: " + id);

        // Verificar que la categoría existe
        getCategoriaById(id);

        boolean eliminado = categoriaDB.deleteCategoria(id);

        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar la categoría");
        }

        System.out.println("Categoría eliminada exitosamente");
    }

    public boolean isNombreCategoriaDisponible(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            return false;
        }
        return !categoriaDB.existeCategoriaPorNombre(nombre.trim());
    }

}
