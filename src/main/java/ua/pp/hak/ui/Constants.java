package ua.pp.hak.ui;

import ua.pp.hak.Runner;

public interface Constants {
	final String applicationName = "Tpad";
	final String defaultFileName = "Untitled";
	final String encoding = "UTF-8";

	final String txtExprRes = "Expression result: ";
	final String txtExpr = "Expression: ";
	final String txtParameters = "Parameters: ";
	final String txtSKU = "SKU ID: ";
	final String defaultSKU = "12345679";
	final String defaultParameters = "AccTree=-1, Evaluate=false, Locale=en-US, ResultSeparator=<>, Verbatim=false, LegacyValues=false";
	final String defaultExpressionResult = "Microsoft Bluetooth Mobile Mouse 3600 - Mouse - Bluetooth 4.0 - Dark red\nOutputProcessors=RemarkProcessor+LegacyProductsToTableProcessor+L";

	final String imgTemplexBigLocation = "/images/templex-big.png";
	final String imgNewLocation = "/images/new.png";
	final String imgOpenLocation = "/images/open.png";
	final String imgSaveLocation = "/images/save.png";
	final String imgCheckLocation = "/images/check.png";
	final String imgParseLocation = "/images/parse.png";
	final String imgUndoLocation = "/images/undo.png";
	final String imgRedoLocation = "/images/redo.png";
	final String imgCopyLocation = "/images/copy.png";
	final String imgCutLocation = "/images/cut.png";
	final String imgPasteLocation = "/images/paste.png";
	final String imgFindLocation = "/images/find.png";
	final String imgReplaceLocation = "/images/replace.png";
	final String imgZoomInLocation = "/images/zoom-in.png";
	final String imgZoomOutLocation = "/images/zoom-out.png";
	final String imgZoomDefaultLocation = "/images/zoom-default.png";
	final String imgFontLocation = "/images/font.png";
	final String imgWrapLocation = "/images/wrap.png";
	final String imgHelpLocation = "/images/help.png";
	final String imgInfoLocation = "/images/info.png";
	final String imgKeyboardLocation = "/images/keyboard.png";

	final String quickReferenceText = "<html><body style=\"font-family:Segoe UI; font-size:9px\">" + "<div>"
			+ "<big><b>Quick reference</b></big> [<a href=\"http://templex.cnetcontent.com/Reference\">click to see the full reference</a>]"
			+ "<hr />" + "<div style=\"zoom: 83%;border-left: 6px solid red;background-color: white;\">"
			+ "<pre style=\"margin: 5; line-height: 125%\">SKU.ProductType_<span style=\"color: #aa5500\">&quot; is &quot;</span>_SKU.Colors.FlattenWithAnd()<br>"
			+ "                                 .ToLower();<br>" + "                                 <br>"
			+ "IF A[<span style=\"color: #009999\">374</span>].Values.Where(<span style=\"color: #aa5500\">&quot;%sec%lock%&quot;</span>) IS NOT NULL<br>"
			+ "THEN A[<span style=\"color: #009999\">374</span>].Values.Where(<span style=\"color: #aa5500\">&quot;%sec%lock%&quot;</span>).First().Prefix(<span style=\"color: #aa5500\">&quot;Yes - &quot;</span>)<br>"
			+ "ELSE <span style=\"color: #aa5500\">&quot;No security lock slot&quot;</span>;<br>" + "<br>"
			+ "A[<span style=\"color: #009999\">5791</span>].Where(<span style=\"color: #aa5500\">&quot;%USB%&quot;</span>).Match(<span style=\"color: #009999\">5792</span>, <span style=\"color: #009999\">5791</span>).Values<br>"
			+ "                                        .UseSeparators(<span style=\"color: #aa5500\">&quot; x &quot;</span>)<br>"
			+ "                                        .Flatten(<span style=\"color: #aa5500\">&quot; | &quot;</span>);<br>"
			+ "                                               <br>"
			+ "A[<span style=\"color: #009999\">5791</span>].Where(<span style=\"color: #aa5500\">&quot;%USB%&quot;</span>).Match(<span style=\"color: #009999\">5792</span>).Total;<br>"
			+ "Coalesce(A[<span style=\"color: #009999\">5796</span>].Value, A[<span style=\"color: #009999\">109</span>].Value, <span style=\"color: #aa5500\">&quot;No&quot;</span>);<br>"
			+ "<br>" + "CASE A[<span style=\"color: #009999\">5784</span>].Value<br>"
			+ "   WHEN <span style=\"color: #aa5500\">&quot;LED&quot;</span> THEN <span style=\"color: #aa5500\">&quot;LED display&quot;</span><br>"
			+ "   WHEN <span style=\"color: #aa5500\">&quot;LCD&quot;</span> THEN <span style=\"color: #aa5500\">&quot;LCD display&quot;</span><br>"
			+ "   ELSE <span style=\"color: #aa5500\">&quot;Other&quot;</span><br>" + "END;</pre>" + "</div>"
			+ "<footer>Quick reference provided by&nbsp;<cite title=\"Asya Kasidova\">Asya</cite></footer><br>"
			+ "</div>" + "</body></html>";

	final String aboutText = "<html><body style=\"font-family:Segoe UI; font-size:9px\"><big><b>Tpad</b><i> beta</i></big><br>"
			+ "v " + Notepad.class.getPackage().getImplementationVersion() + " <hr>"
			+ "THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS<br>"
			+ "\"AS IS\" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT<br>"
			+ "LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR<br>"
			+ "A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT<br>"
			+ "OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,<br>"
			+ "SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT<br>"
			+ "LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,<br>"
			+ "DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY<br>"
			+ "THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT<br>"
			+ "(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE<br>"
			+ "OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.<br><br>"
			+ "Your comments as well as bug reports are very welcome at:<br><span style=\"background-color:#ffffcc;font-size:11px;\"><b><a href='mailto:valerii.reheda@gmail.com' style='color: black; text-decoration: none;'>valerii.reheda@gmail.com</a></b></span>"
			+ "</body></html>";

	final String shortcutsText = "<html> <head> <style> table { border-collapse: collapse; } th, td { text-align: left; border-bottom: 1px solid #dddddd; } td.right { border-right: 1px solid #E03134; } tr:hover{background-color:#f5f5f5 } tr.header{background-color: #E03134; color: white; font-weight: bold; } body {font-family:Segoe UI; font-size:9px; } div {background-color: white; padding:5px; } </style> </head> <body> <div> <table width='682'> <tbody><tr class='header'><td width='203'>Action</td><td width='131'>Shortcuts</td><td width='203'>Action</td><td width='131'>Shortcuts</td></tr><tr><td>Clipboard History</td><td class='right'>Ctrl+Shift+V</td><td>Select Backward</td><td>Shift+LEFT</td></tr><tr><td>Copy</td><td class='right'>Ctrl+C or Ctrl+INSERT</td><td>Select Down</td><td>Shift+DOWN</td></tr><tr><td>Copy Line Down</td><td class='right'>Ctrl+Alt+DOWN</td><td>Select Forward</td><td>Shift+RIGHT</td></tr><tr><td>Copy Line Up</td><td class='right'>Ctrl+Alt+UP</td><td>Select Line End</td><td>Shift+END</td></tr><tr><td>Cut</td><td class='right'>Ctrl+X</td><td>Select Line Start</td><td>Shift+HOME</td></tr><tr><td>Delete Line</td><td class='right'>Ctrl+D</td><td>Select Next Word</td><td>Ctrl+Shift+RIGHT</td></tr><tr><td>Delete Previous Char</td><td class='right'>Shift+BACK_SPACE</td><td>Select Page Down</td><td>Shift+PAGE_DOWN</td></tr><tr><td>Delete Previous Word</td><td class='right'>Ctrl+BACK_SPACE</td><td>Select Page Left</td><td>Ctrl+Shift+PAGE_UP</td></tr><tr><td>Delete to End of Line</td><td class='right'>Ctrl+DELETE</td><td>Select Page Right</td><td>Ctrl+Shift+PAGE_DOWN</td></tr><tr><td>Find Next</td><td class='right'>Ctrl+K</td><td>Select Page Up</td><td>Shift+PAGE_UP</td></tr><tr><td>Find Previous</td><td class='right'>Ctrl+Shift+K</td><td>Select Previous Word</td><td>Ctrl+Shift+LEFT</td></tr><tr><td>Insert Break</td><td class='right'>ENTER or Shift+ENTER</td><td>Select Text End</td><td>Ctrl+Shift+END</td></tr><tr><td>Join Lines</td><td class='right'>Ctrl+J</td><td>Select Text Start</td><td>Ctrl+Shift+HOME</td></tr><tr><td>Line End</td><td class='right'>END</td><td>Select Up</td><td>Shift+UP</td></tr><tr><td>Line Start</td><td class='right'>HOME</td><td>Text End</td><td>Ctrl+END</td></tr><tr><td>Move Line Down</td><td class='right'>Alt+DOWN</td><td>Text Start</td><td>Ctrl+HOME</td></tr><tr><td>Move Line Up</td><td class='right'>Alt+UP</td><td>To Lower Case</td><td>Ctrl+Shift+Y</td></tr><tr><td>Next Word</td><td class='right'>Ctrl+RIGHT</td><td>To Upper Case</td><td>Ctrl+Shift+X</td></tr><tr><td>Paste</td><td class='right'>Ctrl+V or Shift+INSERT</td><td>Toggle Overwrite</td><td>INSERT</td></tr><tr><td>Previous Word</td><td class='right'>Ctrl+LEFT</td><td>Undo</td><td>Ctrl+Z</td></tr><tr><td>Redo</td><td class='right'>Ctrl+Y</td><td>Word Completion</td><td>Ctrl+Space</td></tr><tr><td>Scroll Down</td><td class='right'>Ctrl+DOWN</td><td>Zoom Default</td><td>Ctrl+0</td></tr><tr><td>Scroll Up</td><td class='right'>Ctrl+UP</td><td>Zoom In</td><td>Ctrl+=</td></tr><tr><td>Select All</td><td class='right'>Ctrl+A</td><td>Zoom Out</td><td>Ctrl+-</td></tr> </tbody> </table> </div> </body></html>";

}
