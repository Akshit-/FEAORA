package pcgui;

/**
 * This class is used to setup the PC application
 */

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.Toolkit;

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

import java.awt.GridLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SetupParametersPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7329598722048130596L;
	@SuppressWarnings("unused")
	private JFrame rootFrame;
	private ArrayList<Symbol> paramList;
	private TableUI tableUI;
	private ParameterModel model;
	private ModelParser parser;
	private JTextField repeatCount;
	private HashMap<String, Symbol> hashTable;
	private File importedFile;
	private SetupVisualizationPanel visPanel;
	protected String outputFile;
	private SetupMapVisualizationPanel mapVisPanel;
	JProgressBar pbar;
	private boolean compileSuccess;
	protected JTextArea taskOutput;
	private JFrame progressFrame;

	  static final int MY_MINIMUM = 0;

	  static final int MY_MAXIMUM = 100;
	/**
	 * Create the frame.
	 * 
	 * @param switcher
	 * 
	 * @param rootFrame
	 */

	public SetupParametersPanel(final PanelSwitcher switcher, JFrame rootFrame) {
		this.rootFrame = rootFrame;
		setLayout(new BorderLayout());

		JLabel headingLabel = new JLabel("Setup Parameters");
		headingLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		add(headingLabel, BorderLayout.NORTH);
		JButton saveBtn = new JButton("Save");
		final JButton runModifiedBtn = new JButton("Run");
		
		runModifiedBtn.setEnabled(false);
		
		runModifiedBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				runModel();
				
			}
		});
		
		saveBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				//creating separate copy of model so that changes in one doesn't effect other
				ArrayList<ArrayList<Object>> mod = new ArrayList<ArrayList<Object>>(model.getData());
				//call save model of modelsaver
				//TODO add visualization list and save to excel using similar method as below
				//TODO add modelname, should contain .xls/.xlsx extension
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_hhmmss");
				outputFile = importedFile.toString().replace(".mos", "")+"_"+sdf.format(new Date())+"_FEORA_solution.xlsx";
				ModelSaver.saveInputModel(mod,outputFile);
				
				runModifiedBtn.setEnabled(true);
				
			}
		});
		
		JLabel label = new JLabel("Enter count of random value runs");
		repeatCount = new JTextField();
		repeatCount.setToolTipText("Enter count for which each combination of step variables is run ");
		repeatCount.setText("1");
		repeatCount.setPreferredSize(new Dimension(100, 20));
	
		JPanel runConfig = new JPanel();
		runConfig.setLayout(new GridLayout(2,2));

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.CENTER;

		JPanel configCount = new JPanel(new FlowLayout());
		JPanel configButtons = new JPanel(new FlowLayout());
		configCount.add(label);
		configCount.add(repeatCount);
		
		JButton btnLoadSavedConfiguration = new JButton("Load Saved Configuration");
		btnLoadSavedConfiguration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser jFileChooser = new JFileChooser();
				
				File workingDirectory = new File(System.getProperty("user.dir"));
				
				jFileChooser.setCurrentDirectory(workingDirectory); 
				
				jFileChooser.setFileFilter(new FileFilter() {
					
					@Override
					public String getDescription() {
						// TODO Auto-generated method stub
						return "Excel solution files";
					}
					
					@Override
					public boolean accept(File f) {
						// TODO Auto-generated method stub
						return f.isDirectory() 
								|| f.getName().endsWith(".xls")
								|| f.getName().endsWith(".xlsx");
						
					}
				});
				int returnVal = jFileChooser.showOpenDialog(null);

				if (returnVal == JFileChooser.APPROVE_OPTION) {
					File file = jFileChooser.getSelectedFile();
					
					// This is where a real application would open the file.


					
					if(file.getName().endsWith(".xls") 
							||file.getName().endsWith(".xlsx") ){
						String fileName = file.getAbsolutePath();
						List<List<String>> savedConfig = ModelSaver.loadInputConfigFromExcel(fileName);
						ArrayList<Symbol> symList = new ArrayList<Symbol>();
						
						int i=0;
						for(List<String> list: savedConfig){
							//ignore the header row
							if(i>0){
								System.out.println("VariableName="+list.get(0));
								System.out.println("Datatype="+list.get(1));
								System.out.println("Type="+list.get(2));
								System.out.println("Value="+list.get(3));
								System.out.println("ExternalFile="+list.get(4));
								System.out.println("ExcelRange="+list.get(5));
								System.out.println("DistributionFunction="+list.get(6));
								System.out.println("OutputTracket="+list.get(7));
								Symbol sym = new Symbol<>();
								sym.name=list.get(0);
								sym.typeString=list.get(1);
								sym.mode=list.get(2);
								sym.set(list.get(3));
								sym.externalFile=list.get(4);
								sym.excelFileRange=list.get(5);
								sym.distFunction=list.get(6);
								sym.trackingEnabled=Boolean.parseBoolean(list.get(7));
								symList.add(sym);	
							}
							i++;
							
							
						}
						initParamList(symList, parser);
					}

				} else {
//					System.out.println("Open command cancelled by user.");
				}
				
			}
		});
		configButtons.add(btnLoadSavedConfiguration);
		
		configButtons.add(saveBtn);
		configButtons.add(runModifiedBtn);
		runConfig.add(configCount);
		runConfig.add(configButtons);
		add(runConfig, BorderLayout.SOUTH);

		setVisible(true);
	}
	
	private void runModel(){
		
		
		//retrieve master configuration
		ArrayList<ArrayList<Object>> mod = model.getData();
		//create a new list and check for dependencies
		ArrayList<Symbol> list = new ArrayList<Symbol>();
		//independent variable list
		ArrayList<Symbol> ivList = new ArrayList<Symbol>();
		//step variable list
		ArrayList<Symbol> stepList = new ArrayList<Symbol>();
		//dependent list
		ArrayList<Symbol> depList = new ArrayList<Symbol>();
		//output tracking
		ArrayList<Symbol> trackedSymbols = new ArrayList<Symbol>();
		Symbol tempSym = null;
		//step variable counter to be used for making combinations
		int stepVarCount = 0;
		long stepCombinations = 1;
		TextParser textParser = new TextParser();
		 compileSuccess = true;	
		for (ArrayList<Object> rowData : mod) {
			tempSym = paramList.get(mod.indexOf(rowData));
			if ("Manual".equals((String) rowData.get(2))) {
				System.out.println("Found Customized Symbol : "
						+ rowData.get(0));
				System.out.println("Value : " + rowData.get(3)
						+ ": type=" + rowData.get(3).getClass());
				
				
				tempSym.mode="Manual";
				String data = (String)rowData.get(3);
				
				//checking dependent variables
				if(data!=null && !data.isEmpty()){
					//dependent
					if(hashTable.containsKey(data)){
						tempSym.setDependent(true);
						depList.add(tempSym);
					} else {
						//independents
						tempSym.setDependent(false);
						tempSym.set(data);
						ivList.add(tempSym);
					}
				}
			} else if("External".equals((String) rowData.get(2))) {
				System.out.println("Found External Symbol : "
						+ rowData.get(0));
				System.out.println("External : " + rowData.get(3)
						+ ": type=" + rowData.get(3).getClass());
				

				if (!(((String) rowData.get(4)).isEmpty())) {

					tempSym.externalFile = (String) rowData.get(4);
					tempSym.set(tempSym.externalFile);
					tempSym.mode="External";
					if (tempSym.externalFile.endsWith(".xls")
							|| tempSym.externalFile.endsWith(".xlsx")) {

						
						if (tempSym.excelFileRange.isEmpty()) {
							// TODO show an error dialog
							System.err
									.println("Error user missed excel range arguments");
							JOptionPane.showMessageDialog(new JFrame(), "Error user missed excel range arguments");
							return;
						} else {
							tempSym.excelFileRange = (String) rowData
									.get(5);
							//'[Daten_10$B'+(i)+':B'+(5+n)+']'
							//user has to add quote before the 1st + and after the last +
							//the data file used should be inside Models/Data folder
							tempSym.set("Models//Data//"+tempSym.externalFile + "::"
									+ tempSym.excelFileRange);

						}
					} else {
						//for non excel files
						tempSym.set("Models//Data//"+tempSym.externalFile);
						
					}
				} else {
					System.err.println("Error user missed excel file");
					JOptionPane.showMessageDialog(new JFrame(), "Please enter external file for variable : "+tempSym.name);
					return;
				}
				ivList.add(tempSym);
				list.add(tempSym);
			} else if ("Randomized".equals((String) rowData.get(2))) {
				System.out.println("Found Randomized Symbol : "
						+ rowData.get(0));
				System.out.println("Value : " + rowData.get(6));

				
				boolean isValid = textParser.validate((String) rowData
						.get(6));
				if (isValid) {
					
					//saving the array declaration parameters
					if(((String) rowData.get(1)).trim().startsWith("array")){
						System.out.println("Found randomized array : "+tempSym.name);
						//found an array
						tempSym.isArray=true;
						List<String> arrayParams = parseArrayParam((String) rowData.get(1));
						tempSym.arrayParams=arrayParams;
						System.out.println("Param list : ");
						for(Object obj:tempSym.arrayParams){
							//check if any of the param is symbol
							if(hashTable.containsKey((String)obj)){
								tempSym.isDependentDimension=true;
								depList.add(tempSym);
								break;
							}
							System.out.println((String)obj);
						}
						//add to independent variable list if none of the dimension is based on variable
						if(!tempSym.isDependentDimension){
							ivList.add(tempSym);
						}
						
					}
					Distribution dist = textParser.getDistribution((String) rowData.get(6));
					if("Step".equalsIgnoreCase(dist.getDistribution())){
						System.err.println("Error: User entered step distribution in Randomized variable");
						JOptionPane.showMessageDialog(new JFrame(), "Please enter random distribution only"
													+ " for variable : "+tempSym.name);
						return;
					}
					tempSym.setDistribution(dist);
					tempSym.mode="Randomized";
					//check dependent variables
					List<String> distParamList = dist.getParamList();
					for(String param : distParamList){
						//TODO: if .contains work on Symbol
						if(hashTable.containsKey(param) && !depList.contains(tempSym)){
							tempSym.setDependent(true);
							depList.add(tempSym);
							break;
						}
					}
					//generate apache distribution object for independent vars
					if(!tempSym.isDependent()){
						Object apacheDist = generateDistribution(tempSym);
						tempSym.setApacheDist(apacheDist);
						if("StepDistribution".equals(apacheDist.getClass().getSimpleName())){
							stepVarCount++;
							StepDistribution stepDist = (StepDistribution)apacheDist;
							stepCombinations*=stepDist.getStepCount();
							
							tempSym.isStepDist = true;
							tempSym.setStepDist(stepDist);
							stepList.add(tempSym);
						} else if(!ivList.contains(tempSym) && !tempSym.isDependentDimension){
					    	ivList.add(tempSym);
						}
					}
					list.add(tempSym);
				} else {
					System.err.println("Error: User entered unknown distribution for randomized variable");
					JOptionPane.showMessageDialog(new JFrame(), "Please enter random distribution only"
												+ " for variable : "+tempSym.name);
					return;
				}
			} else if("Step".equals((String) rowData.get(2))) {
				System.out.println("Found Step Symbol : "
						+ rowData.get(0));
				System.out.println("Value : " + rowData.get(6));
				Distribution dist = textParser.getStepDistribution((String) rowData.get(6));
				if(dist==null || !"Step".equalsIgnoreCase(dist.getDistribution())){
					System.err.println("Error: User entered unknown distribution for step variable");
					JOptionPane.showMessageDialog(new JFrame(), "Please enter random distribution only"
												+ " for variable : "+tempSym.name);
					return;
				}
				
				boolean isValid = textParser.validateStepDist((String) rowData
						.get(6));
				if (isValid) {
					
					//saving the array declaration parameters
					if(((String) rowData.get(1)).trim().startsWith("array")){
						//TODO: if this can work for step arrays
						System.out.println("Found step array : "+tempSym.name);
						//found an array
						tempSym.isArray=true;
						List<String> arrayParams = parseArrayParam((String) rowData.get(1));
						tempSym.arrayParams=arrayParams;
						System.out.println("Param list : ");
						for(Object obj:tempSym.arrayParams){
							//check if any of the param is symbol
							if(hashTable.containsKey((String)obj)){
								tempSym.isDependentDimension=true;
								depList.add(tempSym);
								break;
							}
							System.out.println((String)obj);
						}
						//add to independent variable list if none of the dimension is based on variable
						if(!tempSym.isDependentDimension){
							ivList.add(tempSym);
						}
						
					}
					tempSym.setDistribution(dist);
					tempSym.mode="Randomized";
					//check dependent variables
					List<String> distParamList = dist.getParamList();
					for(String param : distParamList){
						if(hashTable.containsKey(param) && !depList.contains(tempSym)){
							tempSym.setDependent(true);
							depList.add(tempSym);
							break;
						}
					}
					//generate apache distribution object for independent vars
					if(!tempSym.isDependent()){
						Object apacheDist = generateDistribution(tempSym);
						tempSym.setApacheDist(apacheDist);
						//dual safe check
						if("StepDistribution".equals(apacheDist.getClass().getSimpleName())){
							stepVarCount++;
							StepDistribution stepDist = (StepDistribution)apacheDist;
							stepCombinations*=stepDist.getStepCount();
							
							tempSym.isStepDist = true;
							tempSym.setStepDist(stepDist);
							stepList.add(tempSym);
						} else if(!ivList.contains(tempSym) && !tempSym.isDependentDimension){
					    	ivList.add(tempSym);
						}
					}
					list.add(tempSym);
				} else {
					System.err.println("Error: User entered unknown distribution for randomized variable");
					JOptionPane.showMessageDialog(new JFrame(), "Please enter random distribution only"
												+ " for variable : "+tempSym.name);
					return;
				}
			} 
			//add symbol to trackedSymbol list for output tracking
			if(tempSym!=null && rowData.get(7) != null && (boolean)rowData.get(7)){
				trackedSymbols.add(tempSym);
			}
				
			
		}
		
		System.out.println("Total step distribution variables ="+stepVarCount);
		System.out.println("Total step combinations ="+stepCombinations);
		System.out.println("====STEP VARIABLES====");
		for(Symbol sym: stepList){
			System.out.println(sym.name);
		}
		
		//resolve dependencies and generate random distributions object
		//list for an instance of execution
				
		HashMap<String,Integer> map = new HashMap<String, Integer>();
		for(int i=0; i<stepList.size();i++){
			map.put(stepList.get(i).name, getSteppingCount(stepList, i));
			System.out.println(stepList.get(i).name+ " has stepping count ="+map.get(stepList.get(i).name));
		}
		int repeatitions=0;
		try{
			repeatitions = Integer.parseInt(repeatCount.getText());
		} catch(NumberFormatException e){
			System.err.println("Invalid repeat count");
			JOptionPane.showMessageDialog(new JFrame(),
					"Please enter integer value for repeat count");
			return;
		}
		//generate instances
		
		showProgressBar();
		
		final long totalIterations = repeatitions*stepCombinations;
		final long repeatitionsFinal = repeatitions;
	    final long combinations = stepCombinations;
	    
	    SwingWorker<Void,Void> executionTask = new SwingWorker<Void, Void>(){

			@Override
			protected Void doInBackground() throws Exception {
				
				long itNum=1;
				int itCount=1;
				
				System.out.println("Total iterations: "+totalIterations);
				// TODO Auto-generated method stub
				for(int c=1;c<=repeatitionsFinal;c++){
					for(int i=1; i<= combinations;i++){
						
						setProgress((int)(itNum*100/totalIterations));
						//step variables first
						for(Symbol sym: stepList){ 
							if(map.get(sym.name)==1 || i%map.get(sym.name)-1==0 || i==1){
								sym.set(sym.getStepDist().sample());
								hashTable.put(sym.name, sym);
							}
							//System.out.println(sym.name+" = "+sym.get());
							
						}
						//independent randomized variables
						for(Symbol sym: ivList){
							if(sym.mode.equals("Randomized")){
								Object distObj=sym.getApacheDist();
								switch (sym.getApacheDist().getClass().getSimpleName()) {
									case "UniformIntegerDistribution":
				
									//case "GeometricDistribution" :
										
								//	case "BinomialDistribution"://not implemented yet
										IntegerDistribution intDist =  (IntegerDistribution)distObj;
										if(sym.isArray){
											//generate Stringified array
											String val = generateIndependentArray(sym, intDist);
											sym.set(val);
											hashTable.put(sym.name,sym);
											
										}
										
										break;
										
									
										
									case "LogisticDistribution":
				
									case "UniformRealDistribution":
				
									case "ExponentialDistribution":
				
										
									case "GammaDistribution":
				
									case "NormalDistribution":
										RealDistribution realDist =  (RealDistribution)distObj;
										if(sym.isArray){
											//generate Stringified array
											String val = generateIndependentArray(sym, realDist);
											sym.set(val);
											hashTable.put(sym.name, sym);
											
										}
										
										break;
				
									default:
										
											System.err.println("Unknown distribution");
											JOptionPane.showMessageDialog(new JFrame(), "Error occurred : Unknown distribution");
											return null;
								}
							
							}
							//System.out.println(sym.name+" = "+sym.get());
							//other types of independent variable already have values
						}
				            
			        
						
						for(Symbol sym: depList){
							
							if(sym.mode!=null && "Manual".equals(sym.mode)){
								//value depends on some other value
								String ref = (String)sym.get();
								Object val = (Object) hashTable.get(ref);
								sym.set(val);
								hashTable.put(sym.name, sym);
								
							} else if (sym.mode!=null && "Randomized".equals(sym.mode)){
								
								Object distObj=null;
								//when a random distribution depends on another variable
								Distribution dist = sym.getDistribution();
								StringBuilder sb =  new StringBuilder();
								sb.append(dist.getDistribution());
								sb.append("(");
								for(String s : dist.getParamList()){
									//replacing a dependency by actual value of that variable
									if(hashTable.containsKey(s)){
										Symbol val = (Symbol) hashTable.get(s);
										sb.append((String)val.get());
									} else{
										//this param is a number itself
										sb.append(s);
									}
									sb.append(",");
								}
								//check if param list length = 0
								if( dist.getParamList()!=null && dist.getParamList().size()>=1){
									sb.deleteCharAt(sb.length()-1);
								}
								sb.append(")");
								if(sym.typeString!=null && sym.typeString.contains("integer")){
									try{
										distObj = textParser.parseText(sb.toString(), TextParser.INTEGER);
									
										sym.setApacheDist(distObj);
									}catch(Exception e){
										System.err.println("Exception occured when trying to get Random distribution for variable"+sym.name+"\n"+e.getMessage());
											e.printStackTrace();
										JOptionPane.showMessageDialog(new JFrame(), "Error occurred : "+e.getMessage());
										return null;
									}
								} else {
									try{
										distObj = textParser.parseText(sb.toString(), TextParser.REAL);
										sym.setApacheDist(distObj);
									}catch(Exception e){
										System.err.println("Exception occured when trying to get Random distribution for variable"+sym.name+"\n"+e.getMessage());
											e.printStackTrace();
										JOptionPane.showMessageDialog(new JFrame(), "Error occurred : "+e.getMessage());
										return null;
									}
								}
								//generation of actual apache distribution objects
								
								switch (distObj.getClass().getSimpleName()) {
									case "UniformIntegerDistribution":
										
									//case "GeometricDistribution" :
				
									//case "BinomialDistribution":
										IntegerDistribution intDist =  (IntegerDistribution)distObj;
										if(sym.isArray){
											//generate Stringified array
											String val = generateDependentArray(sym, intDist);
											sym.set(val);
											hashTable.put(sym.name, sym);
											
										}
										break;
				
								
										
									case "LogisticDistribution":
									
									case "UniformRealDistribution":
				
									case "ExponentialDistribution":
				
									case "GammaDistribution":
				
									case "NormalDistribution":
										RealDistribution realDist =  (RealDistribution)distObj;
										if(sym.isArray){
											//generate Stringified array
											String val = generateDependentArray(sym, realDist);
											sym.set(val);
											hashTable.put(sym.name, sym);
											
										}
										break;
				
									default:
										
										System.err.println("Unknown distribution");
										JOptionPane.showMessageDialog(new JFrame(), "Error occurred : Unknown distribution");
								}
							}
								
			
							//System.out.println(sym.name+" = "+sym.get());
							
							
						}
						ArrayList<Symbol> instanceList = new ArrayList<Symbol>();
						instanceList.addAll(stepList);
						instanceList.addAll(ivList);
						instanceList.addAll(depList);
						System.out.println("=======ITERATION "+itCount+++"=======");
						System.out.println("=======instanceList.size ="+instanceList.size()+" =======");
						for(Symbol sym: instanceList){
							System.out.println(sym.name+" = "+sym.get());
						}
						//runModel here
						try {
							
							//TODO anshul: pass output variables to be written to excel
							System.out.println("Tracked output symbols");
							for(Symbol sym: trackedSymbols){
								System.out.println(sym.name);
							}
							HashMap<String,OutputParams> l = parser.changeModel(instanceList, trackedSymbols,itNum);
							
							if(l==null){
								compileSuccess = false;
								break;
							}
						} catch (/*XPRMCompileException | */XPRMLicenseError | IOException e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(new JFrame(), "Error occurred : "+e.getMessage());
							compileSuccess = false;
							break;
						}
						itNum++;
					}
				}
				this.notifyAll();
				done();
				return null;
			}
			
			@Override
			protected void done() {
				
				super.done();
				//check if compilation was successful
				if(compileSuccess){
					ModelSaver.saveModelResult();
					visPanel.setTrackedVariables(trackedSymbols);
					mapVisPanel.setTrackedVariables(trackedSymbols);
					JOptionPane.showMessageDialog(new JFrame("Success"), "Model execution completed.\nOutput written to excel file : "+outputFile);
					
				} else {
					//Error popup should have been shown by ModelParser
					System.err.println("There was an error while running the model.");
					return;
				}
				if(progressFrame!=null){
					progressFrame.dispose();
				}
			}
	    	
	    };
	    
	    
	    executionTask.addPropertyChangeListener(new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if ("progress" == evt.getPropertyName()) {
		            int progress = (Integer) evt.getNewValue();
		            if(pbar!=null)
		            	pbar.setValue(progress);
		            if(taskOutput!=null)
		            	taskOutput.append(String.format(
		                    "Completed %d%% of task.\n", executionTask.getProgress()));
		        } 
			}
		});
	    executionTask.execute();
		
		
		
	}

	/**
	 * Shows a window with execution progress and logs the percent completion
	 */
	private void showProgressBar() {
		pbar = new JProgressBar();
		pbar.setMinimum(MY_MINIMUM);
	    pbar.setMaximum(MY_MAXIMUM);
	    pbar.setValue(0);
	    pbar.setStringPainted(true);
	  

        taskOutput = new JTextArea(5, 20);
        taskOutput.setMargin(new Insets(5,5,5,5));
        taskOutput.setEditable(false);

        
        progressFrame = new JFrame("Running test instances");
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setOpaque(true);
        progressFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	
        progressFrame.setContentPane(rootPanel);
	    

        JPanel panel = new JPanel();
        panel.add(pbar);

        rootPanel.add(panel, BorderLayout.PAGE_START);
        rootPanel.add(new JScrollPane(taskOutput), BorderLayout.CENTER);
        rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        progressFrame.pack();
        // make the frame half the height and width
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int height = screenSize.height;
        int width = screenSize.width;
        progressFrame.setSize(width/2, height/2);

        // here's the part where i center the jframe on screen
        progressFrame.setLocationRelativeTo(null);
        progressFrame.setVisible(true);
		
		
	}
	



	private String generateDependentArray(Symbol sym,
			IntegerDistribution intDist) {
		ArrayList<Integer> paramValList = new ArrayList<Integer>();
		for(Object param:sym.arrayParams){
			//extract the dimension
			param = (String) param;
			try{
				if(hashTable.containsKey(param)){
					Symbol depSym = (Symbol) hashTable.get(param);
					String typeString = depSym.typeString;
					
					
					if("Integer".equalsIgnoreCase(typeString.trim())){
						//can only allow allow one more level of dependency
						//varA Integer varB
						//ourSym array(varA,varC)
						Object strVal = depSym.get();
						if(hashTable.containsKey(strVal)){
							Symbol s = (Symbol) hashTable.get(strVal);
							Integer val = (Integer)s.get();
							paramValList.add(val);
						} else{
							Integer val = (Integer)strVal;
							paramValList.add(val);
						}
						
					} else if ("Set".equalsIgnoreCase(typeString.trim())){
						String strVal = (String)depSym.get();
						if (strVal.contains("..")){
							//1..varA
							//take the upper limit
							String sizeVar = strVal.split("\\.\\.")[1];
							if(hashTable.containsKey(sizeVar)){
								Symbol s = (Symbol) hashTable.get(sizeVar);
								//only take the value of this var
								//not more than 2 level of dependency
								Integer val = (Integer)s.get();
								paramValList.add(val);
							}else {
								//1..12
								Integer val = Integer.parseInt((String)sizeVar);
								paramValList.add(val);
							}
						} else {
							//can only parse simple set size
							//{A,B,C,D}
							//{A
							//B
							//C
							//D}
							//we can get size by splitting using comma ","
							String strVal2 = (String)depSym.get();
							Integer val = strVal2.split(",").length;
							paramValList.add(val);
						}
					}
				}
				//for constant integers
				//ourSym array(varA,5)
				Integer val = Integer.parseInt((String)param);
				paramValList.add(val);
			} catch(IllegalArgumentException e){
				e.printStackTrace();
				//JOptionPane.showMessageDialog(this,"Check dimensions of variable "+sym.name, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		long arraySize = 1;
		StringBuilder sb= new StringBuilder();
		for(Integer val:paramValList){
//			System.out.println("UpdatedParam : "+val);
			arraySize*=val;
		}
		sb.append("[");
		for(long r=1;r<=arraySize;r++){
			String val = Integer.toString(intDist.sample());
			sb.append(val);
			sb.append(", ");
			
		}
		//remove the last ","
		sb.replace(sb.length()-2, sb.length(), "");
		sb.append("]");
//		System.out.println("Generated Array: "+ sym.name);
//		System.out.println(sb.toString());
		return sb.toString();
	}
	
	private String generateDependentArray(Symbol sym,
			RealDistribution realDist) {
		ArrayList<Integer> paramValList = new ArrayList<Integer>();
		for(Object param:sym.arrayParams){
			//extract the dimension
			param = (String) param;
			System.out.println("Param name ="+param);
			try{
				if(hashTable.containsKey(param)){
					Symbol depSym = (Symbol) hashTable.get(param);
					String typeString = depSym.typeString;
					
					
					if("Integer".equalsIgnoreCase(typeString.trim())){
						Object strVal = depSym.get();
						//can only allow allow one more level of dependency
						//varA Integer varB
						//ourSym array(varA,varC)
						if(hashTable.containsKey(strVal)){
							Symbol s = (Symbol) hashTable.get(strVal);
							Integer val = (Integer)s.get();
							paramValList.add(val);
						} else{
							Integer val = (Integer)strVal;
							paramValList.add(val);
						}
						
					} else if ("Set".equalsIgnoreCase(typeString.trim())){
						String strVal = (String)depSym.get();
						if (strVal.contains("..")){
							//1..varA
							//take the upper limit
							String sizeVar = strVal.split("\\.\\.")[1];
							if(hashTable.containsKey(sizeVar)){
								Symbol s = (Symbol) hashTable.get(sizeVar);
								//only take the value of this var
								//not more than 2 level of dependency
								Integer val = (Integer)s.get();
								paramValList.add(val);
							}else {
								//1..12
								Integer val = Integer.parseInt((String)sizeVar);
								paramValList.add(val);
							}
						} else {
							//can only parse simple set size
							//{A,B,C,D}
							//{A
							//B
							//C
							//D}
							//we can get size by splitting using comma ","
							String strVal2 = (String)depSym.get();
							Integer val = strVal2.split(",").length;
							paramValList.add(val);
						}
					}
				} else {
					//for constant integers
					//ourSym array(varA,5)
					Integer val = Integer.parseInt((String)param);
					paramValList.add(val);
				}
			} catch(IllegalArgumentException e){
				e.printStackTrace();
				//JOptionPane.showMessageDialog(this,"Check dimensions of variable "+sym.name, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		long arraySize = 1;
		StringBuilder sb= new StringBuilder();
		for(Integer val:paramValList){
//			System.out.println("UpdatedParam : "+val);
			arraySize*=val;
		}
		sb.append("[");
		for(long r=1;r<=arraySize;r++){	
			DecimalFormat df = new DecimalFormat("#.##"); 
			df.setRoundingMode(RoundingMode.CEILING); 
			String val = df.format(realDist.sample());
			sb.append(val);
			sb.append(", ");
			
		}
		//remove the last ","
		sb.replace(sb.length()-2, sb.length(), "");
		sb.append("]");
//		System.out.println("Generated Array: "+ sym.name);
//		System.out.println(sb.toString());
		return sb.toString();
	}

	private String generateIndependentArray(Symbol sym,
			IntegerDistribution intDist) {
		ArrayList<Integer> paramValList = new ArrayList<Integer>();
		for(Object param:sym.arrayParams){
		//extract the dimension
		param = (String) param;
		try{
				Integer val = Integer.parseInt((String)param);
				paramValList.add(val);
			} catch(IllegalArgumentException e){
				e.printStackTrace();
//				JOptionPane.showMessageDialog(this,"Check dimension of variable "+sym.name, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		long arraySize = 1;
		StringBuilder sb= new StringBuilder();
		for(Integer val:paramValList){
//			System.out.println("UpdatedParam : "+val);
			arraySize*=val;
		}
		sb.append("[");
		for(long r=1;r<=arraySize;r++){
			String val = Integer.toString(intDist.sample());
			sb.append(val);
			sb.append(", ");
			
		}
		//remove the last ","
		sb.replace(sb.length()-2, sb.length(), "");
		sb.append("]");
//		System.out.println("Generated Array: "+ sym.name);
//		System.out.println(sb.toString());
		return sb.toString();
	}

	/**
	 * @param sym
	 * @param realDist
	 */
	private String generateIndependentArray(Symbol sym, RealDistribution realDist) {
		ArrayList<Integer> paramValList = new ArrayList<Integer>();
		for(Object param:sym.arrayParams){
		//extract the dimension
		param = (String) param;
		try{
				Integer val = Integer.parseInt((String)param);
				paramValList.add(val);
			} catch(IllegalArgumentException e){
				e.printStackTrace();
				//JOptionPane.showMessageDialog(this,"Check dimension of variable "+sym.name, "Error", JOptionPane.ERROR_MESSAGE);
				return null;
			}
		}
		
		long arraySize = 1;
		StringBuilder sb= new StringBuilder();
		for(Integer val:paramValList){
//			System.out.println("UpdatedParam : "+val);
			arraySize*=val;
		}
		sb.append("[");
		for(long r=1;r<=arraySize;r++){
			DecimalFormat df = new DecimalFormat("#.##"); 
			df.setRoundingMode(RoundingMode.CEILING); 
			String val = df.format(realDist.sample());
			sb.append(val);
			sb.append(", ");
			
		}
		//remove the last ","
		sb.replace(sb.length()-2, sb.length(), "");
		sb.append("]");
//		System.out.println("Generated Array: "+ sym.name);
//		System.out.println(sb.toString());
		return sb.toString();
	}
	
	private List<String> parseArrayParam(String datatypeString) {
		// TODO Auto-generated method stub
		List<String> arrayParams = new ArrayList<String>();
		String[] tokens = datatypeString.split("\\(");
		String params = tokens[1].trim();
		if(!params.startsWith("\\)")){
			params = params.split("\\)")[0];
			if(params!=null && !params.isEmpty()){
				tokens = params.split(",");
				if(tokens!=null){
					arrayParams= ((List<String>) Arrays.asList(tokens));
				}
			
			} else {
				//only a single dimension
				arrayParams.add(params.trim());
			}
			
		} 
		return arrayParams;
	}

	
	private Object generateDistribution(Symbol sym){
		StringBuilder sb =  new StringBuilder();
		//var for custom distribution class for simplicity
		Distribution dist = sym.getDistribution();
		//var for apache distribution
		Object distObj = null;
		sb.append(dist.getDistribution());
		sb.append("(");
		for(String s : dist.getParamList()){
			sb.append(s);
			sb.append(",");
		}
		//check if param list length = 0
		//removing comma (,) after last parameter
		if( dist.getParamList()!=null && dist.getParamList().size()>=1){
			sb.deleteCharAt(sb.length()-1);
		}
		sb.append(")");
		TextParser textParser = new TextParser();
		if(sym.typeString!=null && sym.typeString.contains("integer")){
			try{
				distObj = textParser.parseText(sb.toString(), TextParser.INTEGER);
				//TODO: save this in symbol for later sampling
				
			}catch(Exception e){
				System.err.println("Exception occured when trying to get Random distribution for variable"+sym.name+"\n"+e.getMessage());
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "Error occurred : "+e.getMessage());
			}
		} else {
			try{
				distObj = textParser.parseText(sb.toString(), TextParser.REAL);
				//TODO: save this in symbol for later sampling
			}catch(Exception e){
				System.err.println("Exception occured when trying to get Random distribution for variable"+sym.name+"\n"+e.getMessage());
				e.printStackTrace();
				JOptionPane.showMessageDialog(new JFrame(), "Error occurred : "+e.getMessage());
			}
		}
		return distObj;
	}

	public void initParamList(ArrayList<Symbol> paramList, ModelParser parser) {
		// TODO Auto-generated method stub
		this.paramList = paramList;
		// Create and set up the content pane.
		this.parser = parser;

		if (model == null) {
			model = new ParameterModel();
		}
		//clear data just in case we are calling this for loading saved config
		model.clearData();
		hashTable = new HashMap<String,Symbol>();
		for (Symbol param : paramList) {
			hashTable.put(param.name, param);
			ArrayList<Object> rowData = new ArrayList<Object>();
			//System.out.println("Adding param:" + param.name);
			rowData.add(param.name);
			
			//System.out.println("Adding type:" + param.typeString);
			String type = param.typeString;
			rowData.add( type!=null?type:"");
			
			String mode = param.mode;
			rowData.add(mode!=null && !mode.isEmpty()?mode:"Default");
			
			//System.out.println("Adding value:" + param.get());
			Object val = param.get();
			rowData.add( val!=null?val.toString():"");
			
			//System.out.println("Adding externalFile:" + param.externalFile);
			// Imported from file
			String str1 = param.externalFile;
			rowData.add( str1!=null?str1:"");
			
			//System.out.println("Adding excelFileRange:" + param.excelFileRange);			
			// Imported from range for excel files
			String str2 = param.excelFileRange;
			rowData.add( str2!=null?str2:"");
			
			// Distribution function
			String distFunction = param.distFunction;
			rowData.add(distFunction!=null?distFunction:"");
			
			rowData.add(param.trackingEnabled);
			model.addRow(rowData);

		}
		if (tableUI == null) {
			System.out.println("Table initializing");
			tableUI = new TableUI(model);
			tableUI.setOpaque(true); // content panes must be opaque
			add(tableUI, BorderLayout.CENTER);
		} else {
			tableUI.invalidate();
			System.out.println("Table re-initializing");
		}

	}

	public void setImportedFile(File importedFile) {
		// TODO Auto-generated method stub
		this.importedFile=importedFile;
	}
	
   
	
	private int getSteppingCount(ArrayList<Symbol> list, int index){
		int product = 1;
		for(int i=index+1; i<list.size();i++){
			product*=list.get(i).getStepDist().getStepCount();
		}
		return product;
	}

	public void setVisualizationPanel(
			SetupVisualizationPanel setupVisualizationPanel) {
		visPanel = setupVisualizationPanel;
		
	}

	public void setMapVisualizationPanel(
			SetupMapVisualizationPanel setupMapVisualizationPanel) {
		mapVisPanel = setupMapVisualizationPanel;
	}
}
