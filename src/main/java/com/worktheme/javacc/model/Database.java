/**
 * Copyright (c) 2015, Venkat Reddy (venkat@apache.org)
 * All rights reserved.
 */

package com.worktheme.javacc.model;

import java.nio.file.FileSystems;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple database access layer
 * Not a generic one; only specific to this application
 */
public class Database {
	private String dbPath;
	private Connection conn;
	
	// SQL for creating the one and the only table for storing image data
	private static String SQL_CREATE_TBL = "create table if not exists 'works' "
			+ "('make' text, 'model' text, 'thumb' text, 'large' text)";

	private static Database db = null;

	public static Database getInstance() throws SQLException {
		if (db == null) {
			db = new Database(Config.OUT_PATH
					+ FileSystems.getDefault().getSeparator() + Config.DB_FILE);
		}
		return db;
	}

	private Database(String path) throws SQLException {
		this.dbPath = path;
		this.conn = DriverManager.getConnection("jdbc:sqlite:" + this.dbPath);
		this.conn.setAutoCommit(true);
		
		// create the table
		Statement stmt = this.conn.createStatement();
		stmt.execute(SQL_CREATE_TBL);
		stmt.close();
	}

	/**
	 * Executes a prepared statement
	 * Used only for data inserts
	 * 
	 * @param stmt
	 * @param vals
	 * @throws SQLException
	 */
	public void execute(PreparedStatement stmt, String[] vals)
			throws SQLException {
		if (stmt != null) {
			if (vals != null) {
				for (int i = 0; i < vals.length; i++) {
					stmt.setString(i + 1, vals[i]);
				}
			}
			stmt.execute();
		}
	}

	/**
	 * Executes a prepared statement
	 * Used for reading data
	 * Builds and returns results as an array to avoid iteration code at client 
	 * 
	 * @param stmt
	 * @param ccount
	 * @param vals
	 * @return 
	 * @throws SQLException
	 */
	public List<String[]> query(PreparedStatement stmt, int ccount,
			String[] vals) throws SQLException {
		if (stmt == null) {
			return null;
		}
		
		// bind the values to the PreparedStatement
		List<String[]> res = new ArrayList<String[]>();
		if (vals != null) {
			for (int i = 0; i < vals.length; i++) {
				stmt.setString(i + 1, vals[i]);
			}
		}
		ResultSet rs = stmt.executeQuery();
		
		// build the result array
		while (rs.next()) {
			String[] row = new String[ccount];
			for (int i = 0; i < ccount; i++) {
				row[i] = (rs.getString(i + 1));
			}
			res.add(row);
		}
		rs.close();
		return res;
	}

	/**
	 * Used for cases where only an int is to be returned
	 * Example: count of images rows 
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public int getInt(String sql) throws SQLException {
		int ret = -1;
		Statement stmt = this.conn.createStatement();
		ResultSet rs = stmt.executeQuery(sql);
		if (rs.next()) {
			ret = rs.getInt(1);
		}
		rs.close();
		stmt.close();
		return ret;
	}

	/**
	 * Used for the cases of query in a for loop
	 * to allow for reuse of PreparedStatement
	 * Calling code must close the statement
	 * 
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public PreparedStatement getStmt(String sql) throws SQLException {
		return this.conn.prepareStatement(sql);
	}

	public void close() throws SQLException {
		if ((this.conn != null) && !this.conn.isClosed()) {
			this.conn.close();
		}
	}

	static {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
