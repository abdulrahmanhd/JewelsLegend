
import java.awt.GridLayout;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.Dimension
import javax.swing._
import javax.swing.JOptionPane;
import javax.swing.ImageIcon;
import javax.swing.border.LineBorder
import java.awt.Image;
import java.awt.Color;

object interfaz extends App {
   class Diamante (val pos:Int,val color:Int)
	
	//                 0    1    2    3    4    5    6    7    8
	def letras = List(' ', 'A', 'R', 'N', 'V', 'P', 'M', 'G', 'B')
	
	
  	/**
  	 * Funcion que recibe un JFrame, la dificultad 
	   * genera un tablero con las caracterasticas del nivel indicado 
	   * y ejecuta el bucle principal del juego
  	 * 
  	 */
	def iniciarPartida(ventana:JFrame, dificultad:Int, modo:Char) = {

		val partida = jewelsLegend.getLevel(dificultad)
		val filas = partida._1
		val columnas = partida._2
		val puntuacion = 0
		val tablero = jewelsLegend.generarTablero(0, filas, columnas, dificultad)
		ventana.dispose()
		bucle(tablero,dificultad,filas,columnas,puntuacion,modo)

	}
	  
	/**
	 * Funcion recursiva que ejecuta las instrucciones principales del juego 
	 * hasta llegar a una puntuacion max.
	 */
	def bucle(tablero:List[jewelsLegend.Diamante],dificultad:Int,filas:Int,columnas:Int,score:Int,modo:Char):Unit = {

		
	  //Si el numero de vidas es 0 se acaba el juego

		if(score >= 2000){
			JOptionPane.showMessageDialog(null,"JUEGO TERMINADO!!",null,JOptionPane.ERROR_MESSAGE)
		}
	  else{
				        		
				mostrarTablero(filas,columnas,tablero,score,
						dificultad, 
						modo)
			}

	}	 
		
	/**
	 * Funcion que genera los elementos para mostrar el tablero en un JFrame
	 */
	def mostrarTablero(
			filas:Int, 
			columnas:Int, 
			tablero:List[jewelsLegend.Diamante],
			puntuacion:Int, 
			dificultad:Int, 
			modo:Char) = {
		

		val tableroGrafico = new JFrame("JEWELS LEGEND")
		tableroGrafico.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tableroGrafico.setBounds(200, 0, 800,720);
		tableroGrafico.getContentPane().setLayout(null);
		tableroGrafico.getContentPane().setLayout(new BorderLayout(0, 0));
		
		val panel = new JPanel();
		panel.setBounds(86, 150, 380, 205);
		tableroGrafico.getContentPane().add(panel);
		panel.setLayout(null)
		
		val panel_tablero = new JPanel();
		panel_tablero.setBounds(84, 150, 600, 500);
		panel.add(panel_tablero);
		panel_tablero.setLayout(new GridLayout(filas, columnas, 0, 0));

		
		//Se introducen los botones en el panel_tablero, y la informacion de la partida
  		anadirBotones(tableroGrafico,panel_tablero,filas,columnas,tablero,puntuacion,dificultad,modo,0)

     
		
		//Se introducen los botones en el panel_tablero, y la informacion de la partida
  		anadirBotones(
				tableroGrafico,
				panel_tablero, 
				filas, 
				columnas, 
				tablero,   
				puntuacion,   
				dificultad, 
				modo,
				0)

  		
  		
  		val modo_label_texto = new JLabel("Modo");
  		modo_label_texto.setBounds(100, 105, 150, 30);
  		modo_label_texto.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(modo_label_texto);
      		
      		
  		val modo_label = new JLabel();
  		if(modo == 'm'){
			modo_label.setText("M")
  		}else{
  			modo_label.setText("A")
  		}
		modo_label.setBounds(290, 105, 70, 30);
		modo_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
		panel.add(modo_label);
		panel.setVisible(true)
      		
      		
  		val numero_puntuacion_total_label = new JLabel("Puntuacion total");
  		numero_puntuacion_total_label.setBounds(350, 35, 250, 30);
  		numero_puntuacion_total_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_total_label);
  
  		
  		val puntuacion_label = new JLabel("Puntuacion");
  		puntuacion_label.setBounds(350, 70, 150, 30);
  		puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(puntuacion_label);
  		
  		val numero_puntuacion_label = new JLabel(""+puntuacion);
  		numero_puntuacion_label.setBounds(620, 70, 70, 30);
  		numero_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_label);      		

  		val maxima_puntuacion_label = new JLabel("Mejor puntuacion");
  		maxima_puntuacion_label.setBounds(350, 105, 250, 30);
  		maxima_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(maxima_puntuacion_label);
  		



  		//Si el modo elegido es automatico se crea el boton que permite hacer 
  		//continuar la partida de manera automatica
  		if(modo =='a'){
			val botonContinuar = new JButton("Continuar")
      		botonContinuar.setBounds(350, 660, 100, 23)
      		botonContinuar.addActionListener(new java.awt.event.ActionListener() {
                    
				def actionPerformed(evento:ActionEvent):Unit = {

					botonActionPerformed(tableroGrafico,tablero,filas,columnas,filas*columnas-1,
					    puntuacion,dificultad,modo)      


				  val tableroCambiado = jewelsLegend.automaticMode(0,dificultad,Nil, tablero, 0, 0, 0, filas, columnas,puntuacion);
				  tableroGrafico.dispose()
				  bucle(tableroCambiado._1, dificultad, filas, columnas, tableroCambiado._2, modo)

				}
   
			});
			panel.add(botonContinuar)
		}
		tableroGrafico.setVisible(true)
		tableroGrafico.setResizable(false)
		
	}
	/**
	 * Secuencia de instrucciones al pulsar un bloque con el raton
	 */

	
	def  botonActionPerformed(
			ventana:JFrame, 
			tablero:List[jewelsLegend.Diamante],
			filas:Int, 
			columnas:Int , 
			pos:Int, 
			puntuacion:Int, 
			dificultad:Int , 
			modo:Char): Unit = { 

		
		if(modo != 'a'){
  		  //si no es una posicion vacia
  			//Se extrae la fila y la columna
   			val columna = pos % columnas
   			val fila = (pos - columna)/columnas
   		
   			/*  Se elimina la celda de la posici�n recibida 
  				Se obtiene el tablero y los puntos como consecuencia 
  				de la eliminaci�n */
  	    val dato = jewelsLegend.devolverDiamanteLista(fila*columnas+columna,tablero).color
  	 		val ListaExplotar = jewelsLegend.generarListaIguales(tablero,filas,columnas,0)
  	 		val resultado = jewelsLegend.explotar(ListaExplotar, tablero)
  	 		val puntuacionF = puntuacion + jewelsLegend.getZero(resultado, filas, columnas, 0)
  	 		/*val listaEliminar = antique.consultaTablero(
  											dato,
  											fila, 
  											columna,
  											filas, 
  											columnas, 
  											tablero)*/
  											
  	 		/*val resultado = antique.eliminaCeldas(
  											listaEliminar,
  											listaEliminar.length,pos, 
  											tablero, 
  											puntuacion)
  			val tablero_1 = resultado._1
  			val vidas_1= resultado._2
  			val puntuacionFinal = resultado._3*/
  	 		
  	 		
  			//Se reestructura el tablero
  			val tablero2 = jewelsLegend.bucleMoverCeros(filas*columnas-1,tablero,tablero,filas,columnas)
  			val tablero_3 = jewelsLegend.moveLeft(0,tablero2,filas,columnas)
  			val tablero_final = jewelsLegend.reponer(dificultad, tablero_3, 0)
  			//se cierra la ventana 
  			ventana.dispose()
  			//Y se vuelve a ejecutar el bucle del juego
  			
  			bucle(tablero_final,dificultad,filas,columnas,puntuacionF,modo)
  		}
		
		//si no es una posicion vacia
		if(jewelsLegend.devolverDiamanteLista(pos, tablero).color!=0) {
			//Se extrae la fila y la columna
 			val columna = pos % columnas
 			val fila = (pos - columna)/columnas
 		
 			/*  Se elimina la celda de la posicion recibida y se obtiene el tablero y los puntos  */
	    val dato = jewelsLegend.devolverDiamanteLista(fila*columnas+columna,tablero).color
	 		val ListaExplotar = jewelsLegend.generarListaIguales(tablero,filas,columnas,0)
	 		val resultado = jewelsLegend.explotar(ListaExplotar, tablero)
	 		val puntuacionF = puntuacion + jewelsLegend.getZero(resultado, filas, columnas, 0)
	 			 		
			//Se reestructura el tablero
			val tablero2 = jewelsLegend.bucleMoverCeros(filas*columnas-1,tablero,tablero,filas,columnas)
			val tablero_3 = jewelsLegend.moveLeft(0,tablero2,filas,columnas)
			val tablero_final = jewelsLegend.reponer(dificultad, tablero_3, 0)
			
			//se cierra la ventana 
			ventana.dispose()
			//Y se vuelve a ejecutar el bucle del juego
			
			bucle(tablero_final,dificultad,filas,columnas,puntuacionF,modo)
		}else{
			Nil
		}

>>>>>>> 5d19d82ce28bb3e707da51cd57c9d93a3c31c203
	}
	
	
	/**
	 * Funcion que añade los botones a un JPanel recursivamente.
	 * A cada boton se le añade una imagen correspondiente al numero que contiene 
	 * en el tablero, y se le añade  el metodo que ejecutar al pulsarlo si el modo 
	 * es manual. En caso de ser automatico los botones no tienen accion a ejecutar.
	 */
	def anadirBotones(
			v:JFrame, 
			panel:JPanel, 
			filas:Int, 
			columnas:Int, 
			tablero:List[jewelsLegend.Diamante],
			puntuacion:Int, 
			dificultad:Int, 
			modo:Char,
			pos: Int): Unit ={
			
		if(pos > filas*columnas-1 ){
			Nil
		}else{
			
			val boton = new JButton();
			val dato = jewelsLegend.devolverDiamanteLista(pos, tablero).color
	    val botonColoreado = cambiarColorBoton(boton, dato, filas, columnas)
			/*if(pos <= filas*columnas-1)
				botonColoreado.setBorder(new LineBorder(Color.yellow,4))*/
				
			panel.add(botonColoreado);
			if(modo == 'm'){
				botonColoreado.addActionListener(new java.awt.event.ActionListener() {
				def actionPerformed(evento:ActionEvent):Unit = {    
					botonActionPerformed(
							v, 
							tablero,
							filas, 
							columnas,  
						  filas*columnas-1, 
							puntuacion, 
							dificultad, 
							modo)   
				}});
			}
			anadirBotones(
				v,
				panel, 
				filas, 
				columnas, 
				tablero,
				puntuacion,   
				dificultad, 
				modo,
				pos+1)
  		
		}
	}
	
	
	/**
	 * Funcion que recibe un numero y un boton y le coloca una imagen distinta
	 * dependiendo del numero recibido
	 */
	def cambiarColorBoton(
			boton: JButton, 
			numero: Int,
			filas:Int, 
			columnas:Int): JButton = {
		    
															
<<<<<<< HEAD
		val iconoBloqueAzul = new ImageIcon(
				(new ImageIcon("res/azul.png")).getImage().getScaledInstance( 
															500/columnas,
															400/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueAmarillo = new ImageIcon(
				(new ImageIcon("res/amarillo.png")).getImage().getScaledInstance( 
															500/columnas,
															400/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueBlanco = new ImageIcon(
				(new ImageIcon("res/blanco.png")).getImage().getScaledInstance( 
															500/columnas,
															400/filas,
=======
		val d1 = new ImageIcon(
				(new ImageIcon("res/d1.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val d2 = new ImageIcon(
				(new ImageIcon("res/d2.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val d3 = new ImageIcon(
				(new ImageIcon("res/d3.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas,
>>>>>>> 5d19d82ce28bb3e707da51cd57c9d93a3c31c203
															Image.SCALE_FAST))
		
		val d4= new ImageIcon(
				(new ImageIcon("res/d4.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
<<<<<<< HEAD
		val iconoBloqueVerde = new ImageIcon(
				(new ImageIcon("res/verde_claro.png")).getImage().getScaledInstance( 
															500/columnas,
															400/filas, 
															Image.SCALE_FAST))
		val iconoBloqueMorado = new ImageIcon(
				(new ImageIcon("res/morado.png")).getImage().getScaledInstance(
															500/columnas,
															400/filas,
															Image.SCALE_FAST))
		val iconoBloqueMarron = new ImageIcon(
				(new ImageIcon("res/marron.png")).getImage().getScaledInstance( 
															500/columnas,
															400/filas, 
=======
		val d5 = new ImageIcon(
				(new ImageIcon("res/d5.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val d6 = new ImageIcon(
				(new ImageIcon("res/d6.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val d7 = new ImageIcon(
				(new ImageIcon("res/d7.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
>>>>>>> 5d19d82ce28bb3e707da51cd57c9d93a3c31c203
															Image.SCALE_FAST))
		
		if(numero == 0)
			boton.setBackground(Color.WHITE)
		else if(numero == 1)
			boton.setIcon(d1)
		else if(numero == 2)
		    boton.setIcon(d2)
		else if(numero == 3)
			boton.setIcon(d3)
		else if(numero == 4)
			boton.setIcon(d4)
		else if(numero == 5)
			boton.setIcon(d5)
		else if(numero == 6)
			boton.setIcon(d6)
		else if(numero == 7)
			boton.setIcon(d7)

		return boton
	}	
			    
}
