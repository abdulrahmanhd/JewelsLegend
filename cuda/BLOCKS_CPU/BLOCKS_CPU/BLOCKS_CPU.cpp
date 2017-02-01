#include "stdafx.h"
#include <iostream>
#include "block.h"

using namespace std;
const int columnas = 10;
const int filas = 10;
const int nColores=8;

//funcion que hace explotar bloques contiguos
Block *explotarIguales(Block *bloques,int posX,int posY,int filas,int columnas){

	if(bloques[(posX+1)*columnas+posY].color==bloques[posX*columnas+posY].color)
		bloques[(posX+1)*columnas+posY].color=0;

	if(bloques[(posX-1)*columnas+posY].color==bloques[posX*columnas+posY].color)
		bloques[(posX-1)*columnas+posY].color=0;

	if(bloques[posX*columnas+posY+1].color==bloques[posX*columnas+posY].color)
		bloques[posX*columnas+posY+1].color=0;

	if(bloques[posX*columnas+posY-1].color==bloques[posX*columnas+posY].color)
		bloques[posX*columnas+posY-1].color=0;


	bloques[posX*columnas+posY].color=0;


	return bloques;
}

void inicicializarArray(Block *bloques){
	int numAleatorio=0;

	cout << "\n\n\n";

	for (int i =0; i < filas; i++){  //llenamos el array de nums aleatorios
		for (int j = 0; j < columnas; j++) {
			numAleatorio = rand() % nColores;
			bloques[(i*filas) + j] = Block(i, j);
			bloques[(i*filas) + j].color = numAleatorio + 1;
			
		}
	}
}

void printBlocks(Block *bloques,HANDLE hConsole){  //modulo para imprimir el tablero
	for (int i = 0; i<filas; i++) {
			for (int j = 0; j<columnas; j++)
				bloques[i*columnas + j].printBloque(hConsole); 
			cout << "\n";
		}
	
}



//MUEVE CEROS HACIA ARRIBA
int colocarArriba(Block *bloques, int i, int filas, int columnas){
	bool limite=false;
	int x=i;                                       //No queremos perder la posicion del primer bloque por eso la guardamos en x
	
	while(limite==false && bloques[x].color==0){  //Es para saber la posicion con el que hay que cambiar 
		
		if((x-columnas)>=0) x=x-columnas;    //cambiamos las filas hasta encontrar la posicion deseada
		else limite=true;                   //Limite es control de desbordo
	}
	
	bloques[i].color=bloques[x].color;
	bloques[x].color=0;
	
	return 0;
}


////MUEVE CEROS HACIA DERECHA
int colocarDerecha(Block *bloques, int i, int filas, int columnas){
	
	bool limite=false;
	int x=i;                                         //No queremos perder la posicion del primer bloque por eso la guardamos en x
	 
	while(limite==false && bloques[x].color==0){    //Es para saber la posicion con el que hay que cambiar el 0
		
		if(x==((filas*columnas)-1)) limite=true;    //Limite es control de desbordo
		else x++;                                  //subimos posicion
	}
	
	//Intercambiamos las columnas
	while(i>0 && x>0){	//control de desbordo
		bloques[i].color=bloques[x].color;
		bloques[x].color=0;
		i=i-columnas;
		x=x-filas;
	}
	return 0;
}

//MOVER BLOQUES
int moveBlocks(Block *bloques, int filas, int columnas){

	int j=0;
		
	for(j=(((filas*columnas)-1));j>=0;j--){
		if(bloques[j].color==0){		//comprobamos todo el array									
			
			colocarArriba(bloques,j,filas,columnas);
			
		}
	}
	   
	for(j=(((filas*columnas)-1)-columnas);j<((filas*columnas)-1);j++){
		
		if(bloques[j].color==0){       //comprobamos la fila de mas abajo 
			colocarDerecha(bloques,j,filas,columnas);
		}
	}

	return 0;
}


Block *explotarBomba(Block *bloques, int posX, int posY, int filas, int columnas){

	//Metodos recursivos con los quue explotamos bombas y si una bomba explota otra bomba tambien se explota
	bloques[posX*filas + posY].color = 0;

	//Explotar centro-arriba   
	
	if (posX!=0){	//control de desbordo
		if(bloques[(posX-1)*columnas+posY].color==8){
			explotarBomba(bloques , posX-1, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX-1)*columnas+posY].color=0; 
		}
	}

	if ((posX-2)>=0){	//control de desbordo
		if(bloques[(posX-2)*columnas+posY].color==8){
			explotarBomba(bloques , posX-2, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX-2)*columnas+posY].color=0;
		}
	}
	

	//Explotar centro-abajo 

	if (posX!=filas-1){	//control de desbordo
		if(bloques[(posX+1)*columnas+posY].color==8){
			explotarBomba(bloques , posX+1, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX+1)*columnas+posY].color=0;
		}
	}

	if (posX+2<=filas-1){	//control de desbordo
		if(bloques[(posX+2)*columnas+posY].color==8){
			explotarBomba(bloques , posX+2, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX+2)*columnas+posY].color=0;
		}
	}
	
	//Explotar derecha
	if (posY!=columnas-1){	//control de desbordo
	
		if(bloques[posX*columnas+(posY+1)].color==8){
			explotarBomba(bloques , posX, posY+1, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX*columnas)+(posY+1)].color=0;
		}
	}

	if (posY+2 < columnas-1){	//control de desbordo
	
		if(bloques[posX*columnas+(posY+2)].color==8){
			explotarBomba(bloques , posX, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
			
		}
		else{
			bloques[(posX*columnas)+(posY+2)].color=0;
			
		}
	}


	//Explotar izquierda
	if (posY!=0){	//control de desbordo
		
		if(bloques[posX*columnas+(posY-1)].color==8){
			explotarBomba(bloques , posX, posY+1, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX*columnas)+(posY-1)].color=0;
		}
	}

	if (posY-2 > 0){	//control de desbordo
		
		if(bloques[posX*columnas+(posY-2)].color==8){
			explotarBomba(bloques , posX, posY, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
			
		}
		else{
			bloques[(posX*columnas)+(posY-2)].color=0;
			
		}
	}


	//Explotar derecha-abajo

	if (posX!=(filas-1) && posY!=(columnas-1)){	//control de desbordo
		
		if(bloques[(posX+1)*columnas+(posY+1)].color==8){
			explotarBomba(bloques , posX+1, posY+1, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX+1)*columnas+(posY+1)].color=0; 
		}
	}

	//Explotar derecha-arriba

	if (posX!=0 && posY!=(columnas-1)){	//control de desbordo
		
		if(bloques[(posX-1)*columnas+(posY+1)].color==8){
			explotarBomba(bloques , posX+1, posY+1, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX-1)*columnas+(posY+1)].color=0; 
		}
	}

	//Explotar izquierda-arriba
	
	if (posX!=0 && posY!=0){	//control de desbordo
		
		if(bloques[(posX-1)*columnas+(posY-1)].color==8){
			explotarBomba(bloques , posX-1, posY+1, filas, columnas);
			//bloques[(posX-1)*columnas+(posY-1)].color=0;
		}
		else{
			bloques[(posX-1)*columnas+(posY-1)].color=0; 
		}
	}

	//Explotar izquierda-abajo

	if (posX!=(filas-1) && posY!=0){	//control de desbordo
		
		if(bloques[(posX+1)*columnas+(posY-1)].color==8){
			explotarBomba(bloques , posX-1, posY+1, filas, columnas);
			//bloques[(posX+1)*columnas+(posY-1)].color=0; 
		}
		else{
			bloques[(posX+1)*columnas+(posY-1)].color=0; 
		}
	
	bloques[posX*filas + posY].color = 0;
	
	}
	return bloques;
	
}


int _tmain(int argc, _TCHAR* argv[])
{
	
	Block *bloques=new Block[filas*columnas];

	HANDLE hConsole;
	hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

	inicicializarArray(bloques);
	printBlocks(bloques,hConsole);
	while (true) {

		cout << "\n\n\n";
		SetConsoleTextAttribute(hConsole, 15);
		cout<<"Introduce la posicion del bloque que quieras explotar o 99 para salir";
		int x=0, y=0;
		cout<<"\nfila: ";
		cin>>x;

		if(x == 99)
			exit(0);
			
		cout<<"columna: ";
		cin>>y;

		

		if(bloques[x*columnas + y].color == 8){ //se trata de una bomba, explotamos la cruz de bloques
			explotarBomba(bloques, x, y, filas, columnas);
			
		}
		else{
		
			bloques = explotarIguales(bloques, x, y, filas, columnas);
			int size = filas*columnas*sizeof(Block);
		
		}
		
		moveBlocks(bloques, filas, columnas);
		printBlocks(bloques,hConsole);
	}
	free(bloques);
	getchar();
	getchar();


    return 0;
}
