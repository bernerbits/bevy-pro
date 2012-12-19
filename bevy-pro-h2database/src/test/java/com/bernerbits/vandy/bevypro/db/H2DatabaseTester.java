package com.bernerbits.vandy.bevypro.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;

import javax.sql.DataSource;

public class H2DatabaseTester {

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		H2DatabaseSource h2db = new H2DatabaseSource();
		
		h2db.startUp();
		try {
			DataSource ds = h2db.getDataSource();
			Connection c = ds.getConnection();
			try {
				Statement s = c.createStatement();
				ResultSet rs = s.executeQuery("select b.*, bs.slot_number from beverage b inner join beverage_slot bs on b.id = bs.beverage_id");
				ResultSetMetaData rsmd = rs.getMetaData();
				for(int i = 1; i <= rsmd.getColumnCount(); i++) {
					System.out.print(rsmd.getColumnName(i) + "\t");
				}
				System.out.println();
				while(rs.next()) {
					for(int i = 1; i <= rsmd.getColumnCount(); i++) {
						System.out.print(rs.getObject(i) + "\t");
					}
					System.out.println();
				}
				rs.close();
			} finally {
				c.close();
			}
		} finally {
			h2db.shutDown();
		}
	}

}
