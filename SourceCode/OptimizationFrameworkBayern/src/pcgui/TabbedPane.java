package pcgui;
/*
 * TabbedPaneDemo.java requires one additional file:
 *   images/middle.gif.
 */
 
import javax.swing.JTabbedPane;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
 
public class TabbedPane extends JPanel {
	private SetupParametersPanel paramPanel;
	private SetupVisualizationPanel visPanel;
	private SetupMapVisualizationPanel mapVisPanel;
    public TabbedPane(SetupParametersPanel setupParamPanel, SetupVisualizationPanel setupVisualizationPanel, SetupMapVisualizationPanel setupMapVisualizationPanel) {
        super(new GridLayout(1, 1));
        paramPanel = setupParamPanel;
        visPanel = setupVisualizationPanel;
        mapVisPanel = setupMapVisualizationPanel;
        JTabbedPane tabbedPane = new JTabbedPane();
//     
         
        JComponent panel1 = makeTextPanel("Panel #1");
        tabbedPane.addTab("Setup Parameters", null, paramPanel,
                "Setup Input Configuration for Model");
        tabbedPane.setMnemonicAt(0, KeyEvent.VK_1);
         
        JComponent panel2 = makeTextPanel("Panel #2");
        tabbedPane.addTab("Visualization", null, visPanel,
                "Setup visualization configuration for output of model");
        tabbedPane.setMnemonicAt(1, KeyEvent.VK_2);
        
        JComponent panel3 = makeTextPanel("Panel #3");
        tabbedPane.addTab("Map Visualization", null, mapVisPanel,
                "Setup map visualization configuration for output of model");
        tabbedPane.setMnemonicAt(2, KeyEvent.VK_3);
       
         
        //Add the tabbed pane to this panel.
        add(tabbedPane);
         
        //The following line enables to use scrolling tabs.
        tabbedPane.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        setVisible(true);
    }
     
    protected JComponent makeTextPanel(String text) {
        JPanel panel = new JPanel(false);
        JLabel filler = new JLabel(text);
        filler.setHorizontalAlignment(JLabel.CENTER);
        panel.setLayout(new GridLayout(1, 1));
        panel.add(filler);
        return panel;
    }
     
   
     
    
}