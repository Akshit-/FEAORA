package pcgui;

/**
 * This class is used to setup the PC application
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
import model.OutputParams;
import model.Parameter;
import model.ParameterModel;
import model.Visualization;
import model.VisualizationModel;

import java.awt.GridLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SetupVisualizationPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7329598722048130596L;
	@SuppressWarnings("unused")
	private JFrame rootFrame;
	private ArrayList<Visualization> paramList;
	private VisualizationTableUI tableUI;
	private VisualizationModel model;

	private String[] chartTypes = {"LineChart","BarChart","ScatterPlot","PieChart"};
	
	private JComboBox<String> chartTypeCB;
	private JTextField xAxisTF;
	private JTextField yAxisTF;
	private JCheckBox useIterationNumAsXChkBox;
	private JButton addVisualizationBtn;
	private JButton drawVisualizationBtn;
	private ArrayList<Symbol> trackedSymbols;
	private ArrayList<String> trackedVariables;
	private JComboBox<String> xAxisCB;
	private JComboBox<String> yAxisCB;
	/**
	 * Create the frame.
	 * 
	 * @param switcher
	 * 
	 * @param rootFrame
	 */
	
	public SetupVisualizationPanel(final PanelSwitcher switcher, JFrame rootFrame) {
		this.rootFrame = rootFrame;
		setLayout(new BorderLayout());

		if(trackedVariables==null){
			trackedVariables = new ArrayList<String>();
			trackedVariables.add("");
		}
		

		JLabel headingLabel = new JLabel("Setup Visualization");
		headingLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		add(headingLabel, BorderLayout.NORTH);
		
		JLabel chartTypeLabel = new JLabel("Chart Type");
		chartTypeLabel.setPreferredSize(new Dimension(140, 30));
		chartTypeCB = new JComboBox<String>(chartTypes);
		chartTypeCB.setPreferredSize(new Dimension(140, 30));
		
		
		JLabel xAxisLabel = new JLabel("X Axis");
		xAxisLabel.setPreferredSize(new Dimension(140, 30));
//		xAxisTF = new JTextField();
//		xAxisTF.setPreferredSize(new Dimension(100, 20));
		xAxisCB = new JComboBox<String>(new DefaultComboBoxModel(trackedVariables.toArray()));
		xAxisCB.setPreferredSize(new Dimension(140, 30));
		
		
		JLabel yAxisLabel = new JLabel("Y Axis");
		yAxisLabel.setPreferredSize(new Dimension(140, 30));
//		yAxisTF = new JTextField();
//		yAxisTF.setPreferredSize(new Dimension(100, 20));
		yAxisCB = new JComboBox<String>(new DefaultComboBoxModel(trackedVariables.toArray()));
		yAxisCB.setPreferredSize(new Dimension(140, 30));
		
		JLabel iterationAsXLabel = new JLabel("Use Iteration Number as X Axis");
		iterationAsXLabel.setPreferredSize(new Dimension(250, 30));
		useIterationNumAsXChkBox = new JCheckBox("Use Iteration Count as X Axis");
		useIterationNumAsXChkBox.setPreferredSize(new Dimension(250, 30));
		
		addVisualizationBtn = new JButton("Add");
		addVisualizationBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<Object> visRow = new ArrayList<Object>(4);
				visRow.add(chartTypeCB.getSelectedItem());
				visRow.add(xAxisCB.getSelectedItem());
				visRow.add(yAxisCB.getSelectedItem());
				visRow.add(useIterationNumAsXChkBox.isSelected());
				visRow.add("");//for Delete
				model.addRow(visRow);
				
			}
		});
		
		drawVisualizationBtn = new JButton("Draw Visualizations");
		drawVisualizationBtn.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ArrayList<ArrayList<Object>> visList = getVisualizationConfig();				
				boolean successOnCharts = true;
				
				for(ArrayList<Object> row: visList){
					Visualization vis = new Visualization();
					vis.setChartType((String)row.get(0));
					vis.setXAxis((String)row.get(1));
					vis.setYAxis((String)row.get(2));
					vis.setUsingIterationNumAsX((boolean)row.get(3));
					switch(vis.getChartType()){
						case "LineChart": 
							try{
								if(vis.isUsingIterationNumAsX()){
									ModelSaver.drawLineChart(null, vis.getYAxis());
								} else {
									ModelSaver.drawLineChart(vis.getXAxis(), vis.getYAxis());
								}
							}catch(Exception exc){
								successOnCharts = false;
								CustomErrorDialog.showDialog("Exception while drawing LineChart to Excel", exc.toString());
								exc.printStackTrace();
							}
							break;
						case "ScatterPlot": 
							try{
								if(vis.isUsingIterationNumAsX()){
									ModelSaver.drawScatterPlot(null, vis.getYAxis());
								} else {
									ModelSaver.drawScatterPlot(vis.getXAxis(), vis.getYAxis());
								}
							}catch(Exception exc){
								successOnCharts = false;
								CustomErrorDialog.showDialog("Exception while drawing ScatterPlot to Excel", exc.toString());
								exc.printStackTrace();
							}
							break;
						case "BarChart":
							try{
								if(vis.isUsingIterationNumAsX()){
									ModelSaver.drawBarChart(null, vis.getYAxis());
								} else {
									ModelSaver.drawBarChart(vis.getXAxis(), vis.getYAxis());
								}
							}catch(Exception exc){
								successOnCharts = false;
								CustomErrorDialog.showDialog("Exception while drawing BarChart to Excel", exc.toString());
								exc.printStackTrace();
							}
							break;
						case "PieChart":
							try{
								if(vis.isUsingIterationNumAsX()){
									ModelSaver.drawPieChart(null, vis.getYAxis());
								} else {
									ModelSaver.drawPieChart(vis.getXAxis(), vis.getYAxis());
								}
							}catch(Exception exc){
								successOnCharts = false;
								CustomErrorDialog.showDialog("Exception while drawing PieChart to Excel", exc.toString());
								exc.printStackTrace();
							}
							break;
							
					}
					
				}
				
				if(successOnCharts)
					JOptionPane.showMessageDialog(new JFrame("Success"), 
							"Visualization completed.\nOutput charts added to excel file : "
							+ModelSaver.excelFileName);
			
			}
		});
		JPanel runConfig = new JPanel();
		runConfig.setLayout(new GridLayout(3,2));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.CENTER;

		JPanel configLabel = new JPanel(new FlowLayout());
		JPanel configCount = new JPanel(new FlowLayout());
		JPanel configButtons = new JPanel(new FlowLayout());
		configLabel.setAlignmentX(LEFT_ALIGNMENT);
		configLabel.add(chartTypeLabel);
		configLabel.add(xAxisLabel);
		configLabel.add(yAxisLabel);
		configLabel.add(iterationAsXLabel);
		
		configCount.setAlignmentX(LEFT_ALIGNMENT);
		configCount.add(chartTypeCB);
		configCount.add(xAxisCB);
		configCount.add(yAxisCB);
		configCount.add(useIterationNumAsXChkBox);

		configButtons.add(addVisualizationBtn);
		configButtons.add(drawVisualizationBtn);
		
		runConfig.add(configLabel);
		runConfig.add(configCount);
		runConfig.add(configButtons);
		add(runConfig, BorderLayout.SOUTH);
		
		setVisible(true);
	}
	
	

	public void initParamList(ArrayList<Visualization> paramList, ModelParser parser) {
		// TODO Auto-generated method stub
		this.paramList = paramList;
		

		if (model == null) {
			model = new VisualizationModel();
		}
		//clear data just in case we are calling this for loading saved config
		model.clearData();
		if(paramList!=null){
			for (Visualization param : paramList) {
				
				ArrayList<Object> rowData = new ArrayList<Object>();
				
				rowData.add(param.getChartType());
				
				String xAxis = param.getXAxis();
				rowData.add( xAxis!=null?xAxis:"");
				
				String yAxis = param.getYAxis();
				rowData.add( yAxis!=null?yAxis:"");
				
			
				boolean isUsingIterationNumAsX = param.isUsingIterationNumAsX();
				rowData.add( isUsingIterationNumAsX);
				
				
				model.addRow(rowData);

			}
		}
		
		if (tableUI == null) {
			System.out.println("Table initializing");
			tableUI = new VisualizationTableUI(model);
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
		xAxisCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
		yAxisCB.setModel(new DefaultComboBoxModel(trackedVariables.toArray()));
	}
	
	
}
