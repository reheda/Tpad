package ua.pp.hak.compiler;

import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;

public class XSDValidator {

	final static Logger logger = LogManager.getLogger(XSDValidator.class);

	public static void main(String[] args) {
		if (args.length != 2) {
			logger.info("Usage : XSDValidator <file-name.xsd> <file-name.xml>");
		} else {
			boolean isValid = validateXMLSchema(args[0], args[1]);

			if (isValid) {
				logger.info(args[1] + " is valid against " + args[0]);
				
			} else {
				logger.info(args[1] + " is not valid against " + args[0]);
			}
		}
	}

	public static boolean validateXMLSchema(String xsdPath, String xmlPath) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(new File(xsdPath));
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(new File(xmlPath)));
		} catch (IOException e) {
			logger.error("Exception: " + e.getMessage());
			return false;
		} catch (SAXException e1) {
			logger.error("SAX Exception: " + e1.getMessage());
			return false;
		}

		return true;

	}

	public static boolean validateXMLSchema(File xsdFile, File xmlFile) {
		try {
			SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			Schema schema = factory.newSchema(xsdFile);
			Validator validator = schema.newValidator();
			validator.validate(new StreamSource(xmlFile));
		} catch (IOException e) {
			logger.error("Exception: " + e.getMessage());
			return false;
		} catch (SAXException e1) {
			logger.error("SAX Exception: " + e1.getMessage());
			return false;
		}

		return true;

	}
}