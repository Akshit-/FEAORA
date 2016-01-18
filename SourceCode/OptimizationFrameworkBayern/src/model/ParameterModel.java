package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import pcgui.Constants;

public class ParameterModel extends AbstractTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1405438536390252264L;

	private String[] columnNames = {"Param Name",
                                    "Param Data Type",
                                    "Param Type",
                                    "Param Value",
                                    "Import From File",
                                    "Import From Range",
                                    "Distribution Function",
                                    "Track Output"};
    
    private ArrayList<ArrayList<Object>> data = new ArrayList<ArrayList<Object>>();
    
    public void addRow(ArrayList<Object> rowData){
    	data.add(rowData);
    	fireTableRowsInserted(0, data.size());
    }
    
    public void deleteRow(int row){
    	data.remove(row);
    	fireTableRowsInserted(0, data.size());
    }
    
    public ArrayList<ArrayList<Object>> getData(){
    	return data;
    }
    
    public void setData(ArrayList<ArrayList<Object>> data) {
    	
    	this.data =data;
    	fireTableDataChanged();
    }
    
    public void clearData(){
    	data.clear();
    	fireTableDataChanged();
    }
 

  
   
    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return data.size();
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {
        return data.get(row).get(col);
    }

    /*
     * JTable uses this method to determine the default renderer/
     * editor for each cell.  If we didn't implement this method,
     * then the last column would contain text ("true"/"false"),
     * rather than a check box.
     */
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    /*
     * Don't need to implement this method unless your table's
     * editable.
     */
    public boolean isCellEditable(int row, int col) {
        //Note that the data/cell address is constant,
        //no matter where the cell appears onscreen.
    	boolean editable = false;
    	System.out.println("IsCellEditable-->row:"+row+" col:"+col+" ="+editable);
    	if(col==7){
    		//output tracking is always allowed
    		editable=true;
    	} else if (col == 0) {
        	editable = false;
//            return false;
        } else if (!((String) getValueAt(row,1)).contains("mpvar")
        		&& !((String) getValueAt(row,1)).contains("linctr")) {
        	//making datatype and param type as changeable
        	if(col == 1  || col == 2){

        		editable = true;
        	} else if(col==3 && ((String) getValueAt(row,2)).equals("Manual")){
        		//value is only editable if variable is customized
        		editable = true;
        	} else if( col==4 && ((String) getValueAt(row,2)).equals("External")){
        		//file selection only available if variable is external
        		editable = true;
        	} else if(col==5) {
        		String fileName = (String) getValueAt(row,4) ;
        		if(fileName!=null &&
        				(fileName.endsWith(".xls") || fileName.endsWith(".xlsx")) ){
        			//excel range only required for .xls and .xlsx formats
        			editable = true;
        		}
        	} else if(col==6 && (((String) getValueAt(row,2)).equals("Randomized") 
        			|| ((String) getValueAt(row,2)).equals("Step"))){
        		//enable randomized/step function
        		editable = true;
        	}
            	
        }
        System.out.println("IsCellEditable-->row:"+row+" col:"+col+" ="+editable);
        return editable;
    }

    /*
     * Don't need to implement this method unless your table's
     * data can change.
     */
    public void setValueAt(Object value, int row, int col) {
        if (Constants.DEBUG) {
            System.out.println("Setting value at " + row + "," + col
                               + " to " + value
                               + " (an instance of "
                               + value.getClass() + ")");
        }


        ArrayList<Object> rowData = data.get(row);
        rowData.set(col, value);
        data.set(row, rowData);
        fireTableCellUpdated(row, col);


    }


}
