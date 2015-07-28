/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.parser;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.worktheme.javacc.model.Config;
import com.worktheme.javacc.model.Database;
import com.worktheme.javacc.model.HtmlPage;

/**
 * The main HTML writer class. It spawns multiple threads for
 * writing files for each make and for each combination of make and model. 
 * 
 * Uses Java concurrency for multi-threading.
 *
 */
public class OutputWriter {

	/**
	 * Files are written in the following order
	 *   - write index.html
	 *   - write make files (using the main thread)
	 *   - write model files for each make (multi-threaded)
	 * 
	 * @param db
	 * @throws IOException
	 * @throws SQLException
	 */
	public void writeFiles(Database db) throws IOException, SQLException {
		HtmlPage page = new HtmlPage(Config.INDEX_TITLE);
		
		// get data for thumbs on index page
		PreparedStatement ps = db.getStmt(Config.SQL_INDEX_THUMBS);
		List<String[]> indexThumbs = db.query(ps, 2, null);
		ps.close();

		// add thumbs on index page
		for (String[] cols : indexThumbs) {
			page.addThumb(cols[0], cols[1]);
		}
		
		// create a thread pool
		ExecutorService threads = new ThreadPoolExecutor(
				Config.INITIAL_POOL_SIZE, Config.MAX_POOL_SIZE,
				Config.MAX_RUNNING_TIME, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>());

		// get all makes names
		ps = db.getStmt(Config.SQL_MAKES);
		List<String[]> makes = db.query(ps, 1, null);
		ps.close();

		PreparedStatement psMakeThumbs = db.getStmt(Config.SQL_MAKE_THUMBS);
		PreparedStatement psMakeModels = db.getStmt(Config.SQL_MAKE_MODELS);
		for (String[] cols : makes) {
			// add navigation links on index page
			page.addNav(cols[0]);
			
			// kick-off threads to write make-model files
			threads.execute(new ModelThread(cols[0], db));
			List<String[]> makeThumbs = db.query(psMakeThumbs, 2,	new String[] { cols[0] });
			List<String[]> models = db.query(psMakeModels, 1,	new String[] { cols[0] });

			// write all make file using the main thread
			writeMakeFiles(cols[0], makeThumbs, models);
		}
		psMakeThumbs.close();
		psMakeModels.close();

		// write the index file to file-system
		page.writeFile(Config.INDEX);
		threads.shutdown();
		try {
			while (!threads.awaitTermination(5, TimeUnit.SECONDS)) {
				System.out.println("Waiting for threads to complete.");
			}
		} catch (InterruptedException e) {
		}
	}

	/**
	 * Writes a single make file using the title and 
	 * data for thumb-nails and navigation 
	 * 
	 * @param make
	 * @param makeThumbs
	 * @param models
	 * @throws IOException
	 */
	private void writeMakeFiles(String make, List<String[]> makeThumbs,
			List<String[]> models) throws IOException {
		HtmlPage page = new HtmlPage(make);
		
		// add thumbs
		for (String[] thumbCols : makeThumbs) {
			page.addThumb(thumbCols[0], thumbCols[1]);
		}
		// add a link for index page
		page.addNav(Config.INDEX);

		// add other navigation links
		for (String[] modelCols : models) {
			page.addNav(make + "_" + modelCols[0]);
		}
		// write to the file system
		page.writeFile(make);
	}
}
