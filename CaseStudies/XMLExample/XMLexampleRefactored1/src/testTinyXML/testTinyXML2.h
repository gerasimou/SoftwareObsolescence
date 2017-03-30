/*
 * testTinyXML.h
 *
 *  Created on: Nov 30, 2016
 *      Author: sgerasimou
 */

#ifndef TESTTINYXML_TESTTINYXML_H_
#define TESTTINYXML_TESTTINYXML_H_

#include <myNewLib/myNewLib.hpp>
#include <cstdio>

namespace testTinyXML2
{
	namespace inner{
	myNewLib::XMLError testParamDecl(const char *filename);

int testTinyXML(myNewLib::XMLError error);
	}
}

#endif /* TESTTINYXML_TESTTINYXML_H_ */
