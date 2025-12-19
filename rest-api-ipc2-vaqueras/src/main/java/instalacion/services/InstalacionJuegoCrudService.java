package instalacion.services;


import db.InstalacionJuegoDB;
import exceptions.EntityNotFoundException;
import exceptions.InstalacionDataInvalidException;
import exceptions.InstalacionRestrictionException;
import instalacion.dtos.JuegoPrestable;
import instalacion.dtos.NewInstalacionRequest;
import instalacion.models.InstalacionJuego;
import java.util.List;

public class InstalacionJuegoCrudService {

    private final InstalacionJuegoDB instalacionJuegoDB;

    public InstalacionJuegoCrudService() {
        this.instalacionJuegoDB = new InstalacionJuegoDB();
    }

    public List<InstalacionJuego> getAllInstalaciones() {
        return instalacionJuegoDB.getAllInstalaciones();
    }

    public InstalacionJuego getInstalacionById(int id) throws EntityNotFoundException {
        InstalacionJuego instalacion = instalacionJuegoDB.getInstalacionById(id);
        if (instalacion == null) {
            throw new EntityNotFoundException("Instalación no encontrada con ID: " + id);
        }
        return instalacion;
    }
    
    /**
     * Validar antes de guardar
     * @param request
     * @throws InstalacionDataInvalidException 
     */
    private void validarDatosInstalacion(NewInstalacionRequest request) 
            throws InstalacionDataInvalidException {
        
        if (request.getId_biblioteca() <= 0) {
            throw new InstalacionDataInvalidException("El ID de biblioteca es requerido");
        }

        if (request.getId_videojuego() <= 0) {
            throw new InstalacionDataInvalidException("El ID de videojuego es requerido");
        }

        if (request.getEstado() == null || !(request.getEstado().equals("INSTALADO") || request.getEstado().equals("NO_INSTALADO"))) {
            throw new InstalacionDataInvalidException("El estado debe ser INSTALADO o NO_INSTALADO");
        }

        if (request.getTipo_adquisicion() == null || !(request.getTipo_adquisicion().equals("COMPRA") || request.getTipo_adquisicion().equals("PRESTAMO"))) {
            throw new InstalacionDataInvalidException("El tipo de adquisición debe ser COMPRA o PRESTAMO");
        }
    }
    /**
     * crear una instalacion
     * @param request
     * @return
     * @throws InstalacionDataInvalidException
     * @throws InstalacionRestrictionException 
     */
    public InstalacionJuego createInstalacion(NewInstalacionRequest request)
            throws InstalacionDataInvalidException, InstalacionRestrictionException {
        
        validarDatosInstalacion(request);
        
        // Obtener el usuario de la biblioteca
        int id_usuario = instalacionJuegoDB.getUsuarioDeBiblioteca(request.getId_biblioteca());
        if (id_usuario == 0) {
            throw new InstalacionDataInvalidException("Biblioteca no válida");
        }
        
        // Obtener el tipo de adquisición de la biblioteca
        String tipoAdquisicionBiblioteca = instalacionJuegoDB.getTipoAdquisicionDeBiblioteca(request.getId_biblioteca());
        if (tipoAdquisicionBiblioteca == null) {
            throw new InstalacionDataInvalidException("Biblioteca no encontrada");
        }
        
        // Verificar que el tipo de adquisición coincida
        if (!tipoAdquisicionBiblioteca.equals(request.getTipo_adquisicion())) {
            throw new InstalacionDataInvalidException("El tipo de adquisición no coincide con la biblioteca");
        }
        
        // Validar reglas de negocio para juegos prestados
        if (tipoAdquisicionBiblioteca.equals("PRESTAMO") && request.getEstado().equals("INSTALADO")) {
            // Verificar si ya tiene un juego prestado instalado
            if (instalacionJuegoDB.tieneInstalacionPrestadaActiva(id_usuario)) {
                throw new InstalacionRestrictionException(
                    "Ya tienes un juego prestado instalado. " +
                    "Debes desinstalarlo antes de instalar otro juego prestado."
                );
            }
        }
        
        InstalacionJuego nuevaInstalacion = new InstalacionJuego(
            0,
            request.getId_biblioteca(),
            request.getId_videojuego(),
            request.getEstado(),
            request.getTipo_adquisicion()
        );

        InstalacionJuego instalacionCreada = instalacionJuegoDB.createInstalacion(nuevaInstalacion);

        if (instalacionCreada == null) {
            throw new RuntimeException("No se pudo crear la instalación");
        }

        System.out.println("Instalación creada exitosamente con ID: " + instalacionCreada.getId_instalacion());
        return instalacionCreada;
    }

    public InstalacionJuego updateInstalacion(int id, NewInstalacionRequest request)
            throws EntityNotFoundException, InstalacionDataInvalidException, InstalacionRestrictionException {
        
        System.out.println("Actualizando instalación ID: " + id);

        InstalacionJuego instalacionExistente = getInstalacionById(id);

        validarDatosInstalacion(request);
        
        // Obtener el usuario de la biblioteca
        int id_usuario = instalacionJuegoDB.getUsuarioDeBiblioteca(request.getId_biblioteca());
        if (id_usuario == 0) {
            throw new InstalacionDataInvalidException("Biblioteca no válida");
        }
        
        // Obtener el tipo de adquisición de la biblioteca
        String tipoAdquisicionBiblioteca = instalacionJuegoDB.getTipoAdquisicionDeBiblioteca(request.getId_biblioteca());
        if (tipoAdquisicionBiblioteca == null) {
            throw new InstalacionDataInvalidException("Biblioteca no encontrada");
        }
        
        // Verificar que el tipo de adquisición coincida
        if (!tipoAdquisicionBiblioteca.equals(request.getTipo_adquisicion())) {
            throw new InstalacionDataInvalidException("El tipo de adquisición no coincide con la biblioteca");
        }
        
        // Validar reglas de negocio para juegos prestados
        if (tipoAdquisicionBiblioteca.equals("PRESTAMO") && request.getEstado().equals("INSTALADO")) {
            // Verificar si ya tiene otro juego prestado instalado (excluyendo esta instalación)
            // Primero, descontamos esta instalación si ya estaba instalada
            if (!instalacionExistente.getEstado().equals("INSTALADO")) {
                if (instalacionJuegoDB.tieneInstalacionPrestadaActiva(id_usuario)) {
                    throw new InstalacionRestrictionException(
                        "Ya tienes un juego prestado instalado. " +
                        "Debes desinstalarlo antes de instalar otro juego prestado."
                    );
                }
            }
        }

        instalacionExistente.setId_biblioteca(request.getId_biblioteca());
        instalacionExistente.setId_videojuego(request.getId_videojuego());
        instalacionExistente.setEstado(request.getEstado());
        instalacionExistente.setTipo_adquisicion(request.getTipo_adquisicion());

        boolean actualizado = instalacionJuegoDB.updateInstalacion(instalacionExistente);

        if (!actualizado) {
            throw new RuntimeException("No se pudo actualizar la instalación");
        }

        System.out.println("Instalación actualizada exitosamente: " + instalacionExistente.getId_instalacion());
        return instalacionExistente;
    }

    public void deleteInstalacion(int id) throws EntityNotFoundException {
        System.out.println("Eliminando instalación ID: " + id);

        getInstalacionById(id);

        boolean eliminado = instalacionJuegoDB.deleteInstalacion(id);

        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar la instalación");
        }

        System.out.println("Instalación eliminada exitosamente");
    }

    
    public List<InstalacionJuego> getInstalacionesByUsuario(int id_usuario) {
        return instalacionJuegoDB.getInstalacionesByUsuario(id_usuario);
    }

    public boolean puedeInstalarJuegoPrestado(int id_usuario) {
        return !instalacionJuegoDB.tieneInstalacionPrestadaActiva(id_usuario);
    }

    public InstalacionJuego instalarJuegoComprado(int id_usuario, int id_videojuego) 
            throws InstalacionDataInvalidException, InstalacionRestrictionException {
        
        // Verificar si el usuario tiene el juego comprado en su biblioteca
        if (!instalacionJuegoDB.existeBibliotecaParaUsuarioYJuego(id_usuario, id_videojuego, "COMPRA")) {
            throw new InstalacionDataInvalidException("No tienes este juego comprado en tu biblioteca");
        }
        
        // Obtener el ID de la biblioteca
        Integer id_biblioteca = instalacionJuegoDB.getIdBibliotecaParaUsuarioYJuego(id_usuario, id_videojuego, "COMPRA");
        if (id_biblioteca == null) {
            throw new InstalacionDataInvalidException("Error al obtener la biblioteca");
        }
        
        // Crear la instalación
        NewInstalacionRequest request = new NewInstalacionRequest(
            id_biblioteca,
            id_videojuego,
            "INSTALADO",
            "COMPRA"
        );

        return createInstalacion(request);
    }

    public InstalacionJuego instalarJuegoPrestado(int id_usuario, int id_videojuego) 
            throws InstalacionDataInvalidException, InstalacionRestrictionException {
        
        // Verificar restricción de juego prestado
        if (!puedeInstalarJuegoPrestado(id_usuario)) {
            throw new InstalacionRestrictionException(
                "Ya tienes un juego prestado instalado. " +
                "Debes desinstalarlo antes de instalar otro juego prestado."
            );
        }
        
        
        if (!instalacionJuegoDB.existeBibliotecaParaUsuarioYJuego(id_usuario, id_videojuego, "PRESTAMO")) {
            throw new InstalacionDataInvalidException("No tienes este juego prestado en tu biblioteca");
        }
        
        
        Integer id_biblioteca = instalacionJuegoDB.getIdBibliotecaParaUsuarioYJuego(id_usuario, id_videojuego, "PRESTAMO");
        if (id_biblioteca == null) {
            throw new InstalacionDataInvalidException("Error al obtener la biblioteca");
        }
        
        // Crear la instalación
        NewInstalacionRequest request = new NewInstalacionRequest(
            id_biblioteca,
            id_videojuego,
            "INSTALADO",
            "PRESTAMO"
        );

        return createInstalacion(request);
    }

public InstalacionJuego desinstalarJuego(int id_instalacion) 
        throws EntityNotFoundException, InstalacionDataInvalidException, InstalacionRestrictionException {
    
    InstalacionJuego instalacion = getInstalacionById(id_instalacion);
    
    if (instalacion.getEstado().equals("NO_INSTALADO")) {
        throw new InstalacionDataInvalidException("El juego ya está desinstalado");
    }

    NewInstalacionRequest request = new NewInstalacionRequest(
        instalacion.getId_biblioteca(),
        instalacion.getId_videojuego(),
        "NO_INSTALADO",
        instalacion.getTipo_adquisicion()
    );

    return updateInstalacion(id_instalacion, request);
}

/**
 * Jugos par aprestar en un grupo
 * @param id_usuario
 * @return 
 */
public List<JuegoPrestable> getJuegosPrestablesPorGrupo(int id_usuario) {
    try {
        System.out.println("Buscando juegos prestables para usuario: " + id_usuario);
        
        // Verificar conexión primero
        if (!instalacionJuegoDB.verificarConexion()) {
            System.err.println("Error: No hay conexión a la base de datos");
            throw new RuntimeException("Error de conexión a la base de datos");
        }
        
        List<JuegoPrestable> juegos = instalacionJuegoDB.getJuegosPrestablesPorGrupo(id_usuario);
        System.out.println("Encontrados " + juegos.size() + " juegos prestables");
        
        // Agregar información de si ya está prestado
        for (JuegoPrestable juego : juegos) {
            boolean yaPrestado = instalacionJuegoDB.existeBibliotecaParaUsuarioYJuego(
                id_usuario, juego.getId_videojuego(), "PRESTAMO");
            juego.setYa_prestado(yaPrestado);
        }
        
        return juegos;
    } catch (Exception e) {
        System.err.println("Error en getJuegosPrestablesPorGrupo: " + e.getMessage());
        e.printStackTrace();
        throw e;
    }
}



}