/*
 * Rectangle.cpp
 *
 *  Created on: Dec 2, 2016
 *      Author: sgerasimou
 */

#include "Rectangle.h"
//#include "../TinyXML/tinyxml2.h"

namespace elements{
Rectangle::Rectangle() {
	height 	= 0;
	width	= 0;
}

Rectangle::Rectangle(int x, int y) {
	height 	= x;
	width	= y;
}

int Rectangle::area(){
	return width * height;
}

Rectangle::~Rectangle() {
	// TODO Auto-generated destructor stub
}
}


//void testParamDecl (tinyxml2::XMLDocument doc, const char *filename){
//	doc.LoadFile(filename);
//}


