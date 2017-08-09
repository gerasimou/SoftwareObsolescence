#include "LibXML.h"
#include <iostream>
namespace libxml {

XMLAttribute::XMLAttribute() {
	return;
}

const char* XMLAttribute::Value() const {
	return NULL;
}

const XMLAttribute* XMLAttribute::Next() const {
	return NULL;
}

XMLNode::XMLNode(XMLDocument* doc) {
	return;
}

const XMLElement* XMLNode::FirstChildElement(const char* value) const {
	return NULL;
}

const XMLElement* XMLNode::NextSiblingElement(const char* value) const {
	return NULL;
}

const XMLNode* XMLNode::NextSibling() const {
	return NULL;
}

XMLElement::XMLElement(XMLDocument* doc) :
		XMLNode(doc) {
	return;
}

const XMLAttribute* XMLElement::FindAttribute(const char* name) const {
	return NULL;
}

const char* XMLElement::Name() const {
	return NULL;
}

const XMLAttribute* XMLElement::FirstAttribute() const {
	return NULL;
}

const char* XMLElement::Attribute(const char* name, const char* value) const {
	return NULL;
}

XMLDocument::XMLDocument(bool processEntities, Whitespace whitespace) :
		XMLNode(0) {
	return;
}

XMLError XMLDocument::Parse(const char* p, size_t len) {
	return XML_SUCCESS;
}

bool XMLDocument::Accept(XMLVisitor* visitor) const {
	return NULL;
}

}
