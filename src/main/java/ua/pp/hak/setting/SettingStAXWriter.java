package ua.pp.hak.setting;

import java.awt.Font;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class SettingStAXWriter {
	public static void saveSettings(Settings settings) {

		Font font = settings.getFont();
		String fontName = font.getFontName();
		String fontSize = String.valueOf(font.getSize());
		String fontStyle = String.valueOf(font.getStyle());

		String foregroundColor = String.valueOf(settings.getForegroundColor().getRGB());
		String backgroundColor = String.valueOf(settings.getBackgroundColor().getRGB());
		String keywordColor = String.valueOf(settings.getKeywordColor().getRGB());
		String commentColor = String.valueOf(settings.getCommentColor().getRGB());
		String stringColor = String.valueOf(settings.getStringColor().getRGB());

		String isWordWrapEnabled = String.valueOf(settings.isWordWrapEnabled());
		String isStatusBarEnabled = String.valueOf(settings.isStatusBarEnabled());
		String isParserPanelEnabled = String.valueOf(settings.isParserPanelEnabled());

		try {

			String fileName = "settings.xml";
			String encoding = "UTF-8";
			String version = "1.0";
			XMLOutputFactory xMLOutputFactory = XMLOutputFactory.newInstance();
			FileOutputStream f = new FileOutputStream(fileName);
			XMLStreamWriter xMLStreamWriter = xMLOutputFactory.createXMLStreamWriter(f, encoding);

			xMLStreamWriter.writeStartDocument(encoding, version);

			xMLStreamWriter.writeStartElement("settings");
			xMLStreamWriter.writeAttribute("xmlns", "http://hak.pp.ua/settingsSchema");
			xMLStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			xMLStreamWriter.writeAttribute("xsi:schemaLocation", "http://hak.pp.ua/settingsSchema settingsSchema.xsd");

			xMLStreamWriter.writeStartElement("font");
			xMLStreamWriter.writeStartElement("name");
			xMLStreamWriter.writeCharacters(fontName);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeStartElement("style");
			xMLStreamWriter.writeCharacters(fontStyle);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeStartElement("size");
			xMLStreamWriter.writeCharacters(fontSize);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("foreground");
			xMLStreamWriter.writeStartElement("color");
			xMLStreamWriter.writeCharacters(foregroundColor);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("background");
			xMLStreamWriter.writeStartElement("color");
			xMLStreamWriter.writeCharacters(backgroundColor);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();
			
			xMLStreamWriter.writeStartElement("keyword");
			xMLStreamWriter.writeStartElement("color");
			xMLStreamWriter.writeCharacters(keywordColor);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();
			
			xMLStreamWriter.writeStartElement("comment");
			xMLStreamWriter.writeStartElement("color");
			xMLStreamWriter.writeCharacters(commentColor);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();
			
			xMLStreamWriter.writeStartElement("string");
			xMLStreamWriter.writeStartElement("color");
			xMLStreamWriter.writeCharacters(stringColor);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("wordwrap");
			xMLStreamWriter.writeStartElement("enable");
			xMLStreamWriter.writeCharacters(isWordWrapEnabled);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("statusbar");
			xMLStreamWriter.writeStartElement("enable");
			xMLStreamWriter.writeCharacters(isStatusBarEnabled);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeStartElement("parserpanel");
			xMLStreamWriter.writeStartElement("enable");
			xMLStreamWriter.writeCharacters(isParserPanelEnabled);
			xMLStreamWriter.writeEndElement();
			xMLStreamWriter.writeEndElement();

			xMLStreamWriter.writeEndDocument();

			xMLStreamWriter.flush();
			xMLStreamWriter.close();

			f.close();

		} catch (XMLStreamException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
