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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

/*** class FileUtiles ***/
public class FileUtils {

	/**
	 * This is boolean fileExists method use File f as param
	 * @return true if exists
	 * @param f is reference of File
	 */

	public static boolean fileExists(File f) {
		return (f != null && f.exists() && f.isFile() && f.canRead() && (f.length()>2));
	}

	/**
	 * This is boolean fileExists method usd strong s as param
	 * @param s is String s
	 * @return true if exists
	 */

	public static boolean fileExists(String s) {
		return (fileExists(new File(s)));
	}



	/**
	 *  get URLs from file
	 * @param fileName is String fileNames
	 * @return the Arraylist al
	 */
	public static ArrayList<String> getURLsFromFile(String fileName) {
		ArrayList<String> al = new ArrayList<>();
		try {
			File f = new File(fileName);
			Scanner URLString = new Scanner(f);
			while (URLString.hasNext())
				al.add(URLString.next());
			URLString.close();
		} catch (FileNotFoundException e) {
		}
		return al;
	}


	/**
	 * Save URLs to file 
	 * @param al is ArrayList 
	 * @param fileName is String 
	 * @return  File f
	 */
	public static File storeURLsToFile(ArrayList<String> al, String fileName) {
		File f = new File(fileName);
		if (FileUtils.fileExists(f)) 
			f.delete();  // remove old bookmarks file
		try {
			PrintWriter pw = new PrintWriter(f);
			for (String s : al)	
				pw.println(s);
				pw.flush(); 
				pw.close();
		} catch (FileNotFoundException e) {	}
		return f;
	}

}
