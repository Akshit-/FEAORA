package pcgui;
import java.util.ArrayList;
import java.util.Arrays;
/**
 * contains various constants used in application
 * @author Anshul Vij
 *
 */
public class Constants {
	
	//panel ids
	public static final int LOGIN_PANEL_ID = 1001;
	public static final int SETUP_PIN_PANEL_ID = 1002;
	public static final int FORGOT_PIN_PANEL_ID = 1003;
	public static final int RESET_PIN_PANEL_ID = 1004;
	public static final int DEVICE_LIST_PANEL_ID = 1005;
	public static final int SETTINGS_PANEL_ID = 1006;
	public static final int IMPORT_MODEL_PANEL_ID = 1007;
	public static final int SETUP_PARAM_PANEL_ID = 1008;

	//preferences
	public static final String SETUP_DONE = "SETUP_DONE";
	public static final String LOGGED_IN = "LOGGED_IN";
	public static final String PIN = "PIN";
	public static final String SECRET_QUESTION = "SECRET_QUESTION";
	public static final String SECRET_ANSWER = "SECRET_ANSWER";
	public static final String DISCOVERY_SEND_PORT = "DISCOVERY_SEND_PORT";
	public static final String DISCOVERY_RECEIVE_PORT = "DISCOVERY_RECEIVE_PORT";
	public static final String FILE_TRANSFER_PORT = "FILE_TRANSFER_PORT";
	public static final String NOTES_TRANSFER_PORT = "NOTES_TRANSFER_PORT";
	public static final String SCREENSHOT_TRANSFER_PORT = "SCREENSHOT_TRANSFER_PORT";
	public static final String COMMAND_RECEIVER_PORT = "COMMAND_RECEIVER_PORT";
	public static final String ENABLE_COMMAND_LISTENER = "ENABLE_COMMAND_LISTENER";
	public static final String ENABLE_FILE_TRANSFER = "ENABLE_FILE_TRANSFER";
	
	//Error messages
	public static final String ERRMSG_PIN_MISSING = "Pin and confirm Pin cannot be empty!";
	public static final String ERRMSG_PIN_WRONG = "Pin is wrong!";
	public static final String ERRMSG_PIN_MISMATCH = "Pins do not match!";
	public static final String ERRMSG_SEC_QUES_EMPTY = "Please Enter Security question!";
	public static final String ERRMSG_SEC_ANS_EMPTY = "Please Enter Security Answer!";
	public static final String ERRMSG_ANS_WRONG = "Security Answer entered is wrong!";
    public static final String NEW_PIN_SUCC_MSG = "Pin changed successfully!";
    
    //DEBUG variables
    public static final boolean DEBUG = false;
    
//    public static  enum DISTRIBUTIONS_ENUM = {
//			UNIFORM,
//			NORMAL,
//			BINOMIAL,
//			GAMMA,
//			EXPONENTIAL,
//			LOGISTIC,
//			GEOMETRIC,
//			STEP,
//    };
    
	
}
