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
	double area =  rect.area();

	double radius; // Declare 3 floating-point variables

    cout << "Enter the radius: ";  // Prompting message
	cin >> radius;                 // Read input into variable radius


   //now test xml library
   testTinyXML();


	cout << "\nFinished Simple Demo" << endl;

   return 0;
}

