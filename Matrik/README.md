## Como iniciar el proyecto

## 🐋 Configuración de Oracle Database XE

### 1. Instalación de Oracle XE
**Windows/Linux:**
bash
# Descargar desde Oracle Official Site:
https://www.oracle.com/database/technologies/xe-downloads.html

# Ejecutar instalador y seguir pasos (Windows) o:
sudo apt install ./oracle-database-xe-21c-1.0-1.x86_64.rpm
sudo /etc/init.d/oracle-xe-21c configure

-- Conectarse como sysdba
sqlplus sys/your_password@localhost:1521/XE as sysdba

-- Crear usuario y permisos
CREATE USER construmart IDENTIFIED BY project_password;
GRANT CONNECT, RESOURCE, DBA TO construmart;

-- Ejecutar script principal (guardar como schema.sql)
ALTER SESSION SET CURRENT_SCHEMA = CONSTRUMART;
@/path/to/schema.sql

##SCRIPT DE CREACIÓN DE TABLAS NECESARIAS
Ejecutar el script de Script.md

##Crear el aplication.properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XE
spring.datasource.username=CONSTRUMART
spring.datasource.password=project_password
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.Oracle12cDialect
