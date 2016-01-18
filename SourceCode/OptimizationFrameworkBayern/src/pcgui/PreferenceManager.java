package pcgui;

import java.util.prefs.Preferences;

public class PreferenceManager {
	
	private Preferences pref;
	private static PreferenceManager prefMgr;
	
	private PreferenceManager(){
		pref = Preferences.userRoot().node(this.getClass().getName());
	}
	public static PreferenceManager getInstance(){
		if(prefMgr==null)
			prefMgr = new PreferenceManager();
		return prefMgr;
	}
	
	public void putString(String key, String value){
		pref.put(key, value);
	}
	
	public String getString(String key,String def){
		return pref.get(key, def);
	}
	
	public void putInt(String key, int value){
		pref.putInt(key, value);
	}
	
	public int getInt(String key, int def){
		return pref.getInt(key, def);
	}
	
	public void putFloat(String key, float value){
		pref.putFloat(key, value);
	}
	
	public float getFloat(String key, float def){
		return pref.getFloat(key, def);
	}

	public void putDouble(String key, double value){
		pref.putDouble(key, value);
	}
	
	public double getDouble(String key, double def){
		return pref.getDouble(key, def);
	}

	public void putBoolean(String key, boolean value){
		pref.putBoolean(key, value);
	}
	
	public boolean getBoolean(String key, boolean def){
		return pref.getBoolean(key, def);
	}
	
	public void remove(String key){
		pref.remove(key);
	} 

}
