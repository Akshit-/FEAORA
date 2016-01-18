package pcgui;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import pcgui.TableUI.ComboBoxRenderer;
import pcgui.output.map.GeoCode;
import pcgui.output.map.MapUI;
import model.MapVisualizationModel;
import model.OutputParams;
import model.ParameterModel;
import model.VisualizationModel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * TableRenderDemo is just like TableDemo, except that it explicitly initializes
 * column sizes and it uses a combo box as an editor for the Sport column.
 */
public class MapVisualizationTableUI extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5860244730792200475L;
	private boolean DEBUG = false;
	private MapVisualizationModel model;
	JTable table;
	private String[] mapTypes = { "PointsOnly", "PointsWithPath",
			"PointsWithDirectedPath", };

	public MapVisualizationTableUI(MapVisualizationModel model) {
		// TODO Auto-generated constructor stub
		super(new GridLayout(1, 0));

		table = new JTable(model);
		table.setGridColor(Color.GRAY);
		// table.setFillsViewportHeight(true);

		// Create the scroll pane and add the table to it.
		JScrollPane scrollPane = new JScrollPane(table);

		// Set up column sizes.
		initColumnSizes(table);
		setUpParamTypeColumn(table, table.getColumnModel().getColumn(0));
		Action delete = new AbstractAction() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 7966203773376384893L;

			public void actionPerformed(ActionEvent e) {
				JTable table = (JTable) e.getSource();
				int modelRow = Integer.valueOf(e.getActionCommand());
				((MapVisualizationModel) table.getModel()).deleteRow(modelRow);
				return;
			}
		};

		Action show = new AbstractAction() {

			private static final long serialVersionUID = 7966203773376384893L;

			public void actionPerformed(ActionEvent e) {

				String url = generateURL(e);
				if(url==null){
					System.err.println("URL was null");
					JOptionPane.showMessageDialog(null, "Cannot generate URL, please check map options.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				try {
					MapUI.openWebpage(new URL(url));
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
		};
//		Action showLocal = new AbstractAction() {
//
//			private static final long serialVersionUID = 7966203773376384893L;
//
//			public void actionPerformed(ActionEvent e) {
//
//				String url = generateURL(e);
//				if(url==null){
//					System.err.println("URL was null");
//				}
//					MapUI2 map = new MapUI2();
//					map.openLocalWebpage(url);
//				
//
//			}
//		};

		ButtonColumn buttonColumn = new ButtonColumn(table, "Delete", delete, 7);

//		ButtonColumn buttonColumn2 = new ButtonColumn(table, "Show", showLocal,
//				8);

		ButtonColumn buttonColumn3 = new ButtonColumn(table, "Open in browser",
				show, 8);

		// Add the scroll pane to this panel.
		add(scrollPane);
	}

	/**
	 * Returns the URL from the row data give by Actionevent e
	 * 
	 * @param e
	 * @return
	 */
	private String generateURL(ActionEvent e) {
		JTable table = (JTable) e.getSource();
		int modelRowID = Integer.valueOf(e.getActionCommand());

		// Generate the Map URL base on modelRowID
		ArrayList<HashMap<String, OutputParams>> result = ModelSaver.modelResult;
		// Get the datas
		int iterationNumber = -1;
		String mapType = (String) table.getValueAt(modelRowID, 0);
		String srcVar = (String) table.getValueAt(modelRowID, 1);
		String destVar = (String) table.getValueAt(modelRowID, 2);
		String nodeCondtionVar = (String) table.getValueAt(modelRowID, 3);
		String edgeCondtionVar = (String) table.getValueAt(modelRowID, 4);
		boolean showAllLocation = (boolean) table.getValueAt(modelRowID, 5);
		try {
			iterationNumber = (int)
					(Integer.parseInt((String) table.getValueAt(modelRowID, 6)) -1);//arrays are zero indexed
		} catch (IllegalArgumentException ex) {
			System.err.println("User did not enter proper iteration number");
			JOptionPane.showMessageDialog(new JFrame("Error"),
					"Please enter proper iteration number");
			return null;
		}
		OutputParams srcParam = null;
		OutputParams destParam = null;
		if (iterationNumber >= 0 && iterationNumber < result.size()) {
			switch (mapType) {
			case "PointsOnly":
				srcParam = result.get(iterationNumber).get(srcVar);
				if (srcParam == null) {
					System.err
							.println("User did not enter proper iteration number or source variable");
					JOptionPane.showMessageDialog(new JFrame("Error"),
							"Please enter proper iteration number or source variable");
					return null;
				}
				String locationsStr = (String) srcParam.value;
				System.out.println("Locations: " + locationsStr);

				OutputParams nodeConditionParam = null;
				String nodeConditionStr = null;

				if (nodeCondtionVar != null && !nodeCondtionVar.isEmpty()) {
					nodeConditionParam = result.get(iterationNumber).get(
							nodeCondtionVar);
					nodeConditionStr = (String) nodeConditionParam.value;
				}

				String[] tokens = null;
				String[] locations = null;
				if (locationsStr.startsWith("{")) {
					tokens = locationsStr.split("\\{");
					tokens = tokens[1].split("\\}");
				} else if (locationsStr.startsWith("[")) {
					tokens = locationsStr.split("\\[");
					tokens = tokens[1].split("\\]");
				}

				locations = tokens[0].split(",");
				// now locations contains names of places
				// get longitude latitude info
				List<String> sourceLocationList = new ArrayList<String>();
				List<GeoCode> sourceGeoCodeList = null;
				String url = null;
				if (showAllLocation) {
					sourceLocationList = Arrays.asList(locations);
					sourceGeoCodeList = MapUI.getGeoCodes(sourceLocationList);
					url = generateMapURLPointsOnly(sourceGeoCodeList);
					return url;
				} else {
					if (nodeConditionStr != null && !nodeConditionStr.isEmpty()) {
						tokens = null;
						String[] activeLocations = null;
						if (nodeConditionStr.startsWith("{")) {
							tokens = nodeConditionStr.split("\\{");
							tokens = tokens[1].split("\\}");
						} else if (nodeConditionStr.startsWith("[")) {
							tokens = nodeConditionStr.split("\\[");
							tokens = tokens[1].split("\\]");
						}

						activeLocations = tokens[0].split(",");
						for (int i = 0; i < activeLocations.length; i++) {
							if (Math.round(Float.parseFloat(activeLocations[i])) > 0) {
								sourceLocationList.add(locations[i]);
							}
						}
						sourceGeoCodeList = MapUI
								.getGeoCodes(sourceLocationList);
						url = generateMapURLPointsOnly(sourceGeoCodeList);
						return url;
					}
					//error
					return null; 
				}
				

			case "PointsWithPath":
				srcParam = result.get(iterationNumber).get(srcVar);
				if (srcParam == null) {
					System.err
							.println("User did not enter proper iteration number or src Var not available");
					JOptionPane
							.showMessageDialog(new JFrame("Error"),
									"Please check iteration number or source variable!");
					return null;
				}
				String srcStr = (String) srcParam.value;
				//System.out.println("Source Locations: " + srcStr);

				destParam = result.get(iterationNumber).get(destVar);
				if (destParam == null) {
					System.err
							.println("User did not enter proper iteration number or dest Var not available");
					JOptionPane
							.showMessageDialog(new JFrame("Error"),
									"Please check iteration number  or destination variable!");
					return null;
				}
				String destStr = (String) destParam.value;
				//System.out.println("Destination Locations: " + destStr);
				tokens = null;
				String[] srcLocations = null;
				if (srcStr.startsWith("{")) {
					tokens = srcStr.split("\\{");
					tokens = tokens[1].split("\\}");
				} else if (srcStr.startsWith("[")) {
					tokens = srcStr.split("\\[");
					tokens = tokens[1].split("\\]");
				}

				srcLocations = tokens[0].split(",");
				if (destStr.startsWith("{")) {
					tokens = destStr.split("\\{");
					tokens = tokens[1].split("\\}");
				} else if (destStr.startsWith("[")) {
					tokens = destStr.split("\\[");
					tokens = tokens[1].split("\\]");
				}

				String[] destLocations = tokens[0].split(",");
				// now locations contains names of places
				// get longitude latitude info
				sourceLocationList = new ArrayList<String>();
				sourceGeoCodeList = null;
				ArrayList<String> destinationLocationList = new ArrayList<String>();
				List<GeoCode> destinationGeoCodeList = null;

				OutputParams edgeConditionParam = null;
				String edgeConditionStr = null;

				if (edgeCondtionVar != null && !edgeCondtionVar.isEmpty()) {
					edgeConditionParam = result.get(iterationNumber).get(
							edgeCondtionVar);
					edgeConditionStr = (String) edgeConditionParam.value;
				}
				if (edgeConditionStr != null && !edgeConditionStr.isEmpty()) {
					tokens = null;
					String[] activePaths = null;
					if (edgeConditionStr.startsWith("{")) {
						tokens = edgeConditionStr.split("\\{");
						tokens = tokens[1].split("\\}");
					} else if (edgeConditionStr.startsWith("[")) {
						tokens = edgeConditionStr.split("\\[");
						tokens = tokens[1].split("\\]");
					}

					activePaths = tokens[0].split(",");
					//System.out.println("active paths:" + activePaths);
					for (int i = 0; i < activePaths.length; i++) {
						if (Math.round(Float.parseFloat(activePaths[i])) > 0) {
							int rowNum = i / destLocations.length;
							int colNum = i % destLocations.length;
							sourceLocationList.add(srcLocations[rowNum]);
							destinationLocationList.add(destLocations[colNum]);
						}
					}
					/*System.out.println("*******SourceLocationList*******size="+sourceLocationList.size());
					for(String location : sourceLocationList) {
			            System.out.print(location+", ");
			        }
					
					System.out.println("\n*******destinationLocationList*******size="+destinationLocationList.size());
					for(String location : destinationLocationList) {
			            System.out.print(location+", ");
			        }*/
					
					sourceGeoCodeList = MapUI.getGeoCodes(sourceLocationList);
					
					destinationGeoCodeList = MapUI
							.getGeoCodes(destinationLocationList);
					
					url = generateMapURLPointsWithPath(sourceGeoCodeList,
							destinationGeoCodeList);
					return url;
				}
				// error
				return null;

			case "PointsWithDirectedPath":

				srcParam = result.get(iterationNumber).get(srcVar);
				if (srcParam == null) {
					System.err
							.println("User did not enter proper iteration number or src Var not available");
					JOptionPane
							.showMessageDialog(new JFrame("Error"),
									"Please check iteration number or source variable!");
					return null;
				}
				srcStr = (String) srcParam.value;
				//System.out.println("Source Locations: " + srcStr);

				destParam = result.get(iterationNumber).get(destVar);
				if (destParam == null) {
					System.err
							.println("User did not enter proper iteration number or dest Var not available");
					JOptionPane
							.showMessageDialog(new JFrame("Error"),
									"Please check iteration number  or destination variable!");
					return null;
				}
				destStr = (String) destParam.value;
				//System.out.println("Destination Locations: " + destStr);
				tokens = null;
				srcLocations = null;
				if (srcStr.startsWith("{")) {
					tokens = srcStr.split("\\{");
					tokens = tokens[1].split("\\}");
				} else if (srcStr.startsWith("[")) {
					tokens = srcStr.split("\\[");
					tokens = tokens[1].split("\\]");
				}

				srcLocations = tokens[0].split(",");
				if (destStr.startsWith("{")) {
					tokens = destStr.split("\\{");
					tokens = tokens[1].split("\\}");
				} else if (destStr.startsWith("[")) {
					tokens = destStr.split("\\[");
					tokens = tokens[1].split("\\]");
				}

				destLocations = tokens[0].split(",");
				// now locations contains names of places
				// get longitude latitude info
				sourceLocationList = new ArrayList<String>();
				sourceGeoCodeList = null;
			    destinationLocationList = new ArrayList<String>();
				destinationGeoCodeList = null;

				edgeConditionParam = null;
				edgeConditionStr = null;

				if (edgeCondtionVar != null && !edgeCondtionVar.isEmpty()) {
					edgeConditionParam = result.get(iterationNumber).get(
							edgeCondtionVar);
					edgeConditionStr = (String) edgeConditionParam.value;
				}
				if (edgeConditionStr != null && !edgeConditionStr.isEmpty()) {
					tokens = null;
					String[] activePaths = null;
					if (edgeConditionStr.startsWith("{")) {
						tokens = edgeConditionStr.split("\\{");
						tokens = tokens[1].split("\\}");
					} else if (edgeConditionStr.startsWith("[")) {
						tokens = edgeConditionStr.split("\\[");
						tokens = tokens[1].split("\\]");
					}

					activePaths = tokens[0].split(",");
					
					for (int i = 0; i < activePaths.length; i++) {
						//System.out.println("====Before rounding=="+Float.parseFloat(activePaths[i]));
						//System.out.println("====After rounding=="+Math.round(Float.parseFloat(activePaths[i])));
						if (Math.round(Float.parseFloat(activePaths[i])) > 0) {
							int rowNum = i / destLocations.length;
							int colNum = i % destLocations.length;
							sourceLocationList.add(srcLocations[rowNum]);
							destinationLocationList.add(destLocations[colNum]);
						}
					}
					sourceGeoCodeList = MapUI.getGeoCodes(sourceLocationList);
					destinationGeoCodeList = MapUI
							.getGeoCodes(destinationLocationList);
					url = generateMapURLPointsWithDirectedPath(sourceGeoCodeList,
							destinationGeoCodeList);
					return url;
				}
				// error
				return null;

			}
		} else {
			JOptionPane
			.showMessageDialog(new JFrame("Error"),
					"Please check iteration number!");
			return null;
		}
		return null;
	}

	/*
	 * This method picks good column sizes. If all column heads are wider than
	 * the column's cells' contents, then you can just use
	 * column.sizeWidthToFit().
	 */
	private void initColumnSizes(JTable table) {
		TableColumn column = null;
		Component comp = null;
		int headerWidth = 0;
		int cellWidth = 0;
		TableCellRenderer headerRenderer = table.getTableHeader()
				.getDefaultRenderer();

		for (int i = 0; i <9; i++) {
			column = table.getColumnModel().getColumn(i);

			comp = headerRenderer.getTableCellRendererComponent(null,
					column.getHeaderValue(), false, false, 0, 0);
			headerWidth = comp.getPreferredSize().width;

			cellWidth = comp.getPreferredSize().width;

			if (DEBUG) {
				System.out.println("Initializing width of column " + i + ". "
						+ "headerWidth = " + headerWidth + "; cellWidth = "
						+ cellWidth);
			}

			column.setPreferredWidth(Math.max(headerWidth, cellWidth));
		}
	}

	public void setUpParamTypeColumn(JTable table, TableColumn paramTypeColumn) {
		// Set up the editor for the sport cells.

		JComboBox<String> comboBox = new JComboBox<String>(mapTypes);

		paramTypeColumn.setCellEditor(new DefaultCellEditor(comboBox));
		comboBox.setRenderer(new ComboBoxRenderer());
		// Set up tool tips for the sport cells.
		DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
		renderer.setToolTipText("Click for viewing all options");
		paramTypeColumn.setCellRenderer(renderer);
	}

	class ComboBoxRenderer extends BasicComboBoxRenderer {

		/**
* 
*/
		private static final long serialVersionUID = -7871388236564580887L;

		@Override
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			super.getListCellRendererComponent(list, value, index, isSelected,
					cellHasFocus);
			if (value != null) {
				String item = (String) value;
				setText(item);
			}
			return this;
		}
	}

	public void setParameters(MapVisualizationModel model) {
		this.model = model;
		this.model.fireTableDataChanged();
	}

	private String generateMapURLPointsOnly(List<GeoCode> geoCodeList) {
		String path = System.getProperty("user.dir");
		path.replace("\\\\", "/");
		path += "/res/map.html";
		File mapfile = new File(path);
		String absPath = "file://";
		if (mapfile.exists()) {
			// perfect! now get the absolute path
			absPath += mapfile.getAbsolutePath();
		}
		String url = absPath + "?mapType=PointsOnly&points=";
		for (GeoCode geoCode : geoCodeList) {
			url += geoCode.toString() + ";";
		}

		//System.out.println("Points only URL generated : " + url);
		return url;
	}

	private String generateMapURLPointsWithPath(
			List<GeoCode> geoCodeSourceList, List<GeoCode> geoCodeDestList) {
		String path = System.getProperty("user.dir");
		path.replace("\\\\", "/");
		path += "/res/map.html";
		File mapfile = new File(path);
		String absPath = "file://";
		if (mapfile.exists()) {
			// perfect! now get the absolute path
			absPath += mapfile.getAbsolutePath();
		}
		/*System.out.println("*******SourceLocationList*******size="+geoCodeSourceList.size());
		for(GeoCode location : geoCodeSourceList) {
            System.out.print(location+", ");
        }
		
		System.out.println("\n*******destinationLocationList*******size="+geoCodeDestList.size());
		for(GeoCode location : geoCodeDestList) {
            System.out.print(location+", ");
        }
		*/
		String url = absPath + "?mapType=PointsWithPath&points=";
		for (int i = 0; i < geoCodeSourceList.size(); i++) {
			url += geoCodeSourceList.get(i).toString() + ";"
					+ geoCodeDestList.get(i).toString() + "*";
		}

		//System.out.println("PointsWithPath URL generated : " + url);
		return url;
	}

	private String generateMapURLPointsWithDirectedPath(
			List<GeoCode> geoCodeSourceList, List<GeoCode> geoCodeDestList) {
		String path = System.getProperty("user.dir");
		path.replace("\\\\", "/");
		path += "/res/map.html";
		File mapfile = new File(path);
		String absPath = "file://";
		if (mapfile.exists()) {
			// perfect! now get the absolute path
			absPath += mapfile.getAbsolutePath();
		}
		String url = absPath + "?mapType=PointsWithDirectedPath&points=";
		for (int i = 0; i < geoCodeSourceList.size(); i++) {
			url += geoCodeSourceList.get(i).toString() + ";"
					+ geoCodeDestList.get(i).toString() + "*";
		}

		//System.out.println("PointsWithDirectedPath URL generated : " + url);
		return url;
	}

}
