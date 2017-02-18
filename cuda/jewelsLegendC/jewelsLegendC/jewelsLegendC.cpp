// jewelsLegendC.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include "Diamante.h"
#include <string.h>

using namespace std;
const int columnas = 10;
const int filas = 10;
const int nColores = 8;
enum posicion { todos, arriba, abajo, derecha, izquierda };

void printDiamante(Diamante *diam, HANDLE hConsole);
int comprobarIgualesPos(Diamante *diam, int posX, int posY, posicion pos);

Diamante *explotarIguales(Diamante *diam, int posX, int posY, posicion pos) { //esto no esta hecho

	switch (pos)
	{
	case todos:
		if(comprobarIgualesPos(diam, posX, posY, derecha)>1)
		diam = explotarIguales(diam, posX, posY, derecha);

		if(comprobarIgualesPos(diam, posX, posY, izquierda)>1)
		diam = explotarIguales(diam, posX, posY, izquierda);

		if(comprobarIgualesPos(diam, posX, posY, arriba)>1)
		diam = explotarIguales(diam, posX, posY, arriba);

		if(comprobarIgualesPos(diam, posX, posY, abajo)>1)
		diam = explotarIguales(diam, posX, posY, abajo);

		diam[posX*columnas + posY].color = 0;
		break;
	case derecha:
		if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY + 1].color) { // explotamos derecha
			diam = explotarIguales(diam, posX, posY + 1, derecha);
			diam[posX*filas + posY+1].color = 0;
		}
		break;
	case izquierda:
		if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY - 1].color) { //explotamos izquierda
			diam = explotarIguales(diam, posX, posY - 1, izquierda);
			diam[posX*columnas + posY - 1].color = 0;
		}
		break;
	case abajo:
		if (diam[(posX*filas) + posY].color == diam[((posX + 1) * filas) + posY].color) { //explotamos abajo
			diam = explotarIguales(diam, posX + 1, posY, abajo);
			diam[(posX + 1)*columnas + posY].color = 0;
		}
		break;
	case arriba:
		if (diam[(posX*filas) + posY].color == diam[((posX - 1) * filas) + posY + 1].color ) { //explotamos arriba
			diam = explotarIguales(diam, posX - 1, posY, abajo);
			diam[(posX - 1)*columnas + posY].color = 0;
		}
		break;
	default:
		break;
	}

	


	return diam;
}
Diamante *comprobarIguales(Diamante *diam) {
	int cont = 0;
	for (int i = 0; i < filas; i++) {  //Recorremos el array en busca de cadenas de tres numeros iguales
		for (int j = 0; j < columnas; j++) {
			if (diam[(i*filas) + j].color != 0) {
				cont = 1 + comprobarIgualesPos(diam, i, j,todos);
				if (cont > 2) {
					diam = explotarIguales(diam, i, j,todos);
					//cout << "La posicion " << i << " " << j << ": Tiene estos iguales: " << cont << endl;
				}
			}
			//ni esto
			cont = 0;
		}	
	}

	return diam;
}

int comprobarIgualesPos(Diamante *diam, int posX, int posY, posicion pos) {
	int cont = 0;
	switch (pos)
	{
	case todos:
		cont += comprobarIgualesPos(diam, posX, posY, derecha);
		cont += comprobarIgualesPos(diam, posX, posY, izquierda);
		cont += comprobarIgualesPos(diam, posX, posY, arriba);
		cont += comprobarIgualesPos(diam, posX, posY, abajo);
		break;
	case derecha:
		if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY + 1 ].color) { // comprobamos derecha 
			cont += 1 + comprobarIgualesPos(diam, posX, posY + 1,derecha);
		}
		break;
	case izquierda:
		if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY - 1].color) { //comprobamos izquierda
			cont += 1 + comprobarIgualesPos(diam, posX, posY - 1,izquierda);
		}
		break;
	case abajo:
		if (diam[(posX*filas) + posY].color == diam[((posX + 1) * filas) + posY].color) { //comprobamos arriba
			cont += 1 + comprobarIgualesPos(diam, posX + 1, posY,abajo);
		}
	break;
	case arriba:
		if (diam[(posX*filas) + posY].color == diam[((posX - 1) * filas) + posY + 1].color) { //comprobamos abajo
			cont += 1 + comprobarIgualesPos(diam, posX - 1, posY,arriba);
		}
	break;
	default:
		break;
	}

	return cont;

}

void inicicializarArray(Diamante *diam){
	int numAleatorio = 0;

	cout << "\n\n\n";

	for (int i = 0; i < filas; i++) {  //llenamos el array de nums aleatorios
		for (int j = 0; j < columnas; j++) {
			numAleatorio = rand() % nColores + 1;
			diam[(i*filas) + j] = Diamante(i, j); 
			diam[(i*filas) + j].color = numAleatorio;

		}
	}
}

//funcion para mover los diamantes hacia abajo
//Recorremos la matriz de abajo hacia arriba para no tener que repetir movimientos
Diamante *moverAbajo(Diamante *diam) {
	int pos = 0;
	int iaux = 0;
	//Recorremos hacia atras el array
	for (int i = filas - 1; i > 0; i--) {
		for (int j = columnas - 1; j > 0; j--) {

			iaux = i;
			pos = (i*filas) + j;
			
			if (pos-columnas >= 0 && (diam[(i*filas) + j].color == 0)) {
				//Buscamos la primera posicion de la columna con color !=0
				while (diam[((iaux)*filas) + j].color == 0) {
					iaux=iaux-1;
				}

				//Intercambiamos colores
				int colorAux = diam[(iaux*filas) + j].color;

				diam[(i*filas) +j].color = colorAux;
				diam[((iaux)*filas) + j].color = 0;
								
			}
		}
	}
	return diam;
}

Diamante *moverdiam(Diamante *diam) {
	
	int fi = 0;
	int co = 0;
	cout << "fila";
	cin >> fi;
	cout << "columna";
	cin >> co;

	int colorAux = diam[(fi*filas) + co].color;
	int colorAux2 = diam[((fi-1)*filas) + co].color;
	
	int cero = 0;
	
	diam[(fi*filas) + co].color = colorAux2;
	diam[((fi-1)*filas) + co].color = 0;

	return diam;
}
int main()
{

	Diamante *diam = new Diamante[filas*columnas];

	HANDLE hConsole;
	hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

	inicicializarArray(diam);
	diam = comprobarIguales(diam);
	printDiamante(diam, hConsole);

	while (true) {

		cout << "\n\n\n";
		SetConsoleTextAttribute(hConsole, 15);
		cout << "Introduce la posicion del bloque que quieras explotar o 99 para salir";
		int filaInt = 0, columnaInt = 0;
		cout << "\nfila: ";
		cin >> filaInt;

		if (filaInt == 99)
			exit(0);

		cout << "columna: ";
		cin >> columnaInt;
		

			//diam = explotarIguales(diam, filaInt, columnaInt);
			int size = filas*columnas * sizeof(diam);

		//*moveBlocks(bloques, filas, columnas);
		//diam=moverdiam(diam);
		diam = moverAbajo(diam);
		cout << "\n\n\n";
		printDiamante(diam, hConsole);
		
		
		cout << "elementos movidos";
		cout << diam[(0*filas) + 5].color;
	}
	free(diam);
	getchar();
    return 0;
}
void printDiamante(Diamante *diam, HANDLE hConsole) {  //modulo para imprimir el tablero
	for (int i = 0; i<filas; i++) {
		for (int j = 0; j<columnas; j++)
			diam[i*columnas + j].printDiamante(hConsole);
		cout << "\n";
	}

}
