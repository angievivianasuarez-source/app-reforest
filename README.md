# ReForestMobile 🌳 - Panel Administrativo

**ReForestMobile** es una aplicación Android profesional diseñada para la gestión administrativa de emergencias forestales, registro de voluntarios y control de donaciones. La app integra mapas interactivos en tiempo real y captura de datos de campo para optimizar la respuesta ante incendios forestales.

## 🚀 Características Principales

### 🗺️ Mapa de Incendios en Tiempo Real
- **Integración con OpenStreetMap (OSM):** Visualización global de focos de incendio mediante `osmdroid`.
- **Iconografía Dinámica:** Marcadores rojos para incendios y ubicación del usuario resaltada en rojo para máxima visibilidad.
- **Navegación Inteligente:** Botón de centrado GPS y zoom automático para encuadrar todos los reportes activos.
- **Mapa Selector:** Herramienta interactiva para ubicar incendios tocando cualquier punto del mapa con precisión.

### 🛡️ Seguridad y Gestión de Sesión
- **Protección Centralizada:** Todas las rutas administrativas están protegidas mediante un componente `CommonScaffold` que verifica el token de sesión.
- **Persistencia Segura:** Gestión de tokens JWT mediante `TokenManager` con SharedPreferences.
- **Redirección Automática:** Flujo de inicio inteligente que detecta si el usuario está autenticado.

### 📋 Módulos Administrativos (CRUD)
- **Gestión de Incendios:** Listado en tarjetas profesionales con cambio de estado dinámico (Activo, Controlado, Extinguido).
- **Control de Voluntarios:** Registro y visualización detallada de datos de contacto y disponibilidad.
- **Módulo de Donaciones:** Seguimiento de insumos y especies recibidas con detalles técnicos.
- **Notificaciones:** Sistema de alertas para avisos críticos del sistema.

### 🎨 Interfaz y Experiencia de Usuario
- **Diseño Moderno:** Basado en **Material 3** con una paleta de colores "Forest" (Verde Bosque y Marrón Tierra).
- **Navegación Fluida:** Menú lateral (Drawer) translúcido con gradientes elegantes.
- **Interactividad Total:** Tarjetas táctiles con diálogos de detalle integrados y estados de carga en tiempo real.

## 🛠️ Stack Tecnológico

- **Lenguaje:** Kotlin 100%
- **UI Framework:** Jetpack Compose
- **Arquitectura:** MVVM (Model-View-ViewModel)
- **Redes:** Retrofit 2 + OkHttp 3 + Gson
- **Mapas:** osmdroid
- **Ubicación:** Google Play Services Location
- **Inyección de Dependencias:** Estructura modular y limpia.

## 📦 Instalación y Configuración

1. **Requisitos:** Android Studio Ladybug (o superior) y un dispositivo con Android 8.0+.
2. **Backend:** La app consume la API alojada en `reforestapi.codeconheiner.com`.
3. **Compilación:** 
   - Clonar el repositorio.
   - Sincronizar con Gradle.
   - Ejecutar `Build > Rebuild Project` para asentar los cachés de Kotlin.

---
*Desarrollado como una solución tecnológica para la preservación de nuestros bosques.* 🌲✨
