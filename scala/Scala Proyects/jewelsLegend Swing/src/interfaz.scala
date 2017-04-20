
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
  	 * Funci�n que recibe un JFrame, la dificultad, un numero entre 1 y 3 , 
	   * genera un tablero con las caracter�sticas del nivel indicado y ejecuta 
	   * el bucle principal del juego
  	 * 
  	 */
	def iniciarPartida(ventana:JFrame, dificultad:Int, modo:Char) = {
     println("EL DIABLO LOCO")
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
		println("EL DIABLO LOCO1")
	  //Si el numero de vidas es 0 se acaba el juego
		if(score >= 2000){
			JOptionPane.showMessageDialog(null,"JUEGO TERMINADO!!",null,JOptionPane.ERROR_MESSAGE)
		}
	  else{
				/*val puntuacionMaxima = antique.mejorJugada(
													tablero,
													0, 
													filas, 
													columnas, 
													Nil, 
													0, 
													0, 
													0, 
													0)
													
				val mejorPuntuacion = puntuacionMaxima._1
				val puntuacionMaximaFila = puntuacionMaxima._2
				val puntuacionMaximaColumna = puntuacionMaxima._3 */           
				
				mostrarTablero(filas,columnas,tablero,score,
						//puntuacionMaximaFila*columnas+puntuacionMaximaColumna, 
						//mejorPuntuacion, 
						//puntuacionTotal, 
						dificultad, 
						modo)
			}
			
		//}
		
		
	}	 
		
	/**
	 * Funci�n que genera los elementos para mostrar el tablero en un JFrame
	 */
	def mostrarTablero(
			filas:Int, 
			columnas:Int, 
			tablero:List[jewelsLegend.Diamante],
			puntuacion:Int, 
			//posMax:Int, 
			//mejorPuntuacion:Int, 
			//puntuacionTotal: Int, 
			dificultad:Int, 
			modo:Char) = {
		
		println("EL DIABLO LOCO2")
		val tableroGrafico = new JFrame("ANTIQUE BLOCKS")
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
     
		tableroGrafico.setVisible(true)
		panel_tablero.setVisible(true)
		
		//Se introducen los botones en el panel_tablero, y la informaci�n de la partida
  		anadirBotones(
				tableroGrafico,
				panel_tablero, 
				filas, 
				columnas, 
				tablero, 
				tablero,  
				puntuacion,   
				dificultad, 
				modo)
  		
  		
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
      		
      		
  		val numero_puntuacion_total_label = new JLabel("Puntuaci�n total");
  		numero_puntuacion_total_label.setBounds(350, 35, 250, 30);
  		numero_puntuacion_total_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_total_label);
  		
  		/*val numero_puntuacion_total = new JLabel(""+puntuacionTotal);
  		numero_puntuacion_total.setBounds(620, 35, 70, 30);
  		numero_puntuacion_total.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_total);*/
  		
  		val puntuacion_label = new JLabel("Puntuaci�n");
  		puntuacion_label.setBounds(350, 70, 150, 30);
  		puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(puntuacion_label);
  		
  		val numero_puntuacion_label = new JLabel(""+puntuacion);
  		numero_puntuacion_label.setBounds(620, 70, 70, 30);
  		numero_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(numero_puntuacion_label);      		
  		
  		val maxima_puntuacion_label = new JLabel("Mejor puntuaci�n");
  		maxima_puntuacion_label.setBounds(350, 105, 250, 30);
  		maxima_puntuacion_label.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(maxima_puntuacion_label);
  		
  		/*val maxima_puntuacion = new JLabel(""+mejorPuntuacion);
  		maxima_puntuacion.setBounds(620, 105, 70, 30);
  		maxima_puntuacion.setFont(new Font("Tribeca", Font.PLAIN, 18));
  		panel.add(maxima_puntuacion);*/
      	
  		
  		//Si el modo elegido es automatico se crea el boton que permite hacer 
  		//continuar la partida de manera automatica
  		if(modo =='a'){
			val botonContinuar = new JButton("Continuar")
      		botonContinuar.setBounds(350, 660, 100, 23)
      		botonContinuar.addActionListener(new java.awt.event.ActionListener() {
                    
				def actionPerformed(evento:ActionEvent):Unit = {
					botonActionPerformed(
							tableroGrafico, 
							tablero,
							filas, 
							columnas,  
							filas*columnas-1, 
							puntuacion, 
							//posMax, 
							//puntuacionTotal, 
							dificultad, 
							modo)      
				}
   
			}
			);
			panel.add(botonContinuar)
		}
		tableroGrafico.setVisible(true)
		tableroGrafico.setResizable(false)
		
	}
	/**
	 * Secuencia de instrucciones al pulsar un bloque con el rat�n
	 */
	def  botonActionPerformed(
			ventana:JFrame, 
			tablero:List[jewelsLegend.Diamante],
			filas:Int, 
			columnas:Int , 
			pos:Int, 
			puntuacion:Int, 
			//posMax:Int, 
			//puntuacionTotal: Int, 
			dificultad:Int , 
			modo:Char): Unit = { 
		
		
		//si no es una posici�n vac�a
		if(jewelsLegend.devolverDiamanteLista(pos, tablero).color!=0) {
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
		}else{
			Nil
		}

	}
	
	
	/**
	 * Funci�n que a�ade los botones a un JPanel recursivamente.
	 * A cada bot�n se le a�ade una imagen correspondiente al numero que contiene 
	 * en el tablero, y se le a�ade tambi�n el m�todo que ejecutar� al pulsarlo si el modo 
	 * es manual. En caso de ser autom�tico los botones no tienen accion a ejecutar.
	 */
	def anadirBotones(
			v:JFrame, 
			panel:JPanel, 
			filas:Int, 
			columnas:Int, 
			tablero:List[jewelsLegend.Diamante],
			tablero_1:List[jewelsLegend.Diamante], 
			puntuacion:Int, 
			//posMax:Int, 
			//puntuacionTotal: Int, 
			dificultad:Int, 
			modo:Char ): Unit ={
			
		if(tablero.length == 0 ){
			Nil
		}else{
			
			val boton = new JButton();
			val dato = jewelsLegend.devolverDiamanteLista(filas*columnas - tablero.length, tablero_1).color
	        val botonColoreado = cambiarColorBoton(boton, dato, filas, columnas)
			if(filas*columnas - tablero.length == filas*columnas-1)
				botonColoreado.setBorder(new LineBorder(Color.yellow,4))
				
			panel.add(botonColoreado);
			if(modo == 'm'){
				botonColoreado.addActionListener(new java.awt.event.ActionListener() {
				def actionPerformed(evento:ActionEvent):Unit = {    
					botonActionPerformed(
							v, 
							tablero_1,
							filas, 
							columnas,  
						  filas*columnas-1, 
							puntuacion, 
							//posMax, 
							//puntuacionTotal, 
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
				tablero,  
				puntuacion,   
				dificultad, 
				modo)
  		
		}
	}
	
	
	/**
	 * Funci�n que recibe un numero y un bot�n y le coloca una imagen distinta
	 * dependiendo del numero recibido
	 */
	def cambiarColorBoton(
			boton: JButton, 
			numero: Int,
			filas:Int, 
			columnas:Int): JButton = {
		    
		
		val iconoBomba = new ImageIcon(
				(new ImageIcon("res/bomba.gif")).getImage().getScaledInstance(  
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueAzul = new ImageIcon(
				(new ImageIcon("res/azul.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueAmarillo = new ImageIcon(
				(new ImageIcon("res/amarillo.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
															
		val iconoBloqueBlanco = new ImageIcon(
				(new ImageIcon("res/blanco.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas,
															Image.SCALE_FAST))
		
		val iconoBloqueRojo= new ImageIcon(
				(new ImageIcon("res/rojo.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueVerde = new ImageIcon(
				(new ImageIcon("res/verde_claro.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueMorado = new ImageIcon(
				(new ImageIcon("res/morado.png")).getImage().getScaledInstance(
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		val iconoBloqueMarron = new ImageIcon(
				(new ImageIcon("res/marron.png")).getImage().getScaledInstance( 
															600/columnas,
															500/filas, 
															Image.SCALE_FAST))
		
		if(numero == 0)
			boton.setBackground(Color.WHITE)
		else if(numero == 1)
			boton.setIcon(iconoBloqueAzul)
		else if(numero == 2)
		    boton.setIcon(iconoBloqueRojo)
		else if(numero == 3)
			boton.setIcon(iconoBloqueMorado)
		else if(numero == 4)
			boton.setIcon(iconoBloqueVerde)
		else if(numero == 5)
			boton.setIcon(iconoBloqueBlanco)
		else if(numero == 6)
			boton.setIcon(iconoBloqueMarron)
		else if(numero == 7)
			boton.setIcon(iconoBloqueAmarillo)
		else
			boton.setIcon(iconoBomba)
	   
		boton
	}	
			    
}
