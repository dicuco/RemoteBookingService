package paa.reservas.presentation.pruebas;



import paa.reservas.business.BookingServiceException;
import paa.reservas.model.*;


import javax.swing.*;
import java.time.LocalDate;



public class Prueba3JFrame extends JFrame{
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public Prueba3JFrame(Booking[] bookings) {
	        super("Lista de Reservas");
	        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	        // Datos de ejemplo para la lista (reemplazar con datos reales)
	        // Crear la lista con los datos de las reservas
	        
	        JList<Booking> reservaList = new JList<>(bookings);


	        // Añadir la lista a la ventana con un JScrollPane
	        add(new JScrollPane(reservaList));

	        pack();

	    }


	    public static void main(String[] args) throws BookingServiceException{
	    	//hacer el ejemplo con la base de datos mejor que con un array para hacerlo mas real, aunque recordar
	    	//hay que pasar las listas a vector lo dijo en clase usando por ejemplo el toArray
	    	
	        Hotel hotel = new Hotel(1L, "Hotel", "Dirección ", 5, -3.7038, 40.4168, 50, 100);
	        Booking booking1 = new Booking(1L, 101, 1, "Diego", LocalDate.of(2024, 4, 10), LocalDate.of(2024, 4, 15), hotel);
	        Booking booking2 = new Booking(2L, 202, 2, "Cubillo", LocalDate.of(2024, 5, 20), LocalDate.of(2024, 5, 25), hotel);
	        Booking[] bookings = {booking1,booking2};
	        new Prueba3JFrame(bookings).setVisible(true);
	    }
}
