package pcgui;
/**
 * Settings frame used to configure the app
 */
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.text.NumberFormatter;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import java.awt.Color;
import java.awt.Font;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingConstants;
import javax.swing.JCheckBox;

public class SettingsPanel extends JPanel {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2631731418023207897L;

	private JLabel lblErrorMsg;
	private JLabel lblNewLabel;

	private JFormattedTextField txt_dis_tx_port;
	private JFormattedTextField txt_dis_rx_port;
	private JFormattedTextField txt_cmd_list_port;
	private JFormattedTextField txt_file_trans_port;
	private JFormattedTextField txt_screen_trans_port;
	private JFormattedTextField txt_notes_trans_port;
	
	private JLabel lblDiscTxPort;
	private JLabel lblDiscRxPort;
	private JLabel lblCmdLisPort;
	private JLabel lblFileTransPort;
	private JLabel lblScreenTransPort;
	private JLabel lblNotesTransferPort;

	private JCheckBox chckbxEnCmdListener;
	private JButton btnApply;
	
	private PreferenceManager prefMgr;
	
	private List<SettingsChangedListener> settingsChangedListeners;

	private JCheckBox chckbxFileTransfer;

	private JButton btnResetDevice;

	private JLabel lblResetDevice;

	public void addSettingsChangedListener(SettingsChangedListener listener) {
		if (settingsChangedListeners == null) {
			settingsChangedListeners = new ArrayList<SettingsChangedListener>();
		}
		settingsChangedListeners.add(listener);
	}

	public void removeSettingsChangedListener(SettingsChangedListener listener) {
		if (settingsChangedListeners != null) {
			settingsChangedListeners.remove(listener);
		}
	}

	/**
	 * Create the frame.
	 */

	public SettingsPanel(final PanelSwitcher switcher) {
		setLayout(null);

		prefMgr = PreferenceManager.getInstance();

		NumberFormat format = NumberFormat.getIntegerInstance();
		format.setGroupingUsed(false);
		NumberFormatter formatter = new NumberFormatter(format);
		formatter.setValueClass(Integer.class);
		formatter.setMinimum(1024);
		formatter.setMaximum(65535);
		// formatter.setAllowsInvalid(false); //this is the key!!
		// If you want the value to be committed on each keystroke instead of
		// focus lost
		formatter.setCommitsOnValidEdit(false);

		lblErrorMsg = new JLabel("");
		lblErrorMsg.setForeground(Color.RED);
		lblErrorMsg.setBounds(220, 37, 170, 14);
		add(lblErrorMsg);

		setBounds(100, 100, 640, 480);
		lblErrorMsg.setVisible(false);

		lblDiscTxPort = new JLabel("Discovery Tx Port");
		lblDiscTxPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblDiscTxPort.setBounds(144, 48, 122, 14);
		add(lblDiscTxPort);

		txt_dis_tx_port = new JFormattedTextField(formatter);
		txt_dis_tx_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_dis_tx_port.setHorizontalAlignment(SwingConstants.LEFT);
		txt_dis_tx_port.setBounds(315, 45, 122, 20);
		txt_dis_tx_port.setText(Integer.toString(prefMgr.getInt(
				Constants.DISCOVERY_SEND_PORT, 6000)));
		add(txt_dis_tx_port);

		lblNewLabel = new JLabel("Network Settings");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblNewLabel.setBounds(220, 11, 185, 23);
		add(lblNewLabel);

		txt_dis_rx_port = new JFormattedTextField(formatter);
		txt_dis_rx_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_dis_rx_port.setBounds(315, 76, 122, 20);
		txt_dis_rx_port.setText(Integer.toString(prefMgr.getInt(
				Constants.DISCOVERY_RECEIVE_PORT, 6001)));

		add(txt_dis_rx_port);
		txt_dis_rx_port.setColumns(10);

		lblDiscRxPort = new JLabel("Discovery Rx Port");
		lblDiscRxPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblDiscRxPort.setBounds(144, 76, 138, 20);
		add(lblDiscRxPort);

		chckbxEnCmdListener = new JCheckBox("Enable Command Listener");
		chckbxEnCmdListener.setBounds(144, 245, 293, 20);
		chckbxEnCmdListener.setSelected(prefMgr.getBoolean(
				Constants.ENABLE_COMMAND_LISTENER, false));
		add(chckbxEnCmdListener);
		
		chckbxFileTransfer = new JCheckBox("Enable File Transfer");
		chckbxFileTransfer.setBounds(144, 275, 293, 20);
		chckbxFileTransfer.setSelected(prefMgr.getBoolean(
				Constants.ENABLE_FILE_TRANSFER, false));
		add(chckbxFileTransfer);

		txt_cmd_list_port = new JFormattedTextField(formatter);
		txt_cmd_list_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_cmd_list_port.setBounds(315, 112, 122, 20);
		txt_cmd_list_port.setText(Integer.toString(prefMgr.getInt(
				Constants.COMMAND_RECEIVER_PORT, 6010)));
		add(txt_cmd_list_port);
		txt_cmd_list_port.setColumns(10);

		txt_file_trans_port = new JFormattedTextField(formatter);
		txt_file_trans_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_file_trans_port.setBounds(315, 143, 122, 20);
		txt_file_trans_port.setText(Integer.toString(prefMgr.getInt(
				Constants.FILE_TRANSFER_PORT, 6020)));
		add(txt_file_trans_port);
		txt_file_trans_port.setColumns(10);

		txt_screen_trans_port = new JFormattedTextField(formatter);
		txt_screen_trans_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_screen_trans_port.setBounds(315, 174, 122, 20);
		txt_screen_trans_port.setText(Integer.toString(prefMgr.getInt(
				Constants.SCREENSHOT_TRANSFER_PORT, 6030)));
		add(txt_screen_trans_port);
		txt_screen_trans_port.setColumns(10);

		txt_notes_trans_port = new JFormattedTextField(
				formatter);
		txt_notes_trans_port
				.setToolTipText("Enter port number range between 1024(inlc.) & 65535(incl.)");
		txt_notes_trans_port.setText(Integer.toString(prefMgr.getInt(
				Constants.NOTES_TRANSFER_PORT, 6040)));
		txt_notes_trans_port.setColumns(10);
		txt_notes_trans_port.setBounds(315, 206, 122, 20);
		add(txt_notes_trans_port);

		lblCmdLisPort = new JLabel("Command Listener Port");
		lblCmdLisPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblCmdLisPort.setBounds(144, 115, 171, 14);
		add(lblCmdLisPort);

		lblFileTransPort = new JLabel("File Transfer Port");
		lblFileTransPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblFileTransPort.setBounds(144, 146, 122, 14);
		add(lblFileTransPort);

		lblScreenTransPort = new JLabel("Screenshot Transfer Port");
		lblScreenTransPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblScreenTransPort.setBounds(144, 177, 159, 14);
		add(lblScreenTransPort);

		lblNotesTransferPort = new JLabel("Notes Transfer Port");
		lblNotesTransferPort.setHorizontalAlignment(SwingConstants.LEFT);
		lblNotesTransferPort.setBounds(144, 208, 159, 14);
		add(lblNotesTransferPort);
		
		lblResetDevice = new JLabel("Reset the application");
		lblResetDevice.setHorizontalAlignment(SwingConstants.LEFT);
		lblResetDevice.setBounds(144, 312, 159, 14);
		add(lblResetDevice);
		
		
		btnResetDevice = new JButton("Reset");
		btnResetDevice.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				PreferenceManager prefMgr = PreferenceManager
						.getInstance();
				prefMgr.remove(Constants.SETUP_DONE);
				prefMgr.remove(Constants.SECRET_ANSWER);
				prefMgr.remove(Constants.SECRET_QUESTION);
				prefMgr.remove(Constants.COMMAND_RECEIVER_PORT);
				prefMgr.remove(Constants.FILE_TRANSFER_PORT);
				prefMgr.remove(Constants.DISCOVERY_RECEIVE_PORT);
				prefMgr.remove(Constants.DISCOVERY_SEND_PORT);
				prefMgr.remove(Constants.SCREENSHOT_TRANSFER_PORT);
				prefMgr.remove(Constants.LOGGED_IN);
				prefMgr.remove(Constants.PIN);
				switcher.onPanelSwitched(Constants.SETUP_PIN_PANEL_ID);
			}
		});
		btnResetDevice.setBounds(315, 310, 120, 23);
		add(btnResetDevice);

		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				System.out.println("Apply-->DISCOVERY_SEND_PORT="
						+ txt_dis_tx_port.getText());
				prefMgr.putInt(Constants.DISCOVERY_SEND_PORT,
						Integer.parseInt(txt_dis_tx_port.getText()));

				System.out.println("Apply-->DISCOVERY_RECEIVE_PORT="
						+ txt_dis_rx_port.getText());
				prefMgr.putInt(Constants.DISCOVERY_RECEIVE_PORT,
						Integer.parseInt(txt_dis_rx_port.getText()));

				System.out.println("Apply-->COMMAND_RECEIVER_PORT="
						+ txt_cmd_list_port.getText());
				prefMgr.putInt(Constants.COMMAND_RECEIVER_PORT,
						Integer.parseInt(txt_cmd_list_port.getText()));

				System.out.println("Apply-->FILE_TRANSFER_PORT="
						+ txt_file_trans_port.getText());
				prefMgr.putInt(Constants.FILE_TRANSFER_PORT,
						Integer.parseInt(txt_file_trans_port.getText()));

				System.out.println("Apply-->SCREENSHOT_TRANSFER_PORT="
						+ txt_screen_trans_port.getText());
				prefMgr.putInt(Constants.SCREENSHOT_TRANSFER_PORT,
						Integer.parseInt(txt_screen_trans_port.getText()));

				System.out.println("Apply-->NOTES_TRANSFER_PORT="
						+ txt_notes_trans_port.getText());
				prefMgr.putInt(Constants.NOTES_TRANSFER_PORT,
						Integer.parseInt(txt_notes_trans_port.getText()));

				System.out.println("Apply-->ENABLE_COMMAND_LISTENER="
						+ chckbxEnCmdListener.isSelected());

				prefMgr.putBoolean(Constants.ENABLE_COMMAND_LISTENER,
						chckbxEnCmdListener.isSelected());
				
				System.out.println("Apply-->ENABLE_FILE_TRANSFER="
						+ chckbxFileTransfer.isSelected());

				prefMgr.putBoolean(Constants.ENABLE_FILE_TRANSFER,
						chckbxFileTransfer.isSelected());


				if (settingsChangedListeners != null) {
					for (SettingsChangedListener listener : settingsChangedListeners) {
						listener.onNetworkSettingsChanged();
					}
				}
			}
		});
		btnApply.setBounds(259, 350, 120, 23);
		add(btnApply);

	}
}
