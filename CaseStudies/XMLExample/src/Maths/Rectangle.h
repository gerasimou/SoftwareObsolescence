/*
 * Rectangle.h
 *
 *  Created on: Dec 2, 2016
 *      Author: sgerasimou
 */

#ifndef MATHS_RECTANGLE_H_
#define MATHS_RECTANGLE_H_
namespace elements{
class Rectangle {
public:
	int width, height;

public:
	Rectangle();
    Rectangle (int x, int y);
	virtual ~Rectangle();

	int area ();
};
}
#endif /* MATHS_RECTANGLE_H_ */
