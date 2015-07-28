/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.model;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds the data for thmbs and navigation links for a page
 * Also writes the HTML file using the data and the template 
 *
 */
public class HtmlPage {
	private static String template = null;
	private String title;
	private List<String> navs;
	private List<String> thumbs;

	public HtmlPage() throws IOException {
		this.navs = new ArrayList<String>();
		this.thumbs = new ArrayList<String>();
		if (template == null) {
			template = new String(Files.readAllBytes(Paths
					.get(Config.TEMPLATE_FILE)));
		}
	}

	public HtmlPage(String val) throws IOException {
		this();
		this.title = val;
	}

	/**
	 * Add HTML content for a navigation link 
	 * @param nav
	 */
	public void addNav(String nav) {
		this.navs.add("\t<a href=\"" + format(nav) + ".html\">" + nav + "</a>");
	}

	/**
	 * Add HTML content for a thumb
	 * Also make it as link for the large-sized image 
	 * 
	 * @param thumb
	 * @param large
	 */
	public void addThumb(String thumb, String large) {
		this.thumbs.add("\t<a href=\"" + large + "\"><img height=\""
				+ Config.THUMB_HEIGHT + "\" src=\"" + thumb + "\"/></a>");
	}

	/**
	 * Prepares HTML content by substituting the variable literals 
	 * in the template data with the data (tuhmbs, navs) for this instance. 
	 * Also, writes the HTML content to the file system as a file 
	 * 
	 * @param fileName
	 * @throws IOException
	 */
	public void writeFile(String fileName) throws IOException {
		// add title
		String content = template.replaceAll(Config.TITLE, this.title);
		
		// add navigation lines
		StringBuilder sb = new StringBuilder();
		sb.append(String.join(Config.NAV_SEPARATOR + "\n", this.navs));
		content = content.replaceAll(Config.NAVIGATION, sb.toString());

		// add thumb lines
		sb = new StringBuilder();
		sb.append(String.join("\n", this.thumbs));
		content = content.replaceAll(Config.THUMBNAILS, sb.toString());

		// write file
		Files.write(
				Paths.get(Config.OUT_PATH
						+ FileSystems.getDefault().getSeparator()
						+ format(fileName) + ".html"), content.getBytes());
	}

	/**
	 * Replaces the special characters in the file name with an underscore.
	 * 
	 * @param fileName
	 * @return
	 */
	private String format(String fileName) {
		String fname = fileName.trim().toLowerCase();
		fname = fname.replaceAll("[^A-Za-z0-9]", "_");
		return fname;
	}
}
