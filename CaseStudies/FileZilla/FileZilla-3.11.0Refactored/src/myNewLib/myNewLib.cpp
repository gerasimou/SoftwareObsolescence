#include "myNewLib.hpp"
#include <iostream>


TiXmlBase::TiXmlBase() {
	return;
}

TiXmlBase::TiXmlBase(const TiXmlBase&) {
	return;
}

void TiXmlBase::SetCondenseWhiteSpace(bool condense) {
	return;
}

TiXmlNode::TiXmlNode(NodeType _type) :
		TiXmlBase() {
	return;
}

TiXmlNode::TiXmlNode(const TiXmlNode&) :
		TiXmlBase() {
	return;
}

TiXmlElement* TiXmlNode::FirstChildElement(const char* _value) {
	return NULL;
}

TiXmlElement* TiXmlNode::FirstChildElement() {
	return NULL;
}

bool TiXmlNode::RemoveChild(TiXmlNode* removeThis) {
	return nullptr;
}

TiXmlElement* TiXmlNode::NextSiblingElement(const char* _next) {
	return NULL;
}

TiXmlElement* TiXmlNode::NextSiblingElement() {
	return NULL;
}

TiXmlElement* TiXmlNode::ToElement() {
	return NULL;
}

TiXmlNode* TiXmlNode::LinkEndChild(TiXmlNode* addThis) {
	return NULL;
}

TiXmlNode* TiXmlNode::InsertEndChild(const TiXmlNode& addThis) {
	return NULL;
}

TiXmlNode* TiXmlNode::FirstChild() {
	return NULL;
}

TiXmlNode* TiXmlNode::NextSibling() {
	return NULL;
}

TiXmlText* TiXmlNode::ToText() {
	return NULL;
}

const char* TiXmlNode::Value() const {
	return nullptr;
}

void TiXmlNode::Clear() {
	return;
}

TiXmlNode* TiXmlNode::InsertBeforeChild(TiXmlNode* beforeThis,
		const TiXmlNode& addThis) {
	return NULL;
}

TiXmlNode* TiXmlNode::IterateChildren(const char* _value,
		const TiXmlNode* previous) {
	return NULL;
}

TiXmlNode* TiXmlNode::Parent() {
	return NULL;
}

TiXmlText::TiXmlText(const char* initValue) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlText::TiXmlText(const std::string& initValue) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlText::TiXmlText(const TiXmlText& copy) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlDocument::TiXmlDocument() :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlDocument::TiXmlDocument(const char* documentName) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlDocument::TiXmlDocument(const std::string& documentName) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlDocument::TiXmlDocument(const TiXmlDocument& copy) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

int TiXmlDocument::ErrorId() const {
	return NULL;
}

const char* TiXmlDocument::ErrorDesc() const {
	return nullptr;
}

bool TiXmlDocument::Accept(TiXmlVisitor* content) const {
	return nullptr;
}

const char* TiXmlDocument::Parse(const char* p, TiXmlParsingData* data,
		TiXmlEncoding encoding) {
	return nullptr;
}

TiXmlElement::TiXmlElement(const char* in_value) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlElement::TiXmlElement(const std::string& _value) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlElement::TiXmlElement(const TiXmlElement&) :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

const char* TiXmlElement::Attribute(const char* name) const {
	return nullptr;
}

void TiXmlElement::SetAttribute(const char* name, int) {
	return;
}

void TiXmlElement::SetAttribute(const char* name, const char* value) {
	return;
}

const char* TiXmlElement::GetText() const {
	return nullptr;
}

TiXmlHandle::TiXmlHandle(TiXmlNode* _node) {
	return;
}

TiXmlHandle::TiXmlHandle(const TiXmlHandle& ref) {
	return;
}

TiXmlText* TiXmlHandle::Text() const {
	return NULL;
}

TiXmlHandle TiXmlHandle::FirstChild() const {
	return NULL;
}

TiXmlHandle TiXmlHandle::FirstChildElement(const char* value) const {
	return NULL;
}

TiXmlPrinter::TiXmlPrinter() :
		TiXmlVisitor() {
	return;
}

void TiXmlPrinter::SetStreamPrinting() {
	return;
}

size_t TiXmlPrinter::Size() {
	return NULL;
}

const char* TiXmlPrinter::CStr() {
	return nullptr;
}


TiXmlDeclaration::TiXmlDeclaration() :
		TiXmlNode(TINYXML_UNKNOWN) {
	return;
}

TiXmlDeclaration::TiXmlDeclaration(	const char* _version, const char* _encoding, const char* _standalone ):
		TiXmlNode(TINYXML_UNKNOWN){
	return;
}

bool TiXmlDocument::LoadFile(FILE* file, TiXmlEncoding encoding ){
	return nullptr;
}


bool TiXmlDocument::SaveFile( FILE* fp ) const{
	return nullptr;
}

const char* TiXmlElement::Attribute( const char* name, int* i ) const
{
	return nullptr;
}
