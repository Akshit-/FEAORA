package pcgui;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
 
public class CustomErrorDialog {
 
    public static void showDialog(String title, String details) {
        final JTextPane textPane = new JTextPane();
        textPane.setContentType("text/html");
 
        textPane.setText(details);
        textPane.setEditable(false);
 
 
        final JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setAlignmentX(0);
 
        final JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));
         
        final JDialog dialog = new JOptionPane(
                content,
                JOptionPane.ERROR_MESSAGE,
                JOptionPane.DEFAULT_OPTION).createDialog(null, "Error!");
 
        JLabel message = new JLabel(title);
        message.setBorder(new EmptyBorder(10, 10, 10, 10));
        message.setAlignmentX(0);
        Dimension labelSize = message.getPreferredSize();
        labelSize.setSize(300, labelSize.height);
        message.setPreferredSize(labelSize);
        content.add(message);
 
        JCheckBox cb = new JCheckBox(new AbstractAction() {
 
            private static final long serialVersionUID = 1L;
 
            {
                this.putValue(Action.SELECTED_KEY, false);
                this.putValue(Action.NAME, "Show Details");
            }
 
            @Override
            public void actionPerformed(ActionEvent e) {
                if ((Boolean) this.getValue(Action.SELECTED_KEY)) {
                    content.add(scrollPane);
                } else {
                    content.remove(scrollPane);
                }
                content.invalidate();
                dialog.invalidate();
                dialog.pack();
            }
        });
        content.add(cb);
        
        dialog.setResizable(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setVisible(true);
    }
}