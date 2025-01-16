package paa.reservas.presentation.pruebas;


import java.util.List;

import javax.swing.*;

import paa.reservas.business.BookingService;
import paa.reservas.business.BookingServiceException;
import paa.reservas.business.RemoteBookingService;
import paa.reservas.model.Hotel;

public class Prueba4JFrame extends JFrame{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public Prueba4JFrame(List<Hotel> Hoteles) {
        super("Lista de Hoteles");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        String[] nombresHoteles = new String[Hoteles.size()];
        for (int i = 0; i < Hoteles.size(); i++) {
            nombresHoteles[i] = Hoteles.get(i).getName();
        }
        // Crear el JComboBox con los nombres de los hoteles
        JComboBox<String> comboBox = new JComboBox<>(nombresHoteles);

        // Añadir el JComboBox a la ventana
        add(comboBox);

        pack();
    }
    public static void main(String[] args) throws BookingServiceException {
        // Suponiendo que tienes una lista de nombres de hoteles obtenidos desde la base de datos, se recupera con 
    	//la funcion finAll del HotelJPADAO.
        BookingService bs;
        bs = new RemoteBookingService();
        try {
            bs.createHotel("Hotel 1", "Nikola Tesla, 3 ", 5, -3.7038, 40.4168, 50, 100);
            bs.createHotel("Hotel 2", "Almendrales, 74 ", 5, -3.7038, 40.4168, 50, 100);
            bs.createHotel("Hotel 69", "Estafeta, 6", 5, -3.7038, 40.4168, 50, 100);
            List<Hotel> Hoteles = bs.findAllHotels();
            Prueba4JFrame ListaHoteles=new Prueba4JFrame(Hoteles);
            ListaHoteles.setVisible(true);
        	ListaHoteles.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        }catch(Exception e){
			throw new BookingServiceException("Error: ha ocurrido algo al acceder a la base de datos");

        }
    }
}
