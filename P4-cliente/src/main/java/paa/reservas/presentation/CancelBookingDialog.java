package paa.reservas.presentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import paa.reservas.business.BookingServiceException;
import paa.reservas.business.RemoteBookingService;
import paa.reservas.model.*;

public class CancelBookingDialog extends JDialog {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7512265565743450587L;
	private JComboBox<Hotel> comboHoteles; 
	private JComboBox<Booking> comboBooking;
	
	private JButton ok_btn;
	private JButton cancel_btn;
	
	private RemoteBookingService bs;
	private HotelsManager manager;
	private LocalDate simulatedDate;
	
	public CancelBookingDialog (HotelsManager manager, RemoteBookingService bs, Hotel selectedHotel, Booking selectedBooking , LocalDate simulatedDate) {
		super (manager, "Cancel booking", true);
		this.manager=manager;
		this.bs = bs;
		this.simulatedDate = simulatedDate;
		
		JPanel panelInfo = new JPanel (new GridLayout (2,2));
		JLabel hotel_label = new JLabel ("  Hotel: ");
		JLabel booking_label = new JLabel ("  Booking: ");

		booking_label.setHorizontalAlignment(SwingConstants.RIGHT);
		hotel_label.setHorizontalAlignment(SwingConstants.RIGHT);
		panelInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		
		comboHoteles = new JComboBox<Hotel>(new Vector<Hotel>(bs.findAllHotels()));
		comboHoteles.setSelectedItem(selectedHotel);
		comboHoteles.addActionListener(new ComboHotelesValueChange ());
		
		//Lista de reservas del hotel seleccionado
		comboBooking = new JComboBox<Booking>(new Vector<Booking> (selectedHotel.getBookings()));
		comboBooking.setSelectedItem(selectedBooking);
		
		
		panelInfo.add(hotel_label);		panelInfo.add(comboHoteles);
		panelInfo.add(booking_label);	panelInfo.add(comboBooking);

		//Botones
		JPanel panelBotones = new JPanel ();
		ok_btn= new JButton ("OK");
		cancel_btn = new JButton ("Cancel");
		panelBotones.add(ok_btn);
		panelBotones.add(cancel_btn);
		ok_btn.addActionListener(new OKBtnClick());
		cancel_btn.addActionListener(new CancelBtnClick());
		//añadimos todo
		add (panelInfo, BorderLayout.CENTER);
		add (panelBotones, BorderLayout.SOUTH);
		setSize (400, 200);
		setLocationRelativeTo(getParent());
		
		setVisible(true);
				
	}
	
	class OKBtnClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (comboHoteles.getSelectedItem()!=null && comboBooking.getSelectedItem()!=null) {
				Long idBooking = ((Booking)comboBooking.getSelectedItem()).getCode();
				try {
					bs.cancelBooking(idBooking, simulatedDate);
					manager.updateCombo((Hotel)comboHoteles.getSelectedItem());
					dispose ();	
				} catch (BookingServiceException e1) {
					JOptionPane.showMessageDialog(null, e1.getMessage(),"ERROR_MESSAGE", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}

	class CancelBtnClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			dispose ();			
		}
	}
	
	
	class ComboHotelesValueChange implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {

			if (comboHoteles.getSelectedItem()!=null) {
				Hotel selectedHotel = (Hotel) comboHoteles.getSelectedItem();
				comboBooking.removeAllItems();
				for (Booking b : selectedHotel.getBookings()) {
					comboBooking.addItem(b);
				}
				//comboBooking.setSelectedItem(e);
			}else {
				JOptionPane.showMessageDialog(null,  "No hay reservas asociadas a este hotel","WARNING_MESSAGE", JOptionPane.WARNING_MESSAGE );
			}
		}
	}
}