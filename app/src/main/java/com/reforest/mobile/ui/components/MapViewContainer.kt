package com.reforest.mobile.ui.components

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PorterDuff
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.reforest.mobile.data.model.Incendios
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.library.R
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.BoundingBox
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

/**
 * MapViewContainer: Componente optimizado para mostrar OpenStreetMap.
 */
@Composable
fun MapViewContainer(
    incendios: List<Incendios> = emptyList(),
    onMarkerClick: (Incendios) -> Unit = {},
    isPickerMode: Boolean = false,
    selectedLocation: GeoPoint? = null,
    onLocationSelected: (GeoPoint) -> Unit = {},
    showUserLocation: Boolean = true,
    zoomToAllTrigger: Int = 0,
    centerOnUserTrigger: Int = 0,
    forceCenter: GeoPoint? = null // Nuevo: Forzar centro en un punto específico
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    
    remember {
        Configuration.getInstance().userAgentValue = context.packageName
        true
    }

    val mapView = remember { MapView(context) }
    
    fun getTintedBitmap(drawableId: Int): Bitmap? {
        val drawable = ContextCompat.getDrawable(context, drawableId) ?: return null
        val bitmap = Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.setColorFilter(Color.RED, PorterDuff.Mode.SRC_IN)
        drawable.draw(canvas)
        return bitmap
    }

    val myLocationOverlay = remember {
        MyLocationNewOverlay(GpsMyLocationProvider(context), mapView).apply {
            enableMyLocation()
            val tintedIcon = getTintedBitmap(R.drawable.person)
            if (tintedIcon != null) {
                setPersonIcon(tintedIcon)
                setDirectionIcon(tintedIcon)
            }
            setDrawAccuracyEnabled(false)
            
            runOnFirstFix {
                mapView.post {
                    // Solo centrar al inicio si no hay un forceCenter
                    if (forceCenter == null) {
                        mapView.controller.animateTo(myLocation)
                        mapView.controller.setZoom(16.0)
                    }
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    mapView.onResume()
                    if (showUserLocation) myLocationOverlay.enableMyLocation()
                }
                Lifecycle.Event.ON_PAUSE -> {
                    mapView.onPause()
                    myLocationOverlay.disableMyLocation()
                }
                Lifecycle.Event.ON_DESTROY -> mapView.onDetach()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDetach()
        }
    }

    // Nuevo: Lógica para forzar el centro en un punto (Ej: desde la lista de reportes)
    LaunchedEffect(forceCenter) {
        forceCenter?.let {
            mapView.controller.animateTo(it)
            mapView.controller.setZoom(18.0) // Zoom más cercano para ver el detalle
        }
    }

    LaunchedEffect(centerOnUserTrigger) {
        if (centerOnUserTrigger > 0 && showUserLocation) {
            val location = myLocationOverlay.myLocation
            if (location != null) {
                mapView.controller.animateTo(location)
                mapView.controller.setZoom(17.0)
            }
        }
    }

    LaunchedEffect(incendios, zoomToAllTrigger) {
        if (!isPickerMode && (zoomToAllTrigger > 0 || (incendios.isNotEmpty() && forceCenter == null))) {
            val points = incendios.mapNotNull { 
                if (it.latitud != null && it.longitud != null) GeoPoint(it.latitud, it.longitud) else null 
            }
            if (points.isNotEmpty()) {
                val box = BoundingBox.fromGeoPoints(points)
                mapView.zoomToBoundingBox(box.increaseByScale(1.5f), true)
            }
        }
    }

    AndroidView(
        factory = {
            mapView.apply {
                setTileSource(TileSourceFactory.MAPNIK)
                setMultiTouchControls(true)
                zoomController.setVisibility(CustomZoomButtonsController.Visibility.NEVER)
                
                controller.setZoom(6.0)
                val centroColombia = GeoPoint(4.5709, -74.2973)
                controller.setCenter(centroColombia)

                if (showUserLocation) {
                    overlays.add(myLocationOverlay)
                }
            }
        },
        modifier = Modifier.fillMaxSize(),
        update = { mv ->
            val toRemove = mv.overlays.filter { it is Marker || it is MapEventsOverlay }
            mv.overlays.removeAll(toRemove)

            if (isPickerMode) {
                val eventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
                    override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                        onLocationSelected(p)
                        return true
                    }
                    override fun longPressHelper(p: GeoPoint): Boolean = false
                })
                mv.overlays.add(eventsOverlay)

                selectedLocation?.let { loc ->
                    val marker = Marker(mv)
                    marker.position = loc
                    marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    marker.title = "Ubicación del Incendio"
                    
                    val icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                    icon?.setTint(Color.RED)
                    marker.icon = icon
                    
                    marker.setDraggable(true)
                    marker.setOnMarkerDragListener(object : Marker.OnMarkerDragListener {
                        override fun onMarkerDrag(marker: Marker?) {}
                        override fun onMarkerDragEnd(marker: Marker?) {
                            marker?.position?.let { onLocationSelected(it) }
                        }
                        override fun onMarkerDragStart(marker: Marker?) {}
                    })
                    mv.overlays.add(marker)
                }
            } else {
                incendios.forEach { incendio ->
                    val lat = incendio.latitud
                    val lon = incendio.longitud
                    if (lat != null && lon != null) {
                        val marker = Marker(mv)
                        marker.position = GeoPoint(lat, lon)
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        marker.title = incendio.ubicacion
                        marker.snippet = incendio.descripcion
                        
                        // Icono dinámico según estado para los marcadores del mapa
                        val statusColor = when (incendio.estado.lowercase()) {
                            "activo" -> Color.RED
                            "controlado" -> Color.YELLOW
                            else -> Color.GREEN
                        }
                        
                        val icon = ContextCompat.getDrawable(context, R.drawable.marker_default)
                        icon?.setTint(statusColor)
                        marker.icon = icon
                        
                        marker.setOnMarkerClickListener { m, _ ->
                            onMarkerClick(incendio)
                            m.showInfoWindow()
                            true
                        }
                        mv.overlays.add(marker)
                    }
                }
            }
            mv.invalidate()
        }
    )
}
