package paa.reservas.presentation;


import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import paa.reservas.business.BookingServiceException;
import paa.reservas.business.RemoteBookingService;
import paa.reservas.model.Hotel;


public class MakeBookingDialog extends JDialog {

	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3435643571770592379L;
	private HotelsManager manager;
	private RemoteBookingService bs;
	private LocalDate fechaSimulada;
	private JSpinner arrivalDate_tf ;
	private JSpinner departure_tf ;
	private JTextField traveller_tf ;
	private JComboBox<Hotel> comboHoteles;
	private ButtonGroup bg ;
	private JRadioButton single_rb;
	private JRadioButton double_rb ;
	
	public MakeBookingDialog (HotelsManager manager, RemoteBookingService bs, 
			List<Hotel> listaHoteles, Hotel selectedHotel, LocalDate fechaSimulada) {
		
		super (manager, "Make booking", true);
		this.manager = manager;
		this.bs = bs;
		this.fechaSimulada = fechaSimulada;
		JPanel panelInfo = new JPanel (new GridLayout (0,2));
		JLabel arrivalDate_label = new JLabel ("Arrival date:");
		JLabel departure_label = new JLabel ("Departure date:");
		JLabel pax_label = new JLabel ("Requested room: ");
		JLabel traveller_label = new JLabel ("Main traveller's name:");
		JLabel hotel_label = new JLabel ("Hotel:");
		
		arrivalDate_label.setHorizontalAlignment(SwingConstants.RIGHT);
		departure_label.setHorizontalAlignment(SwingConstants.RIGHT);
		pax_label.setHorizontalAlignment(SwingConstants.RIGHT);
		traveller_label.setHorizontalAlignment(SwingConstants.RIGHT);
		hotel_label.setHorizontalAlignment(SwingConstants.RIGHT);
	
//		arrivalDate_tf = new JTextField ();
		arrivalDate_tf=new JSpinner(new SpinnerDateModel());
		departure_tf = new JSpinner(new SpinnerDateModel());
		traveller_tf = new JTextField ();
		
		comboHoteles = new JComboBox<Hotel>(new Vector<Hotel>(listaHoteles));
		comboHoteles.setSelectedItem(selectedHotel);
	
		 single_rb = new JRadioButton("single", false);
		 double_rb = new JRadioButton("double", false);
		 bg = new ButtonGroup();
		 bg.add(single_rb); bg.add(double_rb);
		
		JPanel radioButtonsPanel = new JPanel ();
		radioButtonsPanel.add(single_rb);
		radioButtonsPanel.add(double_rb);
		
		panelInfo.setAlignmentX(Component.RIGHT_ALIGNMENT);
		
		panelInfo.add(arrivalDate_label);		panelInfo.add(arrivalDate_tf);
		panelInfo.add(departure_label);			panelInfo.add(departure_tf);
		panelInfo.add(pax_label);				panelInfo.add(radioButtonsPanel);
		panelInfo.add(traveller_label);			panelInfo.add(traveller_tf);
		panelInfo.add(hotel_label);				panelInfo.add(comboHoteles);

		JPanel panelBotones = new JPanel ();
		JButton ok_btn= new JButton ("OK");
		JButton cancel_btn = new JButton ("Cancel");
		panelBotones.add(ok_btn);
		panelBotones.add(cancel_btn);
		ok_btn.addActionListener(new OKBtnClick());
		cancel_btn.addActionListener(new CancelBtnClick());
		add (panelInfo, BorderLayout.CENTER);
		add (panelBotones, BorderLayout.SOUTH);
		setSize (400, 200);
		setLocationRelativeTo(getParent());
		setVisible (true);	
	}
	
	class OKBtnClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (comboHoteles.getSelectedItem()!=null) {
				int numPersonas=0;
				try {
					if (single_rb.isSelected()) {
						numPersonas = 1;
					}else if (double_rb.isSelected()){
						numPersonas = 2;
					}
					 LocalDate arrivalDate = getLocalDateFromSpinner(arrivalDate_tf);
				     LocalDate departureDate = getLocalDateFromSpinner(departure_tf);
				     if (traveller_tf.getText().isEmpty() || numPersonas==0
							    || ((Hotel)comboHoteles.getSelectedItem()).getCode()==null) {
							    JOptionPane.showMessageDialog(null, "Debe rellenar todos los campos", "WARNING_MESSAGE",
							            JOptionPane.WARNING_MESSAGE);
					}else {
					bs.makeBooking(((Hotel)comboHoteles.getSelectedItem()).getCode(),numPersonas, traveller_tf.getText(),arrivalDate,departureDate, fechaSimulada);
					manager.updateCombo((Hotel)comboHoteles.getSelectedItem());
					dispose ();	
					}
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
	
	 private LocalDate getLocalDateFromSpinner(JSpinner spinner) {
	        Date date = (Date) spinner.getValue();
	        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

	    }
	
}
