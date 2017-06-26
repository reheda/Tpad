package ua.pp.hak.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

import ua.pp.hak.util.Attribute;
import ua.pp.hak.util.FileLocation;
import ua.pp.hak.util.XSDValidator;

public class DatabaseStAXParser {

	static final boolean USE_SCHEMA = true;
	final static Logger logger = LogManager.getLogger(DatabaseStAXParser.class);

	public static List<Attribute> parse() {
		List<Attribute> list = new ArrayList<>();
		try {
			String xmlLocation = FileLocation.getJarOrNotPath("/db.xml.exi");
			String schemaLocation = FileLocation.getJarOrNotPath("/dbSchema.xsd");
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
			boolean bDeactivated = false;
			boolean bGroupId = false;
			boolean bGroupName = false;
			boolean bLastUpdate = false;

			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader(xmlOut.getPath()));

			int id = 0;
			String type = null;
			String name = null;
			boolean isDeactivated = false;
			int groupId = 0;
			String groupName = null;
			String lastUpdate = null;

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
					} else if (qName.equalsIgnoreCase("deactivated")) {
						bDeactivated = true;
					} else if (qName.equalsIgnoreCase("group-id")) {
						bGroupId = true;
					} else if (qName.equalsIgnoreCase("group-name")) {
						bGroupName = true;
					} else if (qName.equalsIgnoreCase("last-update")) {
						bLastUpdate = true;
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
					if (bDeactivated) {
						isDeactivated = Boolean.parseBoolean(characters.getData());
						bDeactivated = false;
					}
					if (bGroupId) {
						groupId = Integer.valueOf(characters.getData());
						bGroupId = false;
					}
					if (bGroupName) {
						groupName = characters.getData().replace("///", "&");
						bGroupName = false;
					}
					if (bLastUpdate) {
						lastUpdate = characters.getData();
						bLastUpdate = false;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					EndElement endElement = event.asEndElement();
					if (endElement.getName().getLocalPart().equalsIgnoreCase("attribute")) {
						list.add(new Attribute(id, type, name, isDeactivated, groupId, groupName, lastUpdate));
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

	@Deprecated
	private static List<Attribute> parseWithoutExi() {
		String schemaLocation = FileLocation.getJarOrNotPath("/dbSchema.xsd");
		String xmlLocation = FileLocation.getJarOrNotPath("/db.xml");
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

}