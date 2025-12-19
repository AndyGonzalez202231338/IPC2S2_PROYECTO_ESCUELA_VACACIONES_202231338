/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package grupo.services;

import db.GrupoDB;
import db.SistemaDB;
import db.UsersDB;
import grupo.dtos.GrupoResponse;
import grupo.dtos.IntegranteResponse;
import grupo.dtos.NewGrupoRequest;
import grupo.models.Grupo;
import user.models.Usuario;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author andy
 */
public class GrupoLogicaService {

    private final GrupoDB grupoDB;
    private final UsersDB usuarioDB;
    private final SistemaDB sistemaDB;

    public GrupoLogicaService() {
        this.grupoDB = new GrupoDB();
        this.usuarioDB = new UsersDB();
        this.sistemaDB = new SistemaDB();
    }

    public GrupoResponse crearGrupoConValidaciones(NewGrupoRequest request)
            throws SQLException, IllegalArgumentException {

        if (request.getId_creador() <= 0) {
            throw new IllegalArgumentException("El ID del creador debe ser mayor a 0");
        }

        if (request.getNombre() == null || request.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre del grupo es requerido");
        }

        Optional<Usuario> usuarioOpt = usuarioDB.getById(request.getId_creador());
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + request.getId_creador());
        }

        NewGrupoRequest grupoInsertado = grupoDB.insertarGrupo(request);
 
        try {
            grupoDB.agregarParticipante(grupoInsertado.getId_grupo(), request.getId_creador());
            
        } catch (SQLException e) {
            // Continuar aunque falle esto, el grupo ya está creado
        }

        GrupoResponse response = new GrupoResponse();
        response.setId_grupo(grupoInsertado.getId_grupo());
        response.setId_creador(grupoInsertado.getId_creador());
        response.setNombre(grupoInsertado.getNombre());
        response.setCantidad_participantes(1); // se suma 1 por que se agrego al creador

        return response;
    }

    /**
     * Agrega un participante con validaciones de límite
     * @param idGrupo
     * @param idUsuario
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException 
     */
    public boolean agregarParticipanteConValidaciones(int idGrupo, int idUsuario)
            throws SQLException, IllegalArgumentException {

        Grupo grupo = grupoDB.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            throw new IllegalArgumentException("Grupo no encontrado con ID: " + idGrupo);
        }

        Optional<Usuario> usuarioOpt = usuarioDB.getById(idUsuario);
        if (!usuarioOpt.isPresent()) {
            throw new IllegalArgumentException("Usuario no encontrado con ID: " + idUsuario);
        }

        boolean yaEnGrupo = grupoDB.usuarioEnGrupo(idGrupo, idUsuario);
        if (yaEnGrupo) {
            throw new IllegalArgumentException("El usuario ya es parte de este grupo");
        }

        // Validar límite máximo de participantes
        int maxParticipantes = obtenerMaximoParticipantes();
        int participantesActuales = grupoDB.contarParticipantes(idGrupo);

        if (participantesActuales >= maxParticipantes) {
            throw new IllegalArgumentException(
                    String.format("El grupo ha alcanzado el límite máximo de %d participantes", maxParticipantes)
            );
        }
        return grupoDB.agregarParticipante(idGrupo, idUsuario);
    }

    /**
     * Elimina un participante
     * @param idGrupo
     * @param idUsuario
     * @param idUsuarioSolicitante
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException 
     */
    public boolean eliminarParticipanteConValidaciones(int idGrupo, int idUsuario, int idUsuarioSolicitante)
            throws SQLException, IllegalArgumentException {

        Grupo grupo = grupoDB.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            throw new IllegalArgumentException("Grupo no encontrado con ID: " + idGrupo);
        }

        if (grupo.getId_creador() != idUsuarioSolicitante) {
            throw new IllegalArgumentException("Solo el creador del grupo puede eliminar participantes");
        }

        if (grupo.getId_creador() == idUsuario) {
            throw new IllegalArgumentException("No se puede eliminar al creador del grupo");
        }

        boolean enGrupo = grupoDB.usuarioEnGrupo(idGrupo, idUsuario);
        if (!enGrupo) {
            throw new IllegalArgumentException("El usuario no es parte de este grupo");
        }

        return grupoDB.eliminarParticipante(idGrupo, idUsuario);
    }

    /**
     * Eliminar un grupo
     * @param idGrupo
     * @param idUsuarioSolicitante
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException 
     */
    public boolean eliminarGrupoConValidaciones(int idGrupo, int idUsuarioSolicitante)
            throws SQLException, IllegalArgumentException {

        Grupo grupo = grupoDB.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            throw new IllegalArgumentException("Grupo no encontrado con ID: " + idGrupo);
        }

        if (grupo.getId_creador() != idUsuarioSolicitante) {
            throw new IllegalArgumentException("Solo el creador del grupo puede eliminarlo");
        }

        return grupoDB.eliminarGrupo(idGrupo);
    }

    /**
     * Obtiene el máximo de participantes desde la configuración del sistema
     * @return 
     */
    private int obtenerMaximoParticipantes() {
        try {
            String maxMiembrosStr = sistemaDB.getValorConfiguracion("MAX_MIEMBROS_GRUPO");
            if (maxMiembrosStr != null) {
                return Integer.parseInt(maxMiembrosStr);
            }
        } catch (Exception e) {
            // Si hay error, usar valor por defecto
        }

        return 6;
    }
    
    /**
     * Obtiene grupos de un usuario
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public List<GrupoResponse> obtenerGruposDeUsuario(int idUsuario) throws SQLException {
        List<Grupo> grupos = grupoDB.obtenerGruposPorUsuario(idUsuario);
        return grupos.stream()
                .map(grupo -> {
                    GrupoResponse response = new GrupoResponse();
                    response.setId_grupo(grupo.getId_grupo());
                    response.setId_creador(grupo.getId_creador());
                    response.setNombre(grupo.getNombre());
                    response.setCantidad_participantes(grupo.getCantidad_participantes());
                    return response;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * Verifica si un usuario puede crear más grupos
     * @param idUsuario
     * @return
     * @throws SQLException 
     */
    public boolean puedeCrearMasGrupos(int idUsuario) throws SQLException {
        int gruposCreados = grupoDB.contarGruposCreadosPorUsuario(idUsuario);
        return gruposCreados < 10;
    }

    /**
     * Obtener espacios disponibles en grupo
     * @param idGrupo
     * @return
     * @throws SQLException 
     */
    public int obtenerEspaciosDisponibles(int idGrupo) throws SQLException {
        int maxParticipantes = obtenerMaximoParticipantes();
        int participantesActuales = grupoDB.contarParticipantes(idGrupo);
        return Math.max(0, maxParticipantes - participantesActuales);
    }
    
    /**
     * Obtener todos los integrantes de un grupo
     * @param idGrupo
     * @return
     * @throws SQLException
     * @throws IllegalArgumentException 
     */
    public List<IntegranteResponse> obtenerIntegrantesDelGrupo(int idGrupo) 
            throws SQLException, IllegalArgumentException {
        
        Grupo grupo = grupoDB.obtenerGrupoPorId(idGrupo);
        if (grupo == null) {
            throw new IllegalArgumentException("Grupo no encontrado con ID: " + idGrupo);
        }
        
        List<Usuario> participantes = grupoDB.obtenerParticipantes(idGrupo);
        
        return participantes.stream()
                .map(usuario -> convertirAIntegranteResponse(usuario, grupo.getId_creador()))
                .collect(Collectors.toList());
    }
    
    /**
     * Convierte Usuario a IntegranteResponse para enviar de respuesta una lista estructurada
     * @param usuario
     * @param idCreadorGrupo
     * @return 
     */
    private IntegranteResponse convertirAIntegranteResponse(Usuario usuario, int idCreadorGrupo) {
        IntegranteResponse response = new IntegranteResponse();
        response.setId_usuario(usuario.getIdUsuario());
        response.setNombre(usuario.getNombre());
        response.setCorreo(usuario.getCorreo());
        response.setPais(usuario.getPais());
        response.setTelefono(usuario.getTelefono());
        response.setEs_creador(usuario.getIdUsuario() == idCreadorGrupo);
        return response;
    }
}
