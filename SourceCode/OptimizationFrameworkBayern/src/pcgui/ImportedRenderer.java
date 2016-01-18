package pcgui;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

import java.awt.Component;

public class ImportedRenderer extends JLabel
                           implements TableCellRenderer {
	
	private static final long serialVersionUID = 8673866724829294375L;

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value,
			boolean isSelected, boolean hasFocus, int row, int column) {
		String file = (String) value;
		if(file!=null && !file.isEmpty()){
			JButton label = new JButton(file);      
	        label.setToolTipText("This value is imported from file : "+file);
	        return label;
		} else {
			JButton label = new JButton("No file selected");      
	        label.setToolTipText("Click this button to import the variable from external file.");
	        return label;
		}
		
       
	}
}

