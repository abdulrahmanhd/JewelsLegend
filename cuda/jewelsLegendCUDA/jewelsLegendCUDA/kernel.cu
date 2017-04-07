#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>
#include <cuda_runtime.h>
#include <cuda_runtime_api.h>
#include <curand.h>
#include <curand_kernel.h>
#include "device_launch_parameters.h"
#include <assert.h>
#include <cmath>
#include <Windows.h>
#include <string.h>
#include <time.h>
#include <string>
#include <stdlib.h>
#include <fstream>
#include <iostream>
# include <stdio.h>
# include <conio.h>


//Poniendo este DEFINE evitamos un error en el que falta la definici�n de HANDLE_ERROR
#define HANDLE_ERROR


int* generaTablero(int filas, int columnas, int bombas);
void imprimeTablero(int* tablero, int filas, int columnas);
char pedirModoEjecucion();
int pedirFilasTablero();
int pedirColumnasTablero();
char pedirDificultad();
void prop();
int* rellenarTablero(int* tablero, int tamFilas, int tamColumnas, int nColores);
enum posicion {arriba, abajo, derecha, izquierda,ArribaIzquierda,ArribaDerecha, AbajoIzquierda,AbajoDerecha};
void guardarPartida(int* tablero, int filas, int columnas, int dificultad);
FILE *doc;
FILE *leer;

//Funci�n que devuelve un error si las dimensiones de la martiz son demasiado grandes para la gr�fica
cudaError_t comprobarPropiedades(int filas, int columnas) {
	cudaDeviceProp prop;
	cudaError_t cudaStatus = cudaSuccess;
	int count;
	long globalMem;
	int sharedMem;
	HANDLE_ERROR(cudaGetDeviceCount(&count));

	for (int i = 0; i < count; i++) {
		HANDLE_ERROR(cudaGetDeviceProperties(&prop, i));
		globalMem = prop.totalGlobalMem;
		sharedMem = prop.sharedMemPerBlock;

		printf("La matriz solicitada ocupa %lu\n", filas*columnas * sizeof(int));
		printf("La memoria global es de %lu\n", globalMem);
		printf("La memoria compartida es de %d\n", sharedMem);

		if ((filas*columnas * sizeof(int)) >= globalMem) {

			fprintf(stderr, "La matriz solicitada ocupa %lu y excede la capacidad de memoria global de tu tarjeta gráfica que es %lu \n",
				filas*columnas * sizeof(int), globalMem);
			goto Error;
		}


		if ((filas*columnas * sizeof(int)) >= sharedMem) {

			fprintf(stderr, "La matriz solicitada ocupa %lu y excede la capacidad de memoria compartida de tu tarjeta gráfica que es %lu \n",
				filas*columnas * sizeof(int), sharedMem);
			goto Error;
		}


	}

Error:
	return cudaStatus;

}

int comprobarIgualesArriba(int *tablero, int posX, int posY,int tamColumnas) {
	int cont = 0;
	if (posX - 1 >= 0 && tablero[(posX*tamColumnas) + posY] == tablero[((posX - 1) * tamColumnas) + posY]) {
		cont = 1 + comprobarIgualesArriba(tablero, posX - 1, posY, tamColumnas);
	}
	return cont;
}
int comprobarIgualesIzquierda(int *tablero, int posX, int posY, int tamColumnas) {
	int cont = 0;
	if (posY - 1 >= 0 && tablero[(posX*tamColumnas) + posY] == tablero[(posX * tamColumnas) + posY - 1]) {
		cont = 1 + comprobarIgualesIzquierda(tablero, posX, posY - 1, tamColumnas);
	}
	return cont;
}

int comprobarIgualesDer(int*tablero, int posX, int posY,int tamColumnas) {
	int cont = 0;
	if (posY + 1 < tamColumnas && tablero[(posX*tamColumnas) + posY] == tablero[(posX * tamColumnas) + posY + 1]) {
		cont = 1 + comprobarIgualesDer(tablero, posX, posY + 1, tamColumnas);
	}
	return cont;
}
int comprobarIgualesAbajo(int *tablero, int posX, int posY, int tamColumnas, int tamFilas) {
	int cont = 0;
	//if(posX >= filas - 1 && posY >= columnas - 1)
	if (posX + 1 < tamFilas && tablero[(posX*tamColumnas) + posY] == tablero[((posX + 1) * tamColumnas) + posY]) {
		cont = 1 + comprobarIgualesAbajo(tablero, posX + 1, posY,tamFilas, tamColumnas);
	}
	return cont;
}
bool explotan(int* dev_tablero, int fila1, int columna1, int fila2, int columna2, int tamFilas, int tamColumnas) {
	bool explotan = false;

	//HAcemos el intercambio en la matriz para comprobar si se puede explotar
	int colorAux1 = dev_tablero[(fila1*tamColumnas) + columna1];
	int colorAux2 = dev_tablero[(fila2*tamColumnas) + columna2];
	dev_tablero[(fila1*tamColumnas) + columna1] = colorAux2;
	dev_tablero[(fila2*tamColumnas) + columna2] = colorAux1;

	int sameHorizon1 = comprobarIgualesDer(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesIzquierda(dev_tablero, fila1, columna1, tamColumnas);
	int sameVertical1 = comprobarIgualesArriba(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesAbajo(dev_tablero, fila1, columna1, tamColumnas, tamFilas);

	int sameHorizon2 = comprobarIgualesDer(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesIzquierda(dev_tablero, fila1, columna1, tamColumnas);
	int sameVertical2 = comprobarIgualesArriba(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesAbajo(dev_tablero, fila1, columna1, tamColumnas, tamFilas);


	// deshacemos los cambios en la matriz
	dev_tablero[(fila1*tamColumnas) + columna1] = colorAux1;
	dev_tablero[(fila2*tamColumnas) + columna2] = colorAux2;

	if (sameVertical1 >= 2 || sameHorizon1 >= 2) { //Comprobamos que en cualquiera de las posiciones haya bombas que puedan explotar
		explotan = true;
	}
	else if (sameVertical2 >= 2 || sameHorizon2 >= 2) {
		explotan = true;
	}



	return explotan;
}
bool hasMoreMovements(int *tablero, int filas, int columnas) {
	bool expl = false;
	int posX = 0;
	while (posX < filas && !expl) {
		for (int posY = 0; posY < columnas; posY++) {
			if (posX + 1 < filas && explotan(tablero, posX, posY, posX + 1, posY, filas, columnas)) { // Abajo
				expl = true;
			}
			else if (posY + 1 < columnas && explotan(tablero, posX, posY, posX, posY + 1, filas, columnas)) { //Derecha
				expl = true;
			}
			else if (posY - 1 >= 0 && explotan(tablero, posX, posY, posX, posY - 1, filas, columnas)) { //Izquierda
				expl = true;
			}
			else if (posX - 1 >= 0 && explotan(tablero, posX, posY, posX - 1, posY, filas, columnas)) {//Arriba
				expl = true;
			}
		}
		posX++;
	}

	return expl;
}

__device__ int comprobarIgualesPos(int *tablero, int posX, int posY, posicion pos, int tamFilas, int tamColumnas) {
	int cont = 0;
	switch (pos)
	{
	case derecha:
		if (posY + 1 < tamColumnas && tablero[(posX*tamColumnas) + posY] == tablero[(posX * tamColumnas) + posY + 1]) { // comprobamos derecha 
			cont += 1 + comprobarIgualesPos(tablero, posX, posY + 1, derecha, tamFilas, tamColumnas);
		}
		break;
	case izquierda:
		if (posY - 1 >= 0 && tablero[(posX*tamColumnas) + posY] == tablero[(posX * tamColumnas) + posY - 1]) { //comprobamos izquierda
			cont += 1 + comprobarIgualesPos(tablero, posX, posY - 1, izquierda, tamFilas, tamColumnas);
		}
		break;
	case abajo:
		if (posX + 1 < tamFilas && tablero[(posX*tamColumnas) + posY] == tablero[((posX + 1) * tamColumnas) + posY]) { //comprobamos abajo
			cont += 1 + comprobarIgualesPos(tablero, posX + 1, posY, abajo, tamFilas, tamColumnas);
		}
		break;
	case arriba:
		if (posX - 1 >= 0 && tablero[(posX*tamColumnas) + posY] == tablero[((posX - 1) * tamColumnas) + posY]) { //comprobamos arriba
			cont += 1 + comprobarIgualesPos(tablero, posX - 1, posY, arriba, tamFilas, tamColumnas);
		}
		break;
	case ArribaIzquierda:
		if (posX - 1 >= 0 && posY>=0 && tablero[(posX*tamColumnas) + posY] == tablero[((posX - 1) * tamColumnas) + posY-1]) { //comprobamos arribaIzquierda
			cont += 1 + comprobarIgualesPos(tablero, posX - 1, posY-1, ArribaIzquierda, tamFilas, tamColumnas);
		}
		break;
	case ArribaDerecha:
		if (posX - 1 >= 0 && posY+1<tamColumnas && tablero[(posX*tamColumnas) + posY] == tablero[((posX - 1) * tamColumnas) + posY+1]) { //comprobamos arribaDerecha
			cont += 1 + comprobarIgualesPos(tablero, posX - 1, posY+1, ArribaDerecha, tamFilas, tamColumnas);
		}
		break;
	case AbajoIzquierda:
		if (posX + 1 < tamFilas && posY - 1 >=  0 && tablero[(posX*tamColumnas) + posY] == tablero[((posX + 1) * tamColumnas) + posY-1]) { //comprobamos abajoIzquierda
			cont += 1 + comprobarIgualesPos(tablero, posX + 1, posY-1, AbajoIzquierda, tamFilas, tamColumnas);
		}
		break;
	case AbajoDerecha:
		if (posX + 1 <tamFilas && posY+1 <tamColumnas && tablero[(posX*tamColumnas) + posY] == tablero[((posX + 1) * tamColumnas) + posY+1]) { //comprobamos abajoDerecha
			cont += 1 + comprobarIgualesPos(tablero, posX + 1, posY+1, AbajoDerecha, tamFilas, tamColumnas);
		}
		break;
	default:
		break;
	}

	return cont;

}
bool proveBig(int sameVertical1, int sameHorizon1, int sameVertical2, int sameHorizon2) {
	bool mayor1 = true;
	if (((sameVertical1 >= sameVertical2) && (sameVertical1 >= sameHorizon2)) || ((sameHorizon1 >= sameVertical2) && (sameHorizon1 >= sameHorizon2)))	mayor1 = true;
	else mayor1 = false;
	return mayor1;
}
// Funcion que determina si alineacion 1 es mayoor que 2 o viceversa
__device__ bool comprobarMayor(int sameVertical1, int sameHorizon1, int sameVertical2, int sameHorizon2) {
	bool mayor1 = true;
	if (((sameVertical1 >= sameVertical2) && (sameVertical1 >= sameHorizon2)) || ((sameHorizon1 >= sameVertical2) && (sameHorizon1 >= sameHorizon2)))	mayor1 = true;
	else mayor1 = false;
	return mayor1;
}
//Funcion que comprueba los estaDentro de la matriz, ya que tratamos con un array
__device__ bool estaDentro(int x, int y, int filas, int columnas) {

	return !(x >= filas || x < 0 || y >= columnas || y < 0);

}
//Funcion que devuelve el numero mas alto de explosiones
int autoContMov(int *dev_tablero, int fila1, int columna1, int fila2, int columna2, int tamFilas, int tamColumnas) {
	int comFilas1, comColum1, comFilas2, comColum2 = 0;

	int colorAux1 = dev_tablero[(fila1*tamColumnas) + columna1];
	int colorAux2 = dev_tablero[(fila2*tamColumnas) + columna2];

	dev_tablero[(fila1*tamColumnas) + columna1] = colorAux2;
	dev_tablero[(fila2*tamColumnas) + columna2] = colorAux1;

	comFilas1 = comprobarIgualesDer(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesIzquierda(dev_tablero, fila1, columna1,tamColumnas);
	comColum1 = comprobarIgualesArriba(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesAbajo(dev_tablero, fila1, columna1, tamColumnas, tamFilas);

	comFilas2 = comprobarIgualesDer(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesIzquierda(dev_tablero, fila1, columna1, tamColumnas);
	comColum2 = comprobarIgualesArriba(dev_tablero, fila1, columna1, tamColumnas) + comprobarIgualesAbajo(dev_tablero, fila1, columna1, tamColumnas, tamFilas);

	dev_tablero[(fila1*tamColumnas) + columna1] = colorAux1;
	dev_tablero[(fila2*tamColumnas) + columna2] = colorAux2;
	
	if (proveBig(comFilas1, comColum1, comFilas2, comColum2)) {
		if (comFilas1 > comColum1) {
			return comFilas1;
		}
		else return comColum1;
	}
	else {
		if (comFilas2 > comColum2) {
			return comFilas2;
		}
		else return comColum2;
	}
}

//funcion que comprueba las posiciones iguales del array,

//Funcion que elimina una celda
__device__ void eliminar(int* dev_tablero, int fila, int columna, int tamColumnas, int* dev_contadorEliminados) {

	// Si el valor examinado es distinto de 0, se suma uno al contador de eliminador
	if (dev_tablero[fila * tamColumnas + columna] != 0)
		dev_contadorEliminados[0] = dev_contadorEliminados[0] + 1;

	//El valor se pone a 0
	dev_tablero[fila * tamColumnas + columna] = 0;
	 

}

// Metodo que sube los 0 que se encuentran en el tablero hacia la parte mas alta del mismo
__device__ void reestructuracionArribaAbajo(int* dev_tablero, int filas, int columnas) {


	int celdax = blockIdx.y* blockDim.y + threadIdx.y;		//Indice de la x
	int celday = blockIdx.x* blockDim.x + threadIdx.x;		//Indice de la y
	int nombre = celdax * columnas + celday;	//Valor del elemento en el array
	int size = (filas*columnas); //Tamaño de la matriz
	int actual = nombre;
	int count = 0;
	int comprobador = 0;
	// Se comprueba que esa celda no es 0 para compararla con los elementos que tiene por debajo
	if (dev_tablero[actual] != 0) {

		actual += columnas;

		// Se comprueban cuantos 0 hay por debajo de la celda. Este numero se guardara en la variable count
		while (actual < size) {

			if (dev_tablero[actual] == 0) {
				count++;
			}

			actual += columnas;

		}

		//Cambio de valor de la celda que se comprueba con la celda de las posiciones que tiene que descender
		if (count > 0) {
			dev_tablero[nombre + (count * columnas)] = dev_tablero[nombre];
		}

		actual = nombre - filas;

		//Comprobacion de cuantos 0 por encima tiene la celda que se comprueba
		while (actual > 0) {
			if (dev_tablero[actual] == 0) {
				comprobador++;
			}
			actual -= filas;
		}

		//Poner a 0 el valor de la celda que se cambia si su fila menos el numero de 0 que tiene por encima
		// es menor o igual que el numero de ceros que tiene por debajo
		if (celdax - comprobador < count) {
			dev_tablero[nombre] = 0;
		}

	}

}

__device__ void reestructuracionIzquierdaDerecha(int* dev_tablero, int filas, int columnas) {

	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y
	
	
	if (dev_tablero[i*columnas + j] == 0) {
		while (j>0) {
			dev_tablero[i*columnas + j] = dev_tablero[i*columnas + (j-1)];
			dev_tablero[i*columnas + (j-1)] = 0;
			j--;
		}
	}
}

//Elimina una fila completa
__device__ void bomba1(int* dev_tablero, int fila, int columnas) {

	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y
	//Identificamos los diamantes por su fila
	if (i == fila)	dev_tablero[i*columnas + j] = 0;

}

//Elimina un columna completa
__device__ void bomba2(int* dev_tablero, int columna, int columnas) {

	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y

	//Identificamos los diamantes por su columna
	if (j == columna)	dev_tablero[i*columnas + j] = 0;

}

//Mueve la matriz en fomra de cuadrados
__device__ void bomba3(int* dev_tablero, int filas , int columnas) {

	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y
	int colorAux = 0;
	/*float fila = i%4;
	float columna = j % 4;*/
	//printf("\nEntro");
	
	if ((i == 1 && j == 1) || ((i-1) % 3 == 0 && (j-1) % 3 == 0) || ((i-1) % 3 == 0 && j==1) || (i==1 && (j-1) % 3 == 0)){
					
		if (j + 1 < columnas && i + 1 < filas) {

			//Intercambio de las puntas del cuadrado
			colorAux = dev_tablero[(i*columnas) + (j - 1)];
			dev_tablero[(i*columnas) + (j - 1)] = dev_tablero[((i + 1)*columnas) + j];
			dev_tablero[((i + 1)*columnas) + j] = dev_tablero[(i*columnas) + (j + 1)];
			dev_tablero[(i*columnas) + (j + 1)] = dev_tablero[((i - 1)*columnas) + j];
			dev_tablero[((i - 1)*columnas) + j] = colorAux;
			
			//Intercambiamos flor del cuadrado
			colorAux = dev_tablero[((i - 1)*columnas) + (j - 1)];
			dev_tablero[((i - 1)*columnas) + (j - 1)] = dev_tablero[((i + 1)*columnas) + (j - 1)];
			dev_tablero[((i + 1)*columnas) + (j - 1)] = dev_tablero[((i + 1)*columnas) + (j + 1)];
			dev_tablero[((i + 1)*columnas) + (j + 1)] = dev_tablero[((i - 1)*columnas) + (j + 1)];
			dev_tablero[((i - 1)*columnas) + (j + 1)] = colorAux;
		}
	}
	
}

//Menu de bombas
__global__ void menuBombas(int *dev_tablero, int filas, int columnas, int explota, int bomba) {
	
	switch (bomba) {
		case 91:	bomba1(dev_tablero, explota, columnas);
					reestructuracionArribaAbajo(dev_tablero, filas, columnas);
					break;
		case 92:	bomba2(dev_tablero, explota, columnas);
					reestructuracionIzquierdaDerecha(dev_tablero, filas, columnas);
					break;
		case 93:	bomba3(dev_tablero, filas, columnas);
					break;
	}
	
}

// Funcion que elimina con un unico bloque
__device__ void comprobarCadena(int* dev_tablero, int fila1, int columna1, int fila2, int columna2,int tamFilas, int tamColumnas, int* dev_contadorEliminados) {
	
	

	//Valores de los indices
	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y
	//eliminar(dev_tablero, i, j, tamFilas, dev_contadorEliminados);
	
	int sameVertical1 = comprobarIgualesPos(dev_tablero, fila1, columna1, abajo, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila1, columna1, arriba, tamFilas, tamColumnas);
	int sameHorizon1 = comprobarIgualesPos(dev_tablero, fila1, columna1, derecha, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila1, columna1, izquierda, tamFilas, tamColumnas);
	int sameVertical2 = comprobarIgualesPos(dev_tablero, fila2, columna2, abajo, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila2, columna2, arriba, tamFilas, tamColumnas);
	int sameHorizon2 = comprobarIgualesPos(dev_tablero, fila2, columna2, derecha, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila2, columna2, izquierda, tamFilas, tamColumnas);
	
	

	if (comprobarMayor(sameVertical1,sameHorizon1,sameVertical2,sameHorizon2)) { //comprobamos cual de las posiciones cambiadas explota mas
	//Ahora comprobamos que el hilo sea el de la posicion que queremos
		if (i == fila1 && j == columna1) {
			if (sameHorizon1 > sameVertical1) {
				int jAux = j;
				while (dev_tablero[i*tamColumnas + j] == dev_tablero[i*tamColumnas + (jAux + 1)] && jAux + 1 < tamColumnas) { //eliminamos igual por derecha
					jAux++;
					eliminar(dev_tablero, i, jAux, tamColumnas, dev_contadorEliminados);
					
				}
				jAux = j;
				while (dev_tablero[i*tamColumnas + j] == dev_tablero[i*tamColumnas + (jAux - 1)] && jAux - 1 >= 0) {//eliminamos igual por izquierda
					jAux--;
					eliminar(dev_tablero, i, jAux , tamColumnas, dev_contadorEliminados);
				}
				eliminar(dev_tablero, i, j, tamColumnas, dev_contadorEliminados); //eliminamos la posicion del hilo
			}
			else { //if(mayor1){
				if (comprobarMayor(sameVertical1, sameHorizon1, sameVertical2, sameHorizon2)) {
					int iAux = i;
					while (dev_tablero[i*tamColumnas + j] == dev_tablero[(iAux + 1)*tamColumnas + j] && iAux + 1 < tamFilas) { //eliminamos igual por arriba
						iAux++;
						eliminar(dev_tablero, iAux, j, tamColumnas, dev_contadorEliminados);

					}
					iAux = i;
					while (dev_tablero[i*tamColumnas + j] == dev_tablero[(iAux - 1)*tamColumnas + j] && iAux - 1 >= 0) {//eliminamos igual por abajo
						iAux--;
						eliminar(dev_tablero, iAux, j, tamColumnas, dev_contadorEliminados);
					}
					eliminar(dev_tablero, i, j, tamColumnas, dev_contadorEliminados); //eliminamos la posicion del hilo
				}
			}
		}
	}
	else {
		if (i == fila2 && j == columna2) {
			if (sameHorizon2 > sameVertical2) {
				int jAux = j;
				while (dev_tablero[i*tamColumnas + j] == dev_tablero[i*tamColumnas + (jAux + 1)] && jAux + 1 < tamColumnas) { //eliminamos igual por derecha
					jAux++;
					eliminar(dev_tablero, i, jAux, tamColumnas, dev_contadorEliminados);
				}
				jAux = j;
				while (dev_tablero[i*tamColumnas + j] == dev_tablero[i*tamColumnas + (jAux - 1)] && jAux - 1 >= 0) {//eliminamos igual por izquierda
					jAux--;
					eliminar(dev_tablero, i, jAux, tamColumnas, dev_contadorEliminados);
				}
				eliminar(dev_tablero, i, j, tamColumnas, dev_contadorEliminados); //eliminamos la posicion
			}
			else{// if(mayor2) {
				if (!comprobarMayor(sameVertical1, sameHorizon1, sameVertical2, sameHorizon2)) {
					int iAux = i;
					while (dev_tablero[i*tamColumnas + j] == dev_tablero[(iAux + 1) * tamColumnas + j] && iAux + 1 < tamFilas) { //eliminamos igual por abajo
						iAux++;
						eliminar(dev_tablero, iAux, j, tamColumnas, dev_contadorEliminados);

					}
					iAux = i;
					while (dev_tablero[i * tamColumnas + j] == dev_tablero[(iAux - 1) * tamColumnas + j] && iAux - 1 >= 0) {//eliminamos igual por arriba
						iAux--;
						eliminar(dev_tablero, iAux, j, tamColumnas, dev_contadorEliminados);
					}
					eliminar(dev_tablero, i, j, tamColumnas, dev_contadorEliminados); //eliminamos la posicion del hilo
				}
			}
			
		}
	}
}




__device__ void rellenarMatriz(int* dev_tablero, int tamFilas, int tamColumnas, int nColores) {

	//Valores de los indices
	int i = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int j = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y

	curandState_t state;

	/* we have to initialize the state */
	curand_init(0, /* the seed controls the sequence of random values that are produced */
		0, /* the sequence number is only important with multiple cores */
		0, /* the offset is how much extra we advance in the sequence for each call, can be 0 */
		&state);

	/* curand works like rand - except that it takes a state as a parameter */
	if (dev_tablero[i*tamColumnas + j] == 0) dev_tablero[i*tamColumnas + j] = curand(&state) % nColores;

}

__global__ void jugarKernel(int* dev_tablero, int fila1, int columna1, int fila2, int columna2,int tamFila, int tamColumnas, int* dev_contadorEliminados) {

	comprobarCadena(dev_tablero, fila1, columna1, fila2, columna2,tamFila,tamColumnas, dev_contadorEliminados);

	__syncthreads();

	reestructuracionArribaAbajo(dev_tablero, tamFila, tamColumnas);

	__syncthreads();

}

__device__ bool adyacentes(int fila1, int columna1, int fila2, int columna2) {
	bool ady = false;

	if (fila1 == fila2 + 1 || fila1 == fila2 - 1 || (fila1 == fila2 && columna1 != columna2)) {
		if (columna1 == columna2 + 1 || columna1 == columna2 - 1 || (columna1 == columna2 && fila1 != fila2)) {
			if ((fila1 == fila2 - 1 && columna1 == columna2 - 1) || (fila1 == fila2 + 1 && columna1 == columna2 - 1) || (fila1 == fila2 - 1 && columna1 == columna2 + 1) || (fila1 == fila2 + 1 && columna1 == columna2 + 1)) {
				ady = false; //Si el movimiento es en diagonal no sera valido
			}
			else { ady = true; }
		}
	}
	return ady;
}

__device__ bool explotChange(int* dev_tablero, int filas1, int columnas1, int fila2, int columna2,int tamFilas,int tamColumnas) {
	int x = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int y = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y

	if ((x == filas1 && y == columnas1) || (x==fila2 && y == columna2)) {
		bool explotan = false;

		//HAcemos el intercambio en la matriz para comprobar si se puede explotar
		int colorAux1 = dev_tablero[(filas1*tamColumnas) + columnas1];
		int colorAux2 = dev_tablero[(fila2*tamColumnas) + columna2];
		dev_tablero[(filas1*tamColumnas) + columnas1] = colorAux2;
		dev_tablero[(fila2*tamColumnas) + columna2] = colorAux1;

		int sameVertical1 = comprobarIgualesPos(dev_tablero, filas1, columnas1, abajo, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, filas1, columnas1, arriba, tamFilas, tamColumnas);
		int sameHorizon1 = comprobarIgualesPos(dev_tablero, filas1, columnas1, derecha, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, filas1, columnas1, izquierda, tamFilas, tamColumnas);
		int sameVertical2 = comprobarIgualesPos(dev_tablero, fila2, columna2, abajo, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila2, columna2, arriba, tamFilas, tamColumnas);
		int sameHorizon2 = comprobarIgualesPos(dev_tablero, fila2, columna2, derecha, tamFilas, tamColumnas) + comprobarIgualesPos(dev_tablero, fila2, columna2, izquierda, tamFilas, tamColumnas);

		if (sameVertical1 >= 2 || sameHorizon1 >= 2) { //Comprobamos que en cualquiera de las posiciones haya bombas que puedan explotar
			explotan = true;
		}
		else if (sameVertical2 >= 2 || sameHorizon2 >= 2) {
			explotan = true;
		}

		// deshacemos los cambios en la matriz
		dev_tablero[(filas1*tamColumnas) + columnas1] = colorAux1;
		dev_tablero[(fila2*tamColumnas) + columna2] = colorAux2;
	
	return explotan;
	}
}


__global__ void probeMovPosi(int* dev_tablero, int filas1, int columnas1, int fila2, int columna2,int tamFilas,int tamColumnas,char modoJuego,bool* dev_mov, int* dev_contadorEliminados) {
	int fil = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int col = blockIdx.x * blockDim.x + threadIdx.x;
	bool TrueMov;
	bool explot;
		if ((col == columnas1 && fil == filas1)) {//comprobamos que el hilo que accede a la funcion sea el que queremos cambiar(se comprueban en las funciones los dos numeros)
			TrueMov = adyacentes(filas1, columnas1, fila2, columna2);
			explot = explotChange(dev_tablero, filas1, columnas1, fila2, columna2, tamFilas, tamColumnas);
		if (TrueMov && explot) {
				//Si ambos son true realizamos el cmbio
				int colorAux1 = dev_tablero[(filas1*tamColumnas) + columnas1];
				int colorAux2 = dev_tablero[(fila2*tamColumnas) + columna2];
				dev_tablero[(filas1*tamColumnas) + columnas1] = colorAux2;
				dev_tablero[(fila2*tamColumnas) + columna2] = colorAux1;
				*dev_mov = true;
			}
			else { printf("MOVIMIENTO ERRONEO, Las posiciones no son adyacentes o no explotan\n"); }
		}

}

cudaError_t jugar(int* tablero, int tamFilas, int tamColumnas, int* contadorEliminados, char m, int nColores) {

	cudaError_t cudaStatus;
	int fila1 = 0, columna1 = 0, fila2 = 0, columna2 = 0;
	bool* mov = false, *dev_mov;
	int* dev_tablero;
	int *dev_contadorEliminados;
	int bomba = 0;
	int explota = 0;
	bool hayBomba = false;
	char guardar=NULL;

	dim3 blocks(1);
	dim3 threads(tamFilas, tamColumnas);
	//Asignamos objeto a memoria global con cudamalloc
	cudaStatus = cudaMalloc((void**)&dev_tablero, tamFilas*tamColumnas * sizeof(int));
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}

	cudaStatus = cudaMalloc((void**)&dev_contadorEliminados, sizeof(int));
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}
	cudaStatus = cudaMalloc((void**)&dev_mov, sizeof(bool));
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}
	//Pasamos parametros a la parte del device
	cudaStatus = cudaMemcpy(dev_tablero, tablero, tamFilas*tamColumnas * sizeof(int), cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy dev_tablero failed!");
		goto Error;
	}

	cudaStatus = cudaMemcpy(dev_contadorEliminados, contadorEliminados, sizeof(int), cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy dev_contadorEliminados failed!");
		goto Error;
	}

	cudaStatus = cudaMemcpy(dev_mov, &mov, sizeof(bool), cudaMemcpyHostToDevice);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy dev_mov failed!");
		goto Error;
	}
	bool activada = false;
	while (!hasMoreMovements(tablero, tamFilas, tamColumnas) && !activada) {
		
		printf("NO HAY MOVIMIENTO\n\nIntroduce el numero de la bomba: ");
		scanf("%d", &bomba);
			if (bomba == 91) {
				printf("Introduce el numero de la fila que deseas explotar:");
				scanf("%d", &explota);
				activada = true;
			}
			if (bomba == 92) {
				printf("Introduce el numero de la columna que deseas explotar:");
				scanf("%d", &explota);
				activada = true;
			}
			if (bomba == 93) {
				explota = 0;
				activada = true;
			}
			menuBombas << <blocks, threads >> > (dev_tablero, tamFilas, tamColumnas, explota, bomba);
			hayBomba = true;

	}
	//Modo manual
	if (m == 'm' && hasMoreMovements(tablero, tamFilas, tamColumnas)) {
		
		printf("-Para usar una bomba introduce 90 ");
		printf("\n-Para terminar el juego 99 ");
		printf("\nIntroduzca la fila del primer diamante: ");
		scanf_s("%d", &fila1);
		if (fila1 == 99) {
			printf("\nQuieres guardar la partida? (s/n): ");
			getchar();
			
				guardarPartida(tablero, tamFilas, tamColumnas, nColores);
				printf("\nPartida guardada correctamente");
		
			
			printf("\n\n-JUEGO TERMINADO-");
			Sleep(1000);
			exit(0);
		}
			if (fila1 == 90) {
				printf("\nIntroduce el numero de la bomba:");
				scanf("%d", &bomba);
				if (bomba == 91) {
					printf("Introduce el numero de la fila que deseas explotar:");
					scanf("%d", &explota);
				}
				if (bomba == 92) {
					printf("Introduce el numero de la columna que deseas explotar:");
					scanf("%d", &explota);
				}
				if (bomba == 93) explota = 0;
				menuBombas << <blocks, threads >> > (dev_tablero, tamFilas, tamColumnas, explota, bomba);
				hayBomba = true;
			}else {
				printf("Introduzca la columna del primer diamante: ");
				scanf_s("%d", &columna1);
				printf("Introduzca la fila del segundo diamante: ");
				scanf_s("%d", &fila2);
				printf("Introduzca la columna del segundo diamante: ");
				scanf_s("%d", &columna2);

				//bool canMove = hasMoreMovements(tablero);
			}
			
	}
	else if(hasMoreMovements(tablero, tamFilas, tamColumnas)){
		
		int posX = 0;
		int movOptimoFila1, movOptimoColumna1, movOptimoFila2, movOptimoColumna2, contMovOptimo = 0;
		int contDiamantesExplot = 0;
		while (posX < tamFilas) {
			for (int posY = 0; posY < tamColumnas; posY++) {
				if (posX + 1 < tamFilas && explotan(tablero, posX, posY, posX + 1, posY, tamFilas, tamColumnas)) { // Abajo
					contDiamantesExplot = autoContMov(tablero, posX, posY, posX + 1, posY, tamFilas, tamColumnas);
					if (contDiamantesExplot >= contMovOptimo) {
						contMovOptimo = contDiamantesExplot;
						movOptimoFila1 = posX;
						movOptimoColumna1 = posY;
						movOptimoFila2 = posX + 1;
						movOptimoColumna2 = posY;
					}
				}
				else if (posY + 1 < tamColumnas && explotan(tablero, posX, posY, posX, posY + 1, tamFilas, tamColumnas)) { //Derecha
					contDiamantesExplot = autoContMov(tablero, posX, posY, posX, posY + 1, tamFilas, tamColumnas);
					if (contDiamantesExplot >= contMovOptimo) {
						contMovOptimo = contDiamantesExplot;
						movOptimoFila1 = posX;
						movOptimoColumna1 = posY;
						movOptimoFila2 = posX;
						movOptimoColumna2 = posY + 1;
					}
				}
				else if (posY - 1 >= 0 && explotan(tablero, posX, posY, posX, posY - 1, tamFilas, tamColumnas)) { //Izquierda
					contDiamantesExplot = autoContMov(tablero, posX, posY, posX, posY - 1, tamFilas, tamColumnas);
					if (contDiamantesExplot > contMovOptimo) {
						contMovOptimo = contDiamantesExplot;
						movOptimoFila1 = posX;
						movOptimoColumna1 = posY;
						movOptimoFila2 = posX;
						movOptimoColumna2 = posY - 1;
					}
				}
				else if (posX - 1 >= 0 && explotan(tablero, posX, posY, posX - 1, posY, tamFilas, tamColumnas)) {//Arriba
					contDiamantesExplot = autoContMov(tablero, posX, posY, posX - 1, posY, tamFilas, tamColumnas);
					if (contDiamantesExplot >= contMovOptimo) {
						contMovOptimo = contDiamantesExplot;
						movOptimoFila1 = posX;
						movOptimoColumna1 = posY;
						movOptimoFila2 = posX - 1;
						movOptimoColumna2 = posY;
					}
				}
			}
			posX++;
		}
			//Definimos el movimiento mas optimo para explotar
			fila1 = movOptimoFila1; fila2 = movOptimoFila2;
			columna1 = movOptimoColumna1; columna2 = movOptimoColumna2;
			printf("Movimiento mas optimo: \n Fila: %d Columna: %d \n Fila: %d Columna: %d ", fila1, columna1, fila2, columna2);
		}


	if (!hayBomba && hasMoreMovements(tablero, tamFilas, tamColumnas))
	{
		probeMovPosi << <blocks, threads >> > (dev_tablero, fila1, columna1, fila2, columna2, tamFilas, tamColumnas, m, dev_mov,dev_contadorEliminados);
		cudaStatus = cudaMemcpy(&mov, dev_mov, sizeof(bool), cudaMemcpyDeviceToHost);
		if (cudaStatus != cudaSuccess) {
			fprintf(stderr, "cudaMemcpy device to host dev_mov failed!");
			goto Error;
		}

	}
	
	if (mov && !hayBomba && hasMoreMovements(tablero, tamFilas, tamColumnas)) {
		jugarKernel << <blocks, threads >> >(dev_tablero, fila1, columna1, fila2, columna2, tamFilas, tamColumnas, dev_contadorEliminados);
	}
	

	cudaStatus = cudaGetLastError();
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "jugarKernel launch failed: %s\n", cudaGetErrorString(cudaStatus));
		goto Error;
	}

	cudaStatus = cudaMemcpy(tablero, dev_tablero, tamFilas*tamColumnas * sizeof(int), cudaMemcpyDeviceToHost);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy tablero failed!");
		goto Error;
	}
	cudaStatus = cudaMemcpy(contadorEliminados, dev_contadorEliminados, sizeof(int), cudaMemcpyDeviceToHost);
	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMemcpy contadorEliminados failed!");
		goto Error;
	}

Error:
	cudaFree(dev_tablero);
	cudaFree(dev_contadorEliminados);
	cudaFree(dev_mov);

	return cudaStatus;

}

//Metodo para guardar la partida en txt
void guardarPartida(int* tablero, int filas, int columnas, int dificultad) {

	doc = fopen("guardado.txt", "w");
	
		fprintf(doc, "%i \n", filas);
		fprintf(doc, "%i \n", columnas);
		fprintf(doc, "%i \n", dificultad);
		for (int i = 0; i < (filas*columnas); i++) {
			fprintf(doc, "%i ", tablero[i]);
		}
		fclose(doc);
}

//Funcion para cargar una partida guardada en el txt guardado
void cargarPartida() {
	
	cudaError_t cudaStatus;
	leer = fopen("guardado.txt", "r");
	int filas=0;
	int columnas=0;
	int dificultad=0;
	int contadorEliminados = 0;

	// leer el variables del txt
	fscanf(leer, "%d", &filas);
	printf("FILAS: %d",filas);
	
	fscanf(leer, "%d", &columnas);
	printf("\nCOLUMNAS: %d", columnas);

	fscanf(leer, "%d", &dificultad);
	printf("\nCOLORES: %d", dificultad);
	
	int* tablero = (int*)malloc(filas*columnas * sizeof(int));
	
	for (int i = 0; i < filas*columnas; i++) {
		fscanf(leer, "%d", &tablero[i]);
	}

	do {

		imprimeTablero(tablero, filas, columnas);
		cudaStatus = jugar(tablero, filas, columnas, &contadorEliminados, 'm', dificultad);

		imprimeTablero(tablero, filas, dificultad);
		tablero = rellenarTablero(tablero, filas, columnas, dificultad);
		printf("Contador  = %d\n ", contadorEliminados);

	} while ((cudaStatus == 0) && (contadorEliminados < 100));
}
	
int main() {
	//Declaracion de variables para la ejecucion del programa

	cudaError_t cudaStatus;
	int tamFilas; //Filas que tendra el tablero del programa
	int tamColumnas; //Columnas que tendra el tablero del programa
	char modo; //Modo de ejecucion del programa
	int contadorEliminados = 0;
	char dificultad;
	int* tablero;
	int nColores;
	char cargar;

	printf("\nQuieres cargar una partida? (s/n): ");
	scanf("%c",&cargar);
	getchar();
	if(cargar=='s') cargarPartida();
	else {
		modo = pedirModoEjecucion();
		dificultad = pedirDificultad();
		tamFilas = pedirFilasTablero();
		tamColumnas = pedirColumnasTablero();

		if (dificultad == 'F') {
			nColores = 4;
		}
		else if (dificultad == 'M') {
			nColores = 6;
		}
		else {
			nColores = 8;
		}

		printf("\nLos datos introducidos por el usuario son: -%c %c %d %d\n", modo, dificultad, tamFilas, tamColumnas);
		cudaStatus = comprobarPropiedades(tamFilas, tamColumnas);

		if (cudaStatus != cudaSuccess) {
			fprintf(stderr, "cudaMalloc failed!");
			goto Error;
		}

		tablero = generaTablero(tamFilas, tamColumnas, nColores);

	}
	do {
		
		imprimeTablero(tablero, tamFilas, tamColumnas);

		if (modo == 'm') {
			cudaStatus = jugar(tablero, tamFilas, tamColumnas, &contadorEliminados, 'm',nColores);
			
		}
		else
		{

			cudaStatus = jugar(tablero, tamFilas, tamColumnas, &contadorEliminados, 'a',nColores);
			getchar();
		}
	

		imprimeTablero(tablero, tamFilas, tamColumnas);
		tablero = rellenarTablero(tablero, tamFilas, tamColumnas, nColores);
		printf("Contador  = %d\n ", contadorEliminados);

	} while ((cudaStatus == 0) && (contadorEliminados < 100));

	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "Algo ha fallado!");
		goto Error;
	}

	printf(" - - - - - - JUEGO TERMINADO - - - - - - - ");
	
Error:

	getchar();
	getchar();


	return cudaStatus;
}

int* rellenarTablero(int* tablero, int tamFilas, int tamColumnas, int nColores) {
	for (int i = 0; i < tamFilas*tamColumnas; i++)
	{
		if(tablero[i] == 0) tablero[i] = 1 + (rand() % nColores);
	}
	return tablero;
}
//Procedimiento que imprime una matriz de filas * columnas de enteros indicando fila y columna 
void imprimeTablero(int* tablero, int filas, int columnas) {

	printf("\n     ------TABLERO------\n\n      ");

	//Imprime el numero de columna
	for (int i = 0; i < columnas; i++) {

		if (i > 99)printf("%d ", i);
		else if (i > 9) printf("%d  ", i);
		else printf("%d   ", i);

	}

	printf("\n");
	for (int i = 0; i < filas*columnas; i++) {

		//Imprime el numero de fila
		if (i % columnas == 0) {

			printf("\n");

			if ((i / columnas) > 99)printf("%d | ", i / columnas);
			else if ((i / columnas) > 9) printf("%d  | ", i / columnas);
			else printf("%d   | ", i / columnas);


		}
		//Según el valor en la posición i del tablero se imprime de un color u otro
		switch (tablero[i]) {
		case 0: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 0);
			break;
		case 1: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 1);
			break;
		case 2: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 2);
			break;
		case 3: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 12);
			break;
		case 4: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 13);
			break;
		case 5: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 14);
			break;
		case 6: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 15);
			break;
		case 7: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 3);
			break;
		case 8: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 10);
			break;
		case 9: SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 11);
			break;
		default:;
		}
		printf("%d", tablero[i]);

		//Se imprime de nuevo en blanco
		SetConsoleTextAttribute(GetStdHandle(STD_OUTPUT_HANDLE), 7);
		printf(" | ");

	}


	printf("\n\n");

}


/*
Procedimiento que genera un tablero de size x size relleno con numeros del 1 al 2
y con bombas representadas con el número 3
*/
int* generaTablero(int filas, int columnas, int nColores) {

	//Reserva de memoria para el numero de columnas
	int* tablero = (int*)malloc(filas*columnas * sizeof(int));

	for (int i = 0; i < filas*columnas; i++)
	{

			tablero[i] = 1 + (rand() % nColores);

	}
	return tablero;

}

char pedirDificultad() {
	char dificultad=' ';
	getchar();
	while (dificultad != 'F' && dificultad != 'M' && dificultad != 'D') {
		printf("Que dificultad desea para el juego? Facil(F), Medio(M), Dificil(D)\n");
		fflush(stdin);
		scanf("%c", &dificultad);
		if (dificultad != 'F' && dificultad != 'M' && dificultad != 'D' ) {
			printf("Usted ha introducido una dificutad no existente: -%c.\n", dificultad);
			printf("Por favor, introduzca uno de las dificultades que se le presentan a continuacion.\n\n");
		}

	};
	
	return dificultad;

}

//Metodo que solicita al usuario el modo de ejecucion del programa
char pedirModoEjecucion() {
	char modo;
	do {
		printf("Existen 2 modos de ejecucion para Jewels Legend:\n\n");
		printf("- Automatica(a): el programa pulsara aleatoriamente las teclas del tablero\n");
		printf("- Manual(m): el programa esperara a que el usuario pulse las teclas del tablero\n");
		printf("Introduce el modo de ejecucion del programa: "); 
		fflush(stdin);
		scanf("%c", &modo);
		if (modo != 'a' && modo != 'm') {
			printf("Usted ha introducido un modo de ejecucion no existente: %c.\n", modo);
			printf("Por favor, introduzca uno de los modos que se le presentan a continuacion.\n\n");
		}
	} while (modo != 'a' && modo != 'm');
	return modo;
}

//Metodo que solicita al usuario el numero de filas que tendra el tablero
int pedirFilasTablero() {
	int filas;

	do {
		printf("\nIntroduzca las filas que tendra el tablero: ");
		fflush(stdin);
		scanf_s("%d", &filas);
		if (filas < 1 || filas > 2147483647) {
			printf("Introduzca un numero de filas correcto\n");
		}
	} while (filas < 1 || filas > 2147483647); //El numero de filas tiene que ser un numero entero positivo
	return filas;
}

//Metodo que solicita al usuario el numero de columnas que tendra el tablero
int pedirColumnasTablero() {
	int columnas;
	do {
		printf("\nIntroduzca las columnas que tendra el tablero: ");
		fflush(stdin);
		scanf_s("%d", &columnas);
		if (columnas < 1 || columnas > 2147483647) {
			printf("Introduzca un numero de columnas correcto\n");
		}
	} while (columnas < 1 || columnas > 2147483647); //El numero de filas tiene que ser un numero entero positivo
	return columnas;
}

void prop() {

	cudaDeviceProp prop;
	int count;
	HANDLE_ERROR(cudaGetDeviceCount(&count));
	for (int i = 0; i < count; i++) {
		HANDLE_ERROR(cudaGetDeviceProperties(&prop, i));
		printf(" --- General Information for device %d ---\n", i);
		printf("Name: %s\n", prop.name);
		printf("Compute capability: %d.%d\n", prop.major, prop.minor);
		printf("Clock rate: %d\n", prop.clockRate);
		printf("Device copy overlap: ");
		if (prop.deviceOverlap)
			printf("Enabled\n");
		else
			printf("Disabled\n");
		printf("Kernel execition timeout : ");



		if (prop.kernelExecTimeoutEnabled)
			printf("Enabled\n");
		else
			printf("Disabled\n");
		printf(" --- Memory Information for device %d ---\n", i);
		printf("Total global mem: %lu\n", prop.totalGlobalMem);
		printf("Total constant Mem: %ld\n", prop.totalConstMem);
		printf("Max mem pitch: %ld\n", prop.memPitch);
		printf("Texture Alignment: %ld\n", prop.textureAlignment);
		printf(" --- MP Information for device %d ---\n", i);
		printf("Multiprocessor count: %d\n",
			prop.multiProcessorCount);
		printf("Shared mem per mp: %ld\n", prop.sharedMemPerBlock);
		printf("Registers per mp: %d\n", prop.regsPerBlock);
		printf("Threads in warp: %d\n", prop.warpSize);
		printf("Max threads per block: %d\n",
			prop.maxThreadsPerBlock);
		printf("Max thread dimensions: (%d, %d, %d)\n",
			prop.maxThreadsDim[0], prop.maxThreadsDim[1],
			prop.maxThreadsDim[2]);
		printf("Max grid dimensions: (%d, %d, %d)\n",
			prop.maxGridSize[0], prop.maxGridSize[1],
			prop.maxGridSize[2]);

		printf("\n");
	}

}