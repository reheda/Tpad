package ua.pp.hak.setting;

import java.awt.Color;
import java.awt.Font;

public class Settings {
	private Font font;
	private Color backgroundColor;
	private Color foregroundColor;
	private Color keywordColor;
	private Color commentColor;
	private Color stringColor;
	private boolean isWordWrapEnabled;
	private boolean isStatusBarEnabled;
	private boolean isParserPanelEnabled;

	public Settings(Font font, Color backgroundColor, Color foregroundColor, Color keywordColor, Color commentColor, Color stringColor, boolean isWordWrapEnabled,
			boolean isStatusBarEnabled, boolean isParserPanelEnabled) {
		this.font = font;
		this.backgroundColor = backgroundColor;
		this.foregroundColor = foregroundColor;
		this.keywordColor = keywordColor;
		this.commentColor = commentColor;
		this.stringColor = stringColor;
		this.isWordWrapEnabled = isWordWrapEnabled;
		this.isStatusBarEnabled = isStatusBarEnabled;
		this.isParserPanelEnabled = isParserPanelEnabled;
	}

	public Color getKeywordColor() {
		return keywordColor;
	}

	public Color getCommentColor() {
		return commentColor;
	}

	public Color getStringColor() {
		return stringColor;
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
		sb.append("Keyword: (");
		sb.append("rgb=");
		sb.append(keywordColor.getRed());
		sb.append(",");
		sb.append(keywordColor.getGreen());
		sb.append(",");
		sb.append(keywordColor.getBlue());
		sb.append("); ");
		sb.append("Comment: (");
		sb.append("rgb=");
		sb.append(commentColor.getRed());
		sb.append(",");
		sb.append(commentColor.getGreen());
		sb.append(",");
		sb.append(commentColor.getBlue());
		sb.append("); ");
		sb.append("String: (");
		sb.append("rgb=");
		sb.append(stringColor.getRed());
		sb.append(",");
		sb.append(stringColor.getGreen());
		sb.append(",");
		sb.append(stringColor.getBlue());
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
