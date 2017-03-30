/*
 * testTinyXML.cpp
 *
 *  Created on: Nov 30, 2016
 *      Author: sgerasimou
 */

#include <iostream>
#include <string>
#include "testTinyXML.h"

using namespace std;
namespace testTinyXML2
{
	namespace inner{

using namespace myNewLib;

		class MyVisitor: public XMLVisitor{
		virtual bool VisitEnter(const XMLElement &element,
								const XMLAttribute *attr) override;

		bool testMe(){
			XMLDocument doc;}
		};


		int testTinyXML(){
			string filename;

			cin.ignore();
			cout << "\nEnter XML file: ";  // Prompting message
			getline(cin, filename);			 // Read input

			// Load and parse an XML file
			XMLDocument xmlDoc;
			XMLError result = xmlDoc.LoadFile(filename.c_str());

			if (result != XML_SUCCESS ){
				cout  << "Error: " << result << filename << endl;
				exit(-1);
			}

			//print xml contents
			cout << "\n\nPrint contents in " << filename << endl;
			cout << "---------------------------------------" << endl;
			XMLPrinter printer;
			xmlDoc.Print(&printer);
			cout <<  printer.CStr();

			cin.ignore();
			//get the root node
			XMLNode *pRoot = xmlDoc.FirstChild();
			if (pRoot == nullptr){
				cout << "Empty XML" << endl;
				return XML_ERROR_FILE_READ_ERROR;
			}


	//		//print only the direct descendant
	//		cout << "\n\nPrinting direct descendants in " << filename <<endl;
	//		cout << "---------------------------------------" << endl;
	//		XMLNode* node = pRoot;
	//		while (node != nullptr){
	//			cout << node->Value()<< endl;
	//			node = node->NextSibling();
	//		}


			//print only the direct descendant
			cout << "\n\nPrint all persons ID in " << filename <<endl;
			cout << "---------------------------------------" << endl;
			XMLNode* personnel =  pRoot;
			if (personnel == nullptr) cout << XML_ERROR_PARSING_ELEMENT;

			XMLElement* personElement = personnel->FirstChildElement();
			if (personElement == nullptr) cout << XML_ERROR_PARSING_ELEMENT;
			while (personElement != nullptr){
				cout << personElement->Attribute("id") << endl;
				personElement = personElement->NextSiblingElement("person");
			}

			XMLAttribute *attr;
			attr->Name();

			return 0;

		}



		XMLError testParamDecl (XMLError error, const char *filename){
			XMLDocument doc;
			doc.LoadFile(filename);

			return error;
		}

	}
}

