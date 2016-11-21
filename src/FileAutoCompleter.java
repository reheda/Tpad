import javax.swing.event.*;
import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com 
public class FileAutoCompleter extends AutoCompleter{ 
    public FileAutoCompleter(JTextComponent comp){ 
        super(comp); 
    } 
 
    protected boolean updateListData(){ 
        String value = textComp.getText(); 
        int index1 = value.lastIndexOf('\\'); 
        int index2 = value.lastIndexOf('/'); 
        int index = Math.max(index1, index2); 
        if(index==-1) 
            return false; 
        String dir = value.substring(0, index+1); 
        final String prefix = index==value.length()-1 ? null : value.substring(index + 1).toLowerCase(); 
        String[] files = new File(dir).list(new FilenameFilter(){ 
            public boolean accept(File dir, String name){ 
                return prefix!=null ? name.toLowerCase().startsWith(prefix) : true; 
            } 
        }); 
        if(files == null){ 
            list.setListData(new String[0]); 
            return true; 
        } else{ 
            if(files.length==1 && files[0].equalsIgnoreCase(prefix)) 
                list.setListData(new String[0]); 
            else 
                list.setListData(files); 
            return true; 
        } 
    } 
 
    protected void acceptedListItem(String selected){ 
        if(selected==null) 
            return; 
 
        String value = textComp.getText(); 
        int index1 = value.lastIndexOf('\\'); 
        int index2 = value.lastIndexOf('/'); 
        int index = Math.max(index1, index2); 
        if(index==-1) 
            return; 
        int prefixlen = textComp.getDocument().getLength()-index-1; 
        try{ 
            textComp.getDocument().insertString(textComp.getCaretPosition(), selected.substring(prefixlen), null); 
        } catch(BadLocationException e){ 
            e.printStackTrace(); 
        } 
    } 
}