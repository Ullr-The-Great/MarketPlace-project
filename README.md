![Vue.js](https://img.shields.io/badge/Vue.js-3.x-green?style=flat-square&logo=vue.js) 
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring-boot)
![Stripe](https://img.shields.io/badge/Stripe-Payments-blue?style=flat-square&logo=stripe)
![License](https://img.shields.io/badge/License-MIT-blue?style=flat-square)

**MiProyecto** es una aplicación web de comercio electrónico que combina un frontend desarrollado en **Vue.js** con un backend en **Spring Boot**. La aplicación permite a los usuarios explorar productos, gestionar carritos de compra, realizar pagos seguros con **Stripe**, y mucho más.

---

## 🚀 **Características Principales**

- **Frontend:**
  - Navegación fluida con Vue Router.
  - Gestión de estado con Pinia.
  - Interfaz moderna y responsiva.
  - Integración con Stripe para pagos seguros.

- **Backend:**
  - API RESTful con Spring Boot.
  - Seguridad con Spring Security y JWT.
  - Gestión de usuarios, productos, pedidos y reseñas.
  - Base de datos relacional con JPA/Hibernate.

- **Funcionalidades:**
  - Registro e inicio de sesión de usuarios.
  - Exploración de productos y categorías.
  - Gestión de carritos de compra.
  - Creación y seguimiento de pedidos.
  - Sistema de reseñas para productos.

---

## 🛠️ **Tecnologías Utilizadas**

### **Frontend**
- [Vue.js 3](https://vuejs.org/)
- [Pinia](https://pinia.vuejs.org/) (Gestión de estado)
- [Vue Router](https://router.vuejs.org/) (Rutas)
- [Vite](https://vitejs.dev/) (Herramienta de construcción)
- [Axios](https://axios-http.com/) (Consumo de API)

### **Backend**
- [Spring Boot 3](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security) (Autenticación y autorización)
- [Stripe API](https://stripe.com/docs/api) (Pagos)
- [JPA/Hibernate](https://hibernate.org/) (Persistencia de datos)
- [H2 Database](https://www.h2database.com/) (Base de datos en memoria para pruebas)

---

## 📂 **Estructura del Proyecto**

```plaintext
MiProyecto/
├── frontend/
│   ├── vue-app/               # Aplicación frontend en Vue.js
│   │   ├── src/               # Código fuente del frontend
│   │   ├── public/            # Archivos públicos
│   │   ├── [package.json](http://_vscodecontentref_/1)       # Dependencias del frontend
│   │   └── vite.config.ts     # Configuración de Vite
│   └── node-server/           # Servidor Node.js para servir el frontend
├── src/
│   ├── main/                  # Código fuente del backend
│   │   ├── java/              # Código Java
│   │   ├── resources/         # Archivos de configuración
│   └── test/                  # Pruebas del backend
├── [pom.xml](http://_vscodecontentref_/2)                    # Configuración de Maven
└── [README.md](http://_vscodecontentref_/3)                  # Este archivo
