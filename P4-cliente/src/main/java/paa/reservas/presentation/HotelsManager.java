package paa.reservas.presentation;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import paa.reservas.business.RemoteBookingService;
import paa.reservas.model.Booking;
import paa.reservas.model.Hotel;
import paa.reservas.util.HotelMap;

public class HotelsManager extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private RemoteBookingService bs;
	private JMenuBar menuBar;
	private LocalDate fechaSimulada;
	private JComboBox<Hotel> combo;
	private JComboBox<LocalDate> comboFechas;
	private List<Hotel> listaHoteles;
	private JList<Booking> listadoReservas;
	private JScrollPane scroller;
	private JLabel fechaLabel;
	private HotelMap mapa;
	//private HotelsManager vp;
	
	
	public HotelsManager () {
		super ("Hotel Manager");
		bs = new RemoteBookingService ();
		//cogemos la dimensiond de la pantalla en la que se ejecuta el programa.
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		setSize((int)width, (int)height-50);
		
		//HIDE_ON_CLOSE: Cierra la ventana pero no termina el programa
		//EXIT_ON_CLOSE: Cierra la ventana y termina el programa	
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//Menu ToolBar
		menuBar = new JMenuBar ();
		//SubOpciones
		JMenu menuOperaciones = new JMenu("Operations");
		JMenuItem new_hotel_menu_Buttom = new JMenuItem ("Nuevo Hotel");
		new_hotel_menu_Buttom.addActionListener(new nuevoHotelClick());
		
		
		JMenuItem make_booking_menu_Buttom = new JMenuItem (new AbstractAction("Make booking") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed (ActionEvent e) {
				new MakeBookingDialog (HotelsManager.this,bs, listaHoteles, ((Hotel)combo.getSelectedItem()), fechaSimulada);
			}
		});
		JMenuItem cancel_booking_menu_Buttom = new JMenuItem (new AbstractAction("Cancel booking") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed (ActionEvent e) {
				//Codigo a ejecutar cuando pulsemos el boton
				new CancelBookingDialog (HotelsManager.this, bs, (Hotel)combo.getSelectedItem(),listadoReservas.getSelectedValue() , fechaSimulada);
			}
		}); 
		
		JMenuItem quit_menu_Buttom = new JMenuItem (new AbstractAction("Salir") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed (ActionEvent e) {
		        System.exit(0);
			}
		});		
		//Ayuda
		JMenu menuHelp = new JMenu ("Help");
		JMenuItem about = new JMenuItem (new AbstractAction("About") {
			private static final long serialVersionUID = 1L;
			public void actionPerformed (ActionEvent e) {
		        JOptionPane.showMessageDialog(HotelsManager.this, "Programa:Practica 3 PAA 23/24 \nAutor: Diego Cubillo");
			}
		}); 

		
		menuOperaciones.add(new_hotel_menu_Buttom);
		menuOperaciones.add(make_booking_menu_Buttom);
		menuOperaciones.add(cancel_booking_menu_Buttom);
		menuOperaciones.add(quit_menu_Buttom);
		menuHelp.add(about);
		
		menuBar.add(menuOperaciones);
		menuBar.add(menuHelp);
		
		this.setJMenuBar (menuBar);

		//BARRA DE BOTONES CON IMAGENES DEBAJO DE LA JMENUBAR
		JPanel barraBotones = new JPanel ();
		barraBotones.setLayout(new BorderLayout());
		JButton newHotelBTN = new JButton (new  ImageIcon (getClass().getResource("/newhotel.png")));
		JButton newBookingBTN = new JButton (new  ImageIcon (getClass().getResource("/makebooking.png")));;
		JButton cancelBooking = new JButton (new  ImageIcon (getClass().getResource("/cancelbooking.png")));
	
		JPanel panelBotones = new JPanel ();	
		panelBotones.setLayout(new FlowLayout(FlowLayout.LEFT));
		panelBotones.add(newHotelBTN);
		panelBotones.add(newBookingBTN);
		panelBotones.add(cancelBooking);
		
		barraBotones.add(panelBotones);
		
		//Panel fechas
		JPanel panelSimulador = new JPanel (new BorderLayout());
		JLabel titleLabel = new JLabel ("Current date simulator");
		JPanel panelFecha = new JPanel ();
		fechaSimulada = LocalDate.now();
		fechaLabel = new JLabel (fechaSimulada.toString());
		JButton buttonLeft = new JButton ("<");
		JButton buttonRight = new JButton (">");
		panelFecha.add(buttonLeft);
		panelFecha.add(fechaLabel);
		panelFecha.add(buttonRight);
		
		panelSimulador.add(titleLabel,BorderLayout.CENTER );
		panelSimulador.add(panelFecha,BorderLayout.SOUTH );
		
		barraBotones.add(panelSimulador, BorderLayout.EAST);
		
		//Lista de hoteles desplegable
		listaHoteles = bs.findAllHotels();
		combo = new JComboBox<Hotel>(new Vector<Hotel>(listaHoteles));
		TitledBorder comboHotelesTitulo  = BorderFactory.createTitledBorder("Hoteles");
		combo.setBorder(comboHotelesTitulo);
		combo.setEditable(false);
		
		//Establecemos un elemento seleccionado inicial
		if (listaHoteles.size()>0) {
			combo.setSelectedItem(0);
		}
		JPanel panelWest = new JPanel ( new BorderLayout());
		panelWest.add(combo, BorderLayout.NORTH);

		//Reservas
		TitledBorder listaReservasTitulo  = BorderFactory.createTitledBorder("Reservas");
		listadoReservas = new JList<Booking> ();
		if (combo.getSelectedItem()!=null) {
			Hotel h = (Hotel)combo.getSelectedItem();
			listadoReservas.setListData(new Vector<Booking> (h.getBookings()));
		}else {
			listadoReservas.setListData(new Vector<Booking> ());
		}
		
		listadoReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		scroller = new JScrollPane (listadoReservas);
		scroller.setBorder (listaReservasTitulo);
		panelWest.add(scroller, BorderLayout.CENTER);
		//Crear el mapa
		JPanel centerPanel = new JPanel(new BorderLayout());
		TitledBorder tituloMapa = BorderFactory.createTitledBorder("Hotel Map");
		centerPanel.setBorder(tituloMapa);
		mapa = new HotelMap(centerPanel.getWidth(), centerPanel.getHeight(), bs);
		
		if (combo.getSelectedItem()!=null) {
			mapa.showAvailability(LocalDate.now());
		}
		comboFechas = new JComboBox<LocalDate>();
		int dias = 0;
		while (dias<365) {
			comboFechas.addItem (LocalDate.now().plusDays(dias));
			dias++;
		}
		comboFechas.setEditable(false);
		comboFechas.setSelectedItem(LocalDate.now());
		comboFechas.addActionListener(new ComboFechasValueChange());
		
		centerPanel.add(comboFechas, BorderLayout.NORTH);
		centerPanel.add(mapa, BorderLayout.CENTER);

		//añadir los actionListener
		listadoReservas.addListSelectionListener(new ListaBookingSelection());
		buttonLeft.addActionListener(new DecFechaClick ());
		buttonRight.addActionListener(new IncFechaClick());
		combo.addActionListener(new ComboHotelesValueChange ());
		newHotelBTN.addActionListener(new nuevoHotelClick());
		newBookingBTN.addActionListener(new nuevoBookingClick());
		cancelBooking.addActionListener(new CancelBooking ());
		
		add (barraBotones, BorderLayout.NORTH);
		add (centerPanel, BorderLayout.CENTER);
		add (panelWest, BorderLayout.WEST);
		
	}
	
	class DecFechaClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			fechaSimulada = fechaSimulada.minusDays(1);
			fechaLabel.setText (fechaSimulada.toString());
		}
		
	}
	
	class IncFechaClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			fechaSimulada = fechaSimulada.plusDays(1);
			fechaLabel.setText (fechaSimulada.toString());
		}
		
	}
	
	class ComboHotelesValueChange implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {

			if (combo.getSelectedItem()!=null) {
				updateList ((Hotel)combo.getSelectedItem());
			}
		}
	}
	
	class ComboFechasValueChange implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (comboFechas.getSelectedItem()!=null) {
				mapa.showAvailability((LocalDate)comboFechas.getSelectedItem());
			}
		}
	}
	
	class nuevoHotelClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unused")
			CreateHotelJDialog nuevoHotelDialog = new CreateHotelJDialog(HotelsManager.this, bs);
		}		
	}
	
	class nuevoBookingClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unused")
			MakeBookingDialog nuevaBookingDialog=new MakeBookingDialog (HotelsManager.this,bs, listaHoteles, ((Hotel)combo.getSelectedItem()), fechaSimulada);
		}
	}
	
	class CancelBooking implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			@SuppressWarnings("unused")
			CancelBookingDialog cancel = new CancelBookingDialog (HotelsManager.this, bs, (Hotel)combo.getSelectedItem(),listadoReservas.getSelectedValue() , fechaSimulada);
		}		
	}
	
	class ListaBookingSelection implements ListSelectionListener{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			if (listadoReservas.getSelectedValue()!=null) {
				new BookingDetail (HotelsManager.this, listadoReservas.getSelectedValue());
			}
		}
		
	}
	
	public void updateList (Hotel updateHotel) {
		listadoReservas.setListData(new Vector<Booking>(updateHotel.getBookings()));
		mapa.showAvailability((LocalDate)comboFechas.getSelectedItem());
	}
	
	
	public void updateCombo (Hotel h) {
		
		combo.removeAllItems();
		listaHoteles = bs.findAllHotels();
		int selectedIndex=0;
		int i=0;
		for (Hotel hotel : listaHoteles ) {
			combo.addItem(hotel);
			if (h.getCode().equals(hotel.getCode())) {
				combo.setSelectedItem(h);
				selectedIndex=i;
			}
			i++;
		}
		combo.setSelectedIndex(selectedIndex);
		updateList ((Hotel)combo.getSelectedItem());
		
	}
//	public static void main(String[] args) {
//
//		HotelsManager manager = new HotelsManager();
//		manager.setVisible(true);
//		
//	}
	
//lo hacemos en una clase aparte para que quede mas ordenado.
	
}
