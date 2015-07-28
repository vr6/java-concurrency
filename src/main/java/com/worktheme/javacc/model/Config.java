/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */
package com.worktheme.javacc.model;

/**
 * Configuration settings and string constants
 */
public class Config {

	// template variables, to replace while generating HTML files
	public static final String TITLE = "\\{\\{TITLE\\}\\}";
	public static final String NAVIGATION = "\\{\\{NAVIGATION\\}\\}";
	public static final String THUMBNAILS = "\\{\\{THUMBNAILS\\}\\}";

	// internal file names
	public static String TEMPLATE_FILE = "template/template.html";
	public static final String DB_FILE = "works.db";
	public static String OUT_PATH = "out";

	// for output writer
	public static final String INDEX = "Index";
	public static final String INDEX_TITLE = "Works";
	public static final String NAV_SEPARATOR = "&nbsp;|&nbsp;";
	public static final String SPECIAL_CHARS = "[^A-Za-z0-9_]";
	public static final int THUMB_HEIGHT = 100;

	// multi-threading configuration
	public static final int INITIAL_POOL_SIZE = 10;
	public static final int MAX_POOL_SIZE = 100;
	public static final long MAX_RUNNING_TIME = 5000;

	// SQL for prepared statements
	public static final String SQL_INDEX_THUMBS = "select thumb, large from works limit 10;";
	public static final String SQL_MAKES = "select distinct(make) from works where make is not null;";
	public static final String SQL_MAKE_THUMBS = "select thumb, large from works where make = ? limit 10;";
	public static final String SQL_MAKE_MODELS = "select distinct(model) from works where model is not null and make = ?;";
	public static final String SQL_MM_THUMBS = "select thumb, large from works where make = ? and model = ?;";
	public static final String SQL_INSERT_WORK = "insert into 'works' (model, make, thumb, large) values (?, ?, ?, ?)";
}
