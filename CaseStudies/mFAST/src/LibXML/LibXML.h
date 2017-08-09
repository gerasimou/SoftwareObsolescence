#ifndef LIBXML_INCLUDED
#define LIBXML_INCLUDED
#include </usr/include/sys/_types/_size_t.h>
namespace libxml {

class XMLAttribute;
class XMLVisitor;
class XMLNode;
class XMLElement;
class XMLDocument;
enum Whitespace {
	PRESERVE_WHITESPACE, COLLAPSE_WHITESPACE
};
enum XMLError {
	XML_SUCCESS = 0,
	XML_NO_ERROR = 0,
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
class XMLAttribute {
public:
	const char* Value() const;
	const XMLAttribute* Next() const;

private:
	XMLAttribute();
	XMLAttribute(const XMLAttribute&);
};
class XMLVisitor {
public:
};
class XMLNode {
public:
	const XMLElement* FirstChildElement(const char* value = 0) const;
	const XMLElement* NextSiblingElement(const char* value = 0) const;
	const XMLNode* NextSibling() const;

protected:
	XMLNode(XMLDocument*);

private:
	XMLNode(const XMLNode&);
};
class XMLElement: public XMLNode {
public:
	const XMLAttribute* FindAttribute(const char* name) const;
	const char* Name() const;
	const XMLAttribute* FirstAttribute() const;
	const char* Attribute(const char* name, const char* value = 0) const;

private:
	XMLElement(XMLDocument* doc);
	XMLElement(const XMLElement&);
};
class XMLDocument: public XMLNode {
public:
	XMLDocument(bool processEntities = true, Whitespace = PRESERVE_WHITESPACE);
	XMLError Parse(const char* xml, size_t nBytes = (size_t) ((-1)));
	virtual bool Accept(XMLVisitor* visitor) const;

private:
	XMLDocument(const XMLDocument&);
};

}
#endif //LIBXML_INCLUDED
