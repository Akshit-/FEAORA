package pcgui;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.ParameterModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.List;

/** 
 * TableRenderDemo is just like TableDemo, except that it
 * explicitly initializes column sizes and it uses a combo box
 * as an editor for the Sport column.
 */
public class TableUI extends JPanel {
    private boolean DEBUG = false;
    private ParameterModel model;
    JTable table;


    public TableUI(ParameterModel model) {
		// TODO Auto-generated constructor stub
    	super(new GridLayout(1,0));

        table = new JTable(model);
        table.setGridColor(Color.GRAY);
//        table.setFillsViewportHeight(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Set up column sizes.
        initColumnSizes(table);
//        Action delete = new AbstractAction()
//        {
//            public void actionPerformed(ActionEvent e)
//            {
//                JTable table = (JTable)e.getSource();
//                int modelRow = Integer.valueOf( e.getActionCommand() );
//            }
//        };
        //Fiddle with the param type column's cell editors/renderers.
        setUpParamTypeColumn(table, table.getColumnModel().getColumn(2));
       // ButtonColumn btnColumn = new ButtonColumn(table, 4)
        table.getColumnModel().getColumn(4).setCellEditor(new ImportedEditor());
        table.getColumnModel().getColumn(4).setCellRenderer(new ImportedRenderer());
        

        //Add the scroll pane to this panel.
        add(scrollPane);
	}

	/*
     * This method picks good column sizes.
     * If all column heads are wider than the column's cells'
     * contents, then you can just use column.sizeWidthToFit().
     */
    private void initColumnSizes(JTable table) {
        TableColumn column = null;
        Component comp = null;
        int headerWidth = 0;
        int cellWidth = 0;
        TableCellRenderer headerRenderer =
            table.getTableHeader().getDefaultRenderer();

        for (int i = 0; i < 8; i++) {
            column = table.getColumnModel().getColumn(i);

            comp = headerRenderer.getTableCellRendererComponent(
                                 null, column.getHeaderValue(),
                                 false, false, 0, 0);
            headerWidth = comp.getPreferredSize().width;


            cellWidth = comp.getPreferredSize().width;

            if (DEBUG) {
                System.out.println("Initializing width of column "
                                   + i + ". "
                                   + "headerWidth = " + headerWidth
                                   + "; cellWidth = " + cellWidth);
            }

            column.setPreferredWidth(Math.max(headerWidth, cellWidth));
        }
    }

    public void setUpParamTypeColumn(JTable table,
                                 TableColumn paramTypeColumn) {
        //Set up the editor for the sport cells.
        JComboBox<String> comboBox = new JComboBox<String>();
        comboBox.addItem("Default");
        comboBox.addItem("Manual");
        comboBox.addItem("External");
        comboBox.addItem("Randomized");
        comboBox.addItem("Step");
        paramTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
        comboBox.setRenderer(new ComboBoxRenderer());
        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer();
        renderer.setToolTipText("Click for viewing all options");
        paramTypeColumn.setCellRenderer(renderer);
    }
    
    class ComboBoxRenderer extends BasicComboBoxRenderer{

		/**
		 * 
		 */
		private static final long serialVersionUID = -7871388236564580887L;
		@Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value != null) {
                String item = (String) value;
                setText(item);
            }
            return this;
        }
    }

    
    public void setParameters(ParameterModel params){
    	model= params;
    	model.fireTableDataChanged();
    }

  
}
