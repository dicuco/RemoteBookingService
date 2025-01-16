package paa.reservas.presentation;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import paa.reservas.business.BookingServiceException;
import paa.reservas.business.RemoteBookingService;
import paa.reservas.model.Hotel;

public class CreateHotelJDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7608633848467448040L;
	private JTextField nameTF ;
	private JTextField addressTF ;
	private JTextField starsTF;
	private JTextField singleRoomTF;
	private JTextField doubleRoomTF;
	private JTextField longitudeTF;
	private JTextField latitudeTF ;
	private RemoteBookingService bs;
	private HotelsManager manager;
	
	public CreateHotelJDialog (HotelsManager manager, RemoteBookingService bs) {
		super (manager, "Create Hotel", true);
		this.bs = bs;
		this.manager=manager;
		JPanel panelInfo = new JPanel (new GridLayout(7, 2));
		
		JLabel nameLabel = new JLabel ("Name: ");
		JLabel addressLabel = new JLabel ("Address: ");
		JLabel starsLabel = new JLabel ("Stars: ");
		JLabel singleRoomLabel = new JLabel ("Single rooms: ");
		JLabel doubleRoomLabel = new JLabel ("Double rooms: ");
		JLabel longitudeLabel = new JLabel ("Longitude: ");
		JLabel latitudeHotelLabel = new JLabel ("Latitude: ");
		
		nameTF = new JTextField();
		addressTF = new JTextField();
		starsTF= new JTextField();
		singleRoomTF= new JTextField();
		doubleRoomTF= new JTextField();
		longitudeTF= new JTextField();
		latitudeTF = new JTextField();
		
		
		panelInfo.add(nameLabel); 		panelInfo.add(nameTF); 
		panelInfo.add(addressLabel); 	panelInfo.add(addressTF); 
		panelInfo.add(starsLabel); 		panelInfo.add(starsTF); 
		panelInfo.add(singleRoomLabel); panelInfo.add(singleRoomTF); 
		panelInfo.add(doubleRoomLabel); panelInfo.add(doubleRoomTF); 
		panelInfo.add(longitudeLabel); 	panelInfo.add(longitudeTF); 
		panelInfo.add(latitudeHotelLabel); panelInfo.add(latitudeTF); 
		
		JPanel panelBotones = new JPanel ();
		JButton ok_btn= new JButton ("OK");
		JButton cancel_btn= new JButton ("Cancel");
		panelBotones.add(ok_btn);
		panelBotones.add(cancel_btn);
		
		cancel_btn.addActionListener(new CancelClick());
		ok_btn.addActionListener(new OkClick());
	
		setLayout (new BorderLayout ());
		add (panelInfo, BorderLayout.CENTER);
		add (panelBotones, BorderLayout.SOUTH);
		
		setLocationRelativeTo(getParent());
		setSize (400, 200);

		setVisible (true);
	}
	
	class CancelClick implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			dispose(); 
		}
	}
	
	class OkClick implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			
			try {
				if(nameTF.getText().isEmpty() || addressTF.getText().isEmpty()||starsTF.getText().isEmpty()
					    || singleRoomTF.getText().isEmpty() || doubleRoomTF.getText().isEmpty()
					    || longitudeTF.getText().isEmpty() || latitudeTF.getText().isEmpty()) {
					JOptionPane.showMessageDialog(null, "Debe rellenar todos los campos", "WARNING_MESSAGE",
				            JOptionPane.WARNING_MESSAGE);
				
			}else {
				Hotel nuevoHotel = bs.createHotel(nameTF.getText(), addressTF.getText(), 
						Integer.parseInt(starsTF.getText()),
						Double.parseDouble(longitudeTF.getText()),
						Double.parseDouble(latitudeTF.getText()),
						Integer.parseInt(doubleRoomTF.getText()),
						Integer.parseInt(singleRoomTF.getText()));
				
				if (nuevoHotel !=null) {
					JOptionPane.showMessageDialog(CreateHotelJDialog.this,  "El hotel se ha creado correctamente");
					manager.updateCombo(nuevoHotel);
					dispose ();
				
				}else {
					JOptionPane.showMessageDialog(CreateHotelJDialog.this,  "No se ha podido crear el hotel");					
				}
			}
				
			}catch(BookingServiceException e2) {
				JOptionPane.showMessageDialog(CreateHotelJDialog.this, e2.getMessage(),
						  "WARNING_MESSAGE", JOptionPane.WARNING_MESSAGE);
			}	
		}
	}
	
}

