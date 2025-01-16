package paa.reservas.presentation.pruebas;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class Prueba1JFrame extends JFrame {
	
	Icon icon= new ImageIcon(getClass().getResource("/newhotel.png"));//importante añadir la /fichero
	private JButton b1=new JButton("Añadir");
	private JButton b2=new JButton(icon);

	public Prueba1JFrame(String titulo){
		//dentro desarrollamos el panel(incluyendo los botones y todo, es decir el diseño)
		super(titulo);
		setLayout(new FlowLayout());
		add(b1);
		add(b2);
	}
    public static void main(String[] ar) {
    	Prueba1JFrame prueba= new Prueba1JFrame("prueba");
    	prueba.setSize(400,200);
    	prueba.setVisible(true);
    	prueba.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

}

