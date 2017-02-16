// jewelsLegendC.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include "Diamante.h"

using namespace std;
const int columnas = 10;
const int filas = 10;
const int nColores = 8;

void printDiamante(Diamante *diam, HANDLE hConsole);

Diamante *explotarIguales(Diamante *diam, int posX, int posY) { //esto no esta hecho

	if (diam[(posX + 1)*columnaExp + posY].color == diam[posX*columnaExp + posY].color) {
			diam[(posX + 1)*columnaExp + posY].color = 0;
	}
		

	if (diam[(posX - 1)*columnaExp + posY].color == diam[posX*columnaExp + posY].color)
		diam[(posX - 1)*columnaExp + posY].color = 0;

	if (diam[posX*columnaExp + posY + 1].color == diam[posX*columnaExp + posY].color)
		diam[posX*columnaExp + posY + 1].color = 0;

	if (diam[posX*columnaExp + posY - 1].color == diam[posX*columnaExp + posY].color)
		diam[posX*columnas + posY - 1].color = 0;


	diam[posX*columnas + posY].color = 0;


	return diam;
}
Diamante *comprobarIguales(Diamante *diam) {
	int cont = 0;
	for (int i = 0; i < filas; i++) {  //Recorremos el array en busca de cadenas de tres numeros iguales
		for (int j = 0; j < columnas; j++) {
			if (diam[(i*filas) + columnas].color != 0) {
				cont = comprobarIgualesPos(diam, i, j);
				if (cont > 2) {
					diam = explotarIguales(diam, i, j);
				}
			}
			//ni esto
		}
		cont = 0;
	}

	return diam;
}

int comprobarIgualesPos(Diamante *diam, int posX, int posY) {
	int cont = 0;
	if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY + 1 ].color) { // comprobamos derecha 
		cont += 1 + comprobarIgualesPos(diam, posX, posY + 1);
	}
	if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY - 1].color) { //comprobamos izquierda
		cont += 1 + comprobarIgualesPos(diam, posX, posY - 1);
	}
	if (diam[(posX*filas) + posY].color == diam[((posX + 1) * filas) + posY].color) { //comprobamos arriba
		cont += 1 + comprobarIgualesPos(diam, posX + 1, posY);
	}
	if (diam[(posX*filas) + posY].color == diam[((posX - 1) * filas) + posY + 1].color) { //comprobamos abajo
		cont += 1 + comprobarIgualesPos(diam, posX - 1, posY);
	}

	return cont;

}

void inicicializarArray(Diamante *diam){
	int numAleatorio = 0;

	cout << "\n\n\n";

	for (int i = 0; i < filas; i++) {  //llenamos el array de nums aleatorios
		for (int j = 0; j < columnas; j++) {
			numAleatorio = rand() % nColores;
			diam[(i*filas) + j] = Diamante(i, j); 
			diam[(i*filas) + j].color = numAleatorio + 1;

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
		

			diam = explotarIguales(diam, filaInt, columnaInt);
			int size = filas*columnas * sizeof(diam);

		//*moveBlocks(bloques, filas, columnas);
		//diam=moverdiam(diam);
		diam = moverAbajo(diam);
		cout << "\n\n\n";
		printDiamante(diam, hConsole);
		
		
		cout << "elementos movidos";
		cout << diam[(0*filas) + 5].color;
	}
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
