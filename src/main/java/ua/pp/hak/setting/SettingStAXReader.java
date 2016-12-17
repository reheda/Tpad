package ua.pp.hak.setting;

import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ua.pp.hak.util.FileLocation;
import ua.pp.hak.util.XSDValidator;

public class SettingStAXReader {
	final static Logger logger = LogManager.getLogger(SettingStAXReader.class);
	public static Settings parseSettings() {
		final String settingsLocation = "settings.xml";

		// located in jar
		final String settingsSchemaLocation = FileLocation.getJarOrNotPath("/settingsSchema.xsd");

		File settingsFile = new File(settingsLocation);
		File settingsSchemaFile = new File(settingsSchemaLocation);

		if (settingsFile == null || settingsSchemaFile == null || !settingsFile.exists()
				|| !settingsSchemaFile.exists()) {
			return null;
		}
		boolean isValid = XSDValidator.validateXMLSchema(settingsSchemaFile, settingsFile);
		if (!isValid) {
			logger.info("XML file is not valid against XSD Schema");
			return null;
		}

		boolean bName = false;
		boolean bStyle = false;
		boolean bSize = false;
		boolean bColor = false;
		boolean bEnable = false;
		final String FONT_TEXT = "font";
		final String FOREGROUND_TEXT = "foreground";
		final String BACKGROUND_TEXT = "background";
		final String WORDWRAP_TEXT = "wordwrap";
		final String STATUSBAR_TEXT = "statusbar";
		final String PARSERPANEL_TEXT = "parserpanel";
		final String NAME_TEXT = "name";
		final String STYLE_TEXT = "style";
		final String SIZE_TEXT = "size";
		final String COLOR_TEXT = "color";
		final String ENABLE_TEXT = "enable";

		String fontName = "Consolas";
		int fontSize = 14; // px
		int fontStyle = 0; // plain
		Font font = new Font(fontName, fontStyle, fontSize);

		Color foregroundColor = new Color(-16777216); // black
		Color backgroundColor = new Color(-1); // white

		boolean isWordWrapEnabled = false;
		boolean isStatusBarEnabled = false;
		boolean isParserPanelEnabled = false;

		try {

			FileReader f = new FileReader(settingsLocation);
			XMLInputFactory factory = XMLInputFactory.newInstance();
			XMLEventReader eventReader = factory.createXMLEventReader(f);

			String startElementName = "";
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				switch (event.getEventType()) {
				case XMLStreamConstants.START_ELEMENT:
					StartElement startElement = event.asStartElement();
					String qName = startElement.getName().getLocalPart();
					if (qName.equalsIgnoreCase(FONT_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(FOREGROUND_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(BACKGROUND_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(WORDWRAP_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(STATUSBAR_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(PARSERPANEL_TEXT)) {
						startElementName = qName;
					} else if (qName.equalsIgnoreCase(NAME_TEXT)) {
						bName = true;
					} else if (qName.equalsIgnoreCase(STYLE_TEXT)) {
						bStyle = true;
					} else if (qName.equalsIgnoreCase(SIZE_TEXT)) {
						bSize = true;
					} else if (qName.equalsIgnoreCase(COLOR_TEXT)) {
						bColor = true;
					} else if (qName.equalsIgnoreCase(ENABLE_TEXT)) {
						bEnable = true;
					}
					break;
				case XMLStreamConstants.CHARACTERS:
					Characters characters = event.asCharacters();
					if (bName) {
						String temp = characters.getData();
						fontName = temp;
						bName = false;
					}
					if (bStyle) {
						String temp = characters.getData();
						fontStyle = Integer.parseInt(temp);
						bStyle = false;
					}
					if (bSize) {
						String temp = characters.getData();
						fontSize = Integer.parseInt(temp);
						bSize = false;
					}
					if (bColor) {
						String temp = characters.getData();
						if (startElementName.equals(FOREGROUND_TEXT)) {
							foregroundColor = new Color(Integer.parseInt(temp));
						} else if (startElementName.equals(BACKGROUND_TEXT)) {
							backgroundColor = new Color(Integer.parseInt(temp));
						}
						bColor = false;
					}
					if (bEnable) {
						String temp = characters.getData();
						if (startElementName.equals(WORDWRAP_TEXT)) {
							isWordWrapEnabled = Boolean.parseBoolean(temp);
						} else if (startElementName.equals(STATUSBAR_TEXT)) {
							isStatusBarEnabled = Boolean.parseBoolean(temp);
						} else if (startElementName.equals(PARSERPANEL_TEXT)) {
							isParserPanelEnabled = Boolean.parseBoolean(temp);
						}
						bEnable = false;
					}
					break;
				case XMLStreamConstants.END_ELEMENT:
					break;
				}
			}

			eventReader.close();
			f.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		font = new Font(fontName, fontStyle, fontSize);

		return new Settings(font, backgroundColor, foregroundColor, isWordWrapEnabled, isStatusBarEnabled,
				isParserPanelEnabled);
	}
	public static void main(String[] args) {
		Settings set = parseSettings();
		System.out.println(set);
	}
}