package com.housing;

import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

/**
 * XmlValidator
 *
 * Uses JAXP SchemaFactory and Validator to validate an XML document
 * against a given XSD schema.
 *
 * Technologies:
 *   - javax.xml.validation.SchemaFactory
 *   - javax.xml.validation.Schema
 *   - javax.xml.validation.Validator
 */
public class XmlValidator {

    /**
     * Validate an XML file against an XSD schema.
     *
     * @param xmlFilePath  Path to the XML file to validate
     * @param xsdFilePath  Path to the XSD schema file
     * @return true if the XML document is valid, false otherwise
     */
    public boolean validate(String xmlFilePath, String xsdFilePath) {
        System.out.println("  📄 XML File : " + xmlFilePath);
        System.out.println("  📋 XSD File : " + xsdFilePath);
        System.out.println();

        try {
            // 1. Create a SchemaFactory for W3C XML Schema
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

            // 2. Load the XSD schema from file
            Schema schema = schemaFactory.newSchema(new File(xsdFilePath));

            // 3. Create a Validator from the schema
            Validator validator = schema.newValidator();

            // 4. Validate the XML source
            validator.validate(new StreamSource(new File(xmlFilePath)));

            System.out.println("  ✅ Validation SUCCESS — The XML document is VALID according to the XSD schema.");
            return true;

        } catch (SAXException e) {
            System.err.println("  ❌ Validation FAILED — XML does not conform to XSD schema.");
            System.err.println("     Reason: " + e.getMessage());
            return false;

        } catch (IOException e) {
            System.err.println("  ❌ IO Error — Could not read XML or XSD file.");
            System.err.println("     Reason: " + e.getMessage());
            return false;
        }
    }
}
