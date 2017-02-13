#include <Windows.h>
class Diamante
{
public:
	int X, Y;
	int color;
	bool xplode;

	Diamante();
	Diamante(int x, int y);
	void explotarDiamante();
	void printDiamante(HANDLE hConsole);
	void comprobarColor();
	~Diamante();
};

