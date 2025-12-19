package comentario.services;

import comentario.dto.ComentarioRequest;
import comentario.model.Comentario;
import db.ComentarioDB;
import exceptions.ComentarioDataInvalidException;
import exceptions.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public class ComentarioCrudService {

    private final ComentarioDB comentarioDB;

    public ComentarioCrudService() {
        this.comentarioDB = new ComentarioDB();
    }

    /**
     * Obtiene todos los comentarios de un usuario
     * @param idUsuario
     * @return 
     */
    public List<Comentario> getComentariosPorUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a 0");
        }
        return comentarioDB.getComentariosPorUsuario(idUsuario);
    }

    /**
     * Obtiene un comentario por ID
     * @param id
     * @return
     * @throws EntityNotFoundException 
     */
    public Comentario getComentarioById(int id) throws EntityNotFoundException {
        Comentario comentario = comentarioDB.getComentarioById(id);
        if (comentario == null) {
            throw new EntityNotFoundException("Comentario no encontrado con ID: " + id);
        }
        return comentario;
    }
    
    /**
     * Obtiene todos los comentarios de una biblioteca específica
     * @param idBiblioteca
     * @return 
     */
    public List<Comentario> getComentariosPorBiblioteca(int idBiblioteca) {
        if (idBiblioteca <= 0) {
            throw new IllegalArgumentException("El ID de biblioteca debe ser mayor a 0");
        }
        return comentarioDB.getComentariosPorBiblioteca(idBiblioteca);
    }

    /**
     * Obtiene todos los comentarios de un videojuego
     * @param idVideojuego
     * @return 
     */
    public List<Comentario> getComentariosPorVideojuego(int idVideojuego) {
        if (idVideojuego <= 0) {
            throw new IllegalArgumentException("El ID del videojuego debe ser mayor a 0");
        }
        return comentarioDB.getComentariosPorVideojuego(idVideojuego);
    }
    
    /**
     * Obtiene los comentarios de un usuario específico para un videojuego específico
     * @param idUsuario
     * @param idVideojuego
     * @return 
     */
    public List<Comentario> getComentariosPorUsuarioYVideojuego(int idUsuario, int idVideojuego) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a 0");
        }
        if (idVideojuego <= 0) {
            throw new IllegalArgumentException("El ID del videojuego debe ser mayor a 0");
        }
        return comentarioDB.getComentariosPorUsuarioYVideojuego(idUsuario, idVideojuego);
    }

    /**
     * Valida los datos de entrada para un comentario
     * @param idUsuario
     * @param idBiblioteca
     * @param comentario
     * @throws ComentarioDataInvalidException 
     */
    private void validarDatosComentario(int idUsuario, int idBiblioteca, String comentario) 
            throws ComentarioDataInvalidException {
        
        if (idUsuario <= 0) {
            throw new ComentarioDataInvalidException("El ID de usuario debe ser mayor a 0");
        }

        if (idBiblioteca <= 0) {
            throw new ComentarioDataInvalidException("El ID de biblioteca debe ser mayor a 0");
        }

        if (comentario == null || comentario.trim().isEmpty()) {
            throw new ComentarioDataInvalidException("El comentario no puede estar vacío");
        }

        if (comentario.trim().length() < 3) {
            throw new ComentarioDataInvalidException("El comentario debe tener al menos 3 caracteres");
        }

        if (comentario.trim().length() > 1000) {
            throw new ComentarioDataInvalidException("El comentario no puede exceder 1000 caracteres");
        }
    }

    /**
     * Crea un nuevo comentario
     * @param comentarioRequest
     * @return
     * @throws ComentarioDataInvalidException 
     */
    public Comentario createComentario(ComentarioRequest comentarioRequest)
            throws ComentarioDataInvalidException {

        validarDatosComentario(
            comentarioRequest.getId_usuario(),
            comentarioRequest.getId_biblioteca(),
            comentarioRequest.getComentario()
        );

        Comentario comentario = new Comentario(
            comentarioRequest.getId_usuario(),
            comentarioRequest.getId_biblioteca(),
            comentarioRequest.getComentario().trim(),
            LocalDateTime.now()
        );

        Comentario comentarioCreado = comentarioDB.createComentario(comentario);

        System.out.println("Comentario creado exitosamente para usuario: " + 
            comentarioRequest.getId_usuario() + 
            ", biblioteca: " + comentarioRequest.getId_biblioteca());
        
        return comentarioCreado;
    }

    /**
     * Obtiene el total de comentarios de un videojuego
     * @param idVideojuego
     * @return 
     */
    public int getTotalComentariosPorVideojuego(int idVideojuego) {
        if (idVideojuego <= 0) {
            throw new IllegalArgumentException("El ID del videojuego debe ser mayor a 0");
        }
        return comentarioDB.getTotalComentariosPorVideojuego(idVideojuego);
    }
}