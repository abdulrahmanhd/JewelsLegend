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

Diamante *explotarIguales(Diamante *diam, int posX, int posY, int filaExp, int columnaExp) {

	if (diam[(posX + 1)*columnaExp + posY].color == diam[posX*columnaExp + posY].color)
		diam[(posX + 1)*columnaExp + posY].color = 0;

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
	for (int i = 0; i < filas; i++) {  //Recorremos el array en busca de cadenas de tres numeros iguales
		for (int j = 0; j < columnas; j++) {

		}
	}

	return diam;
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


			diam = explotarIguales(diam, filaInt, columnaInt, filas, columnas);
			int size = filas*columnas * sizeof(diam);

		//*moveBlocks(bloques, filas, columnas);
		printDiamante(diam, hConsole);
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
