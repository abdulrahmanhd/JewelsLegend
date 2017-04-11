

object jewelsLegend {
  
  class Diamante (val pos:Int,val color:Int)
  
 //Funncion que explota elementos iguales
  def explotar(ListaExplotar:List[Int], tablero:List[Diamante]):List[Diamante]={
    if(ListaExplotar.head == tablero.head.pos){
      val diamante = new Diamante(tablero.head.pos,0);
      if(ListaExplotar.tail.isEmpty){
        return diamante::tablero.tail;
      }else{
        return diamante::explotar(ListaExplotar.tail,tablero.tail);
      }
    }else{
      tablero.head::explotar(ListaExplotar.tail,tablero.tail);
    }
  }
  
  //Cambia los ceros por otro numero alatorio
  def reponer(dificultad:Int, tablero:List[Diamante],pos:Int):List[Diamante]={
    if(tablero.tail==Nil){return tablero}
    else{
      if(tablero.head.color==0){
        val rnd = scala.util.Random
        val diamante = (pos,1+rnd.nextInt(colores_tablero(dificultad)))::reponer(dificultad,tablero.tail,pos+1)
        return tablero
      }
      else{
        tablero.head::reponer(dificultad,tablero.tail,pos+1)
        return tablero
      }
    }
  }
  
  //Devuelve diamante de una posicion (OK)
  def devolverDiamanteLista(posicion:Int,tablero:List[Diamante]):Diamante={
    if(tablero.head.pos==posicion){return tablero.head}
    else devolverDiamanteLista(posicion,tablero.tail)
  }

  //Devuelve el numero de colores segun la dificultad (OK)
  def colores_tablero(dificultad:Int):Int={
    if(dificultad==1) return 4
    else{
      if(dificultad==2) return 6
      else return 8
    }
  }
  
  //Insertar un numero en una posicion (OK)
  def insertar_diamante(color:Int, pos:Int, lista:List[Diamante]):List[Diamante]={
    if(pos==0){
      val diamante = new Diamante(pos, color)
      diamante::lista.tail}
    else lista.head::insertar_diamante(color,pos-1,lista.tail)
  }
  
  //Funciones de contar iguales 
  def contar3(tablero:List[Diamante], filas:Int, columnas:Int):List[Diamante]={
    if(!tablero.isEmpty){
        println("Cabeza : " + tablero.head)
        val cont = contarAbajo(tablero.head.color,tablero.tail, columnas-1, filas, columnas);
        if(cont >= 2){
         // explotar(ListaExplotado,tablero);
        }
        println("Iguales : " + cont);
     }
    return tablero;
  }
  
 //Contamos iguales abajo
 def contarAbajo(color:Int, tablero:List[Diamante],pos:Int, filas:Int, columnas:Int):Int={
   println("Posicion : " + pos + " y diamante: "+color+ " y cabeza: "+tablero.head)
   if(pos==0){
     if(color == tablero.head.color){
       println("SUMO UNO")
      return 1 + contarAbajo(color,tablero.tail, columnas-1, filas, columnas);
     }else{return 0}
   }else if(pos > 0){return contarAbajo(color,tablero.tail,pos-1,filas,columnas)}else{
     return 0;
   }
 }
 
 //Comprobar si un numero esta en la última columna
 def ultima_columna(posDiamante:Int,columna:Int,filas:Int):Boolean={
   if(posDiamante>columna)  false
   else{
     if(posDiamante==columna) true
     else  ultima_columna(posDiamante,columna+(filas-1),filas)
   }
 }
 
 //Comprobar si un numero esta en la primera columna
  def primera_columna(posDiamante:Int,columna:Int,filas:Int):Boolean={
   if(posDiamante>columna)  false
   else{
     if(posDiamante==columna) true
     else  ultima_columna(posDiamante,columna+(filas-1),filas)
   }
 }
 
 //Comprobar si un numero esta en la primera fila
  def primera_fila(posDiamante:Int,columnas:Int):Boolean={
    if(posDiamante<columnas) true
    else false
  }
  
  //Comprobar si un numero esta en la ultima fila
  def ultima_fila(posDiamante:Int,columnas:Int,filas:Int):Boolean={
    if(posDiamante<(filas*columnas)-columnas) true
    else false
  }
  
  
 //Comprueba que dos posiciones sean contiguas
  def diamantes_contiguos(pos1:Int, pos2:Int, filas:Int, columnas:Int):Boolean={
   //Poicion central
   if(!primera_columna(pos1,0,filas) && !ultima_columna(pos1,0,filas) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*Mirar derecha izquierda arriba abajo*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2-columnas) || (pos1==pos2-columnas))  true
     else false
   }
   //Posicion en ultima fila o columna
   else if(primera_fila(pos1,columnas) && !primera_columna(pos1,0,filas) && !ultima_columna(pos1,0,filas)){
     /*mirar abajo derecha izquierda*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2+columnas))  true
     else false
   }
   else if(ultima_fila(pos1,columnas,filas) && !primera_columna(pos1,0,filas) && !ultima_columna(pos1,0,filas)){
     /*mirar arriba derecha izquierda*/
     if ((pos1==pos2+1) || (pos1==pos2-1) || (pos1==pos2-columnas))  true
     else false
   }
   else if(primera_columna(pos1,0,filas) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*mirar arriba abajo derecha*/
     if ((pos1==pos2+1) || (pos1==pos2-columnas) || (pos1==pos2+columnas))  true
     else false
   }
   else if(ultima_columna(pos1,0,filas) && !primera_fila(pos1,columnas) && !ultima_fila(pos1,columnas,filas)){
     /*mirar arriba abajo izquierda*/
     if ((pos1==pos2-1) || (pos1==pos2-columnas) || (pos1==pos2+columnas))  true
     else false
   }
   //Posicion en esquina
   else if(primera_fila(pos1,columnas) && primera_columna(pos1,0,filas)){
     /*mirar derecha y abajo*/
     if ((pos1==pos2+1) || (pos1==pos2+columnas))  true
     else false
   }
   else if(primera_fila(pos1,columnas) && ultima_columna(pos1,0,filas)){
     /*mirar izquierda y abajo*/
     if ((pos1==pos2-1) || (pos1==pos2+columnas))  true
     else false  
   }
   else if(ultima_fila(pos1,columnas,filas) && primera_columna(pos1,0,filas)){
     /*mirar derecha y arriba*/
     if ((pos1==pos2+1) || (pos1==pos2-columnas))  true
     else false
   }
   else if(ultima_fila(pos1,columnas,filas) && ultima_columna(pos1,0,filas)){
     /*mirar izquierda y arriba*/
     if ((pos1==pos2-1) || (pos1==pos2-columnas))  true
     else false
   }
   else false
 }

//Comprueba el color de un diamante (OK)
  def obtenerColor(pos:Int, tablero:List[Diamante]):Int={
    if(tablero.head.pos==pos) tablero.head.color
    else  obtenerColor(pos,tablero.tail)
  }
  
//Comprobar el numero de iguales de una posicion
  def comprobarIgualesPos(pos:Int, direccion:String, cont:Int, filas:Int, columnas:Int, tablero:List[Diamante]):Int={
    if(direccion=="arriba" && !primera_fila(pos,columnas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos-columnas,tablero)){ comprobarIgualesPos(pos-columnas,direccion,cont+1,filas,columnas,tablero)}
      else{ return cont}
    }
    else if(direccion=="abajo" && !ultima_fila(pos,columnas,filas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos+columnas,tablero)){ comprobarIgualesPos(pos+columnas,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else if(direccion=="derecha" && !ultima_columna(pos,columnas,filas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos+1,tablero)){ comprobarIgualesPos(pos+1,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else if(direccion=="izquierda" && !primera_columna(pos,columnas,filas)){
      if(obtenerColor(pos,tablero)==obtenerColor(pos-1,tablero)){ comprobarIgualesPos(pos-1,direccion,cont+1,filas,columnas,tablero)}
      else{return cont}
    }
    else return 0
  }

 //Comprueba si un mov es posible segun los diamantes iguales
  def comprobarIguales(pos1:Int, pos2:Int, filas:Int, columnas:Int, tablero:List[Diamante]):Boolean={
    val arriba1 = comprobarIgualesPos(pos1,"arriba",0,filas,columnas,tablero)
    val arriba2 = comprobarIgualesPos(pos2,"arriba",0,filas,columnas,tablero)
    val abajo1 = comprobarIgualesPos(pos1,"abajo",0,filas,columnas,tablero)
    val abajo2 = comprobarIgualesPos(pos2,"abajo",0,filas,columnas,tablero)
    val derecha1 = comprobarIgualesPos(pos1,"derecha",0,filas,columnas,tablero)
    val derecha2 = comprobarIgualesPos(pos2,"derecha",0,filas,columnas,tablero)
    val izquierda1 = comprobarIgualesPos(pos1,"izquierda",0,filas,columnas,tablero)
    val izquierda2 = comprobarIgualesPos(pos2,"izquierda",0,filas,columnas,tablero)
    
    if(arriba1>2 || arriba2>2 || abajo1>2 || abajo2>2 || derecha1>2 || derecha2>2 || izquierda1>2 || izquierda2>2) true
    else false
  }
  
 //Comprobacion para saber si se puede realizar un movimiento
 def comprobarMovimiento(diamante1:Diamante, diamante2:Diamante, tablero:List[Diamante], filas:Int, columnas:Int):Boolean={
   val pos1 = diamante1.pos
   val pos2 = diamante2.pos
   val color1 = diamante1.color
   val color2 = diamante2.color
   
   //Comprobamos que esten contiguos
   val contiguo = diamantes_contiguos(pos1,pos2,filas,columnas)
   
   //Comprobamos que los iguales sean mayor que 2
   val tableroAux1 = insertar_diamante(color1,pos2,tablero)
   val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1)
   val iguales=comprobarIguales(pos1,pos2,filas,columnas,tableroAux2)
 
   return contiguo && iguales
 }
 
 
  //Intercambiar posicioines de diamantes
  def intercambiar(pos1:Int, pos2:Int, tablero:List[Diamante], filas:Int, columnas:Int):List[Diamante]={
    val color1 = devolverDiamanteLista(pos1:Int,tablero).color
    val color2 = devolverDiamanteLista(pos2:Int,tablero).color
    println("paso1")
    if(comprobarMovimiento(devolverDiamanteLista(pos1,tablero),devolverDiamanteLista(pos2,tablero), tablero, filas, columnas)){
      val tableroAux1 = insertar_diamante(color1,pos2,tablero)
      val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1)
      return tableroAux2
    }
    else return tablero
  }
  
  //generar un lista que sera el tablero (OK)
  def generarTablero(pos: Int, filas:Int, columnas:Int, dificultad:Int):List[Diamante]={
    if(pos==((filas*columnas)-1)){
      val rnd = scala.util.Random
      val diamante = new Diamante(pos,1+rnd.nextInt(colores_tablero(dificultad)))
      diamante::Nil
    }
    else{
      val rnd = scala.util.Random
      val diamante = new Diamante(pos,1+rnd.nextInt(colores_tablero(dificultad)))
      diamante::generarTablero(pos+1,filas,columnas,dificultad)
    }
  }
  
  
  //funcion para imprimir un tablero (OK)
  def print_tablero(tablero:List[Diamante], columnas:Int, cont:Int){
    if(!tablero.isEmpty){
      if(cont==columnas){
        print(tablero.head.color + "\n")
        print_tablero(tablero.tail,columnas,1)
      }
      else{
        print(tablero.head.color + " ")
        print_tablero(tablero.tail,columnas,cont+1)
      }
    }
    
  }
  
  def main(args: Array[String]){
    println("Introduzca dificultad")
    val dificultad=readInt
    println("Introduce filas")
    val filas=readInt
    println("Introduce columnas")
    val columnas=readInt
    
    val tablero = generarTablero(0,filas,columnas,dificultad)
    print_tablero(tablero,columnas,1)
    
    println("Introduzca fila 1:")
    val fila1=readInt
    println("Introduzca columna 1:")
    val columna1=readInt
    println("Introduzca fila 2:")
    val fila2=readInt
    println("Introduzca columna 1:")
    val columna2=readInt
    
    val pos1=(fila1*columnas)+columna1
    val pos2=(fila2*columnas)+columna2
    val tableroAux1 = intercambiar(pos1, pos2, tablero, filas, columnas)
    print_tablero(tableroAux1,columnas,1)
  }
}