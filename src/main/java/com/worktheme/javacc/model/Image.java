/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.model;

/**
 * Data bean for Image info
 * Holds the data before writing to database
 *
 */
public class Image {
	private String thumbURL;
	private String largeURL;
	private String model;
	private String make;

	public String getThumbURL() {
		return this.thumbURL;
	}

	public void setThumbURL(String thumbURL) {
		this.thumbURL = thumbURL;
	}

	public String getLargeURL() {
		return this.largeURL;
	}

	public void setLargeURL(String imgURL) {
		this.largeURL = imgURL;
	}

	public String getModel() {
		return this.model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getMake() {
		return this.make;
	}

	public void setMake(String make) {
		this.make = make;
	}
}
