package comentario.services;


import comentario.dto.CalificacionRequest;
import comentario.model.Calificacion;
import db.CalificacionDB;
import exceptions.CalificacionDataInvalidException;
import exceptions.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;

public class CalificacionCrudService {

    private final CalificacionDB calificacionDB;

    public CalificacionCrudService() {
        this.calificacionDB = new CalificacionDB();
    }

    /**
     * Obtiene todas las calificaciones de una biblioteca
     * @param idBiblioteca
     * @return 
     */
    public List<Calificacion> getCalificacionesPorBiblioteca(int idBiblioteca) {
        return calificacionDB.getCalificacionesPorBiblioteca(idBiblioteca);
    }
    
    /**
     * Obtiene todas las calificaciones a un videojuego
     * @param idVideojuego
     * @return 
     */
    public List<Calificacion> getCalificacionesPorVideojuego(int idVideojuego) {
        if (idVideojuego <= 0) {
            throw new IllegalArgumentException("El ID del videojuego debe ser mayor a 0");
        }
        return calificacionDB.getCalificacionesPorVideojuego(idVideojuego);
    }

    /**
     * Obtiene una calificación realizada, por medio de ID
     * @param id
     * @return
     * @throws EntityNotFoundException 
     */
    public Calificacion getCalificacionById(int id) throws EntityNotFoundException {
        Calificacion calificacion = calificacionDB.getCalificacionById(id);
        if (calificacion == null) {
            throw new EntityNotFoundException("Calificación no encontrada con ID: " + id);
        }
        return calificacion;
    }

    /**
     * Obtiene la calificación de un usuario específico para una biblioteca
     * @param idUsuario
     * @param idBiblioteca
     * @return
     * @throws EntityNotFoundException 
     */
    public Calificacion getCalificacionPorUsuarioYBiblioteca(int idUsuario, int idBiblioteca) 
            throws EntityNotFoundException {
        Calificacion calificacion = calificacionDB.getCalificacionPorUsuarioYBiblioteca(idUsuario, idBiblioteca);
        if (calificacion == null) {
            throw new EntityNotFoundException(
                "Calificación no encontrada para usuario ID: " + idUsuario + 
                " y biblioteca ID: " + idBiblioteca
            );
        }
        return calificacion;
    }

    /**
     * Valida los datos de entrada para una calificación
     * @param idUsuario
     * @param idBiblioteca
     * @param calificacion
     * @throws CalificacionDataInvalidException 
     */
    private void validarDatosCalificacion(int idUsuario, int idBiblioteca, int calificacion) 
            throws CalificacionDataInvalidException {
        
        if (idUsuario <= 0) {
            throw new CalificacionDataInvalidException("El ID de usuario debe ser mayor a 0");
        }

        if (idBiblioteca <= 0) {
            throw new CalificacionDataInvalidException("El ID de biblioteca debe ser mayor a 0");
        }

        if (calificacion < 1 || calificacion > 5) {
            throw new CalificacionDataInvalidException("La calificación debe estar entre 1 y 5");
        }
    }

    /**
     * Crea una nueva calificación o actualiza una existente
     * @param calificacionRequest
     * @return
     * @throws CalificacionDataInvalidException 
     */
    public Calificacion createCalificacion(CalificacionRequest calificacionRequest)
            throws CalificacionDataInvalidException {

        validarDatosCalificacion(
            calificacionRequest.getId_usuario(),
            calificacionRequest.getId_biblioteca(),
            calificacionRequest.getCalificacion()
        );

        int idUsuario = calificacionRequest.getId_usuario();
        int idBiblioteca = calificacionRequest.getId_biblioteca();
        
        Calificacion calificacionExistente = calificacionDB.getCalificacionPorUsuarioYBiblioteca(
            idUsuario, idBiblioteca
        );

        Calificacion calificacion;
        
        if (calificacionExistente != null) {

            calificacionExistente.setCalificacion(calificacionRequest.getCalificacion());
            calificacionExistente.setFecha_hora(LocalDateTime.now());
            
            calificacion = new Calificacion(
                idUsuario,
                idBiblioteca,
                calificacionRequest.getCalificacion(),
                LocalDateTime.now()
            );

        } else {
            
            calificacion = new Calificacion(
                idUsuario,
                idBiblioteca,
                calificacionRequest.getCalificacion(),
                LocalDateTime.now()
            );
        }

        Calificacion calificacionCreada = calificacionDB.createCalificacion(calificacion);
        
        return calificacionCreada;
    }

    /**
     * Obtiene el promedio de calificaciones de una biblioteca
     * @param idBiblioteca
     * @return 
     */
    public double getPromedioCalificacionesPorBiblioteca(int idBiblioteca) {
        Double promedio = calificacionDB.getPromedioCalificacionesPorBiblioteca(idBiblioteca);
        return promedio != null ? promedio : 0.0;
    }

    /**
     * Obtiene el total de calificaciones de una biblioteca
     * @param idBiblioteca
     * @return 
     */
    public int getTotalCalificacionesPorBiblioteca(int idBiblioteca) {
        return calificacionDB.getTotalCalificacionesPorBiblioteca(idBiblioteca);
    }

    /**
     * Verifica si un usuario ya calificó una biblioteca
     * @param idUsuario
     * @param idBiblioteca
     * @return 
     */
    public boolean usuarioYaCalificoBiblioteca(int idUsuario, int idBiblioteca) {
        return calificacionDB.existeCalificacionPorUsuarioYBiblioteca(idUsuario, idBiblioteca);
    }
    
}