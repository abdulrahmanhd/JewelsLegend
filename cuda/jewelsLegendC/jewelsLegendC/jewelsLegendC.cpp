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
	printDiamante(diam, hConsole);
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
