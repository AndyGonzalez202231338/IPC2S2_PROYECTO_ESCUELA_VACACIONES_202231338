/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.services;

import db.CategoriaDB;
import categoria.models.Categoria;
import exceptions.EntityAlreadyExistsException;
import exceptions.EntityNotFoundException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import db.VideojuegoDB;
import exceptions.VideojuegoDataInvalidException;
import videojuego.dtos.NewVideojuegoRequest;
import videojuego.dtos.VideojuegoCategoriaRequest;
import videojuego.models.Videojuego;

/**
 *
 * @author andy
 */
public class VideojuegoCrudService {

    private final VideojuegoDB videojuegoDB;
    private final CategoriaDB categoriaDB;

    public VideojuegoCrudService() {
        this.videojuegoDB = new VideojuegoDB();
        this.categoriaDB = new CategoriaDB();
    }

    public List<Videojuego> getAllVideojuegos(boolean incluirCategorias) {
        return videojuegoDB.getAllVideojuegos(incluirCategorias);
    }

    public Videojuego getVideojuegoById(int id, boolean incluirCategorias) throws EntityNotFoundException {
        Videojuego videojuego = videojuegoDB.getVideojuegoById(id, incluirCategorias);
        if (videojuego == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + id);
        }
        return videojuego;
    }

    public Videojuego getVideojuegoByTitulo(String titulo, boolean incluirCategorias) {
        Videojuego videojuego = videojuegoDB.getVideojuegoByTitulo(titulo, incluirCategorias);
        return videojuego;
    }

    private void validarDatosVideojuego(NewVideojuegoRequest request) throws VideojuegoDataInvalidException {
        // Validar campos obligatorios
        if (request.getTitulo() == null || request.getTitulo().trim().isEmpty()) {
            throw new VideojuegoDataInvalidException("El título del videojuego es requerido");
        }

        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new VideojuegoDataInvalidException("La descripción del videojuego es requerida");
        }

        if (request.getRecursos_minimos() == null || request.getRecursos_minimos().trim().isEmpty()) {
            throw new VideojuegoDataInvalidException("Los recursos mínimos son requeridos");
        }

        if (request.getPrecio() == null || request.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {
            throw new VideojuegoDataInvalidException("El precio debe ser mayor a 0");
        }

        if (request.getClasificacion_edad() == null || !Arrays.asList("E", "T", "M").contains(request.getClasificacion_edad())) {
            throw new VideojuegoDataInvalidException("Clasificación de edad inválida. Valores permitidos: E, T, M");
        }

        if (request.getFecha_lanzamiento() == null) {
            throw new VideojuegoDataInvalidException("La fecha de lanzamiento es requerida");
        }

        // Validar longitudes
        if (request.getTitulo().trim().length() > 200) {
            throw new VideojuegoDataInvalidException("El título no puede exceder 200 caracteres");
        }

        // Validar fecha futura
        /*Date hoy = new Date();
        if (request.getFecha_lanzamiento().before(hoy)) {
            throw new VideojuegoDataInvalidException("La fecha de lanzamiento debe ser futura");
        }*/
    }

    private void validarCategorias(List<Integer> categoriasIds) throws VideojuegoDataInvalidException {
        if (categoriasIds == null || categoriasIds.isEmpty()) {
            throw new VideojuegoDataInvalidException("Debe seleccionar al menos una categoría");
        }

        if (categoriasIds.size() > 5) {
            throw new VideojuegoDataInvalidException("Máximo 5 categorías por videojuego");
        }

        // Verificar que todas las categorías existen
        for (Integer idCategoria : categoriasIds) {
            Categoria categoria = categoriaDB.getCategoriaById(idCategoria);
            if (categoria == null) {
                throw new VideojuegoDataInvalidException("La categoría con ID " + idCategoria + " no existe");
            }
        }
    }

    public Videojuego createVideojuego(NewVideojuegoRequest request)
            throws VideojuegoDataInvalidException, EntityAlreadyExistsException {

        // Validar datos básicos
        validarDatosVideojuego(request);

        // Validar categorías
        validarCategorias(request.getCategorias_ids());

        // Validar que el título sea único para esta empresa
        String tituloNormalizado = request.getTitulo().trim();
        if (videojuegoDB.existeTituloPorEmpresa(tituloNormalizado, request.getId_empresa())) {
            throw new EntityAlreadyExistsException(
                    "Ya existe un videojuego con el título '" + tituloNormalizado
                    + "' para esta empresa. Por favor, elige un título diferente."
            );
        }

        Videojuego nuevoVideojuego = new Videojuego();
        nuevoVideojuego.setId_empresa(request.getId_empresa());
        nuevoVideojuego.setTitulo(tituloNormalizado);
        nuevoVideojuego.setDescripcion(request.getDescripcion().trim());
        nuevoVideojuego.setRecursos_minimos(request.getRecursos_minimos().trim());
        nuevoVideojuego.setPrecio(request.getPrecio());
        nuevoVideojuego.setClasificacion_edad(request.getClasificacion_edad());
        nuevoVideojuego.setFecha_lanzamiento(request.getFecha_lanzamiento());

        // Crear en base de datos
        Videojuego videojuegoCreado = videojuegoDB.createVideojuego(nuevoVideojuego, request.getCategorias_ids());

        // Obtener con categorías
        return videojuegoDB.getVideojuegoById(videojuegoCreado.getId_videojuego(), true);
    }

    public Videojuego updateVideojuego(int id, NewVideojuegoRequest request)
            throws EntityNotFoundException, VideojuegoDataInvalidException, EntityAlreadyExistsException {

        // Verificar que el videojuego existe
        Videojuego videojuegoExistente = getVideojuegoById(id, false);

        // Validar datos básicos
        validarDatosVideojuego(request);

        // Validar categorías
        validarCategorias(request.getCategorias_ids());

        // Validar que el título sea único (excluyendo este videojuego)
        String nuevoTitulo = request.getTitulo().trim();
        if (!videojuegoExistente.getTitulo().equals(nuevoTitulo)
                || videojuegoExistente.getId_empresa() != request.getId_empresa()) {

            if (videojuegoDB.existeTituloPorEmpresa(nuevoTitulo, request.getId_empresa())) {
                throw new EntityAlreadyExistsException(
                        "Ya existe un videojuego con el título '" + nuevoTitulo
                        + "' para esta empresa. Por favor, elige un título diferente."
                );
            }
        }

        videojuegoExistente.setId_empresa(request.getId_empresa());
        videojuegoExistente.setTitulo(nuevoTitulo);
        videojuegoExistente.setDescripcion(request.getDescripcion().trim());
        videojuegoExistente.setRecursos_minimos(request.getRecursos_minimos().trim());
        videojuegoExistente.setPrecio(request.getPrecio());
        videojuegoExistente.setClasificacion_edad(request.getClasificacion_edad());
        videojuegoExistente.setFecha_lanzamiento(request.getFecha_lanzamiento());

        // Actualizar en base de datos
        boolean actualizado = videojuegoDB.updateVideojuego(videojuegoExistente);

        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar el videojuego");
        }

        // Actualizar categorías
        videojuegoDB.actualizarCategoriasVideojuego(id, request.getCategorias_ids());

        // Obtener videojuego actualizado con categorías
        return videojuegoDB.getVideojuegoById(id, true);
    }

    public void deleteVideojuego(int id) throws EntityNotFoundException {
        // Verificar que existe el videojuego
        getVideojuegoById(id, false);

        boolean eliminado = videojuegoDB.deleteVideojuego(id);

        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar el videojuego");
        }
    }

    public Videojuego actualizarCategoriasVideojuego(VideojuegoCategoriaRequest request)
            throws EntityNotFoundException, VideojuegoDataInvalidException {

        // Verificar que el videojuego existe
        getVideojuegoById(request.getId_videojuego(), false);

        // Validar categorías
        validarCategorias(request.getCategorias_ids());

        // Actualizar categorías
        videojuegoDB.actualizarCategoriasVideojuego(request.getId_videojuego(), request.getCategorias_ids());

        // Obtener videojuego actualizado
        return videojuegoDB.getVideojuegoById(request.getId_videojuego(), true);
    }

    public List<Categoria> getCategoriasAprobadas(int idVideojuego) throws EntityNotFoundException {
        // Verificar que el videojuego existe
        getVideojuegoById(idVideojuego, false);

        return videojuegoDB.obtenerCategoriasAprobadas(idVideojuego);
    }

    public boolean actualizarEstadoCategoria(int idVideojuego, int idCategoria, String estado)
            throws EntityNotFoundException {

        // Verificar que el videojuego existe
        getVideojuegoById(idVideojuego, false);

        // Verificar que la categoría existe
        Categoria categoria = categoriaDB.getCategoriaById(idCategoria);
        if (categoria == null) {
            throw new EntityNotFoundException("Categoría no encontrada con ID: " + idCategoria);
        }

        // Validar estado de la cas categoiras relacionadas
        if (!Arrays.asList("PENDIENTE", "APROBADA", "RECHAZADA").contains(estado)) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: PENDIENTE, APROBADA, RECHAZADA");
        }

        return videojuegoDB.actualizarEstadoCategoria(idVideojuego, idCategoria, estado);
    }

    public boolean isTituloDisponible(String titulo, int idEmpresa) {
        if (titulo == null || titulo.trim().isEmpty()) {
            return false;
        }
        return !videojuegoDB.existeTituloPorEmpresa(titulo.trim(), idEmpresa);
    }
    
    public List<Videojuego> getVideojuegosByEmpresa(int idEmpresa, boolean incluirCategorias) {
    return videojuegoDB.getVideojuegosByEmpresa(idEmpresa, incluirCategorias);
}
}
