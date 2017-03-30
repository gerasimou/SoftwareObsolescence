#ifndef myNewLib_INCLUDED
#define myNewLib_INCLUDED
#include </Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/include/c++/v1/iosfwd>
#include </Applications/Xcode.app/Contents/Developer/Toolchains/XcodeDefault.xctoolchain/usr/include/c++/v1/iosfwd>


class TiXmlBase;
class TiXmlNode;
class TiXmlText;
class TiXmlDocument;
class TiXmlElement;
class TiXmlHandle;
class TiXmlVisitor;
class TiXmlPrinter;
class TiXmlVisitor;
class TiXmlDeclaration;


enum NodeType {
	TINYXML_DOCUMENT,
	TINYXML_ELEMENT,
	TINYXML_COMMENT,
	TINYXML_UNKNOWN,
	TINYXML_TEXT,
	TINYXML_DECLARATION,
	TINYXML_TYPECOUNT
};

enum TiXmlEncoding {
	TIXML_ENCODING_UNKNOWN,
	TIXML_ENCODING_UTF8,
	TIXML_ENCODING_LEGACY,
	TIXML_DEFAULT_ENCODING
};


struct TiXmlCursor
{
	TiXmlCursor()		{ Clear(); }
	void Clear()		{ row = col = -1; }

	int row;	// 0 based.
	int col;	// 0 based.
};


class TiXmlParsingData
{
	friend class TiXmlDocument;
  public:
	void Stamp( const char* now, TiXmlEncoding encoding );

	const TiXmlCursor& Cursor() const	{ return cursor; }

  private:
	// Only used by the document!
	TiXmlParsingData( const char* start, int _tabsize, int row, int col )
	{
		stamp = start;
		tabsize = _tabsize;
		cursor.row = row;
		cursor.col = col;
	}

	TiXmlCursor		cursor;
	const char*		stamp;
	int				tabsize;
};


class TiXmlBase {
public:
	TiXmlBase();
	static void SetCondenseWhiteSpace(bool condense);


	enum
	{
		TIXML_NO_ERROR = 0,
		TIXML_ERROR,
		TIXML_ERROR_OPENING_FILE,
		TIXML_ERROR_PARSING_ELEMENT,
		TIXML_ERROR_FAILED_TO_READ_ELEMENT_NAME,
		TIXML_ERROR_READING_ELEMENT_VALUE,
		TIXML_ERROR_READING_ATTRIBUTES,
		TIXML_ERROR_PARSING_EMPTY,
		TIXML_ERROR_READING_END_TAG,
		TIXML_ERROR_PARSING_UNKNOWN,
		TIXML_ERROR_PARSING_COMMENT,
		TIXML_ERROR_PARSING_DECLARATION,
		TIXML_ERROR_DOCUMENT_EMPTY,
		TIXML_ERROR_EMBEDDED_NULL,
		TIXML_ERROR_PARSING_CDATA,
		TIXML_ERROR_DOCUMENT_TOP_ONLY,

		TIXML_ERROR_STRING_COUNT
	};

private:
	TiXmlBase(const TiXmlBase&);
};
class TiXmlNode: public TiXmlBase {
public:
	TiXmlElement* FirstChildElement(const char* _value);
	bool RemoveChild(TiXmlNode* removeThis);
	TiXmlElement* NextSiblingElement(const char* _next);
	virtual TiXmlElement* ToElement();
	TiXmlNode* LinkEndChild(TiXmlNode* addThis);
	TiXmlNode* InsertEndChild(const TiXmlNode& addThis);
	TiXmlNode* FirstChild();
	TiXmlNode* NextSibling();
	virtual TiXmlText* ToText();
	const char* Value() const;
	void Clear();
	TiXmlNode* InsertBeforeChild(TiXmlNode* beforeThis,
			const TiXmlNode& addThis);
	TiXmlNode* IterateChildren(const char* _value, const TiXmlNode* previous);
	TiXmlNode* Parent();

	TiXmlElement* FirstChildElement();
	TiXmlElement* NextSiblingElement();

protected:
	TiXmlNode(NodeType _type);

private:
	TiXmlNode(const TiXmlNode&);
};
class TiXmlText: public TiXmlNode {
public:
	TiXmlText(const char* initValue);
	TiXmlText(const std::string& initValue);
	TiXmlText(const TiXmlText& copy);
};
class TiXmlDocument: public TiXmlNode {
public:
	TiXmlDocument();
	TiXmlDocument(const char* documentName);
	TiXmlDocument(const std::string& documentName);
	TiXmlDocument(const TiXmlDocument& copy);
	int ErrorId() const;
	const char* ErrorDesc() const;
	virtual bool Accept(TiXmlVisitor* content) const;
	const virtual char* Parse(const char* p, TiXmlParsingData* data = 0,
			TiXmlEncoding encoding = TIXML_DEFAULT_ENCODING);

	bool LoadFile( FILE*, TiXmlEncoding encoding = TIXML_DEFAULT_ENCODING );
	bool SaveFile( FILE* ) const;

};
class TiXmlElement: public TiXmlNode {
public:
	TiXmlElement(const char* in_value);
	TiXmlElement(const std::string& _value);
	TiXmlElement(const TiXmlElement&);
	const char* Attribute(const char* name) const;
	void SetAttribute(const char* name, int);
	void SetAttribute( const char* name, const char * _value );
	const char* GetText() const;

	const char* Attribute( const char* name, int* i ) const;
};
class TiXmlHandle {
public:
	TiXmlHandle(TiXmlNode* _node);
	TiXmlHandle(const TiXmlHandle& ref);
	TiXmlText* Text() const;
	TiXmlHandle FirstChild() const;
	TiXmlHandle FirstChildElement(const char* value) const;
};
class TiXmlVisitor {
};
class TiXmlPrinter: public TiXmlVisitor {
public:
	TiXmlPrinter();
	void SetStreamPrinting();
	size_t Size();
	const char* CStr();
};

class TiXmlDeclaration : public TiXmlNode
{
public:
	TiXmlDeclaration();
	TiXmlDeclaration(	const char* _version,
						const char* _encoding,
						const char* _standalone );
};

#endif //myNewLib_INCLUDED
