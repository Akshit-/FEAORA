package pcgui.output.map;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import javafx.embed.swing.JFXPanel;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.Chart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;

import pcgui.PreferenceManager;
import pcgui.SystemClipboard;

import com.google.code.geocoder.Geocoder;
import com.google.code.geocoder.GeocoderRequestBuilder;
import com.google.code.geocoder.model.GeocodeResponse;
import com.google.code.geocoder.model.GeocoderRequest;
import com.google.code.geocoder.model.GeocoderStatus;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.XsiNilLoader.Array;

import sun.tools.jar.Main;

/**
 * MapUI using JavaFX
 *
 * Note using the browser might require setting the properties - http.proxyHost
 * - http.proxyPort
 *
 * e.g. -Dhttp.proxyHost=webcache.mydomain.com -Dhttp.proxyPort=8080
 * 
 */
public class MapUI extends JFrame {

	private PreferenceManager pref;
	private final int PANEL_WIDTH_INT = 1280;
	private final int PANEL_HEIGHT_INT = 720;
	private static String API_KEY;//not used right now but may be required later on
	private JFXPanel browserFxPanel;

	private Pane browser;
	private String url;

	public MapUI() {
		pref = PreferenceManager.getInstance();
		API_KEY = pref.getString("GEOCODE_API_KEY", "");
	}

	public void init() {

		// create javafx panel for browser
		browserFxPanel = new JFXPanel();

		// create tabbed pane
		JTabbedPane tabbedPane = new JTabbedPane();

		tabbedPane.addTab("Map", browserFxPanel);

		getContentPane().add(tabbedPane, BorderLayout.CENTER);
		Platform.runLater(new Runnable() {

			@Override
			public void run() {
				createScene();
			}
		});

	}

	public static void main(String[] args) {

		MapUI frame = new MapUI();
		frame.setBounds(0, 0, 1280, 720);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.init();
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}

	private void createScene() {

		browser = createBrowser();
		browserFxPanel.setScene(new Scene(browser));
	}

	public static List<GeoCode> getGeoCodes(List<String> locations) {
		List<GeoCode> geoCodes = new ArrayList<GeoCode>();
		for (String location : locations) {
			// String url =
			// "https://maps.googleapis.com/maps/api/geocode/json?address="+location
			// +"&key="+API_KEY;
			// API key not required
			final Geocoder geocoder = new Geocoder();
			//Added this to prevent over_query_limit error
			try {
				Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			GeocoderRequest geocoderRequest = new GeocoderRequestBuilder()
					.setAddress(location).setLanguage("en")
					.getGeocoderRequest();
			try {
				GeocodeResponse geocoderResponse = geocoder
						.geocode(geocoderRequest);
				if (geocoderResponse.getStatus() == GeocoderStatus.OK) {
					/*System.out.println("geocoder result : "
							+ geocoderResponse.toString());*/
					BigDecimal latitude = geocoderResponse.getResults().get(0)
							.getGeometry().getLocation().getLat();
					BigDecimal longitude = geocoderResponse.getResults().get(0)
							.getGeometry().getLocation().getLng();
					GeoCode geoCode = new GeoCode(longitude, latitude, location);
					geoCodes.add(geoCode);
				} else if (geocoderResponse.getStatus() == GeocoderStatus.ERROR) {
					System.err.println("Geocode response ERROR");

				} else {
					System.err.println("#####Geocode response unknown = "+geocoderResponse.getStatus());
					System.out.println("response"+geocoderResponse.toString());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Example geoCode API url
		// https://maps.googleapis.com/maps/api/geocode/json?address=1600+Amphitheatre+Parkway,+Mountain+View,+CA&key=API_KEY
		return geoCodes;
	}

	private Pane createBrowser() {
		Double widthDouble = new Integer(PANEL_WIDTH_INT).doubleValue();
		Double heightDouble = new Integer(PANEL_HEIGHT_INT).doubleValue();
		WebView view = new WebView();
		view.setMinSize(widthDouble, heightDouble);
		view.setPrefSize(widthDouble, heightDouble);
		final WebEngine eng = view.getEngine();
		final Label warningLabel = new Label(
				"Do you need to specify web proxy information?");
		eng.load(url);

		ChangeListener<Number> handler = new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable,
					Number oldValue, Number newValue) {
				if (warningLabel.isVisible()) {
					warningLabel.setVisible(false);
				}
			}
		};
		eng.getLoadWorker().progressProperty().addListener(handler);
		// String url =
		// "file:///Users/anshulvij/idp_code/OptimizationFrameworkBayern//res/map.html?points=44.5,46.64,Delhi;34.6,65.8,Bombay";
		final TextField locationField = new TextField(url);
		locationField.setMaxHeight(Double.MAX_VALUE);
		Button goButton = new Button("Refresh");
		goButton.setDefaultButton(true);
		EventHandler<ActionEvent> goAction = new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				System.out.println("openning  " + locationField.getText());
				eng.load(locationField.getText());

			}
		};
		goButton.setOnAction(goAction);
		locationField.setOnAction(goAction);
		eng.locationProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable,
					String oldValue, String newValue) {
				locationField.setText(newValue);
			}
		});
		GridPane grid = new GridPane();
		grid.setPadding(new Insets(5));
		grid.setVgap(5);
		grid.setHgap(5);
		GridPane.setConstraints(locationField, 0, 0, 1, 1, HPos.CENTER,
				VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES);
		GridPane.setConstraints(goButton, 0, 0, 1, 1, HPos.RIGHT, VPos.CENTER,
				Priority.ALWAYS, Priority.SOMETIMES);
		GridPane.setConstraints(view, 0, 1, 2, 1, HPos.CENTER, VPos.CENTER,
				Priority.ALWAYS, Priority.ALWAYS);
		GridPane.setConstraints(warningLabel, 0, 2, 2, 1, HPos.CENTER,
				VPos.CENTER, Priority.ALWAYS, Priority.SOMETIMES);
		grid.getColumnConstraints().addAll(
				new ColumnConstraints(widthDouble - 200, widthDouble - 200,
						Double.MAX_VALUE, Priority.ALWAYS, HPos.CENTER, true),
				new ColumnConstraints(40, 40, 40, Priority.NEVER, HPos.CENTER,
						true));
		grid.getChildren().addAll(locationField, goButton, warningLabel, view);
		return grid;
	}

	public void openLocalWebpage(String url) {
		this.url = url;
		MapUI.main(null);
	}

	public static void openWebpage(URI uri) {

		System.out.println("URI: " + uri);
		String os = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();

		try {

			if (os.indexOf("win") >= 0) {
				
				System.out.println("url:"+uri.toString());
				//this can be taken from user settings
				String iexplorer = "\"C:\\Program Files (x86)\\Internet Explorer\\iexplore.exe\"";
				// this doesn't support showing urls in the form of
				// "page.html#nameLink"
				String cmd = iexplorer + " " + uri.toString().replace("&", "$");
				System.out.println("Command: "+cmd);
				rt.exec( cmd);
				/*SystemClipboard.copy(uri.toString());
				JOptionPane
						.showMessageDialog(
								new JFrame(),
								"URL copied to your clipboard : "
										+ "\nPlease paste the copied URL in a browser address bar.",
								"Info", JOptionPane.INFORMATION_MESSAGE);
								*/

			} else if (os.indexOf("mac") >= 0) {

				Desktop desktop = Desktop.isDesktopSupported() ? Desktop
						.getDesktop() : null;
				if (desktop != null
						&& desktop.isSupported(Desktop.Action.BROWSE)) {
					try {

						desktop.browse(uri);
						return;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

			}
		} catch (Exception e) {
			System.err.println("Unable to open browser");
			JOptionPane.showMessageDialog(new JFrame(),
					"Unable to open browser : " + e, "Error",
					JOptionPane.ERROR_MESSAGE);
		}
		return;
	}

	public static void openWebpage(URL url) {
		try {
			openWebpage(url.toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
