#include "stdafx.h"
#include "Diamante.h"


Diamante::Diamante()
{
}
//Constructor 2
Diamante::Diamante(int x, int y) {
	X = x;				 	  //Posicion x del diamante
	Y = y;
	xplode = false;
	//Posicion y del diamante
}


void Diamante::explotarDiamante() {		//Explotar un diamante
	color = 0;
}


void Diamante::printDiamante(HANDLE hConsole) {

	switch (color) {
	case 0: SetConsoleTextAttribute(hConsole, 0);
		break;
	case 1: SetConsoleTextAttribute(hConsole, 1);
		break;
	case 2: SetConsoleTextAttribute(hConsole, 2);
		break;
	case 3: SetConsoleTextAttribute(hConsole, 12);
		break;
	case 4: SetConsoleTextAttribute(hConsole, 13);
		break;
	case 5: SetConsoleTextAttribute(hConsole, 14);
		break;
	case 6: SetConsoleTextAttribute(hConsole, 15);
		break;
	case 7: SetConsoleTextAttribute(hConsole, 3);
		break;
	case 8: SetConsoleTextAttribute(hConsole, 10);
		break;
	case 9: SetConsoleTextAttribute(hConsole, 11);
		break;
	default:;
	}
	printf("%d ", color);

}


void Diamante::comprobarColor() {		//Imprime el color de un diamante
	printf("%d", color);
}


Diamante::~Diamante()
{
}
