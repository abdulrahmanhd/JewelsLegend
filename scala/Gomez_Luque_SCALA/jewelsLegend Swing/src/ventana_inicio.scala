import java.awt.BorderLayout;
import java.awt.EventQueue;
import javax.swing.ButtonGroup;
import javax.swing.JRadioButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLayeredPane;
import java.awt.Button;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;



object ventana_inicio extends App {

	iniciarComponentes
  
	/**
	 * Funci�n con la que se generan los componentes de la ventana inicial, y 
	 *  muestra los botones para los ajustes iniciales del tablero 
	 */
	def iniciarComponentes() = {
  
		/**
	 	* Launch the application.
	 	*/
		val ventana = new JFrame("JEWELS LEGEND")
		ventana.setVisible(true);

		/**
	 	* Create the frame.
	 	*/
		ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		ventana.setBounds(500, 200, 550, 350);
		val contentPane = new JPanel
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		ventana.setContentPane(contentPane);
		
		val layeredPane = new JLayeredPane();
		contentPane.add(layeredPane, BorderLayout.CENTER);
		
		val radioButtonManual = new JRadioButton("Modo Manual")
		radioButtonManual.setBounds(190, 190, 150,23)
		radioButtonManual.setSelected(true)
		layeredPane.add(radioButtonManual)
		
		
		val radioButtonAutomatico = new JRadioButton("Modo Automatico")
		radioButtonAutomatico.setBounds(190, 230, 150,23)
		layeredPane.add(radioButtonAutomatico)
		
		val grupoBotones = new ButtonGroup()
		grupoBotones.add(radioButtonManual)
		grupoBotones.add(radioButtonAutomatico)
		
		
		val boton1 = new JButton("Nivel 1");
		boton1.addActionListener(new ActionListener() {
			def actionPerformed(e:ActionEvent ) {
				if(radioButtonManual.isSelected() ){
					interfaz.iniciarPartida(ventana,1, 'm')
				}else{
			      	interfaz.iniciarPartida(ventana,1, 'a')
			  	}
			}
		});
		boton1.setBounds(40, 60, 89, 23);
		layeredPane.add(boton1);
		
		val boton2 = new JButton("Nivel 2");
		boton2.addActionListener(new ActionListener() {
			def actionPerformed(arg0:ActionEvent ) {
			  	if(radioButtonManual.isSelected() ){
		      		interfaz.iniciarPartida(ventana,2, 'm')
			  	}else{
		      		interfaz.iniciarPartida(ventana,2, 'a')
		  	}
		}
		});
		boton2.setBounds(40, 100, 89, 23);
		layeredPane.add(boton2);
		
		val boton3 = new JButton("Nivel 3");
		boton3.addActionListener(new ActionListener() {
			def actionPerformed(arg0:ActionEvent ) {
			  	if(radioButtonManual.isSelected() ){
			      	interfaz.iniciarPartida(ventana,3, 'm')
			  	}else{
			      	interfaz.iniciarPartida(ventana,3, 'a')
			  	}
 
			}
		});
		boton3.setBounds(40, 140, 89, 23);
		layeredPane.add(boton3);
  
		val etiqueta1 = new JLabel("Tablero = 7x9, Colores = 4")
	  	etiqueta1.setBounds(150, 60, 350, 23);
		layeredPane.add(etiqueta1);
		
		val etiqueta2 = new JLabel("Tablero = 11x17, Colores = 6")
	  	etiqueta2.setBounds(150, 100, 350, 23);
		layeredPane.add(etiqueta2);
		
            
		val etiqueta3 = new JLabel("Tablero = 15x27, Colores = 7")
	  	etiqueta3.setBounds(150, 140, 350, 23);
		layeredPane.add(etiqueta3);
		
		val etiqueta4 = new JLabel("SELECCIONA UN NIVEL PARA LA PARTIDA")
	  	etiqueta4.setBounds(150, 5, 350, 23);
		layeredPane.add(etiqueta4);
 
}	  
}