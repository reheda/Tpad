package ua.pp.hak.db;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXIResult;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

import ua.pp.hak.util.Attribute;
import ua.pp.hak.util.FileLocation;
import ua.pp.hak.util.XSDValidator;

public class DatabaseStAXWriter {
	static final boolean USE_SCHEMA = true;
	final static String encoding = "UTF-8";
	final static Logger logger = LogManager.getLogger(DatabaseStAXWriter.class);


	public static void save(List<Attribute> attributes, String filePath, String lastUpdate) {
		try {

			StringWriter stringWriter = new StringWriter();

			XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
			XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(stringWriter);

			xMLStreamWriter.writeStartDocument(encoding, "1.0");
			xMLStreamWriter.writeStartElement("attributes");
			xMLStreamWriter.writeAttribute("xmlns", "http://hak.pp.ua/dbSchema");
			xMLStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			xMLStreamWriter.writeAttribute("xsi:schemaLocation", "http://hak.pp.ua/dbSchema dbSchema.xsd");

			for (Attribute attribute : attributes) {


				xMLStreamWriter.writeStartElement("attribute");

				xMLStreamWriter.writeStartElement("id");
				xMLStreamWriter.writeCharacters(String.valueOf(attribute.getId()));
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("type");
				xMLStreamWriter.writeCharacters(attribute.getType());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("name");
				xMLStreamWriter.writeCharacters(attribute.getName());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("deactivated");
				xMLStreamWriter.writeCharacters(String.valueOf(attribute.isDeactivated()));
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("group-id");
				xMLStreamWriter.writeCharacters(String.valueOf(attribute.getGroupId()));
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("group-name");
				xMLStreamWriter.writeCharacters(attribute.getGroupName());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeStartElement("last-update");
				xMLStreamWriter.writeCharacters(attribute.getLastUpdate());
				xMLStreamWriter.writeEndElement();

				xMLStreamWriter.writeEndElement();

			}

			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndDocument();

			xMLStreamWriter.flush();
			xMLStreamWriter.close();

			String xmlString = stringWriter.getBuffer().toString();

			stringWriter.close();

			saveExiToFile(xmlString, new File(filePath));
			saveTextToFile(lastUpdate, new File("db/last-update.txt"));

		} catch (XMLStreamException e) {
			logger.error(e);
		} catch (IOException e) {
			logger.error(e);
		}
	}

	private static void saveExiToFile(String content, File file) {
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		Writer fout = null;
		try {
			fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			fout.write(content);

		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		} finally {
			try {
				fout.close();
			} catch (IOException excp) {
				logger.error(excp.getMessage());
			}
		}

		String schemaLocation = FileLocation.getJarOrNotPath("/dbSchema.xsd");
		try {
			encodeXmlToExi(file.getPath(), schemaLocation);
		} catch (EXIException | IOException | SAXException e) {
			logger.error(e);
		}
		
		if (file!=null){
			file.delete();
			file.deleteOnExit();
		}
	}

	private static void saveTextToFile(String content, File file) {
		File parent = file.getParentFile();
		if (!parent.exists() && !parent.mkdirs()) {
			throw new IllegalStateException("Couldn't create dir: " + parent);
		}
		Writer fout = null;
		try {
			fout = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
			fout.write(content);

		} catch (IOException ioe) {
			logger.error(ioe.getMessage());
		} finally {
			try {
				fout.close();
			} catch (IOException excp) {
				logger.error(excp.getMessage());
			}
		}

	}

	private static void encodeXmlToExi(String xmlLocation, String schemaLocation)
			throws EXIException, IOException, SAXException {
		// https://stackoverflow.com/questions/6540474/java-sample-for-encoding-decoding-exi
		File xmlIn = new File(xmlLocation);

		boolean isValid = XSDValidator.validateXMLSchema(new File(schemaLocation), xmlIn);
		if (!isValid) {
			logger.error("XML file is not valid against XSD Schema");
			return;
		}

		FileInputStream xmlIns = new FileInputStream(xmlIn);

		/*
		 * File exi = new File(xmlIn.getAbsolutePath() + "." + (USE_SCHEMA ?
		 * "schema" : "schemaless") + ".exi");
		 */
		File exi = new File(xmlIn.getAbsolutePath() + ".exi");
		FileOutputStream exiOuts = new FileOutputStream(exi);

		// settings
		EXIFactory exiFactory = DefaultEXIFactory.newInstance();
		exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars(schemaLocation));

		// encode
		InputSource xmlIs = new InputSource(xmlIns);
		EXIResult exiResult = USE_SCHEMA ? new EXIResult(exiFactory) : new EXIResult();
		exiResult.setOutputStream(exiOuts);
		XMLReader xmlReader = XMLReaderFactory.createXMLReader();
		xmlReader.setContentHandler(exiResult.getHandler());
		xmlReader.parse(xmlIs);

	}
}
