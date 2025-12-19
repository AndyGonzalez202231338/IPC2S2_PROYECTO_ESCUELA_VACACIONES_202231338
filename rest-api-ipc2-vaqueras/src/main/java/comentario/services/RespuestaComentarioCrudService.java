package comentario.services;


import comentario.dto.RespuestaComentarioRequest;
import comentario.model.RespuestaComentario;
import db.RespuestaComentarioDB;
import exceptions.EntityNotFoundException;
import exceptions.RespuestaComentarioDataInvalidException;
import java.time.LocalDateTime;
import java.util.List;

public class RespuestaComentarioCrudService {

    private final RespuestaComentarioDB respuestaComentarioDB;

    public RespuestaComentarioCrudService() {
        this.respuestaComentarioDB = new RespuestaComentarioDB();
    }

    /**
     * Obtiene todas las respuestas de un comentario padre
     * @param idComentarioPadre
     * @return 
     */
    public List<RespuestaComentario> getRespuestasPorComentarioPadre(int idComentarioPadre) {
        if (idComentarioPadre <= 0) {
            throw new IllegalArgumentException("El ID del comentario padre debe ser mayor a 0");
        }
        return respuestaComentarioDB.getRespuestasPorComentarioPadre(idComentarioPadre);
    }

    /**
     * Obtiene una respuesta por ID
     * @param id
     * @return
     * @throws EntityNotFoundException 
     */
    public RespuestaComentario getRespuestaById(int id) throws EntityNotFoundException {
        RespuestaComentario respuesta = respuestaComentarioDB.getRespuestaById(id);
        if (respuesta == null) {
            throw new EntityNotFoundException("Respuesta no encontrada con ID: " + id);
        }
        return respuesta;
    }
    
    /**
     * Obtiene todas las respuestas de un usuario
     * @param idUsuario
     * @return 
     */
    public List<RespuestaComentario> getRespuestasPorUsuario(int idUsuario) {
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("El ID de usuario debe ser mayor a 0");
        }
        return respuestaComentarioDB.getRespuestasPorUsuario(idUsuario);
    }

    /**
     * Obtiene todas las respuestas de un videojuego
     * @param idVideojuego
     * @return 
     */
    public List<RespuestaComentario> getRespuestasPorVideojuego(int idVideojuego) {
        if (idVideojuego <= 0) {
            throw new IllegalArgumentException("El ID del videojuego debe ser mayor a 0");
        }
        return respuestaComentarioDB.getRespuestasPorVideojuego(idVideojuego);
    }

    /**
     * Valida los datos de entrada para una respuesta
     * @param idComentarioPadre
     * @param idUsuario
     * @param comentario
     * @throws RespuestaComentarioDataInvalidException 
     */
    private void validarDatosRespuesta(int idComentarioPadre, int idUsuario, String comentario) 
            throws RespuestaComentarioDataInvalidException {
        
        if (idComentarioPadre <= 0) {
            throw new RespuestaComentarioDataInvalidException("El ID del comentario padre debe ser mayor a 0");
        }

        if (idUsuario <= 0) {
            throw new RespuestaComentarioDataInvalidException("El ID de usuario debe ser mayor a 0");
        }

        if (comentario == null || comentario.trim().isEmpty()) {
            throw new RespuestaComentarioDataInvalidException("La respuesta no puede estar vacía");
        }

        if (comentario.trim().length() < 3) {
            throw new RespuestaComentarioDataInvalidException("La respuesta debe tener al menos 3 caracteres");
        }

        if (comentario.trim().length() > 1000) {
            throw new RespuestaComentarioDataInvalidException("La respuesta no puede exceder 1000 caracteres");
        }
        
        if (!respuestaComentarioDB.existeComentarioPadre(idComentarioPadre)) {
            throw new RespuestaComentarioDataInvalidException("El comentario padre no existe");
        }
    }

    /**
     * Crea una nueva respuesta a comentario
     * @param respuestaRequest
     * @return
     * @throws RespuestaComentarioDataInvalidException 
     */
    public RespuestaComentario createRespuestaComentario(RespuestaComentarioRequest respuestaRequest)
            throws RespuestaComentarioDataInvalidException {

        validarDatosRespuesta(
            respuestaRequest.getId_comentario_padre(),
            respuestaRequest.getId_usuario(),
            respuestaRequest.getComentario()
        );


        RespuestaComentario respuesta = new RespuestaComentario(
            respuestaRequest.getId_comentario_padre(),
            respuestaRequest.getId_usuario(),
            respuestaRequest.getComentario().trim(),
            LocalDateTime.now()
        );

        RespuestaComentario respuestaCreada = respuestaComentarioDB.createRespuestaComentario(respuesta);

        System.out.println("Respuesta creada exitosamente para comentario padre: " + 
            respuestaRequest.getId_comentario_padre() + 
            ", usuario: " + respuestaRequest.getId_usuario());
        
        return respuestaCreada;
    }


    

}