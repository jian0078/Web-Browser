/**
 * File name: MyJavaFXBrowser.java
 * Author: Yi Jiang
 * Course: CST8284-OOP
 * Section: 300
 * Assignment: 2
 * Date: Jan. 10, 2018
 * Professor: DAVID B HOUTMAN
 * Purpose: Create A JavaFX Web Browser
 * Class list: MyJavaFXBrowser, Menus, FileUtils, WebPage
 * Remark: This assignment2 base on Prof. Dave Houtman's assignment1 code, ©2017
 */

package assignment2;



import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

/**This is public class WebPage  */
public class WebPage {

	private WebView webview = new WebView();
	private WebEngine engine;

	// public WebEngine createWebEngine(Stage stage) {
	public WebEngine createWebEngine(Stage stage) {
		WebView wv = getWebView();
		engine = wv.getEngine();

		engine.getLoadWorker().stateProperty().addListener(new ChangeListener<State>() {
			@Override
			public void changed(ObservableValue<? extends State> ov, State oldState, State newState) {
				if (newState == Worker.State.RUNNING) {

					stage.setTitle(engine.getLocation());
				}

			}
		});

		return engine;
	}
	
	
/**
 * This is getWebView method
 * @return webview
 */
	public WebView getWebView() {
		return webview;
	}

	/**
	 * This is getWebEngine method
	 * @return engine
	 */
	public WebEngine getWebEngine() {
		return engine;
	}
}
