# Proyecto BBDDA-JDBC

## Descripción

Este proyecto se centra en el aprendizaje de Java Database Connectivity (JDBC) para interactuar con una base de datos MySQL u Oracle 19c. Permite realizar operaciones básicas sobre algunas de las tablas de los schemas de ejemplo `employees` de MySQL y `hr` de Oracle 19c.

## Características

- **Conexión a Base de Datos**: Establece conexión con bases de datos MySQL y Oracle 19c utilizando JDBC.
- **Lectura de datos**: Permite obtener información de las tablas de los schemas `employees` de MySQL y `hr` de Oracle 19c.
- **Escritura de datos**: Permite escribir información en algunas de la tablas, ya sea de forma directa o a través de ficheros CSV usando la librería [OpenCSV](https://www.baeldung.com/opencsv).
- **Batch Processing**: Optimiza las operaciones de inserción y actualización utilizando batch processing.

## Tecnologías Utilizadas

- Java
- Maven
- MySQL
- Oracle 19c
- JDBC
- Lombok
- OpenCSV

## Requisitos

- Java 21
- Maven
- Sería recomendable disponer de contenedores Docker con MySQL y Oracle 19c para realizar pruebas locales.

## Configuración y uso

- [Instalar maven de forma manual (Windows, Linux, Mac)](https://maven.apache.org/install.html)
- [Instalar maven de forma automática (Mac)](https://formulae.brew.sh/formula/maven)

Revisa las clases del aula virtual y los recursos adicionales para obtener información sobre cómo configurar y utilizar el proyecto.

