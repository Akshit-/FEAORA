package model;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import pcgui.Constants;

public class MapVisualizationModel extends AbstractTableModel {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1405438536390252264L;

	private String[] columnNames = 
		  { "MapType",//Will display map options points, line , directed-lines,line-pairs,directed-line-pairs
            "Source",//list of points that be marked
            "Destination",//list of points that be marked
            "Node Condition",//1D-array decision variable for which points will be marked
            "Path Condition",//2D-array decision variable for which lines/edges will be drawn between points
            "Show All Points",
            "Iteration Count",
            "Delete Map",//option to delete row
//            "Show Map",//option to Show map
            "Open in Browser"//option to Show map
           };

    
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
    	boolean editable = true;
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
