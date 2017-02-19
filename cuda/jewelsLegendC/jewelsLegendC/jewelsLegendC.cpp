// jewelsLegendC.cpp : Defines the entry point for the console application.
//

#include "stdafx.h"
#include <iostream>
#include "Diamante.h"
#include <string.h>
#include <time.h>

using namespace std;
const int columnas = 10;
const int filas = 10;
const int nColores = 8;
enum posicion { todos, arriba, abajo, derecha, izquierda };

void printDiamante(Diamante *diam, HANDLE hConsole);
int comprobarIgualesPos(Diamante *diam, int posX, int posY, posicion pos);
int comprobarIgualesDer(Diamante *diam, int posX, int posY);
int comprobarIgualesAbajo(Diamante *diam, int posX, int posY);
Diamante *moverAbajo(Diamante *diam);

Diamante *explotarIguales(Diamante *diam, int posX, int posY) { //esto no esta hecho

	int contFilas = comprobarIgualesDer(diam, posX, posY);
	int contColumnas = comprobarIgualesAbajo(diam, posX, posY);

	if (contFilas > 0) {
		diam = explotarIguales(diam, posX, posY + 1);
	}
	if (contColumnas > 0) {
		diam = explotarIguales(diam, posX + 1, posY);
	}
	diam[posX*columnas + posY].color = 0;

	return diam;
}
Diamante *comprobarIguales(Diamante *diam) {
	
	for (int i = 0; i < filas; i++) {  //Recorremos el array en busca de cadenas de tres numeros iguales
		for (int j = 0; j < columnas; j++) {
			if (diam[(i*filas) + j].color != 0) {
				int contFilas = comprobarIgualesDer(diam, i, j);
				int contColumnas = comprobarIgualesAbajo(diam, i, j);

				if (contFilas > 1) 
					diam = explotarIguales(diam, i, j);
				if (contColumnas > 1) 
					diam = explotarIguales(diam, i, j);
			}
		}	
	}
	diam = moverAbajo(diam);
	return diam;
}


int comprobarIgualesDer(Diamante *diam, int posX, int posY) {
	int cont = 0;
	if (diam[(posX*filas) + posY].color == diam[(posX * filas) + posY + 1].color) {
		cont = 1 + comprobarIgualesDer(diam, posX, posY + 1);
	}
	return cont;
}
int comprobarIgualesAbajo(Diamante *diam, int posX, int posY) {
	int cont = 0;
	//if(posX >= filas - 1 && posY >= columnas - 1)
	if (diam[(posX*filas) + posY].color == diam[((posX + 1) * filas) + posY].color) {
		cont = 1 + comprobarIgualesAbajo(diam, posX + 1, posY );
	}
	return cont;
}
int comprobarIgualesPos(Diamante *diam, int posX, int posY,posicion pos) {
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
		if (diam[(posX*filas) + posY].color == diam[((posX + 1) * filas) + posY].color) { //comprobamos abajo
			cont += 1 + comprobarIgualesPos(diam, posX + 1, posY,abajo);
		}
	break;
	case arriba:
		if (diam[(posX*filas) + posY].color == diam[((posX - 1) * filas) + posY].color) { //comprobamos arriba
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
	srand(time(0));
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
	int FilaAux = 0;
	//Recorremos hacia atras el array
	for (int i = columnas - 1; i >= 0; i--) {
		for (int j = filas - 1; j >= 0; j--) {

			FilaAux = j;
			pos = (j*filas)+i;
			if (diam[(j*filas) + i].color == 0) {
				//Buscamos la primera posicion de la columna con color !=0
				while (diam[((FilaAux)*filas) + i].color == 0 && FilaAux >= 0) {
					FilaAux -= 1;
				}

				//Intercambiamos colores
				Diamante colorAux = diam[(FilaAux*filas) + i];
				diam[(FilaAux*filas) + i].color = 0;
				diam[pos] = colorAux;
				
								
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

//Funcion que comprueba si dos diamantes son adyacentes
bool adyacentes(Diamante diam, int f1, int c1, int f2, int c2) {
	bool ady = false;

	if (f1 == f2 + 1 || f1 == f2 - 1 || (f1 == f2 && c1 != c2)) {
		if (c1 == c2 + 1 || c1 == c2 - 1 || (c1 == c2 && f1 != f2)) {
			if ((f1 == f2 - 1 && c1 == c2 - 1) || (f1 == f2 + 1 && c1 == c2 - 1) ||	(f1 == f2 - 1 && c1 == c2 + 1) || (f1 == f2 + 1 && c1 == c2 + 1)) {
				ady = false; //Si el movimiento es en diagonal no sera valido
			}
			else { ady = true; }
		}
	}
	if (!ady) cout << "\nMOVIMIENTO ERRONEO, debe ser entre diamantes adyacentes y que no esten en diagonal";
	return ady;
}

//Funcion que comprueba si los diamantes explotan
bool explotan(Diamante *diam, int f1, int c1, int f2, int c2, bool ady) {
	
	bool expl = false;
	int contIgualesArriba1=0, contIgualesAbajo1=0, contIgualesDer1=0, contIgualesIzq1=0, contIgualesVert1=0 , contIgualesHoriz1=0;
	int contIgualesArriba2=0, contIgualesAbajo2=0, contIgualesDer2=0, contIgualesIzq2=0, contIgualesVert2=0, contIgualesHoriz2=0;
	
	//HAcemos el intercambio en la matriz para comprobar si se puede explotar
	int colorAux1 = diam[(f1*filas) + c1].color;
	int colorAux2 = diam[(f2*filas) + c2].color;
	diam[(f1*filas) + c1].color = colorAux2;
	diam[(f2*filas) + c2].color = colorAux1;

	//Comprobamos todas las direcciones posibles
	contIgualesArriba1 = comprobarIgualesPos(diam, f1, c1, arriba);
	contIgualesAbajo1 = comprobarIgualesPos(diam, f1, c1, abajo);
	contIgualesVert1 = comprobarIgualesPos(diam, f1, c1, arriba) + comprobarIgualesPos(diam, f1, c1, abajo);
	contIgualesIzq1 = comprobarIgualesPos(diam, f1, c1, izquierda);
	contIgualesDer1 = comprobarIgualesPos(diam, f1, c1, derecha);
	contIgualesHoriz1 = comprobarIgualesPos(diam, f1, c1, izquierda) + comprobarIgualesPos(diam, f1, c1, derecha);

	contIgualesArriba2 = comprobarIgualesPos(diam, f2, c2, arriba);
	contIgualesAbajo2 = comprobarIgualesPos(diam, f2, c2, abajo);
	contIgualesVert2 = comprobarIgualesPos(diam, f2, c2, arriba) + comprobarIgualesPos(diam, f2, c2, abajo);
	contIgualesIzq2 = comprobarIgualesPos(diam, f2, c2, izquierda);
	contIgualesDer2 = comprobarIgualesPos(diam, f2, c2, derecha);
	contIgualesHoriz2 = comprobarIgualesPos(diam, f2, c2, izquierda) + comprobarIgualesPos(diam, f2, c2, derecha);

	//cout << "\n" << contIgualesArriba1 << contIgualesAbajo1 << contIgualesVert1 << contIgualesIzq1 << contIgualesDer1 << contIgualesHoriz1;
	//cout << "\n" << contIgualesArriba2 << contIgualesAbajo2 << contIgualesVert2 << contIgualesIzq2 << contIgualesDer2 << contIgualesHoriz2;

	//En caso de que alguno de estos parametros sea 2 o m�s, al menos uno explota
	if (contIgualesArriba1>=2 || contIgualesAbajo1>=2 || contIgualesVert1>=2 || contIgualesIzq1>=2 || contIgualesDer1>=2 || contIgualesHoriz1>=2) {
		expl = true;
	}
	else if (contIgualesArriba2>=2 || contIgualesAbajo2>=2 || contIgualesVert2>=2 || contIgualesIzq2>=2 || contIgualesDer2>=2 || contIgualesHoriz2>=2) {
		expl = true;
	}

	if(!ady || !expl){//En caso de que no sea posible deshacemos los cambios en la matriz
		diam[(f1*filas) + c1].color = colorAux1;
		diam[(f2*filas) + c2].color = colorAux2;
		if(!expl) cout << "\nMOVIMIENTO ERRONEO, los diamantes seleccionados no explotan";
	}

	return expl;
}

//Funcion que pide movimiento hasta que sea correcto
bool movPosible(Diamante *diam) {
	int f1 = 0, c1 = 0, f2 = 0, c2 = 0;
	bool expl = false, ady = false;

	cout << "\n--INTRODUCE TU JUGADA--";
	cout << "\nIntroduce la fila del primer diamante: ";
	cin >> f1;
	if (f1 == 99) exit(0);
	cout << "Introduce la columna del primer diamante: ";
	cin >> c1;
	cout << "Introduce la fila del segundo diamante: ";
	cin >> f2;
	cout << "Introduce la columna del segundo diamante: ";
	cin >> c2;

	ady = adyacentes(*diam, f1, c1, f2, c2);
	expl = explotan(diam, f1, c1, f2, c2, ady);
	
	/*//Pedimos de nuevo los datos
	while (!ady && !expl) {
		cout << "\nIntroduce la fila del primer diamante: ";
		cin >> f1;
		if (f1 == 99) exit(0);
		cout << "Introduce la columna del primer diamante: ";
		cin >> c1;
		cout << "Introduce la fila del segundo diamante: ";
		cin >> f2;
		cout << "Introduce la columna del segundo diamante: ";
		cin >> c2;
	}*/
	return true;
}

int main()
{

	Diamante *diam = new Diamante[filas*columnas];

	HANDLE hConsole;
	hConsole = GetStdHandle(STD_OUTPUT_HANDLE);

	inicicializarArray(diam);
	//diam = comprobarIguales(diam);
	printDiamante(diam, hConsole);

	while (true) {
		bool mov = false;
		cout << "\n\n\n";
		SetConsoleTextAttribute(hConsole, 15);
		cout << "Introduce 99 para salir";
		int filaInt = 0;

		if (filaInt == 99)
			exit(0);

		while (!mov) {
			mov=movPosible(diam);
		}

			//diam = explotarIguales(diam, filaInt, columnaInt);
			int size = filas*columnas * sizeof(diam);

		//*moveBlocks(bloques, filas, columnas);
		//diam=moverdiam(diam);
		//diam = moverAbajo(diam);
		cout << "\n\n\n";
		printDiamante(diam, hConsole);
		getchar();
		
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
