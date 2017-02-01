#include <windows.h>

class Block
{

public:

	int X,Y;
	int color;
	bool xplode;


	Block(void);
	Block(int x, int y);	
	void explotarBloque();
	void printBloque(HANDLE hConsole);
	void comprobarColor();

};

