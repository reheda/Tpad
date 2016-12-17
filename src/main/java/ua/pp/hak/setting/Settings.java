package ua.pp.hak.setting;

import java.awt.Color;
import java.awt.Font;

public class Settings {
	private Font font;
	private Color backgroundColor;
	private Color foregroundColor;
	private boolean isWordWrapEnabled;
	private boolean isStatusBarEnabled;
	private boolean isParserPanelEnabled;

	public Settings(Font font, Color backgroundColor, Color foregroundColor, boolean isWordWrapEnabled,
			boolean isStatusBarEnabled, boolean isParserPanelEnabled) {
		this.font = font;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.isWordWrapEnabled = isWordWrapEnabled;
		this.isStatusBarEnabled = isStatusBarEnabled;
		this.isParserPanelEnabled = isParserPanelEnabled;
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public Color getForegroundColor() {
		return foregroundColor;
	}

	public Font getFont() {
		return font;
	}

	public boolean isWordWrapEnabled() {
		return isWordWrapEnabled;
	}

	public boolean isStatusBarEnabled() {
		return isStatusBarEnabled;
	}

	public boolean isParserPanelEnabled() {
		return isParserPanelEnabled;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Font: (");
		sb.append("name='");
		sb.append(font.getFontName());
		sb.append("',");
		sb.append("style=");
		sb.append(font.getStyle());
		sb.append(",");
		sb.append("size=");
		sb.append(font.getSize());
		sb.append("); ");
		sb.append("Background: (");
		sb.append("rgb=");
		sb.append(backgroundColor.getRed());
		sb.append(",");
		sb.append(backgroundColor.getGreen());
		sb.append(",");
		sb.append(backgroundColor.getBlue());
		sb.append("); ");
		sb.append("Foreground: (");
		sb.append("rgb=");
		sb.append(foregroundColor.getRed());
		sb.append(",");
		sb.append(foregroundColor.getGreen());
		sb.append(",");
		sb.append(foregroundColor.getBlue());
		sb.append("); ");
		sb.append("isWordWrapEnabled=");
		sb.append(isWordWrapEnabled);
		sb.append("; ");
		sb.append("isStatusBarEnabled=");
		sb.append(isStatusBarEnabled);
		sb.append("; ");
		sb.append("isParserPanelEnabled=");
		sb.append(isParserPanelEnabled);
		
		return sb.toString();
	}

}
