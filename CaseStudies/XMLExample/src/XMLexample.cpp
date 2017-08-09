//============================================================================
// Name        : XMLexample.cpp
// Author      : SPG
// Version     :
// Copyright   : Your copyright notice
// Description : Hello World in C++, Ansi-style
//============================================================================

#include <iostream>
#include "testTinyXML/testTinyXML.h"
#include "Maths/Rectangle.h"

using namespace std;
using namespace testTinyXML2::inner;
using namespace elements;


int main() {
	cout << "This is a simple example" << endl;
	cout << "------------------------" << endl;

	Rectangle rect(5,4);

	double width, height; // Declare 3 floating-point variables

    cout << "Enter width: ";  		// Prompting message
	cin >> width;                 // Read input into variable radius
	rect.width = width;

	cout << "Enter height: ";  		// Prompting message
	cin >> height;                 // Read input into variable radius
	rect.height = height;

	cout << "Rectangle's area is :" << rect.area() << endl;


   //now test xml library
   testTinyXML();


	cout << "\nFinished Simple Demo" << endl;

   return 0;
}

