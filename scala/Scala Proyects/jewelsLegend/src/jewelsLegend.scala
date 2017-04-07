

object jewelsLegend {
  
  class Diamante (val pos:Int,val color:Int)
  
 
  /*def explotar(tablero:List[Diamante]):List[Diamante]={
    val pos=tablero.head.pos
    val diamante = Diamante(pos,0)
    //explotarIguales(diamente, tablero)
    diamante::tablero.tail;
    return tablero
  }*/
  
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
  
  //Devuelve diamante de una posicion
  def devolver_bloque_lista(posicion:Int,tablero:List[Diamante]):Diamante={
    if(tablero.head.pos==posicion){return tablero.head}
    else devolver_bloque_lista(posicion,tablero.tail)
  }

  //Devuelve el numero de colores segun la dificultad
  def colores_tablero(dificultad:Int):Int={
    if(dificultad==1) return 4
    else{
      if(dificultad==2) return 6
      else return 8
    }
  }
  
  //Insertar un numero en una posicion
  def insertar_diamante(color:Int, pos:Int, lista:List[Diamante]):List[Diamante]={
    if(pos==0){
      val diamante = new Diamante(pos, color)
      diamante::lista.tail}
    else lista.head::insertar_diamante(color,pos-1,lista.tail)
  }
  
  //Funciones de contar iguales 
  /*def contar3(tablero:List[Int], filas:Int, columnas:Int):List[Int]={
    if(!tablero.isEmpty){
        println("Cabeza : " + tablero.head)
        val cont = contarAbajo(tablero.head,tablero.tail, columnas-1, filas, columnas);
        if(cont >= 2){
          explotar(tablero);
        }
        println("Iguales : " + cont);
     }
    return tablero;
  }
 def contarAbajo(Diamante:Int, tablero:List[Int],pos:Int, filas:Int, columnas:Int):Int={
   println("Posicion : " + pos + " y diamante: "+Diamante+ " y cabeza: "+tablero.head)
   if(pos==0){
     if(Diamante == tablero.head){
       println("SUMO UNO")
      return 1 + contarAbajo(Diamante,tablero.tail, columnas-1, filas, columnas);
     }else{return 0}
   }else if(pos > 0){return contarAbajo(Diamante,tablero.tail,pos-1,filas,columnas)}else{
     return 0;
   }
 }*/
  //Intercambiar posicioines de diamantes
  def intercambiar(pos1:Int, pos2:Int, tablero:List[Diamante]):List[Diamante]={
    val color1 = devolver_bloque_lista(pos1:Int,tablero).color
    val color2 = devolver_bloque_lista(pos2:Int,tablero).color
    
    val tableroAux1 = insertar_diamante(color1,pos2,tablero)
    val tableroAux2 = insertar_diamante(color2,pos1,tableroAux1)
    return tableroAux2
  }
  
  //generar un lista que sera el tablero
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
  
  
  //funcion para imprimir un tablero
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
    val taberoAux1 = generarTablero(0,filas,columnas,dificultad)
    print_tablero(tablero,columnas,1)
 
  }
}