package pcgui;



import javax.swing.AbstractCellEditor;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.TableCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JTable;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class ImportedEditor extends AbstractCellEditor
                         implements TableCellEditor,
			            ActionListener {


	private static final long serialVersionUID = 7306276250470495230L;
	JButton button;
    String file;

    protected static final String EDIT = "edit";

    public ImportedEditor() {
        //Set up the editor (from the table's point of view),
        //which is a button.
        //This button brings up the color chooser dialog,
        //which is the editor from the user's point of view.
        button = new JButton("Add External File");
        button.setVisible(true);
        button.setActionCommand(EDIT);
        button.addActionListener(this);
       
      
    }

    /**
     * Handles events from the editor button and from
     * the dialog's OK button.
     */
    public void actionPerformed(ActionEvent e) {
        if (EDIT.equals(e.getActionCommand())) {
            //The user has clicked the cell, so
            //bring up the dialog.
        	//TODO: load the last used directory
        	JFileChooser fileChooser = new JFileChooser();
        	File workingDirectory = new File(System.getProperty("user.dir"));
        	fileChooser.setCurrentDirectory(workingDirectory);
        	fileChooser.setFileFilter(new FileFilter() {
				
				@Override
				public String getDescription() {
					return ".dat/.xls/.xlsx/.csv";
							
				}
				
				@Override
				public boolean accept(File f) {
					// TODO Auto-generated method stub
					if(f.isDirectory()||f.getName().endsWith(".dat")
							||f.getName().endsWith(".csv")
							||f.getName().endsWith(".xls")
							||f.getName().endsWith(".xlsx")){
						return true;
					}
					return false;
				}
			});
        	int returnVal = fileChooser.showOpenDialog(null);

			if (returnVal == JFileChooser.APPROVE_OPTION) {
				 file = fileChooser.getSelectedFile().getName();
				 fireEditingStopped();

			} else {
				System.out.println("Open command cancelled by user.");
				fireEditingCanceled();
			}
            //Make the renderer reappear.
			
            

        } else { 
        	System.out.println("some other command");
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
        button.setText((String)value);
        return button;
    }
}

