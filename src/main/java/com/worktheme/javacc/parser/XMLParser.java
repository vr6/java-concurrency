/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.log4j.Logger;

import com.worktheme.javacc.model.Config;
import com.worktheme.javacc.model.Database;
import com.worktheme.javacc.model.Image;

/**
 * The main program. It orchestrates the work in the following steps
 *   - Validate inputs and prepare.
 *   - Read the XML file (single thread)
 *   - Insert data into SQLite database (embedded database, single thread)
 *   - Write HTML files (multi-threaded)
 * 
 *   The data inserts are not parallelized because it doesn't help much for SQlite
 *   PostgreSQL or MySQL would have allowed multi-threaded inserts for better results.
 *   I chose SQLite because it allows simplicity of the application (zero setup) 
 * 
 */
public class XMLParser {
	static final Logger logger = Logger.getLogger(XMLParser.class.getSimpleName());

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// validate input, create db table etc.
		String inputFile = prepare(args);
		Database db = null;
		try {
			db = Database.getInstance();
		} catch (SQLException e) { error("Could not create database", e);
		}
		
		// parse xml data
		long t1 = System.currentTimeMillis();
		List<Image> images = null;
		try {
			images = WorkReader.readWorks(inputFile, db);
		} catch (IOException e) { error("Could not read input data file", e);
		} catch (SQLException e) { error("Error while inserting data into database", e);
		} catch (XMLStreamException e) { error("Error while parsing the input XML data", e);
		}
		if (images == null) {
			error("Could not read input data file", new Throwable("No data."));
		}
		
		// insert data into database
		long t2 = System.currentTimeMillis();
		try {
			insertData(images, db);
		} catch (SQLException e) { error("Error while reading data from database", e);
		}
		
		// write HTML files
		long t3 = System.currentTimeMillis();
		OutputWriter writer = new OutputWriter();
		try {
			writer.writeFiles(db);
		} catch (IOException e) { error("Could not write output files", e);
		} catch (SQLException e) { error("Error while reading data from database", e);
		}
		
		// cleanup
		long t4 = System.currentTimeMillis();
		try {
			db.close();
			cleanup();
		} catch (Exception e) { error("Error while cleaning up", e);
		}
		
		// report
		logger.info("Parsing XML data (" + images.size() + " images) took "	+ (t2 - t1) + " milliseconds.");
		logger.info("Inserting data took "	+ (t3 - t2) + " milliseconds.");
		logger.info("Writing files took " + (t4 - t3) + " milliseconds.");
		logger.info("Done.");
	}

	private static void insertData (List<Image> images, Database db) throws SQLException {
		PreparedStatement psInsert = db.getStmt(Config.SQL_INSERT_WORK);
		for (Image image : images) {
			db.execute(psInsert, new String[] { image.getModel(), image.getMake(),
			image.getThumbURL(), image.getLargeURL() });
		}
		psInsert.close();
	}
	
	private static String prepare(String[] args) {
		if (args.length < 2) {
			usage();
		}
		Config.OUT_PATH = args[1];
		logger.info("Using input file: " + args[0]);
		logger.info("Using output location: " + Config.OUT_PATH);

		File outDir = new File(Config.OUT_PATH);
		if (!outDir.exists()) {
			Path path = FileSystems.getDefault().getPath(Config.OUT_PATH);
			try {
				Files.createDirectory(path);
			} catch (IOException e) {
				error("Could not create output directory", e);
			}
		} else {
			for (File file : outDir.listFiles()) {
				file.delete();
			}
		}
		return args[0];
	}

	private static void usage() {
		System.out.println("Usage (Windows):\trun.bat <input file location> <output path>");
		System.out.println("Usage (Other OS):\trun.sh <input file location> <output path>");
		System.exit(0);
	}

	private static void error(String msg, Throwable e) {
		logger.error(msg + ": " , e);
		System.exit(1);
	}

	private static void cleanup() throws IOException {
		Path path = FileSystems.getDefault().getPath(Config.OUT_PATH,	Config.DB_FILE);
		Files.delete(path);
	}
	// TODO readme
}
