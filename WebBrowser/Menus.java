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

import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Worker.State;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebHistory;
import javafx.scene.web.WebHistory.Entry;


/**** The Menu class */
public class Menus {

	/**** Generic Menu/Menu Item Properties ****/
	private static MenuItem mnuItm;
	private static Menu mnu;

	/********* Address Bar Properties **********/
	private static VBox topPanel = new VBox();
	private static VBox rightPanel = new VBox();
	private static	VBox bottomPanel = new VBox();
	static ListView<WebHistory.Entry> hisList = new ListView<>();
	
	private static HBox hbxAddressBar;
	private static HBox historyButton = new HBox();
	private static TextField txtfldAddress;
	static TextArea sourceView = new TextArea();
	private static Button btnGo;
	private static boolean toggleAddress = false;
	private static boolean toggleSource = true;

	/********* Bookmarks Properties **********/
	private static ArrayList<String> bookmarkURLs = new ArrayList<>();
	private static WebEngine webEngine;
	private static ArrayList<Entry> historyView;
	private static Menu mnuBookmarks;
	
	/**************** MenuBar ****************/

	/**
	 * This method will tell you how to add the menu object
	 * @param we is the reference value of WebEngine
	 * @return menuBar, showing all menu in menuBar
	 */
	public static MenuBar getMenuBar(WebEngine we) {
		MenuBar menuBar = new MenuBar();
		menuBar.getMenus().addAll(getMnuFile(we), getMnuSettings(we), getMnuBookmarks(), getMnuHelp());
		return menuBar;
	}

	/***************** Menu ******************/

	
	/**
	 * This method show how to add  menu File and short cut
	 * @param we is the reference value of WebEngine
	 * @return menu "File"
	 */
	private static Menu getMnuFile(WebEngine we) {
		mnu = new Menu("_File");
		mnu.setAccelerator(KeyCombination.keyCombination("ALT+F"));
		/**
		 * ALL SHORT CUT
		 * reference:https://stackoverflow.com/questions/24499500/javafx-menu-first-letter-underline-decoration
		 **/
		mnu.getItems().addAll(getMnuItmRefresh(we), getMnuItmExit());
		return mnu;
	}

	
	/** 
	 * This method show how to add  menu Settings and short cut
	 * @param we is the reference value of WebEngine
	 * @return menu "Settings"
	 */
	private static Menu getMnuSettings(WebEngine we) {
		mnu = new Menu("_Settings");
		mnu.setAccelerator(KeyCombination.keyCombination("ALT+S"));
		mnu.getItems().addAll(getMnuItmAddressBar(), getMnuItmHistory(), getMnuItmSource(we), getMnuItmSaveStartupPage());
		return mnu;
	}


	/**
	 *  This method show how to add  menu Bookmarks and short cut
	 * @return menu "Bookmarks"
	 */
	private static Menu getMnuBookmarks() {
		mnuBookmarks = new Menu("_Bookmarks");
		mnuBookmarks.setAccelerator(KeyCombination.keyCombination("ALT+B"));
		mnuBookmarks.getItems().addAll(getMnuItmBookmarks(mnuBookmarks));
		loadBookmarksToMenu(mnuBookmarks);
		return mnuBookmarks;
	}

	
	/**
	 * This method show how to add  menu Help and short cut
	 * @return menu "Help"
	 */
	private static Menu getMnuHelp() {
		mnu = new Menu("_Help");
		mnu.setAccelerator(KeyCombination.keyCombination("ALT+H"));
		mnu.getItems().addAll(getMnuItmJavaHelp(), getMnuItmAbout());
		return mnu;
	}

	/*************** MenuItems ***************/

	/**
	 * This method show how to add  menuItem Refresh and short cut
	 * @param we is the reference value of WebEngine
	 * @return menuItem "Refresh"
	 */
	private static MenuItem getMnuItmRefresh(WebEngine we) {
		mnuItm = new MenuItem("_Refresh");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+R"));
		
		/**reference:https://stackoverflow.com/questions/24499500/javafx-menu-first-letter-underline-decoration **/
		
		mnuItm.setOnAction((ActionEvent e) -> we.reload());
		return mnuItm;
	}

	
	/**  
	 * This method show how to add  menuItem Exit and short cut
	 * @return menuItem "Exit"
	 */
	private static MenuItem getMnuItmExit() {
		mnuItm = new MenuItem("_Exit");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+E"));
		mnuItm.setOnAction(e -> Platform.exit());
		return mnuItm;
	}

	
	/** 
	 * This method show how to add menuItem "Save Current Page as Startup" and show how to save it
	 * @return menuItem "Save Current Page as Startup"
	 */
	private static MenuItem getMnuItmSaveStartupPage() {
		mnuItm = new MenuItem("Save Current _Page as Startup");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+P"));
		mnuItm.setOnAction((ActionEvent e) -> {
			String currentURL = getCurrentURL();
			if (currentURL.length() > 0) { // if the currentURL is not ""...
				ArrayList<String> al = new ArrayList<>();
				al.add(currentURL); // ...load it into the ArrayList and save it to the file
				FileUtils.storeURLsToFile(al, "default.web");
			}
		});
		return mnuItm;
	}

	
	/**
	 *  This method will show how to add menuItem "Show/Hide Address Bar" and show toggle
	 * @return menuItem "Show/Hide Address Bar"
	 */
	private static MenuItem getMnuItmAddressBar() {
		mnuItm = new MenuItem("Show/Hide A_ddress Bar");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+D"));
		mnuItm.setOnAction((ActionEvent e) -> {
			if (!toggleAddress)
				topPanel.getChildren().add(hbxAddressBar);
			else
				topPanel.getChildren().remove(hbxAddressBar);
			toggleAddress = !toggleAddress;
		});
		return mnuItm;
	}

	/** 
	 * This method will show how to add menuItem "Show/Hide History" and show toggle
	 * @return menuItem "Show/Hide History"
	 */
	static MenuItem getMnuItmHistory() {
		mnuItm = new MenuItem("Show/Hide Histor_y");

		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+Y"));
		mnuItm.setOnAction(( e) -> {
			//rightPanel.getChildren().addAll(hisList, historyButton);
			if(rightPanel.isVisible()) {
				rightPanel.setVisible(false);
				rightPanel.setManaged(false);
				
			}else{
				rightPanel.setVisible(true);
				rightPanel.setManaged(true);
			}
		});

		return mnuItm;
	}
	
	/**
	 * This method will show the button backward and forward and set rightPanel
	 * @param we is the reference value of WebEngine
	 * @return ArrayList of historyView
	 */
	public static ArrayList<WebHistory.Entry> getHistory(WebEngine we) {
		Button backwards = new Button("backwards");
		backwards.setOnAction(e -> {
			try {

				we.getHistory().go(-1);
			} catch (Exception e1) {

			}
		});

		Button forwards = new Button("forwards");
		forwards.setOnAction(e -> {
			try {
				we.getHistory().go(1);
			} catch (Exception e1) {

			}
		});
		
		historyButton = new HBox(backwards, forwards);
		historyButton.setAlignment(Pos.CENTER);
		historyButton.setSpacing(50);
		hisList.setItems(we.getHistory().getEntries());
		rightPanel = new VBox(hisList, historyButton);
		rightPanel.setVisible(false);
		rightPanel.setManaged(false);
		hisList.setPrefHeight(1000);
	
		return historyView;
	}
	

/**
 * This method show you how to get MenuItem "Show/Hide SourceView"
 * @param we is the reference value of WebEngine
 * @return mnuItm "Show/Hide SourceView"
 */
	private static MenuItem getMnuItmSource(WebEngine we) {
		mnuItm = new MenuItem("Show/Hide So_urceView");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+U"));
		TextArea sourceView = new TextArea();
		we.getLoadWorker().stateProperty().addListener((obs, oldValue, newValue) -> {
		              if (newValue == State.SUCCEEDED) {
		            	  try {
		                      TransformerFactory transformerFactory = TransformerFactory.newInstance();
		                      Transformer transformer = transformerFactory.newTransformer();
		                      StringWriter stringWriter = new StringWriter();
		                      transformer.transform(new DOMSource(we.getDocument()),new StreamResult(stringWriter));}
		                         
		            	  catch (StringIndexOutOfBoundsException | IllegalArgumentException e) {
								e.printStackTrace();
							}
		            	  catch (TransformerException e1) {
		            		  	
		            		  e1.printStackTrace();
							}
		           	  
		               String html = (String)we.executeScript("document.documentElement.outerHTML");
		                sourceView.setText(html);

		              }
		            }); 
		
		mnuItm.setOnAction(e -> {
			if (toggleSource)
				MyJavaFXBrowser.root.setBottom(sourceView);
			else
				MyJavaFXBrowser.root.setBottom(null);
			toggleSource = !toggleSource;
		});
		return mnuItm;
	}

		
	/**
	 * This method will show you how to add "Add Bookmark"
	 * @param mnuBookmarks  is the reference value of Menu "Add Bookmark"
	 * @return mnuItm "Add _Bookmark"
	 */
	private static MenuItem getMnuItmBookmarks(Menu mnuBookmarks) {
		mnuItm = new MenuItem("Add _Bookmark");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+B"));
		mnuItm.setOnAction((ActionEvent e) -> {
			addDeleteBookmarkToMenu(mnuBookmarks, getCurrentURL());
			getBookmarkURLs().add(getCurrentURL());
		});
		return mnuItm;
	}

	
	/** 
	 * This method will show the information of help page click the MenuItem "Java _Help"
	 * @return mnuItm is the reference value of Menu "Java _Help"
	 */
	private static MenuItem getMnuItmJavaHelp() {
		mnuItm = new MenuItem("Java _Help");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+H"));
		mnuItm.setOnAction((ActionEvent e) -> goToURL("https://www.google.ca/search?q=java"));
		return mnuItm;
	}

	/** 
	 * This method will show the information of about click the MenuItem "About"
	 * @return mnuItm is the reference value of Menu "About"
	 */
	private static MenuItem getMnuItmAbout() {
		/* From Marco Jakob, code.makery, */
		/* http://code.makery.ch/blog/javafx-dialogs-official/ */
		mnuItm = new MenuItem("Ab_out");
		mnuItm.setAccelerator(KeyCombination.keyCombination("SHORTCUT+O"));
		mnuItm.setOnAction((ActionEvent e) -> {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("About");
			alert.setHeaderText("Yi Jiang's Browser");
			alert.setContentText("Student No.:040880681");
			alert.showAndWait();
		});
		return mnuItm;
	}

	/*** Panel method of loadTopPanel(),loadRightPanel(), loadBottomPanel() ****/

	/**
	 * This method will load AddressBar and menuBar to the TopPanel 
	 * @param we is the reference value of WebEngine
	 * @return object topPanel of VBox
	 */
	public static VBox loadTopPanel(WebEngine we) {
		hbxAddressBar = createAddressBar(we);
		MenuBar mb = getMenuBar(we);
		topPanel.getChildren().add(mb);
		return topPanel;
	}

	/**
	 * This method will use getHistory to load the button then load hisList to the rightPanel
	 * @param we is the reference value of WebEngine
	 * @return object rightPanel of VBox
	 */
	public static VBox loadRightPanel(WebEngine we) {
		hisList.setPrefHeight(1000);
		getHistory(we);
		return rightPanel;
	}


	/**
	 * This method will load sourceView to the bottomPanel
	 * @return object bottomPanel of VBox
	 */
	public static VBox loadBottomPanel() {
		VBox bottom = new VBox();
		TextArea sourceView = new TextArea();
		bottom.getChildren().add(sourceView);
		return bottomPanel;
		
	}

	/*************** Address Bar Methods ***************/
	
	/**
	 * This method will show you how to create addressBar for label, txtfldAddress and button GO
	 * @param we  is the reference value of WebEngine
	 * @return hbx is object of address Bar
	 */
	public static HBox createAddressBar(WebEngine we) {
		Label lblEnterURL = new Label("Enter URL:");
		lblEnterURL.setPadding(new Insets(4, 4, 4, 4));
		txtfldAddress = new TextField();

		btnGo = new Button("Go");
		btnGo.setOnAction(e -> redirect(we));
		
	/** Reference from-Professor: DAVID B HOUTMAN-Assignment 2 Description*/
		txtfldAddress.setOnKeyPressed(e->{
	            if (e.getCode().equals(KeyCode.ENTER))
	            {
	            	redirect(we);
	            }
	   });
		
		HBox hbx = new HBox();
		hbx.getChildren().addAll(lblEnterURL, txtfldAddress, btnGo);
		hbx.setHgrow(txtfldAddress, Priority.ALWAYS);

		return hbx;
	}
	
	 public static void redirect(WebEngine we) {
		 
		try {
			new URL(getCurrentURL());
			we.load(getCurrentURL());
		} 
		catch (MalformedURLException ex) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("MalformedURLException");
			alert.setHeaderText("Yi Jiang's Browser");
			alert.setContentText("There is MalformedURLException");
			alert.showAndWait();
		} 
		catch (IllegalArgumentException e){
			 
		 	Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("IllegalArgumentException");
			alert.setHeaderText("Yi Jiang's Browser");
			alert.setContentText("There is IllegalArgumentException");
			alert.showAndWait();
		} 
		catch (StringIndexOutOfBoundsException e) {
			Alert alert = new Alert(AlertType.INFORMATION);
			alert.setTitle("StringIndexOutOfBoundsException");
			alert.setHeaderText("Yi Jiang's Browser");
			alert.setContentText("There is StringIndexOutOfBoundsException");
			alert.showAndWait();
			
		}
	}
	
	/**
	 * This method is set String URL
	 * @param URL is String URL
	 */
	public static void setURL(String URL) {
		txtfldAddress.setText(URL);
	}

	/**
	 * This method is get String URL
	 * @return txtfldAddress.getText()
	 */
	private static String getCurrentURL() {
		return txtfldAddress.getText();
	}

	/**
	 * This method is setURL and check whether there is Exception use try--catch
	 * @param URL is String URL
	 */
	public static void goToURL(String URL) {
		
		
		 try {
			   URL enterURL=new URL(URL);
				setURL(enterURL.toString());
			} 
		 catch (MalformedURLException e1) {
				
					Alert alert = new Alert(AlertType.INFORMATION);
					alert.setTitle("MalformedURLException");
					alert.setHeaderText("Yi Jiang's Browser");
					alert.setContentText("There is MalformedURLException");
					alert.showAndWait();
		}
		 catch(IllegalArgumentException e){
			 
			 	Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("IllegalArgumentException");
				alert.setHeaderText("Yi Jiang's Browser");
				alert.setContentText("There is IllegalArgumentException");
				alert.showAndWait();
			 
			}	
		 catch (NullPointerException e) {
				
			}
		
		btnGo.fire();
	}

	/*************** Bookmarks Methods ***************/

/**
 * This method is get ArrayList
 * @return ArrayList bookmarkURLs
 */
	public static ArrayList<String> getBookmarkURLs() {
		return bookmarkURLs;
	}

	/**
	 * This method is set ArrayList for Bookmarks
	 * @param al is ArrayList 
	 */
	public static void setBookmarks(ArrayList<String> al) {
		bookmarkURLs = al;
	}

	
	/** 
	 * This method show how to load Bookmarks To Menu
	 * @param mnu is menu Bookmarks
	 */
	private static void loadBookmarksToMenu(Menu mnu) {
		if (FileUtils.fileExists("bookmarks.web")) {
			setBookmarks(FileUtils.getURLsFromFile("bookmarks.web"));
			
			for (String url : bookmarkURLs)
				addDeleteBookmarkToMenu(mnu, url);
			
		}
	}
	
	
	
	/**
	 * This method will show how to add Delete Bookmark To Menu
	 * @param mnu is menu reference
	 * @param URL is String URL
	 */
	private static void addDeleteBookmarkToMenu(Menu mnu, String URL) {
		
		mnuItm = new MenuItem(URL);
		mnuItm.setOnAction((ActionEvent e1) -> goToURL(URL));
		CustomMenuItem mnuItm = new CustomMenuItem(new Label(URL), false);
		if (mnu.getItems().size() == 1)
			mnu.getItems().add(new SeparatorMenuItem());
		mnu.getItems().add(mnuItm); // Add new URL to Menu
		
		ContextMenu cm = new ContextMenu(new MenuItem("Remove Bookmark"));
		ObservableList<MenuItem> items = mnuBookmarks.getItems();
		for (int i = 1; i < items.size(); i++) {
				mnuItm.getContent().setOnMouseClicked(e -> {
					/**Reference from https://docs.oracle.com/javafx/2/ui_controls/menu_controls.htm */
					if (e.getButton() == MouseButton.SECONDARY) {
						cm.show(mnuItm.getContent(), e.getScreenX(), e.getScreenY());
						cm.setOnAction(e1 -> {
							mnuItm.getParentMenu().getItems().remove(mnuItm);
							getBookmarkURLs().remove(URL);
							
						});
						mnuItm.setHideOnClick(false);
					}

					else if (e.getButton() == MouseButton.PRIMARY) {
						 goToURL(URL);
				
						mnuItm.getParentMenu().hide();
					}
				});
		}
	}


}
			

