package pcgui;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Font;
import java.awt.Frame;

import javax.swing.SwingConstants;
import javax.swing.JButton;
import javax.swing.filechooser.FileFilter;

import com.dashoptimization.XPRMCompileException;
import com.dashoptimization.XPRMLicenseError;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import javax.swing.SpringLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.factories.FormFactory;
import com.jgoodies.forms.layout.RowSpec;

public class ImportModelPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JFileChooser jFileChooser;
	private JLabel lblSelectedFile;
	private ArrayList<Symbol> paramList;
	private JButton btnNext;
	
	private PanelSwitcher switcher;
	private ParameterListReceiver paramListReceiver;

	public ImportModelPanel(final PanelSwitcher switcher,final ParameterListReceiver listReceiver) {
		this.switcher = switcher;
		paramListReceiver = listReceiver;
		initialize();

	}
	
	public void initialize() {
						FormLayout formLayout = new FormLayout(new ColumnSpec[] {
								ColumnSpec.decode("default:grow"),
								ColumnSpec.decode("107px"),
								FormFactory.LABEL_COMPONENT_GAP_COLSPEC,
								ColumnSpec.decode("235px"),
								ColumnSpec.decode("default:grow"),},
							new RowSpec[] {
								FormFactory.RELATED_GAP_ROWSPEC,
								RowSpec.decode("20px"),
								RowSpec.decode("46px"),
								RowSpec.decode("29px"),
								RowSpec.decode("25dlu"),
								RowSpec.decode("29px"),});
						formLayout.setHonorsVisibility(false);
						setLayout(formLayout);
				
						JLabel lblNewLabel_1 = new JLabel("Import Model");
						lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
						add(lblNewLabel_1, "4, 2, left, center");
						
								
						
								JButton btnSelectFile = new JButton("Select File");
								btnSelectFile.addActionListener(new ActionListener() {
									public void actionPerformed(ActionEvent e) {
										jFileChooser = new JFileChooser();
										
										File workingDirectory = new File(System.getProperty("user.dir"));
										
										jFileChooser.setCurrentDirectory(workingDirectory); 
										
										jFileChooser.setFileFilter(new FileFilter() {
											
											@Override
											public String getDescription() {
												// TODO Auto-generated method stub
												return "Mosel files";
											}
											
											@Override
											public boolean accept(File f) {
												// TODO Auto-generated method stub
												return f.isDirectory()||f.getName().endsWith(".mos");
												
											}
										});
										int returnVal = jFileChooser.showOpenDialog(null);

										if (returnVal == JFileChooser.APPROVE_OPTION) {
											File file = jFileChooser.getSelectedFile();
											
											// This is where a real application would open the file.


											lblSelectedFile.setText(file.getName());
											if(file.getName().endsWith(".mos")){
												btnNext.setEnabled(true);
											}

										} else {
//					System.out.println("Open command cancelled by user.");
										}
									}
								});
						add(btnSelectFile, "2, 4, left, center");
						lblSelectedFile = new JLabel("No file selected");
						add(lblSelectedFile, "4, 4, fill, center");
				
						btnNext = new JButton("Next");
						btnNext.addActionListener(new ActionListener() {

							public void actionPerformed(ActionEvent e) {
								if(jFileChooser!=null){
									File file = jFileChooser.getSelectedFile();
									
									if(file.getName().endsWith("mos")){
										ModelParser parser = new ModelParser();
										try {
											paramList = parser.modelImport(file);
											
											if(paramList!=null){
												paramListReceiver.onParamListReceived(file,paramList,parser);
												switcher.onPanelSwitched(Constants.SETUP_PARAM_PANEL_ID);
											}
										} catch (XPRMLicenseError e1) {
											// TODO Auto-generated catch block
											System.out.println(" XPRMLicenseError occured : "+e1.getMessage());
											JOptionPane.showMessageDialog(new JFrame(), "XPRMLicenseError occurred : "+e1.getMessage(),"XPRMLicenseError",JOptionPane.ERROR_MESSAGE);
//							e1.printStackTrace();
										} 
//						catch (XPRMCompileException e1) {
//							// TODO Auto-generated catch block
//							System.out.println("XPRMCompileException occured : "+e1.getMessage());
//							JOptionPane.showMessageDialog(new JFrame(), "XPRMCompileException occurred : "+e1.getMessage());
//							e1.printStackTrace();
//						} 
										catch (IOException e1) {
											// TODO Auto-generated catch block
											System.out.println("IOException occured : "+e1.getMessage());
											JOptionPane.showMessageDialog(new JFrame(), "IOException occurred : "+e1.getMessage());
											e1.printStackTrace();
										}
									}
									
								}
							}
						});
						setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{btnSelectFile, lblSelectedFile, lblNewLabel_1, btnNext}));
						add(btnNext, "4, 6, left, center");
	}



}
