# Walkthrough - Mapa y Navegación Lateral

Se ha completado la implementación de la nueva interfaz principal para ReForestMobile, integrando mapas dinámicos y una navegación moderna.

## Cambios Realizados

### 1. Sistema de Mapas (OpenStreetMap)
- **[MapViewContainer.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/components/MapViewContainer.kt):** Integración de OSMDroid para visualizar el mapa a pantalla completa con marcadores dinámicos.
- **[IncendioViewModel.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/viewmodels/IncendioViewModel.kt):** Lógica para cargar los reportes de incendios desde la API y servirlos al mapa.
- **Permisos:** Se añadieron permisos de ubicación y red en el `AndroidManifest.xml`.

### 2. Navegación Lateral (Drawer)
- **[AppDrawer.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/components/AppDrawer.kt):** Menú lateral con diseño translúcido (gradiente verde), iconos de Material3 y botón de "Cerrar Sesión" destacado en la parte inferior.
- **[MainScreen.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/screens/MainScreen.kt):** Rediseño total usando `Scaffold`, `TopAppBar` con icono de menú y acceso rápido a notificaciones.

### 3. Identidad Visual
- **[Color.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/theme/Color.kt):** Definición de paleta de colores "Forest" y variantes para Material3.
- **[Theme.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/theme/Theme.kt):** Configuración del tema para usar los nuevos colores y ajustar la barra de estado.

## Cómo Probarlo
1.  **Iniciar Sesión:** Entra con tus credenciales.
2.  **Pantalla Principal:** Verás el mapa de OpenStreetMap ocupando toda la pantalla.
3.  **Menú Lateral:** Pulsa el icono de las 3 barras (arriba a la izquierda) para ver el menú translúcido.
4.  **Notificaciones:** Pulsa el círculo blanco en la esquina superior derecha para ir a notificaciones.
5.  **Cerrar Sesión:** Abre el menú y pulsa el botón inferior para salir.

> [!TIP]
> Si el mapa no carga, verifica que el dispositivo tenga conexión a Internet. Los marcadores aparecerán automáticamente si hay incendios registrados con coordenadas válidas en el backend.
