/*
 * testTinyXML.h
 *
 *  Created on: Nov 30, 2016
 *      Author: sgerasimou
 */

#ifndef TESTTINYXML_TESTTINYXML_H_
#define TESTTINYXML_TESTTINYXML_H_

#include <TinyXML/tinyxml2.h>
#include <cstdio>

using namespace tinyxml2;

namespace testTinyXML2
{
	namespace inner{
		XMLError testParamDecl(XMLError error, const char *filename);

		int testTinyXML();
	}
}

#endif /* TESTTINYXML_TESTTINYXML_H_ */
