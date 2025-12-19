package instalacion.dtos;

public class JuegoPrestable {
    private int id_videojuego;
    private String titulo;
    private int id_usuario_propietario;
    private String nombre_propietario;
    private int id_grupo;
    private String nombre_grupo;
    private String clasificacion_edad;
    private String descripcion;
    private boolean ya_prestado;

    public JuegoPrestable() {
    }

    
    public JuegoPrestable(int id_videojuego, String titulo, int id_usuario_propietario, String nombre_propietario, int id_grupo, String nombre_grupo,
        String clasificacion_edad, String descripcion, boolean ya_prestado) {
        this.id_videojuego = id_videojuego;
        this.titulo = titulo;
        this.id_usuario_propietario = id_usuario_propietario;
        this.nombre_propietario = nombre_propietario;
        this.id_grupo = id_grupo;
        this.nombre_grupo = nombre_grupo;
        this.clasificacion_edad = clasificacion_edad;
        this.descripcion = descripcion;
        this.ya_prestado = ya_prestado;
    }

    public int getId_videojuego() {
        return id_videojuego;
    }

    public void setId_videojuego(int id_videojuego) {
        this.id_videojuego = id_videojuego;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getId_usuario_propietario() {
        return id_usuario_propietario;
    }

    public void setId_usuario_propietario(int id_usuario_propietario) {
        this.id_usuario_propietario = id_usuario_propietario;
    }

    public String getNombre_propietario() {
        return nombre_propietario;
    }

    public void setNombre_propietario(String nombre_propietario) {
        this.nombre_propietario = nombre_propietario;
    }

    public int getId_grupo() {
        return id_grupo;
    }

    public void setId_grupo(int id_grupo) {
        this.id_grupo = id_grupo;
    }

    public String getNombre_grupo() {
        return nombre_grupo;
    }

    public void setNombre_grupo(String nombre_grupo) {
        this.nombre_grupo = nombre_grupo;
    }

    public String getClasificacion_edad() {
        return clasificacion_edad;
    }

    public void setClasificacion_edad(String clasificacion_edad) {
        this.clasificacion_edad = clasificacion_edad;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isYa_prestado() {
        return ya_prestado;
    }

    public void setYa_prestado(boolean ya_prestado) {
        this.ya_prestado = ya_prestado;
    }
}