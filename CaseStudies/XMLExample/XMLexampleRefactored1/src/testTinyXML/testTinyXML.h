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

using namespace myNewLib;

namespace testTinyXML2
{
	namespace inner{
		XMLError testParamDecl(XMLError error, const char *filename);

		int testTinyXML();
	}
}

#endif /* TESTTINYXML_TESTTINYXML_H_ */
