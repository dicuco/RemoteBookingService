package paa.reservas.presentation.pruebas;

import javax.swing.*;
import javax.swing.border.Border;

import paa.reservas.business.BookingService;
import paa.reservas.business.RemoteBookingService;
import paa.reservas.util.HotelMap;

public class Prueba2JFrame extends JFrame{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HotelMap hotelMap;

    public Prueba2JFrame(BookingService em) {
        super("HotelManager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        JOptionPane.showMessageDialog(this, "Programa:Practica 3 PAA 23/24 \nAutor: Diego Cubillo");
    	Icon iconNewHotel= new ImageIcon(getClass().getResource("/newhotel.png"));//importante añadir la /fichero



        // Crear instancia de HotelMap con el servicio de reservas
        hotelMap = new HotelMap(800, 600, em);

        // Crear un borde con título para el HotelMap
        Border titledBorder = BorderFactory.createTitledBorder("Mapa de Hoteles");
        hotelMap.setBorder(titledBorder);

        // Añadir el HotelMap al JFrame
        getContentPane().add(hotelMap);
        // Añadir barra de menús


        JMenu menu = new JMenu("Operations");
        JMenuItem menuItem = new JMenuItem(iconNewHotel);
        JMenuItem menuItem1 = new JMenuItem("Make Booking");
        JMenuItem menuItem2 = new JMenuItem("Cancel Booking");
        JMenuItem menuItem3 = new JMenuItem("Quit");
        //añadimos los elementos creados al menú y tambien cada submenu 
        menu.add(menuItem);
        menu.add(menuItem1);
        menu.add(menuItem2);
        menu.add(menuItem3);
        
        JMenuBar menuBar = new JMenuBar();
        JMenu menu1 = new JMenu("Help");

        menuBar.add(menu);
        menuBar.add(menu1);
        setJMenuBar(menuBar);


        pack();
    }

    public static void main(String[] args) {
    	RemoteBookingService em = new RemoteBookingService();
        Prueba2JFrame mapa=new Prueba2JFrame(em);
        mapa.setVisible(true);
    }
}
