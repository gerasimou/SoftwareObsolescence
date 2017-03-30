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

namespace testTinyXML2
{
	namespace inner{
		tinyxml2::XMLError testParamDecl(const char *filename);

		int testTinyXML(tinyxml2::XMLError error);
	}
}

#endif /* TESTTINYXML_TESTTINYXML_H_ */
