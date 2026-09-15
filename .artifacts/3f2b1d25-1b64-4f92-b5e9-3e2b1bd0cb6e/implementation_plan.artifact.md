# Implementation Plan - Map with OpenStreetMap and Navigation Drawer

Implementar una interfaz moderna para ReForestMobile con un mapa de OpenStreetMap a pantalla completa, un menú lateral (Drawer) translúcido y amigable, y un sistema de navegación basado en Material3.

## User Review Required

> [!IMPORTANT]
> - **Estética:** El Drawer será translúcido con tonos verdes/terrosos (reforestación).
> - **Notificaciones:** Se añadirá un icono de notificaciones circular en la parte superior derecha.
> - **Cerrar Sesión:** Se incluirá un botón de cierre de sesión centrado en la parte inferior del menú lateral.
> - **Mapa:** Se usará OSMDroid para visualizar los reportes de incendios.

## Proposed Changes

### 1. Dependencias y Configuración
Añadir OSMDroid para mapas.

#### [MODIFY] [libs.versions.toml](file:///home/heiner/AndroidStudioProjects/ReForestMobile/gradle/libs.versions.toml)
- Añadir `osmdroid = "6.1.18"` y su librería.

#### [MODIFY] [build.gradle.kts (app)](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/build.gradle.kts)
- Implementar `libs.osmdroid.android`.

---

### 2. Estructura de Navegación y UI
Implementar el `ModalNavigationDrawer` con estilo translúcido.

#### [NEW] [AppDrawer.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/components/AppDrawer.kt)
- Crear el contenido del Drawer.
- Fondo translúcido (Surface con Alpha).
- Colores verdes/naturaleza.
- Botón "Cerrar Sesión" centrado en la parte inferior.

#### [MODIFY] [MainScreen.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/screens/MainScreen.kt)
- Configurar `Scaffold` con `topBar`.
- `TopAppBar` con icono de 3 barras (izquierda) y Notificaciones (derecha, circular).
- Implementar `ModalNavigationDrawer`.

---

### 3. Visualización del Mapa
Integrar el mapa OSM.

#### [NEW] [MapViewContainer.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/components/MapViewContainer.kt)
- Implementar `MapView` de OSMDroid dentro de un `AndroidView`.
- Configurar marcadores dinámicos para los incendios.

#### [NEW] [IncendioViewModel.kt](file:///home/heiner/AndroidStudioProjects/ReForestMobile/app/src/main/java/com/reforest/mobile/ui/viewmodels/IncendioViewModel.kt)
- Cargar incendios desde la API y exponerlos para el mapa.

## Verification Plan

### Automated Tests
- N/A (UI visual verification primarily).

### Manual Verification
- Iniciar sesión y ver el mapa a pantalla completa.
- Desplegar el menú y verificar la transparencia y colores.
- Probar el botón de cerrar sesión.
- Verificar que los incendios aparecen como pines en el mapa.
- Probar el icono de notificaciones en la esquina superior derecha.
