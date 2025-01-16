package paa.reservas.util;

import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.viewer.*;
import paa.reservas.business.BookingService;
import paa.reservas.business.BookingServiceException;
import paa.reservas.model.Hotel;
import paa.reservas.util.impl.HotelWaypointRenderer;
import paa.reservas.util.impl.HotelWaypoint;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * HotelMap es una subclase de JXMapKit, a su vez una subclase de JPanel que permite
 * visualizar un mapa. Esta subclase añade un método que muestra sobreimpresos al mapa
 * todos los hoteles conocidos por la capa de negocio en la fecha que el usuario elija,
 * así como su número actual de habitaciones disponibles.
 *
 * La documentación de la clase JXMapKit original se puede consultar en <a href=
 * "https://github.com/msteiger/jxmapviewer2">https://github.com/msteiger/jxmapviewer2</a>
 *
 * @author PAA
 */
public class HotelMap extends JXMapKit {
    private static final long serialVersionUID = 1L;
    protected WaypointPainter<HotelWaypoint> waypointPainter;
    protected Set<HotelWaypoint> waypoints;
    protected BookingService service;

    /**
     * Construye un nuevo mapa con la vista centrada en Madrid, con el tamaño
     * preferido indicado por el usuario.
     *
     * @param preferredWidth Ancho preferido
     * @param preferredHeight Alto preferido
     */
    public HotelMap(int preferredWidth, int preferredHeight, BookingService service) {
        super();
        this.setDefaultProvider(DefaultProviders.OpenStreetMaps);
        this.service = service;

        TileFactoryInfo info = new OSMTileFactoryInfo();
        TileFactory tf = new DefaultTileFactory(info);
        this.setTileFactory(tf);
        this.setZoom(7);
        this.setAddressLocation(new GeoPosition(40.438889, -3.691944)); // Madrid
        this.getMainMap().setRestrictOutsidePanning(true);
        this.getMainMap().setHorizontalWrapped(false);

        this.waypointPainter = new WaypointPainter<HotelWaypoint>();
        waypointPainter.setRenderer(new HotelWaypointRenderer());
        this.getMainMap().setOverlayPainter(this.waypointPainter);
        this.waypoints = new HashSet<HotelWaypoint>();

        ((DefaultTileFactory) this.getMainMap().getTileFactory()).setThreadPoolSize(8);
        //modificaciones parte opcional
        this.setPreferredSize(new Dimension(preferredWidth, preferredHeight));
  }
    /*
     * Estos son las implementacione de funciones para la parte opcional
     */
/*
 * Hay que hacer un metodo que devuelva el hotel seleccionado para ponerlo en la lista de mostrar las reservas
 * en el ejemplo hace que se muestre un nombre encima del pincho, nosotros queremos que nos devuelva el hotel
 * fijarse en el ejemplo del github para ver el funcionamiento
 */
    
    
    /**
     * Pinta en el mapa la situación de ocupación de los hoteles del sistema
     * en la fecha solicitada. Para ello hace uso de la interfaz BookingService
     * del paquete paa.hotel.business.
     *
     * @param date Fecha para la que se desea pintar la disponibilidad.
     */
    public void showAvailability(LocalDate date) {
        this.waypoints.clear();
        for (Hotel p: this.service.findAllHotels()) {
            try {
                waypoints.add(new HotelWaypoint(p.getName(),
                        p.getStars(),
                        p.getSingleRooms() - this.service.occupiedSingleRooms(p.getCode(), date),
                        p.getSingleRooms(),
                        p.getDoubleRooms() - this.service.occupiedDoubleRooms(p.getCode(), date),
                        p.getDoubleRooms(),
                        p.getCode(),
                        p.getLongitude(),
                        p.getLatitude()));
            } catch (BookingServiceException e) {
                // It's never going to happen because we are iterating over *existing* hotels
            }
        }
        this.waypointPainter.setWaypoints(waypoints);
        this.repaint();
    }
}
