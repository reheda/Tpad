package ua.pp.hak.compiler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.InputSource;

import com.siemens.ct.exi.EXIFactory;
import com.siemens.ct.exi.GrammarFactory;
import com.siemens.ct.exi.api.sax.EXISource;
import com.siemens.ct.exi.exceptions.EXIException;
import com.siemens.ct.exi.helpers.DefaultEXIFactory;

public class StAXParser {

	static final boolean USE_SCHEMA = true;
	final static Logger logger = LogManager.getLogger(StAXParser.class);

	public static List<Attribute> parse() {
		List<Attribute> list = new ArrayList<>();
		try {
			String xmlLocation = getJarOrNotPath("/db.xml.exi");
			String schemaLocation = getJarOrNotPath("/dbSchema.xsd");
			File exi = new File(xmlLocation);
			File xmlOut = new File(xmlLocation + (USE_SCHEMA ? "schema" : "schemaless") + ".xml");

			// settings
			EXIFactory exiFactory = DefaultEXIFactory.newInstance();
			exiFactory.setGrammars(GrammarFactory.newInstance().createGrammars(schemaLocation));

			// decode
			FileOutputStream xmlOuts = new FileOutputStream(xmlOut);
			FileInputStream exiIns = new FileInputStream(exi);
			InputSource exiIs = new InputSource(exiIns);
			EXISource exiSource = USE_SCHEMA ? new EXISource(exiFactory) : new EXISource();
			exiSource.setInputSource(exiIs);
			exiSource.setXMLReader(exiSource.getXMLReader());
			TransformerFactory.newInstance().newTransformer().transform(exiSource, new StreamResult(xmlOuts));

			boolean isValid = XSDValidator.validateXMLSchema(new File(schemaLocation), xmlOut);
			if (!isValid) {
				System.err.println("XML file is not valid against XSD Schema");
				return null;
			}

			boolean bId = false;
			boolean bType = false;
			boolean bName = false;

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(xmlOut.getPath()));

			int id = 0;
			String type = null;
			String name = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().getLocalPart();
					if (qName.equalsIgnoreCase("id")) {
						bId = true;
					} else if (qName.equalsIgnoreCase("type")) {
						bType = true;
					} else if (qName.equalsIgnoreCase("name")) {
						bName = true;
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if (bId) {
						id = Integer.valueOf(characters.getData());
						bId = false;
					}
					if (bType) {
						type = characters.getData();
						bType = false;
					}
					if (bName) {
						name = characters.getData().replace("///", "&");
						bName = false;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equalsIgnoreCase("attribute")) {
						list.add(new Attribute(id, type, name));
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (XMLStreamException e) {
			logger.error(e.getMessage());
		} catch (EXIException e) {
			logger.error(e.getMessage());
		} catch (TransformerConfigurationException e) {
			logger.error(e.getMessage());
		} catch (TransformerException e) {
			logger.error(e.getMessage());
		} catch (TransformerFactoryConfigurationError e) {
			logger.error(e.getMessage());
		}

		return list;
	}

	private static List<Attribute> parseWithoutExi() {
		String schemaLocation = getJarOrNotPath("/dbSchema.xsd");
		String xmlLocation = getJarOrNotPath("/db.xml");
		boolean isValid = XSDValidator.validateXMLSchema(schemaLocation, xmlLocation);
		if (!isValid) {
			System.err.println("XML file is not valid against XSD Schema");
			return null;
		}

		List<Attribute> list = new ArrayList<>();

		boolean bId = false;
		boolean bType = false;
		boolean bName = false;
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(xmlLocation));

			int id = 0;
			String type = null;
			String name = null;

			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().getLocalPart();
					if (qName.equalsIgnoreCase("id")) {
						bId = true;
					} else if (qName.equalsIgnoreCase("type")) {
						bType = true;
					} else if (qName.equalsIgnoreCase("name")) {
						bName = true;
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if (bId) {
						id = Integer.valueOf(characters.getData());
						bId = false;
					}
					if (bType) {
						type = characters.getData();
						bType = false;
					}
					if (bName) {
						name = characters.getData().replace("///", "&");
						bName = false;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equalsIgnoreCase("attribute")) {
						list.add(new Attribute(id, type, name));
					}
					break;
				}
			}
		} catch (FileNotFoundException e) {
			logger.error(e.getMessage());
		} catch (XMLStreamException e) {
			logger.error(e.getMessage());
		}

		return list;
	}

	public static void main(String[] args) {
		List<Attribute> list = parse();
		for (Attribute attr : list) {
			System.out.printf("id=%d, type='%s', name='%s'%n", attr.getId(), attr.getType(), attr.getName());
		}
	}

	private static String getJarOrNotPath(String resource) {
		File file = null;
		URL res = StAXParser.class.getClass().getResource(resource);
		if (res.toString().startsWith("jar:")) {
			try {
				InputStream input = StAXParser.class.getClass().getResourceAsStream(resource);
				file = File.createTempFile("tempfile", ".tmp");
				OutputStream out = new FileOutputStream(file);
				int read;
				byte[] bytes = new byte[1024];

				while ((read = input.read(bytes)) != -1) {
					out.write(bytes, 0, read);
				}
				out.close();
				file.deleteOnExit();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		} else {
			// this will probably work in your IDE, but not from a JAR
			String filePath;
			try {
				filePath = URLDecoder.decode(res.getFile(), "UTF-8");
				file = new File(filePath);
			} catch (UnsupportedEncodingException e) {
				logger.error(e.getMessage());
			}
		}

		if (file != null && !file.exists()) {
			throw new RuntimeException("Error: File " + file + " not found!");

		}
		return file.getPath();
	}
}