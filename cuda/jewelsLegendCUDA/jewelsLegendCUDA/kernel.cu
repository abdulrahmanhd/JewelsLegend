#include <stdio.h>
#include <stdlib.h>
#include <cuda.h>
#include <cuda_runtime.h>
#include <cuda_runtime_api.h>
#include "device_launch_parameters.h"
#include <assert.h>
#include <cmath>
#include <Windows.h>


//Poniendo este DEFINE evitamos un error en el que falta la definición de HANDLE_ERROR
#define HANDLE_ERROR
#define COLORES 2
#define ID_BOMBA 8

enum posicion {arriba, abajo, derecha, izquierda };


//Función que devuelve un error si las dimensiones de la martiz son demasiado grandes para la gráfica
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

//Funcion que comprueba los estaDentro de la matriz, ya que tratamos con un array
__device__ bool estaDentro(int x, int y, int filas, int columnas) {

	return !(x >= filas || x < 0 || y >= columnas || y < 0);

}

//funcion que comprueba las posiciones iguales del array,
__device__ int comprobarIgualesPos(int *tablero, int posX, int posY, posicion pos, int tamFilas,int tamColumnas) {
	int cont = 0;
	switch (pos)
	{
	case derecha:
		if (posY + 1 < tamColumnas && tablero[(posX*tamFilas) + posY] == tablero[(posX * tamFilas) + posY + 1]) { // comprobamos derecha 
			cont += 1 + comprobarIgualesPos(tablero, posX, posY + 1, derecha, tamFilas,tamColumnas);
		}
		break;
	case izquierda:
		if (posY - 1 >= 0 && tablero[(posX*tamFilas) + posY] == tablero[(posX * tamFilas) + posY - 1]) { //comprobamos izquierda
			cont += 1 + comprobarIgualesPos(tablero, posX, posY - 1, izquierda, tamFilas,tamColumnas);
		}
		break;
	case abajo:
		if (posX + 1 < tamFilas && tablero[(posX*tamFilas) + posY] == tablero[((posX + 1) * tamFilas) + posY]) { //comprobamos abajo
			cont += 1 + comprobarIgualesPos(tablero, posX + 1, posY, abajo, tamFilas, tamColumnas);
		}
		break;
	case arriba:
		if (posX - 1 >= 0 && tablero[(posX*tamFilas) + posY] == tablero[((posX - 1) * tamFilas) + posY]) { //comprobamos arriba
			cont += 1 + comprobarIgualesPos(tablero, posX - 1, posY, arriba, tamFilas, tamColumnas);
		}
		break;
	default:
		break;
	}

	return cont;

}

//Funcion que elimina una celda
__device__ void eliminar(int* dev_tablero, int fila, int columna, int tamFilas, int* dev_contadorEliminados) {

	// Si el valor examinado es distinto de 0, se suma uno al contador de eliminador
	if (dev_tablero[fila * tamFilas + columna] != 0)
		dev_contadorEliminados[0] = dev_contadorEliminados[0] + 1;

	//El valor se pone a 0
	dev_tablero[fila * tamFilas + columna] = 0;


}
// Funcion que determina si alineacion 1 es mayoor que 2 o viceversa
__device__ bool comprobarMayor(int sameVertical1, int sameHorizon1, int sameVertical2, int sameHorizon2) {
	bool mayor1 = true;
	if (((sameVertical1 >= sameVertical2) && (sameVertical1 >= sameHorizon2)) || ((sameHorizon1 >= sameVertical2) && (sameHorizon1 >= sameHorizon2)))	mayor1 = true;
	else mayor1 = false;
	return mayor1;
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
				while (dev_tablero[i*tamFilas + j] == dev_tablero[i*tamFilas + (jAux + 1)] && jAux + 1 < tamColumnas) { //eliminamos igual por derecha
					jAux++;
					eliminar(dev_tablero, i, jAux, tamFilas, dev_contadorEliminados);
					
				}
				jAux = j;
				while (dev_tablero[i*tamFilas + j] == dev_tablero[i*tamFilas + (jAux - 1)] && jAux - 1 >= 0) {//eliminamos igual por izquierda
					jAux--;
					eliminar(dev_tablero, i, jAux , tamFilas, dev_contadorEliminados);
				}
				eliminar(dev_tablero, i, j, tamFilas, dev_contadorEliminados); //eliminamos la posicion del hilo
			}
			else { //if(mayor1){
				if (comprobarMayor(sameVertical1, sameHorizon1, sameVertical2, sameHorizon2)) {
					int iAux = i;
					while (dev_tablero[i*tamFilas + j] == dev_tablero[(iAux + 1)*tamFilas + j] && iAux + 1 < tamFilas) { //eliminamos igual por arriba
						iAux++;
						eliminar(dev_tablero, iAux, j, tamFilas, dev_contadorEliminados);

					}
					iAux = i;
					while (dev_tablero[i*tamFilas + j] == dev_tablero[(iAux - 1)*tamFilas + j] && iAux - 1 >= 0) {//eliminamos igual por abajo
						iAux--;
						eliminar(dev_tablero, iAux, j, tamFilas, dev_contadorEliminados);
					}
					eliminar(dev_tablero, i, j, tamFilas, dev_contadorEliminados); //eliminamos la posicion del hilo
				}
			}
		}
	}
	else {
		if (i == fila2 && j == columna2) {
			if (sameHorizon2 > sameVertical2) {
				int jAux = j;
				while (dev_tablero[i*tamFilas + j] == dev_tablero[i*tamFilas + (jAux + 1)] && jAux + 1 < tamColumnas) { //eliminamos igual por derecha
					jAux++;
					eliminar(dev_tablero, i, jAux, tamFilas, dev_contadorEliminados);
				}
				jAux = j;
				while (dev_tablero[i*tamFilas + j] == dev_tablero[i*tamFilas + (jAux - 1)] && jAux - 1 >= 0) {//eliminamos igual por izquierda
					jAux--;
					eliminar(dev_tablero, i, jAux, tamFilas, dev_contadorEliminados);
				}
				eliminar(dev_tablero, i, j, tamFilas, dev_contadorEliminados); //eliminamos la posicion
			}
			else{// if(mayor2) {
				if (!comprobarMayor(sameVertical1, sameHorizon1, sameVertical2, sameHorizon2)) {
					int iAux = i;
					while (dev_tablero[i*tamFilas + j] == dev_tablero[(iAux + 1) * tamFilas + j] && iAux + 1 < tamFilas) { //eliminamos igual por abajo
						iAux++;
						eliminar(dev_tablero, iAux, j, tamFilas, dev_contadorEliminados);

					}
					iAux = i;
					while (dev_tablero[i * tamFilas + j] == dev_tablero[(iAux - 1) * tamFilas + j] && iAux - 1 >= 0) {//eliminamos igual por arriba
						iAux--;
						eliminar(dev_tablero, iAux, j, tamFilas, dev_contadorEliminados);
					}
					eliminar(dev_tablero, i, j, tamFilas, dev_contadorEliminados); //eliminamos la posicion del hilo
				}
			}
			
		}
	}
}



// Metodo que sube los 0 que se encuentran en el tablero hacia la parte mas alta del mismo
__device__ void reestructuracionArribaAbajo(int* dev_tablero, int filas, int columnas) {

	
	int celdax = blockIdx.y* blockDim.y + threadIdx.y;		//Indice de la x
	int celday = blockIdx.x* blockDim.x + threadIdx.x;		//Indice de la y

	if(dev_tablero[celdax*filas+celday] == 0){
		int celdaxAux = celdax;
		while (celdaxAux-1 >= 0) {
			if (dev_tablero[(celdaxAux - 1)*filas + celday] != 0) {
				int colorAux = dev_tablero[(celdaxAux - 1)*filas + celday];
				dev_tablero[(celdaxAux - 1)*filas + celday] = 0;
				dev_tablero[celdaxAux*filas+celday] = colorAux;
			}
			celdaxAux--;
			
		}
	}

	/*int nombre = celdax * columnas + celday;	//Valor del elemento en el array
	int actual = nombre;
	int count = 0;
	int comprobador = 0;
	int size = (filas*columnas); //Tamaño de la matriz
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

	}*/

}

__device__ void reestructuracionIzquierdaDerecha(int* dev_tablero, int filas, int columnas, int fila, int columna) {

	int size = (filas*columnas);						//Tamaño de la matriz
	int x = blockIdx.y * blockDim.y + threadIdx.y;		//Indice de la x
	int y = blockIdx.x * blockDim.x + threadIdx.x;		//Indice de la y

	int comprobador = 0; // Numero de elementos que no son 0 por encima de la celda visitada que contiene un 0
	int actual;
	int auxiliar;

	//La celda eliminada es quien reestructura la matriz
	if (x == fila && y == columna) {

		//Por cada celda se comprueba si es 0
		for (int i = size; i >= 0; i--)
		{
			//Si es 0, el 0 se desplaza a la derecha tantas veces como numeros diferentes por la derecha tenga
			if (dev_tablero[i] == 0) {

				actual = i + 1;

				while (actual % columnas != 0) {
					if (dev_tablero[actual] != 0) {
						comprobador++;
					}
					actual += 1;
				}

			}

			actual = i;

			//Intercambio
			for (int k = 0; k < comprobador; k++) {

				auxiliar = dev_tablero[actual + 1];
				dev_tablero[actual + 1] = dev_tablero[actual];
				dev_tablero[actual] = auxiliar;

				actual += 1;

			}

			comprobador = 0;

		}

	}

}

__global__ void jugarKernel(int* dev_tablero, int fila1, int columna1, int fila2, int columna2,int tamFila, int tamColumnas, int* dev_contadorEliminados) {

	comprobarCadena(dev_tablero, fila1, columna1, fila2, columna2,tamFila,tamColumnas, dev_contadorEliminados);

	__syncthreads();

	//reestructuracionArribaAbajo(dev_tablero, tamFila, tamColumnas);

	__syncthreads();

	//reestructuracionIzquierdaDerecha(dev_tablero, filas, columnas, fila, columna);

	//__syncthreads();

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
	bool explotan = false;

	//HAcemos el intercambio en la matriz para comprobar si se puede explotar
	int colorAux1 = dev_tablero[(filas1*tamFilas) + columnas1];
	int colorAux2 = dev_tablero[(fila2*tamFilas) + columna2];
	dev_tablero[(filas1*tamFilas) + columnas1] = colorAux2;
	dev_tablero[(fila2*tamFilas) + columna2] = colorAux1;

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
	dev_tablero[(filas1*tamFilas) + columnas1] = colorAux1;
	dev_tablero[(fila2*tamFilas) + columna2] = colorAux2;

	return explotan;
}


__global__ void probeMovPosi(int* dev_tablero, int filas1, int columnas1, int fila2, int columna2,int tamFilas,int tamColumnas,bool* dev_mov) {
	int col = threadIdx.x;
	int fil = threadIdx.y;
	bool TrueMov;
	bool explot;
	if ((col == columnas1 && fil == filas1)) {//comprobamos que el hilo que accede a la funcion sea el que queremos cambiar(se comprueban en las funciones los dos numeros)
		TrueMov = adyacentes(filas1, columnas1, fila2, columna2);
		explot = explotChange(dev_tablero, filas1, columnas1, fila2, columna2,tamFilas,tamColumnas);
		if (TrueMov && explot) {
			//Si ambos son true realizamos el cmbio
			int colorAux1 = dev_tablero[(filas1*tamFilas) + columnas1];
			int colorAux2 = dev_tablero[(fila2*tamFilas) + columna2];
			dev_tablero[(filas1*tamFilas) + columnas1] = colorAux2;
			dev_tablero[(fila2*tamFilas) + columna2] = colorAux1;
			*dev_mov = true;
		}
		else { printf("MOVIMIENTO ERRONEO, Las posiciones no son adyacentes o no explotan\n"); }
	}
}

cudaError_t jugar(int* tablero, int tamFilas, int tamColumnas, int* contadorEliminados, char m) {

	cudaError_t cudaStatus;
	int fila1 = 0, columna1 = 0, fila2 = 0, columna2 = 0;
	bool* mov = false, *dev_mov;
	int* dev_tablero;
	int *dev_contadorEliminados;

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
	//Modo manual
	if (m == 'm') {
		while (!mov) {
			printf("Introduzca la fila del primer diamante: ");
			scanf_s("%d", &fila1);
			printf("Introduzca la columna del primer diamante: ");
			scanf_s("%d", &columna1);
			printf("Introduzca la fila del segundo diamante: ");
			scanf_s("%d", &fila2);
			printf("Introduzca la columna del segundo diamante: ");
			scanf_s("%d", &columna2);
			
			//bool canMove = hasMoreMovements(tablero);
			probeMovPosi<< <blocks, threads>> >(dev_tablero, fila1, columna1, fila2, columna2, tamFilas, tamColumnas,dev_mov);

			cudaStatus = cudaMemcpy(&mov, dev_mov, sizeof(bool), cudaMemcpyDeviceToHost);
			if (cudaStatus != cudaSuccess) {
				fprintf(stderr, "cudaMemcpy device to host dev_mov failed!");
				goto Error;
			}

		}

	}
	else {
		/*AUTOMATICO
		while (tablero[fila *columnas + columna] == 0) {
			fila = 0 + (rand() % filas);
			columna = 0 + (rand() % columnas);
		}

		printf("Numero de fila: %d\n", fila);
		printf("Numero de columna: %d\n", columna);*/
	}

	jugarKernel<< <blocks, threads >> >(dev_tablero, fila1, columna1, fila2, columna2, tamFilas, tamColumnas, dev_contadorEliminados);

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

int* generaTablero(int filas, int columnas, int bombas);
void imprimeTablero(int* tablero, int filas, int columnas);
char pedirModoEjecucion();
int pedirFilasTablero();
int pedirColumnasTablero();
char pedirDificultad();
void prop();

int main() {
	//Declaracion de variables para la ejecucion del programa

	cudaError_t cudaStatus;
	//prop();
	int tamFilas; //Filas que tendra el tablero del programa
	int tamColumnas; //Columnas que tendra el tablero del programa
	char modo; //Modo de ejecucion del programa
	int contadorEliminados = 0;
	char dificultad;
	int* tablero;
	int nColores;

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

	printf("\nLos datos introducidos por el usuario son: -%c %c %d %d\n", modo,dificultad, tamFilas, tamColumnas);

	cudaStatus = comprobarPropiedades(tamFilas, tamColumnas);

	if (cudaStatus != cudaSuccess) {
		fprintf(stderr, "cudaMalloc failed!");
		goto Error;
	}

	tablero = generaTablero(tamFilas, tamColumnas, nColores);


	do {
		imprimeTablero(tablero, tamFilas, tamColumnas);

		if (modo == 'm') {
			cudaStatus = jugar(tablero, tamFilas, tamColumnas, &contadorEliminados, 'm');
		}
		else
		{
			cudaStatus = jugar(tablero, tamFilas, tamColumnas, &contadorEliminados, 'a');
		}

		imprimeTablero(tablero, tamFilas, tamColumnas);

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
		printf("Existen 2 modos de ejecucion para Jewels Leyend:\n\n");
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
/*
bool hasMoreMovements(int *tablero) {
	bool expl = false;
	int posX = 0;
	while (posX < filas && !expl) {
		for (int posY = 0; posY < columnas; posY++) {
			if (posX + 1 < filas && explotan(tablero, posX, posY, posX + 1, posY, true)) { // Abajo
				expl = true;
			}
			else if (posY + 1 < columnas && explotan(tablero, posX, posY, posX, posY + 1, true)) { //Derecha
				expl = true;
			}
			else if (posY - 1 >= 0 && explotan(tablero, posX, posY, posX, posY - 1, true)) { //Izquierda
				expl = true;
			}
			else if (posX - 1 >= 0 && explotan(tablero, posX, posY, posX - 1, posY, true)) {//Arriba
				expl = true;
			}
		}
		posX++;
	}

	return expl;
}*/


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