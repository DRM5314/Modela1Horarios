drop database modela1;
create database modela1;
use modela1;

CREATE TABLE carrera (
    codigo varchar(10) PRIMARY KEY,
    numeroMaterias int,
    numeroSemestres int,
    nombre varchar(25)
);

CREATE TABLE profesor (
    codigo varchar(10) PRIMARY KEY,
    nombre varchar (120),
    codigoCarrera varchar(10),
    CONSTRAINT FOREIGN KEY (codigoCarrera) REFERENCES carrera (codigo)
);

CREATE TABLE materia (
    codigo varchar(10) PRIMARY KEY,
    codigoCarrera varchar (10),
    nombre varchar (75),
    semestre int,
    maxSecciones int,
    CONSTRAINT FOREIGN KEY (codigoCarrera) REFERENCES carrera (codigo)
);

CREATE TABLE salon (
    codigo varchar(10) PRIMARY KEY,
    nombre varchar (45) UNIQUE,
    capacidad int
);

CREATE TABLE contrato (
    codigo varchar (10) PRIMARY KEY,
    codigoProfesor varchar (10),
    horarioEntrada TIME,
    horarioSalida TIME,
    fechaContrato DATE,
    fechaVencimiento DATE,
    CONSTRAINT FOREIGN KEY (codigoProfesor) REFERENCES profesor(codigo)
);

CREATE TABLE preAsignacion (
    codigo varchar (10) PRIMARY KEY,
    codigoMateria varchar(10) UNIQUE,
    numeroAsignaciones int,
    CONSTRAINT FOREIGN KEY (codigoMateria) REFERENCES materia(codigo)
);

CREATE TABLE dia (
    codigo varchar (10) PRIMARY KEY,
    nombre varchar (15)
);

CREATE TABLE asignacionHorario (
    codigo varchar (10) PRIMARY KEY,
    dia varchar (45),
    codigoMateria varchar (10),
    codigoSalon varchar (10),
    codigoProfesor varchar (10),
    CONSTRAINT FOREIGN KEY (codigoMateria) REFERENCES materia(codigo),
    CONSTRAINT FOREIGN KEY (codigoSalon) REFERENCES salon(codigo),
    CONSTRAINT FOREIGN KEY (codigoProfesor) REFERENCES profesor(codigo)
);

CREATE TABLE prioridadProfesorCurso(
    codigo varchar (10) PRIMARY KEY,
    prioridad varchar (10)
);

CREATE TABLE calificacion (
    codigo varchar (10) PRIMARY KEY,
    codigoMateria varchar (10),
    codigoProfesor varchar (10),
    prioridad varchar (10),
    CONSTRAINT FOREIGN KEY (codigoMateria) REFERENCES materia(codigo) ,
    CONSTRAINT FOREIGN KEY (codigoProfesor) REFERENCES profesor(codigo),
    CONSTRAINT FOREIGN KEY (prioridad) REFERENCES prioridadProfesorCurso(codigo)
);

CREATE TABLE parametro(
    codigo varchar(50) PRIMARY KEY,
    descripcion varchar (150),
    valor int
);