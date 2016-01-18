package pcgui;



 
import javax.swing.AbstractCellEditor;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JTable;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
 
public class ButtonCellEditor extends AbstractCellEditor
                         implements TableCellEditor,
                        ActionListener {
	private static final long serialVersionUID = -4575845739010063350L;
	JButton button;
    JDialog dialog;
    JFileChooser fileChooser;
    File file;
    protected static final String IMPORT = "IMPORT";
 
    public ButtonCellEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton();
        button.setActionCommand(IMPORT);
        button.addActionListener(this);
        button.setBorderPainted(false);
 
        
    }
 
    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (IMPORT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
            
        	//Set up the dialog that the button brings up.
            fileChooser = new JFileChooser();
            fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					// TODO Auto-generated method stub
					return "Dat files";
				}
				
				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					return f.isDirectory()||f.getName().endsWith(".dat");
					
				}
			});
			int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				 file = fileChooser.getSelectedFile();
				
				// This is where a real application would open the file.

			} else {
				System.out.println("Import command cancelled by user.");
			}
         
 
            //Make the renderer reappear.
            fireEditingStopped();
 
        } 
    }
 
    //Implement the one CellEditor method that AbstractCellEditor doesn't.
    public Object getCellEditorValue() {
        return file;
    }
 
    //Implement the one method defined by TableCellEditor.
    public Component getTableCellEditorComponent(JTable table,
                                                 Object value,
                                                 boolean isSelected,
                                                 int row,
                                                 int column) {
        file = new File((String)value);
        return button;
    }
}