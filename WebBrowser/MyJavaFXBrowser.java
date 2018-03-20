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

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.stage.Stage;



 /**This is public class MyJavaFXBrowser extends Application */
 
 
public class MyJavaFXBrowser extends Application {

	static BorderPane root = new BorderPane();
	@Override
	public void start(Stage primaryStage) {
		
	    WebPage currentPage = new WebPage();
		WebView webView = currentPage.getWebView();

		root.setTop(Menus.loadTopPanel(webView.getEngine()));
		root.setRight(Menus.loadRightPanel(webView.getEngine()));
		root.setBottom(Menus.loadBottomPanel());
		
		String startupURL = (FileUtils.fileExists("default.web"))?FileUtils.getURLsFromFile("default.web").get(0):"https://www.google.ca/";
		Menus.goToURL(startupURL);
		root.setCenter(webView);
		

		Scene scene = new Scene(root, 800, 500);
		primaryStage.setScene(scene);
		primaryStage.show();	
	}
	
	/**
	 * This is Override stop method
	 */
	@Override
	public void stop() {
		
        
		FileUtils.storeURLsToFile(Menus.getBookmarkURLs(), "bookmarks.web");

	}
	
	
	/**
	 * This is main method to launch the Application
	 * @param args is Array String
	 */
	public static void main(String[] args) {
		Application.launch(args);
	}

}
