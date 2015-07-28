/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.parser;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import com.worktheme.javacc.model.Config;
import com.worktheme.javacc.model.Database;
import com.worktheme.javacc.model.HtmlPage;

/**
 * Writes HTML file for a make and model combination
 *
 */
public class ModelThread implements Runnable {

	static final Logger logger = Logger.getLogger(ModelThread.class.getSimpleName());
	private String make;
	private Database db;

	public ModelThread(String make, Database db) {
		this.make = make;
		this.db = db;
	}
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			PreparedStatement psMakeModels = this.db.getStmt(Config.SQL_MAKE_MODELS);
			PreparedStatement psMmThumbs = this.db.getStmt(Config.SQL_MM_THUMBS);

			// get model data for the given make
			List<String[]> models = this.db.query(psMakeModels, 1, new String[] { this.make });
			
			for (String[] modelCols : models) {
				// get thumb data for the make and model
				List<String[]> mmThumbs = this.db.query(psMmThumbs, 2,
						new String[] { this.make, modelCols[0] });

				// write the make-model html file. No nav data is required 
				writeModelFiles(this.make, modelCols[0], mmThumbs);
			}
			psMakeModels.close();
			psMmThumbs.close();
		} catch (IOException e) { logger.error("Could not write output file", e);
		} catch (SQLException e) { logger.error("Error while reading data from database", e);
		}
	}
	/**
	 * Writes a single make-model file using the data for thumb-nails
	 *  
	 * @param make
	 * @param model
	 * @param mmThumbs
	 * @throws IOException
	 */
	private void writeModelFiles(String make, String model,	List<String[]> mmThumbs) throws IOException {
		HtmlPage page = new HtmlPage(make + " - " + model);
		for (String[] mmCols : mmThumbs) {
			// add thumbs
			page.addThumb(mmCols[0], mmCols[0]);
		}
		// add a link for index page
		page.addNav(Config.INDEX);
		
		// add a link for make page
		page.addNav(make);
		
		// write to the file system
		page.writeFile(make + "_" + model);
	}
}
