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

CREATE TABLE comision (
    id_comision INT AUTO_INCREMENT PRIMARY KEY,
    id_empresa INT NOT NULL,
    porcentaje DECIMAL(10,2) NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_final DATE DEFAULT NULL,
    tipo_comision ENUM('global', 'especifica') NOT NULL DEFAULT 'global',
    INDEX idx_comision_empresa (id_empresa),
    CONSTRAINT fk_comision_empresa
        FOREIGN KEY (id_empresa)
        REFERENCES empresa(id_empresa)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);-- al borrar una empresa se borran sus comisiones


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


CREATE TABLE grupo (
    id_grupo INT AUTO_INCREMENT PRIMARY KEY,
    id_creador INT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    cantidad_participantes INT DEFAULT 0,
    INDEX idx_grupo_creador (id_creador),
    CONSTRAINT fk_grupo_creador
        FOREIGN KEY (id_creador)
        REFERENCES usuario(id_usuario)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);-- solo un creador de grupo puede borrar el grupo

CREATE TABLE grupo_usuario (
    id_grupo INT NOT NULL,
    id_usuario INT NOT NULL,
    KEY fk_grupo_usuario_grupo (id_grupo),
    KEY fk_grupo_usuario_usuario (id_usuario),
    CONSTRAINT fk_grupo_usuario_grupo
        FOREIGN KEY (id_grupo)
        REFERENCES grupo (id_grupo)
        ON DELETE CASCADE,
    CONSTRAINT fk_grupo_usuario_usuario
        FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario)
        ON DELETE RESTRICT
);

CREATE TABLE IF NOT EXISTS videojuego (
  id_videojuego int NOT NULL AUTO_INCREMENT,
  id_empresa int NOT NULL,
  titulo varchar(200) NOT NULL,
  descripcion text NOT NULL,
  recursos_minimos text NOT NULL,
  precio decimal(10,2) NOT NULL,
  clasificacion_edad enum('E','T','M') NOT NULL,
  fecha_lanzamiento date NOT NULL,
  comentarios_bloqueados tinyint(1) DEFAULT '0',
  PRIMARY KEY (id_videojuego),
  KEY id_empresa (id_empresa),
  CONSTRAINT videojuego_ibfk_1 FOREIGN KEY (id_empresa) REFERENCES empresa (id_empresa)
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

CREATE TABLE IF NOT EXISTS comentario (
  id_comentario int NOT NULL AUTO_INCREMENT,
  id_usuario int NOT NULL,
  id_biblioteca int NOT NULL,
  comentario text NOT NULL,
  fecha_hora datetime NOT NULL,
  PRIMARY KEY (id_comentario),
  KEY id_usuario (id_usuario),
  KEY id_biblioteca (id_biblioteca),
  CONSTRAINT comentario_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CONSTRAINT comentario_ibfk_2 FOREIGN KEY (id_biblioteca) REFERENCES biblioteca_usuario (id_biblioteca)
);

CREATE TABLE IF NOT EXISTS calificacion (
  id_calificacion int NOT NULL AUTO_INCREMENT,
  id_usuario int NOT NULL,
  id_biblioteca int NOT NULL,
  calificacion int NOT NULL,
  fecha_hora datetime NOT NULL,
  PRIMARY KEY (id_calificacion),
  UNIQUE KEY unique_calificacion_usuario_biblioteca (id_usuario,id_biblioteca),
  KEY id_biblioteca (id_biblioteca),
  CONSTRAINT calificacion_ibfk_1 FOREIGN KEY (id_usuario) REFERENCES usuario (id_usuario),
  CONSTRAINT calificacion_ibfk_2 FOREIGN KEY (id_biblioteca) REFERENCES `biblioteca_usuario` (id_biblioteca)
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

-- inserts configuraciones de sistema
INSERT INTO sistema (configuracion, valor, descripcion, fecha_inicio, fecha_final) VALUES
('COMISION_GLOBAL', '2', 'Porcentaje de comisión global para todas las ventas', '2025-12-14', '2026-11-21'),
('EDAD_MENORES', '16', 'Edad considerada para menores', '2025-12-14', NULL),
('MAX_MIEMBROS_GRUPO', '6', 'Máximo número de miembros por grupo familiar', '2025-12-14', NULL);

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
