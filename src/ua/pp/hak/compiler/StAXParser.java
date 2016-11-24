package ua.pp.hak.compiler;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.HashMap;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

public class StAXParser {

	public static HashMap<Integer, String> parse() {
		boolean isValid = XSDValidator.validateXMLSchema("dbSchema.xsd", "db.xml");
		if (!isValid) {
			System.err.println("XML file is not valid against XSD Schema");
			return null;
		}

		HashMap<Integer, String> hm = new HashMap<>();

		boolean bId = false;
		boolean bType = false;
		try {
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(new FileReader("db.xml"));

			Integer id = null;
			String type = null;
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
					break;
				case XMLStreamConstants.END_ELEMENT:
					hm.put(id, type);
					break;
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		}

		return hm;
	}

	public static void main(String[] args) {
		HashMap<Integer, String> hm = parse();
		for (Integer key : hm.keySet()) {
			System.out.printf("id: %d,\t\t type: %s%n", key, hm.get(key));
		}
	}
}