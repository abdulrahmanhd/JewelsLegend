

object jewelsLegend {
  
  //devuelve el numero de colores segun la dificultad
  def colores_tablero(dificultad:Int):Int={
    if(dificultad==1) return 4
    else{
      if(dificultad==2) return 6
      else return 8
    }
  }
  
  //Insertar un numero en una posicion
  def insertar_diamante(diamante:Int, pos:Int, lista:List[Int]):List[Int]={
    if(pos==0)diamante::lista.tail
    else lista.head::insertar_diamante(diamante,pos-1,lista.tail)
  }
  
  //generar un lista que sera el tablero
  def generarTablero(pos: Int, filas:Int, columnas:Int, dificultad:Int):List[Int]={
    if(pos==((filas*columnas)-1)){
      val rnd = scala.util.Random
      1+rnd.nextInt(colores_tablero(dificultad)+1)::Nil
    }
    else{
      val rnd = scala.util.Random
      1+rnd.nextInt(colores_tablero(dificultad)+1)::generarTablero(pos+1,filas,columnas,dificultad)
    }
  }
  
  //funcion para imprimir un tablero
  def print_tablero(tablero:List[Int], columnas:Int, cont:Int){
    if(!tablero.isEmpty){
      if(cont==columnas){
        print(tablero.head + "\n")
        print_tablero(tablero.tail,columnas,1)
      }
      else{
        print(tablero.head + " ")
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
    
  }
}