#include "myNewLib.hpp"
#include <iostream>
namespace myNewLib {

bool XMLVisitor::VisitEnter(const XMLElement&, const XMLAttribute*) {
	return nullptr;
}

XMLPrinter::XMLPrinter(FILE* file, bool compact, int depth) :
		XMLVisitor() {
	return;
}

const char* XMLPrinter::CStr() const {
	return nullptr;
}

XMLNode::XMLNode(XMLDocument*) {
	return;
}

XMLNode::XMLNode(const XMLNode&) {
	return;
}

XMLNode* XMLNode::FirstChild() {
	return NULL;
}

XMLElement* XMLNode::FirstChildElement(const char* name) {
	return NULL;
}

XMLElement* XMLNode::NextSiblingElement(const char* name) {
	return NULL;
}

XMLDocument::XMLDocument(bool processEntities, Whitespace) :
		XMLNode(nullptr) {
	return;
}

XMLDocument::XMLDocument(const XMLDocument&) :
		XMLNode(nullptr) {
	return;
}

XMLError XMLDocument::LoadFile(const char* filename) {
	return XML_SUCCESS;
}

void XMLDocument::Print(XMLPrinter* streamer) const {
	return;
}

XMLElement::XMLElement(XMLDocument* doc) :
		XMLNode(nullptr) {
	return;
}

XMLElement::XMLElement(const XMLElement&) :
		XMLNode(nullptr) {
	return;
}

const char* XMLElement::Attribute(const char* name, const char* value) const {
	return nullptr;
}

XMLAttribute::XMLAttribute() {
	return;
}

XMLAttribute::XMLAttribute(const XMLAttribute&) {
	return;
}

const char* XMLAttribute::Name() const {
	return nullptr;
}

}
