package com.bernerbits.vandy.bevypro.db;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.h2.tools.Server;

/**
 * Helper class for H2 Database.<br/>
 * 
 * Spins up the H2 database; if the schema does not exist, creates tables and 
 * prepopulates beverage data. <br/>
 * 
 * Also spins up a web console on port 9091 for quick admin access to the data.
 * 
 * @author derekberner
 *
 */
public class H2DatabaseSource {
	
	private Server h2WebServer;
	
	public DataSource getDataSource() throws SQLException, IOException {
		// Check for DB file before creating the data source.
		File dbFile = new File(new File(System.getProperty("user.home")), ".bevypro.h2.db");
		boolean newDatabase = !dbFile.exists();

		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:file:~/.bevypro");
		ds.setUser("sa");
		ds.setPassword("sa");
		
		// If it didn't exist, run the schema creation and data insert scripts.
		if(newDatabase) {
			initializeDatabase(ds);
		}
		
		return ds;
	}
	
	private void initializeDatabase(DataSource ds) throws IOException, SQLException {
		Connection conn = ds.getConnection();
		try {
			runResourceScript(conn,"/schema.sql");
			runResourceScript(conn,"/insert.sql");
		} finally {
			conn.close();
		}
	}

	private void runResourceScript(Connection conn, String resource) throws IOException, SQLException {
		String[] statements = parseStatements(resource);
		Statement s = conn.createStatement();
		try {
			for(String statement : statements) {
				s.execute(statement);
			}
		} finally {
			s.close();
		}
	}

	private String[] parseStatements(String resource) throws IOException, SQLException {
		
		InputStream is = getClass().getResourceAsStream(resource);
		BufferedReader r = new BufferedReader(new InputStreamReader(is));
		
		StringBuffer contents = new StringBuffer();
		
		String line;
		while((line=r.readLine()) != null) {
			if(!line.startsWith("--")) {
				contents.append(line).append("\n");
			}
		}
		
		return contents.toString().split(";");
	}

	@PostConstruct
	public void startUp() throws SQLException, IOException {
		h2WebServer = Server.createWebServer("-webPort","9091","-webAllowOthers");
		h2WebServer.start();
		getDataSource().getConnection().close(); 
	}
	
	@PreDestroy
	public void shutDown() {
		h2WebServer.stop();
	}
	
}
