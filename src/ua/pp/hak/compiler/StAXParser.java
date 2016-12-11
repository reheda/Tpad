package ua.pp.hak.compiler;

import java.io.FileNotFoundException;
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

public class StAXParser {

	public static List<Attribute> parse() {
		boolean isValid = XSDValidator.validateXMLSchema("dbSchema.xsd", "db.xml");
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
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader("db.xml"));

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
                    if(endElement.getName().getLocalPart().equalsIgnoreCase("attribute")){
                       list.add(new Attribute(id, type, name));
                    }
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
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