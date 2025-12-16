/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package videojuego.services;

import db.MultimediaDB;
import db.VideojuegoDB;
import exceptions.EntityNotFoundException;
import exceptions.MultimediaDataInvalidException;
import java.util.Base64;
import java.util.List;
import videojuego.dtos.NewMultimediaRequest;
import videojuego.models.Multimedia;

/**
 *
 * @author andy
 */
public class MultimediaCrudService {
    
    private final MultimediaDB multimediaDB;
    private final VideojuegoDB videojuegoDB;
    
    public MultimediaCrudService() {
        this.multimediaDB = new MultimediaDB();
        this.videojuegoDB = new VideojuegoDB();
    }
    
    public List<Multimedia> getAllMultimedia() {
        return multimediaDB.getAllMultimedia();
    }
    
    public List<Multimedia> getMultimediaByVideojuego(int idVideojuego) throws EntityNotFoundException {
        // Verificar que el videojuego existe
        if (videojuegoDB.getVideojuegoById(idVideojuego, false) == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + idVideojuego);
        }
        
        return multimediaDB.getMultimediaByVideojuego(idVideojuego);
    }
    
    public Multimedia getMultimediaById(int id) throws EntityNotFoundException {
        Multimedia multimedia = multimediaDB.getMultimediaById(id);
        if (multimedia == null) {
            throw new EntityNotFoundException("Multimedia no encontrada con ID: " + id);
        }
        return multimedia;
    }
    
    private void validarDatosMultimedia(NewMultimediaRequest request) throws MultimediaDataInvalidException {
        if (request.getImagenBase64() == null || request.getImagenBase64().trim().isEmpty()) {
            throw new MultimediaDataInvalidException("La imagen en Base64 es requerida");
        }
        
        // Validar que es un Base64 válido, que la infromacion llegue adecuada
        try {
            Base64.getDecoder().decode(request.getImagenBase64());
        } catch (IllegalArgumentException e) {
            throw new MultimediaDataInvalidException("La imagen no es un Base64 válido");
        }
        
        // Validar tamaño máximo
        byte[] imagenBytes = Base64.getDecoder().decode(request.getImagenBase64());
        if (imagenBytes.length > 5 * 1024 * 1024) { // 5MB
            throw new MultimediaDataInvalidException("La imagen no puede exceder 5MB");
        }
        
    }
    
    public Multimedia createMultimedia(NewMultimediaRequest request) 
            throws MultimediaDataInvalidException, EntityNotFoundException {
        
        
        validarDatosMultimedia(request);
        
        // Verificar que el videojuego existe
        if (videojuegoDB.getVideojuegoById(request.getId_videojuego(), false) == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + request.getId_videojuego());
        }
        
        // Limitar cantidad de imágenes por videojuego (ejemplo: máximo 10)
        int cantidadActual = multimediaDB.countMultimediaByVideojuego(request.getId_videojuego());
        if (cantidadActual >= 10) {
            throw new MultimediaDataInvalidException("Máximo 10 imágenes por videojuego");
        }
        
        // Convertir Base64 a byte[]
        byte[] imagenBytes = Base64.getDecoder().decode(request.getImagenBase64());
        
        
        Multimedia nuevaMultimedia = new Multimedia();
        nuevaMultimedia.setId_videojuego(request.getId_videojuego());
        nuevaMultimedia.setImagen(imagenBytes);
        
        return multimediaDB.createMultimedia(nuevaMultimedia);
    }
    
    public Multimedia updateMultimedia(int id, NewMultimediaRequest request) 
            throws EntityNotFoundException, MultimediaDataInvalidException {
        
        
        Multimedia multimediaExistente = getMultimediaById(id);
        
        
        validarDatosMultimedia(request);
        
        
        if (multimediaExistente.getId_videojuego() != request.getId_videojuego()) {
            if (videojuegoDB.getVideojuegoById(request.getId_videojuego(), false) == null) {
                throw new EntityNotFoundException("Videojuego no encontrado con ID: " + request.getId_videojuego());
            }
        }
        
        // Convertir Base64 a byte[]
        byte[] imagenBytes = Base64.getDecoder().decode(request.getImagenBase64());
        
        
        multimediaExistente.setId_videojuego(request.getId_videojuego());
        multimediaExistente.setImagen(imagenBytes);
        
        
        boolean actualizado = multimediaDB.updateMultimedia(multimediaExistente);
        
        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar la multimedia");
        }
        
        return multimediaExistente;
    }
    
    public void deleteMultimedia(int id) throws EntityNotFoundException {
        
        getMultimediaById(id);
        
        boolean eliminado = multimediaDB.deleteMultimedia(id);
        
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar la multimedia");
        }
    }
    
    public void deleteMultimediaByVideojuego(int idVideojuego) throws EntityNotFoundException {
        
        if (videojuegoDB.getVideojuegoById(idVideojuego, false) == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + idVideojuego);
        }
        
        boolean eliminado = multimediaDB.deleteMultimediaByVideojuego(idVideojuego);
        
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar la multimedia del videojuego");
        }
    }
    
    public int countMultimediaByVideojuego(int idVideojuego) throws EntityNotFoundException {
        
        if (videojuegoDB.getVideojuegoById(idVideojuego, false) == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + idVideojuego);
        }
        
        return multimediaDB.countMultimediaByVideojuego(idVideojuego);
    }
    
    /**
     * Crear múltiples imágenes para un videojuego
     * @param idVideojuego
     * @param requests
     * @return
     * @throws EntityNotFoundException
     * @throws MultimediaDataInvalidException 
     */
    public List<Multimedia> crearMultiplesImagenes(int idVideojuego, List<NewMultimediaRequest> requests) 
            throws EntityNotFoundException, MultimediaDataInvalidException {
        
        // Verificar que el videojuego existe
        if (videojuegoDB.getVideojuegoById(idVideojuego, false) == null) {
            throw new EntityNotFoundException("Videojuego no encontrado con ID: " + idVideojuego);
        }
        
        // Validar cantidad máxima
        int cantidadActual = multimediaDB.countMultimediaByVideojuego(idVideojuego);
        if (cantidadActual + requests.size() > 10) {
            throw new MultimediaDataInvalidException("Máximo 10 imágenes por videojuego. Ya tienes " + cantidadActual);
        }
        
        List<Multimedia> multimediasCreadas = new java.util.ArrayList<>();
        
        for (NewMultimediaRequest request : requests) {
            request.setId_videojuego(idVideojuego);
            Multimedia multimedia = createMultimedia(request);
            multimediasCreadas.add(multimedia);
        }
        
        return multimediasCreadas;
    }
}
