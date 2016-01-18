package pcgui;

/**
 * This class is used to setup the PC application
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Label;

import javax.swing.JSpinner;
import javax.swing.filechooser.FileFilter;
import javax.swing.text.TabExpander;

import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import randomization.StepDistribution;
import randomization.TextParser;

import com.dashoptimization.XPRMCompileException;
import com.dashoptimization.XPRMLicenseError;

import model.Distribution;
import model.MapVisualization;
import model.MapVisualizationModel;
import model.OutputParams;
import model.Parameter;
import model.ParameterModel;
import model.Visualization;
import model.VisualizationModel;

import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SetupMapVisualizationPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7329598722048130596L;
	@SuppressWarnings("unused")
	private JFrame rootFrame;
	private ArrayList<MapVisualization> paramList;
	private MapVisualizationTableUI tableUI;
	private MapVisualizationModel model;

	private String[] mapTypes = {"PointsOnly","PointsWithPath","PointsWithDirectedPath"};
	
	private JComboBox<String> mapTypeCB;
	private JTextField locationsTF;
	private JComboBox<String> locationsCB;
	private JTextField destLocationsTF;
	private JComboBox<String> destinationsCB;
	private JTextField nodeConditionTF;
	private JComboBox<String> nodeConditionCB;
	private JTextField edgeConditionTF;
	private JComboBox<String> edgeConditionCB;
	private JCheckBox showAllNodes;
	private JTextField iterationCountTF;
	private JButton addMapVisualizationBtn;
	private ArrayList<Symbol> trackedSymbols;
	private List<String> trackedVariables;
//	private JButton drawVisualizationBtn;
	/**
	 * Create the frame.
	 * 
	 * @param switcher
	 * 
	 * @param rootFrame
	 */
	
	public SetupMapVisualizationPanel(final PanelSwitcher switcher, JFrame rootFrame) {
		this.rootFrame = rootFrame;
		init();
	}
	
	

	public void initParamList(ArrayList<MapVisualization> paramList, ModelParser parser) {
		
		this.paramList = paramList;
		

		if (model == null) {
			model = new MapVisualizationModel();
		}
		//clear data just in case we are calling this for loading saved config
		model.clearData();
		if(paramList!=null){
			for (MapVisualization param : paramList) {
				
				ArrayList<Object> rowData = new ArrayList<Object>();
				
				rowData.add(param.getMapType());
				String locations = param.getSourceLocations();
				rowData.add( locations!=null?locations:"");
				
				String destLocations = param.getDestination();
				rowData.add( destLocations!=null?destLocations:"");
				
				String nodeCondition = param.getNodeCondition();
				rowData.add( nodeCondition!=null?nodeCondition:"");
				
				String edgeCondition = param.getEdgeCondition();
				rowData.add( edgeCondition!=null?edgeCondition:"");
				
			
				boolean showAllNodes = param.isShowAllNodes();
				rowData.add( showAllNodes);
				
				String iterationCount = param.getIterationCount();
				rowData.add( iterationCount!=null?iterationCount:"");
				
				rowData.add(false);//show All locations
				rowData.add("");//delete btn
				rowData.add("");//show btn
				rowData.add("");//show in browser btn
				
				model.addRow(rowData);

			}
		}
		
		if (tableUI == null) {
			System.out.println("Table initializing");
			tableUI = new MapVisualizationTableUI(model);
			tableUI.setOpaque(true); // content panes must be opaque
			add(tableUI, BorderLayout.CENTER);
		} else {
			tableUI.invalidate();
			System.out.println("Table re-initializing");
		}

	}

	public ArrayList<ArrayList<Object>> getVisualizationConfig(){
		
		return model.getData();
	}



	public void setTrackedVariables(ArrayList<Symbol> trackedSymbols) {
		System.out.println("Setting tracked variables in map visualization");
		this.trackedSymbols = trackedSymbols;
		trackedVariables = new ArrayList<String>();
		trackedVariables.add("");
		for(Symbol sym : trackedSymbols){
			System.out.println("Tracking variable:"+sym.name);
			trackedVariables.add(sym.name);
		}
		locationsCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
		destinationsCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
		nodeConditionCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
		edgeConditionCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
	}
	
	public void init(){
		setLayout(new BorderLayout());
		JLabel headingLabel = new JLabel("Setup Map Visualization");
		headingLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		add(headingLabel, BorderLayout.NORTH);
		
		JLabel mapTypeLabel = new JLabel("Map Type");
		mapTypeLabel.setPreferredSize(new Dimension(140, 30));
		mapTypeLabel.setHorizontalTextPosition(JLabel.CENTER);
		mapTypeCB = new JComboBox<String>(mapTypes);
		mapTypeCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel sourceLabel = new JLabel("Source");
		sourceLabel.setPreferredSize(new Dimension(140, 30));
		sourceLabel.setHorizontalTextPosition(JLabel.CENTER);
//		locationsTF = new JTextField();
//		locationsTF.setPreferredSize(new Dimension(100, 20));
		if(trackedVariables==null){
			trackedVariables= new ArrayList<String>();
			trackedVariables.add("");
		}
		locationsCB = new JComboBox<String>(new DefaultComboBoxModel( trackedVariables.toArray()));
		locationsCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel destLabel = new JLabel("Destination");
		destLabel.setPreferredSize(new Dimension(140, 30));
		destLabel.setHorizontalTextPosition(JLabel.CENTER);
//		destLocationsTF = new JTextField();
//		destLocationsTF.setPreferredSize(new Dimension(140, 30));
		destinationsCB = new JComboBox<String>(new DefaultComboBoxModel( trackedVariables.toArray()));
		destinationsCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel nodeCondLabel = new JLabel("Node condition");
		nodeCondLabel.setPreferredSize(new Dimension(140, 30));
		nodeCondLabel.setHorizontalTextPosition(JLabel.CENTER);
//		nodeConditionTF = new JTextField();
//		nodeConditionTF.setPreferredSize(new Dimension(140, 30));
		nodeConditionCB = new JComboBox<String>(new DefaultComboBoxModel( trackedVariables.toArray()));
		nodeConditionCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel edgeCondLabel = new JLabel("Edge Condition");
		edgeCondLabel.setPreferredSize(new Dimension(140, 30));
		edgeCondLabel.setHorizontalTextPosition(JLabel.CENTER);
//		edgeConditionTF = new JTextField();
//		edgeConditionTF.setPreferredSize(new Dimension(140, 30));
		edgeConditionCB = new JComboBox<String>(new DefaultComboBoxModel( trackedVariables.toArray()));
		edgeConditionCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel showAllLabel = new JLabel("Show All Nodes");
		showAllLabel.setPreferredSize(new Dimension(140, 30));
		showAllLabel.setHorizontalTextPosition(JLabel.CENTER);
		showAllNodes = new JCheckBox("Show All Nodes");
		showAllNodes.setPreferredSize(new Dimension(140, 30));
		
		JLabel iterationNumLabel = new JLabel("Iteration Number");
		iterationNumLabel.setPreferredSize(new Dimension(140, 30));
		iterationNumLabel.setHorizontalTextPosition(JLabel.CENTER);
		iterationCountTF = new JTextField("1");
		iterationCountTF.setPreferredSize(new Dimension(140, 30));
		
		addMapVisualizationBtn = new JButton("Add");
		addMapVisualizationBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Object> visRow = new ArrayList<Object>(7);
				//TODO: Add combo box for all variable entries
				//don't add if locations is null or empty 
				//don't add if locations is not null but other conditions are false
				visRow.add(mapTypeCB.getSelectedItem());
				visRow.add(locationsCB.getSelectedItem());
				visRow.add(destinationsCB.getSelectedItem());
				visRow.add(nodeConditionCB.getSelectedItem());
				visRow.add(edgeConditionCB.getSelectedItem());
				visRow.add(showAllNodes.isSelected());
				visRow.add(iterationCountTF.getText());
				visRow.add("");//for Delete Button
				visRow.add("");//for Show Button
				visRow.add("");//for Show in browsersButton
				model.addRow(visRow);
				
			}
		});
		
	
		JPanel runConfig = new JPanel();
		runConfig.setLayout(new GridLayout(3,2));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.CENTER;
		JPanel configLabels = new JPanel(new FlowLayout());
		configLabels.add(mapTypeLabel);
		configLabels.add(sourceLabel);
		configLabels.add(destLabel);
		configLabels.add(nodeCondLabel);
		configLabels.add(edgeCondLabel);
		configLabels.add(showAllLabel);
		configLabels.add(iterationNumLabel);
		
		JPanel configCount = new JPanel(new FlowLayout());
		JPanel configButtons = new JPanel(new FlowLayout());
		configCount.add(mapTypeCB);
		configCount.add(locationsCB);
		configCount.add(destinationsCB);
		configCount.add(nodeConditionCB);
		configCount.add(edgeConditionCB);
		configCount.add(showAllNodes);
		configCount.add(iterationCountTF);

		configButtons.add(addMapVisualizationBtn);
		
		runConfig.add(configLabels);
		runConfig.add(configCount);
		runConfig.add(configButtons);
		add(runConfig, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	
	
}
