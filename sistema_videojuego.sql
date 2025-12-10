-- ----------------------------------------
--  DCL
-- ----------------------------------------

-- creacion base de datos
CREATE DATABASE IF NOT EXISTS sistema_videojuegos;

-- cracion usuario para la base de datos
CREATE USER 'usuarioAdministrador'@'localhost' IDENTIFIED BY 'admin123';

GRANT ALL PRIVILEGES ON sistema_videojuegos.* TO 'usuarioAdministrador'@'localhost';

FLUSH PRIVILEGES;

-- ----------------------------------------
--  DDL
-- ----------------------------------------

-- uso de base de datos
USE sistema_videojuegos;

-- creacion tablas
CREATE TABLE IF NOT EXISTS rol (
    id_rol INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS empresa (
    id_empresa INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS comision (
    id_comision INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    porcentaje DECIMAL(10,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_final DATE NULL,
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE IF NOT EXISTS usuario (
    id_usuario INT AUTO_INCREMENT PRIMARY KEY,
    correo VARCHAR(250) NOT NULL UNIQUE,
    id_rol INT NOT NULL,
    id_empresa INT NULL,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NULL,
    pais VARCHAR(50) NULL,
    telefono VARCHAR(8) NULL,
    saldo_cartera DECIMAL(10,2) DEFAULT 0.00,
    avatar MEDIUMBLOB NULL,
    FOREIGN KEY (id_rol) REFERENCES rol(id_rol),
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
);


CREATE TABLE IF NOT EXISTS grupo (
    id_grupo INT AUTO_INCREMENT PRIMARY KEY,
    id_creador INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    cantidad_participantes INT NULL,
    FOREIGN KEY (id_creador) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS grupo_usuario (
    id_grupo INT NOT NULL,
    id_usuario INT NOT NULL,
    FOREIGN KEY (id_grupo) REFERENCES grupo(id_grupo),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS videojuego (
    id_videojuego INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    titulo VARCHAR (200) NOT NULL,
    descripcion TEXT NOT NULL,
    recursos_minimos TEXT NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    clasificacion_edad ENUM('E', 'T', 'M') NOT NULL,
    fecha_lanzamiento DATE NOT NULL,
    FOREIGN KEY (id_empresa) REFERENCES empresa(id_empresa)
);

CREATE TABLE IF NOT EXISTS categoria (
    id_categoria INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS videojuego_categoria (
    id_categoria INT NOT NULL,
    id_videojuego INT NOT NULL,
    estado ENUM('PENDIENTE', 'APROBADA', 'RECHAZADA') DEFAULT 'PENDIENTE',
    FOREIGN KEY (id_categoria) REFERENCES categoria(id_categoria),
    FOREIGN KEY (id_videojuego) REFERENCES videojuego(id_videojuego)
);

CREATE TABLE IF NOT EXISTS multimedia (
    id_multimedia INT AUTO_INCREMENT PRIMARY KEY,
    id_videojuego INT NOT NULL,
    imagen MEDIUMBLOB NOT NULL,
    FOREIGN KEY (id_videojuego) REFERENCES videojuego(id_videojuego)
);

CREATE TABLE IF NOT EXISTS compra (
    id_compra INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_videojuego INT NOT NULL,
    monto_pago DECIMAL(10,2) NOT NULL,
    fecha_compra DATE NOT NULL,
    comision_aplicada DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (id_videojuego) REFERENCES videojuego(id_videojuego),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS biblioteca_usuario (
    id_biblioteca INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT NOT NULL,
    id_videojuego INT NOT NULL,
    id_compra INT NOT NULL,
    tipo_adquisicion ENUM('COMPRA', 'PRESTAMO') NOT NULL,
    FOREIGN KEY (id_videojuego) REFERENCES videojuego(id_videojuego),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario),
    FOREIGN KEY (id_compra) REFERENCES compra(id_compra)
);

CREATE TABLE IF NOT EXISTS instalacion_juego (
    id_instalacion INT AUTO_INCREMENT PRIMARY KEY,
    id_biblioteca INT NOT NULL,
    id_videojuego INT NOT NULL,
    estado ENUM('INSTALADO', 'NO_INSTALADO') NOT NULL,
    tipo_adquisicion ENUM('COMPRA', 'PRESTAMO') NOT NULL,
    FOREIGN KEY (id_videojuego) REFERENCES videojuego(id_videojuego),
    FOREIGN KEY (id_biblioteca) REFERENCES biblioteca_usuario(id_biblioteca)
);

CREATE TABLE IF NOT EXISTS reseña (
    id_reseña INT AUTO_INCREMENT PRIMARY KEY,
    id_biblioteca INT NOT NULL,
    calificacion INT NOT NULL,
    comentario VARCHAR(250) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    FOREIGN KEY (id_biblioteca) REFERENCES biblioteca_usuario(id_biblioteca)
);

CREATE TABLE IF NOT EXISTS respuesta (
    id_respuesta INT AUTO_INCREMENT PRIMARY KEY,
    id_reseña INT NOT NULL,
    id_usuario INT NOT NULL,
    comentario VARCHAR(250) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    FOREIGN KEY (id_reseña) REFERENCES reseña(id_reseña),
    FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario)
);

CREATE TABLE IF NOT EXISTS sistema (
    id_configuracion INT AUTO_INCREMENT PRIMARY KEY,
    configuracion VARCHAR(100) UNIQUE NOT NULL,
    valor VARCHAR(50) NOT NULL,
    descripcion TEXT,
    fecha_inicio DATE NOT NULL,
    fecha_final DATE NULL  
);

CREATE TABLE IF NOT EXISTS banner (
    id_banner INT AUTO_INCREMENT PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT
);

CREATE TABLE IF NOT EXISTS multimedia_banner (
    id_multimedia_banner INT AUTO_INCREMENT PRIMARY KEY,
    orden INT NOT NULL,
    imagen MEDIUMBLOB NOT NULL
);

-- ----------------------------------------
--  DML
-- ----------------------------------------

-- inserts roles
INSERT INTO rol (nombre, descripcion) VALUES 
('ADMINISTRADOR DE SISTEMA', 'Usuario encargado de crear empresas, categorias de juegos y reportes globales de ganancias'),
('ADMINISTRADOR DE EMPRESA', 'Ususario encargado de crear videojuegos, ocultar comentarios y reportes de ventas y reseñas'),
('COMUN', 'Usuario final del sistema, compra juegos, interactua con reseñas y forma grupos de usuarios. Tiene acceso a historial de comra y comentarios realizados');

-- Insertar usuario, el usuario debe ser borrado luego de crear un usuario real como administrador
INSERT INTO usuario (correo, id_rol, id_empresa, nombre, password, fecha_nacimiento, pais, telefono, saldo_cartera, avatar)
VALUES (
    'admin@gmail.com',
    1,
    NULL,
    'Administrador Sistema',
    'admin123',
    NULL,
    NULL,
    NULL,
    0.00,
    NULL
);
