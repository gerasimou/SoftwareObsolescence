#ifndef myNewLib_INCLUDED
#define myNewLib_INCLUDED
#include </usr/include/stdio.h>
namespace myNewLib {

class XMLVisitor;
class XMLPrinter;
class XMLNode;
class XMLDocument;
class XMLElement;
class XMLAttribute;
enum XMLError {
	XML_SUCCESS = 0,
	XML_NO_ATTRIBUTE,
	XML_WRONG_ATTRIBUTE_TYPE,
	XML_ERROR_FILE_NOT_FOUND,
	XML_ERROR_FILE_COULD_NOT_BE_OPENED,
	XML_ERROR_FILE_READ_ERROR,
	XML_ERROR_ELEMENT_MISMATCH,
	XML_ERROR_PARSING_ELEMENT,
	XML_ERROR_PARSING_ATTRIBUTE,
	XML_ERROR_IDENTIFYING_TAG,
	XML_ERROR_PARSING_TEXT,
	XML_ERROR_PARSING_CDATA,
	XML_ERROR_PARSING_COMMENT,
	XML_ERROR_PARSING_DECLARATION,
	XML_ERROR_PARSING_UNKNOWN,
	XML_ERROR_EMPTY_DOCUMENT,
	XML_ERROR_MISMATCHED_ELEMENT,
	XML_ERROR_PARSING,
	XML_CAN_NOT_CONVERT_TEXT,
	XML_NO_TEXT_NODE,
	XML_ERROR_COUNT
};
enum Whitespace {
	PRESERVE_WHITESPACE, COLLAPSE_WHITESPACE
};
class XMLVisitor {
public:
	virtual bool VisitEnter(const XMLElement&, const XMLAttribute*);
};
class XMLPrinter: public XMLVisitor {
public:
	XMLPrinter(FILE* file = 0, bool compact = false, int depth = 0);
	const char* CStr() const;
};
class XMLNode {
public:
	XMLNode* FirstChild();
	XMLElement* FirstChildElement(const char* name = 0);
	XMLElement* NextSiblingElement(const char* name = 0);

protected:
	XMLNode(XMLDocument*);

private:
	XMLNode(const XMLNode&);
};
class XMLDocument: public XMLNode {
public:
	XMLDocument(bool processEntities = true, Whitespace = PRESERVE_WHITESPACE);
	XMLError LoadFile(const char* filename);
	void Print(XMLPrinter* streamer = 0) const;

private:
	XMLDocument(const XMLDocument&);
};
class XMLElement: public XMLNode {
public:
	const char* Attribute(const char* name, const char* value = 0) const;

private:
	XMLElement(XMLDocument* doc);
	XMLElement(const XMLElement&);
};
class XMLAttribute {
public:
	const char* Name() const;

private:
	XMLAttribute();
	XMLAttribute(const XMLAttribute&);
};

}
#endif //myNewLib_INCLUDED
