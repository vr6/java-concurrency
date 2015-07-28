/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.parser;

import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import com.worktheme.javacc.model.Config;
import com.worktheme.javacc.model.Database;
import com.worktheme.javacc.model.Image;
import com.worktheme.javacc.parser.OutputWriter;
import com.worktheme.javacc.parser.WorkReader;

/**
 * Unit test for XMLParser.
 * 
 */
public class XMLParserTest extends TestCase {
	/**
	 * Create the test case
	 *
	 * @param testName
	 *            name of the test case
	 */
	public XMLParserTest(String testName) {
		super(testName);
	}

	/**
	 * @return the suite of tests being tested
	 */
	public static Test suite() {
		return new TestSuite(XMLParserTest.class);
	}

	/**
	 * Test
	 */
	public void testApp() throws Exception {
		Config.OUT_PATH = "src/test/resources/out";
		Config.TEMPLATE_FILE = "src/test/resources/template.html";

		File outDir = new File(Config.OUT_PATH);
		if (!outDir.exists()) {
			Path path = FileSystems.getDefault().getPath(Config.OUT_PATH);
			Files.createDirectory(path);
		} else {
			for (File file : outDir.listFiles()) {
				file.delete();
			}
		}
		Database db = Database.getInstance();
		int workCount = 14;
		
		// check for number of images parsed
		List<Image> images = WorkReader.readWorks(
				"src/test/resources/works.xml", db);
		assertTrue("The number of images parsed is incorrect: " + images.size(),
				images.size() == workCount);

		// check for number of data rows inserted 
		insertData(images, db);
		int dbCount = db.getInt("select count(*) from works;");
		assertTrue("The number of records in database is incorrect: " + dbCount,
				dbCount == workCount);

		// check for number of files written 
		OutputWriter writer = new OutputWriter();
		writer.writeFiles(db);
		File dir = new File(Config.OUT_PATH);
		int files = dir.listFiles().length;
		assertTrue("The number of files is incorrect: " + files,
				files == (workCount + 1)); // +1 to account for the db file

		db.close();
	}
	private void insertData (List<Image> images, Database db) throws SQLException {
		PreparedStatement psInsert = db.getStmt(Config.SQL_INSERT_WORK);
		for (Image image : images) {
			db.execute(psInsert, new String[] { image.getModel(), image.getMake(),
			image.getThumbURL(), image.getLargeURL() });
		}
		psInsert.close();
	}
}
