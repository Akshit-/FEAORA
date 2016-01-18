package pcgui;

import java.io.File;
import java.util.ArrayList;

public interface ParameterListReceiver {
	public void onParamListReceived(File file, ArrayList<Symbol> list, ModelParser parser);
	
}