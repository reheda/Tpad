package ua.pp.hak.ui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.Serializable;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 * Font Settings dialog box
 * 
 * @version 1.0
 */
@SuppressWarnings("serial")
public class FontChooser extends JDialog
    implements ActionListener, ListSelectionListener, Serializable {
    private JPanel pCenter = new JPanel();
    private JTextField txtName = new JTextField();
    private JTextField txtStyle = new JTextField();
    private JTextField txtSize = new JTextField();
    private JScrollPane spName = new JScrollPane();
    private JScrollPane spStyle = new JScrollPane();
    private JScrollPane spSize = new JScrollPane();
    private JPanel pPreview = new JPanel();
    private TitledBorder titledBorder1;
    private JLabel lbName = new JLabel();
    private JLabel lbStyle = new JLabel();
    private JLabel lbSize = new JLabel();

    /**
     * Get all the font names
     */
    String fontNames[] = GraphicsEnvironment.getLocalGraphicsEnvironment().
        getAvailableFontFamilyNames();
    private JList listName = new JList(fontNames);
    String fontStyles[] = { "Regular","Bold","Italic","Bold Italic" };
    private JList listStyle = new JList(fontStyles);
    String fontSizes[] = {
        "8", "9", "10", "11", "12", "14", "16", "18", "20",
        "22", "24", "26", "28", "36", "48", "72"};
    private JList listSize = new JList(fontSizes);
    private JLabel lbPreview = new JLabel();
    private JButton bttOK = new JButton();
    private JButton bttCancel = new JButton() {
        public Insets getInsets() {
            return new Insets(0, 0, 0, 0);
        }
    };
    boolean hasCancel = true;
    public static Font showDialog(Component c, String title, boolean modal,
                                  Font initFont) {
        FontChooser dialog;
        Window owner = getRootWindow(c);
        new InitFont(initFont); //remember init font
        if (owner instanceof Dialog) {
            dialog = new FontChooser ( (Dialog) owner, title, modal);
        }
        else if (owner instanceof Frame) {
            dialog = new FontChooser ( (Frame) owner, title, modal);
        }
        else {
            dialog = new FontChooser ();
            dialog.setTitle(title);
        }
        if (initFont != null) {
            dialog.setFont(initFont);
        }
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        dialog.setLocation( (d.width - dialog.getSize().width) / 2,
                           (d.height - dialog.getSize().height) / 2);
        dialog.setVisible(true);
        return dialog.getFont();
    }

    /**
     * get root window
     * @param c Component
     * @return Window
     */
    static Window getRootWindow(Component c) {
        if (c == null){
            return null;
        }
        Container parent = c.getParent();
        if (c instanceof Window){
            return(Window)c;
        }
        while (! (parent instanceof Window)){
            parent = parent.getParent();
        }
        return (Window) parent;
    }

    public FontChooser(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public FontChooser(Dialog frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public FontChooser() {
        this( (Frame)null, "", false);
        try {
            jbInit();
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void jbInit() throws Exception {
        this.setSize(480, 310);
        titledBorder1 = new TitledBorder("Preview");
        pCenter.setLayout(null);
        txtName.setBackground(Color.white);
        txtName.setEditable(false);
        txtName.setBounds(new Rectangle(15, 22, 152, 21));
        txtStyle.setBackground(Color.white);
        txtStyle.setEditable(false);
        txtStyle.setBounds(new Rectangle(174, 22, 123, 21));
        txtSize.setBackground(Color.white);
        txtSize.setEditable(false);
        txtSize.setBounds(new Rectangle(304, 22, 63, 21));
        spName.setBounds(new Rectangle(15, 47, 152, 220));
        spStyle.setBounds(new Rectangle(174, 47, 123, 113));
        spSize.setBounds(new Rectangle(304, 47, 63, 113));
        pPreview.setBorder(titledBorder1);
        pPreview.setBounds(new Rectangle(174, 170, 193, 98));
        pPreview.setLayout(null);
        lbName.setText("Font");
        lbName.setBounds(new Rectangle(15, 4, 151, 17));
        lbStyle.setText("Style");
        lbStyle.setBounds(new Rectangle(174, 4, 123, 17));
        lbSize.setText("Size");
        lbSize.setBounds(new Rectangle(304, 4, 62, 17));
        lbPreview.setBorder(BorderFactory.createLoweredBevelBorder());
        lbPreview.setHorizontalAlignment(SwingConstants.CENTER);
        lbPreview.setText("AaBbCcXxYyZz");
        lbPreview.setBounds(new Rectangle(10, 23, 171, 61));
        bttOK.setBounds(new Rectangle(379, 21, 79, 22));
        bttOK.setMnemonic('O');
        bttOK.setText("Ok");
        bttCancel.setBounds(new Rectangle(379, 48, 79, 22));
        bttCancel.setMnemonic('C');
        bttCancel.setText("Cancel");
        listName.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listStyle.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSize.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        getContentPane().add(pCenter);
        pCenter.add(spName, null);
        pCenter.add(lbName, null);
        pCenter.add(spStyle, null);
        pCenter.add(txtName, null);
        pCenter.add(txtStyle, null);
        pCenter.add(lbStyle, null);
        pCenter.add(txtSize, null);
        pCenter.add(lbSize, null);
        pCenter.add(spSize, null);
        pCenter.add(pPreview, null);
        pPreview.add(lbPreview, null);
        pCenter.add(bttOK, null);
        pCenter.add(bttCancel, null);
        spSize.getViewport().add(listSize, null);
        spStyle.getViewport().add(listStyle, null);
        spName.getViewport().add(listName, null);
        bttOK.addActionListener(this);
        bttCancel.addActionListener(this);
        listName.addListSelectionListener(this);
        listSize.addListSelectionListener(this);
        listStyle.addListSelectionListener(this);
        setFont(lbPreview.getFont());

        getRootPane().registerKeyboardAction(this,KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                                             JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void setFont(Font f) {
        if (f == null)
            throw new NullPointerException("Error: fonts can not be empty!");
        listName.setSelectedValue(f.getFamily(), true);
        listStyle.setSelectedIndex(f.getStyle());
        listSize.setSelectedValue("" + f.getSize(), true);
        super.setFont(f);
    }

    public Font getFont() {
        if (hasCancel){
        	return InitFont.getInitFont();
//            return new Font("Courier New", Font.PLAIN, 15);
        }
        return lbPreview.getFont();
    }

    public void valueChanged(ListSelectionEvent e) {
        JList obj = (JList)e.getSource();
        if (obj == listName) {
            txtName.setText(listName.getSelectedValue().toString());
        }
        else if (obj == listStyle) {
            txtStyle.setText(listStyle.getSelectedValue().toString());
        }
        else if (obj == listSize) {
            txtSize.setText(listSize.getSelectedValue().toString());
        }
        obj.scrollRectToVisible(obj.getCellBounds(
            obj.getSelectedIndex(), obj.getSelectedIndex()));
        try {
            Font f = new Font(txtName.getText(), listStyle.getSelectedIndex(),
                              Integer.parseInt(txtSize.getText()));
            lbPreview.setFont(f);
        }
        catch (NumberFormatException ex) {
        }
    }

    public void actionPerformed(ActionEvent e) {
        Object obj = e.getSource();
        if (obj == bttOK) {
            hasCancel = false;
        }
        else if (obj == bttCancel || obj == getRootPane()) {
            hasCancel = true;
        }
        setVisible(false);
    }

}

class InitFont{
	private static Font initFont;
	public InitFont(Font initFont) {
		this.initFont = initFont;
	}
	static Font getInitFont(){
		return initFont;
	}
}
