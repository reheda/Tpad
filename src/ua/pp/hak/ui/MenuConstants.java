package ua.pp.hak.ui;

/**************************************/
// public
public interface MenuConstants {
	final String fileText = "File";
	final String editText = "Edit";
	final String formatText = "Format";
	final String viewText = "View";
	final String helpText = "Help";

	final String fileNew = "New";
	final String fileOpen = "Open...";
	final String fileSave = "Save";
	final String fileSaveAs = "Save As...";
	final String filePageSetup = "Page Setup...";
	final String filePrint = "Print";
	final String fileExit = "Exit";

	final String editUndo = "Undo";
	final String editRedo = "Redo";
	final String editCut = "Cut";
	final String editCopy = "Copy";
	final String editPaste = "Paste";
	final String editDelete = "Delete";
	final String editFind = "Find...";
	final String editFindNext = "Find Next";
	final String editReplace = "Replace...";
	final String editGoTo = "Go To...";
	final String editSelectAll = "Select All";
	final String editTimeDate = "Time/Date";

	final String formatWordWrap = "Word Wrap";
	final String formatFont = "Font...";
	final String formatForeground = "Set Text color...";
	final String formatBackground = "Set Pad color...";
	
	final String viewParserPanel = "Parser Panel";
	final String viewStatusBar = "Status Bar";
	final String viewZoomIn = "Zoom In";
	final String viewZoomOut = "Zoom Out";
	final String viewZoomDefault = "Zoom Default";

	final String helpKeyboardShortcuts = "Keyboard Shortcuts";
	final String helpHelpTopic = "Help Topic";
	final String helpAboutNotepad = "About Notepad";

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
			+ "v 1.0.0." + Notepad.build + " <hr>"
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
	final String shortcutsText = "<html><head><style>" + "span.keyboard {" + "    background: #3d3c40;"
			+ "    font-weight: 700;" + "    padding: 2px .35rem;" + "    font-size: .8rem;" + "    margin: 0 2px;"
			+ "    border-radius: .25rem;" + "    color: #ffffff;" + "    border-bottom: 2px solid #9e9ea6;"
			+ "    box-shadow: 0 1px 2px rgba(0,0,0,.5);" + "    text-shadow: none;" + "}" + "table {"
			+ "    border-collapse: collapse;" + "}" + "th, td {" + "    text-align: left;"
			+ "    border-bottom: 1px solid #dddddd;" + "}" + "td.r {" + "    text-align: left;"
			+ "    border-bottom: 1px solid #dddddd;" + "    border-right: 1px solid #E03134;" + "}"
			+ "tr:hover{background-color:#f5f5f5}" + "</style></head>"
			+ "<body style=\"font-family:Segoe UI; font-size:9px\">"
			+ "<div style=\"background-color: white; padding:5px;\">" + "<table width='682'>" + "<tbody>"
			+ "<tr style='background-color: #E03134; color: white; font-weight: bold;'>" + "<td width='203'>Action</td>"
			+ "<td width='131'>Shortcuts</td>" + "<td width='203'>Action</td>" + "<td width='131'>Shortcuts</td>"
			+ "</tr>" + "<tr>" + "<td>Copy Lines</td>" + "<td class='r'>Ctrl+Alt+Down</td>" + "<td>Next Word</td>"
			+ "<td>Ctrl+Right</td>" + "</tr>" + "<tr>" + "<td>Delete Line</td>" + "<td class='r'>Ctrl+D</td>"
			+ "<td>Previous Word</td>" + "<td>Ctrl+Left</td>" + "</tr>" + "<tr>" + "<td>Delete Next Word</td>"
			+ "<td class='r'>Ctrl+Delete</td>" + "<td>Reset Command</td>" + "<td>Ctrl+0</td>" + "</tr>" + "<tr>"
			+ "<td>Delete Previous Word</td>" + "<td class='r'>Ctrl+Backspace</td>" + "<td>Scroll Line Down</td>"
			+ "<td>Ctrl+Down</td>" + "</tr>" + "<tr>" + "<td>Delete to End of Line</td>"
			+ "<td class='r'>Ctrl+Shift+Delete</td>" + "<td>Scroll Line Up</td>" + "<td>Ctrl+Up</td>" + "</tr>" + "<tr>"
			+ "<td>Duplicate Lines</td>" + "<td class='r'>Ctrl+Alt+Up</td>" + "<td>Select Line End</td>"
			+ "<td>Shift+End</td>" + "</tr>" + "<tr>" + "<td>Expand</td>" + "<td class='r'>Ctrl+Numpad_Add</td>"
			+ "<td>Select Line Start</td>" + "<td>Shift+Home</td>" + "</tr>" + "<tr>" + "<td>Find Next</td>"
			+ "<td class='r'>Ctrl+K</td>" + "<td>Select Next Word</td>" + "<td>Ctrl+Shift+Right</td>" + "</tr>" + "<tr>"
			+ "<td>Find Previous</td>" + "<td class='r'>Ctrl+Shift+K</td>" + "<td>Select Previous Word</td>"
			+ "<td>Ctrl+Shift+Left</td>" + "</tr>" + "<tr>" + "<td>Go to Line</td>" + "<td class='r'>Ctrl+G</td>"
			+ "<td>Text End</td>" + "<td>Ctrl+End</td>" + "</tr>" + "<tr>" + "<td>Incremental Find Reverse</td>"
			+ "<td class='r'>Ctrl+Shift+J</td>" + "<td>Text Start</td>" + "<td>Ctrl+Home</td>" + "</tr>" + "<tr>"
			+ "<td>Insert Line Above Current Line</td>" + "<td class='r'>Ctrl+Shift+Enter</td>"
			+ "<td>To Lower Case</td>" + "<td>Ctrl+Shift+Y</td>" + "</tr>" + "<tr>"
			+ "<td>Insert Line Below Current Line</td>" + "<td class='r'>Shift+Enter</td>" + "<td>To Upper Case</td>"
			+ "<td>Ctrl+Shift+X</td>" + "</tr>" + "<tr>" + "<td>Join Lines</td>" + "<td class='r'>Ctrl+J</td>"
			+ "<td>Toggle Overwrite</td>" + "<td>Insert</td>" + "</tr>" + "<tr>" + "<td>Line End</td>"
			+ "<td class='r'>End</td>" + "<td>Word Completion</td>" + "<td>Alt+/</td>" + "</tr>" + "<tr>"
			+ "<td>Line Start</td>" + "<td class='r'>Home</td>" + "<td>zoomIn</td>" + "<td>Ctrl+=</td>" + "</tr>"
			+ "<tr>" + "<td>Move Lines Down</td>" + "<td class='r'>Alt+Down</td>" + "<td>zoomOut</td>"
			+ "<td>Ctrl+-</td>" + "</tr>" + "<tr>" + "<td>Move Lines Up</td>" + "<td class='r'>Alt+Up</td>"
			+ "<td>&nbsp;</td>" + "<td>&nbsp;</td>" + "</tr>" + "</tbody>" + "</table>" + "</div>" + "</body></html>";

}