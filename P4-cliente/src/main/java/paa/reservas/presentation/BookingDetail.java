package paa.reservas.presentation;

import java.awt.GridLayout;

import javax.swing.JDialog;
import javax.swing.JLabel;


import paa.reservas.model.Booking;

public class BookingDetail extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7694645809993023079L;

	public BookingDetail (HotelsManager manager, Booking b) {
		
		super (manager, "Informacion reserva", true);
		
		JLabel codigo_label = new JLabel ("Codigo: ");
		JLabel nombre_Jl = new JLabel ("Nombre Viajero: ");
		JLabel pax_label = new JLabel ("Numero de Personas: ");
		JLabel arrival_label = new JLabel ("Llegada: ");
		JLabel salida_label = new JLabel ("Salida: ");
		JLabel numHabitacion_label = new JLabel ("Nº habitacion: ");
		
		JLabel info_codigo_label = new JLabel (b.getCode().toString());
		JLabel info_nombre_label = new JLabel (b.getTravellerName());
		JLabel info_pax_label = new JLabel (""+b.getNumberOfPeople());
		JLabel info_arrival_label = new JLabel (b.getArrivalDate().toString());
		JLabel info_salida_label = new JLabel (b.getDepartureDate().toString());
		JLabel info_numHabitacion_label = new JLabel (""+b.getRoomNumber());
		
		setLayout(new GridLayout(0,2));
		
		add(codigo_label); add(info_codigo_label);
		add(nombre_Jl); add(info_nombre_label);
		add(pax_label); add(info_pax_label);
		add(arrival_label); add(info_arrival_label);
		add(salida_label); add(info_salida_label);
		add(numHabitacion_label); add(info_numHabitacion_label);
		
		setLocationRelativeTo(getParent());
		setSize(500, 200);
		setVisible(true);

	}
}