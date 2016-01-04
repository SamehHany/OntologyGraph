/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eg.edu.alexu.ehr.util.db;

import eg.edu.alexu.ehr.util.io.BufferedFileReader;
import eg.edu.alexu.ehr.util.io.BufferedFileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author sameh
 */
public class Database {

    private Connection connection;
    private Statement stmt = null;

    private static final String statsPath = "stats";

    public Database() {
        connection = null;
    }

    public Database(String url, String username) {
        connection = null;
        connect(url, username, "");
    }

    public Database(String url, String username, String password) {
        connection = null;
        connect(url, username, password);
    }

    public void connect(String url, String username, String password) {
        connection = null;
        try {
            Class.forName("org.postgresql.Driver");
            connection = DriverManager
                    .getConnection(url, username, password);
            stmt = connection.createStatement();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public void executeQuery(String query) {
        try {
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public List<String> getTables(String schema) {
        List<String> tables = new ArrayList();
        DatabaseMetaData md;
        ResultSet rs;
        try {
            md = connection.getMetaData();
            rs = md.getTables(null, schema, "%", new String[]{"TABLE"});
            while (rs.next()) {
                tables.add(rs.getString(3));
            }
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

        return tables;
    }

    public void getAndSaveStats(String schema) {
        BufferedFileWriter writer;
        List<String> tables = getTables(schema);
        ResultSet rs;
        try {
            DatabaseMetaData metaData = connection.getMetaData();
            for (String table : tables) {
                ResultSet columnsRs = metaData.getColumns(null, schema, table,
                        null);
                //ResultSetMetaData meta = columnsRs.getMetaData();
                List<String> columnNames = new ArrayList();
                while(columnsRs.next()) {
                    columnNames.add(columnsRs.getString("COLUMN_NAME"));
                }
                
                writer = new BufferedFileWriter(statsPath + "/" + table
                        + ".csv");
                writer.writeln("starelid,column,stainherit,stanullfrac,"
                        + "stawidth,stadistinct,stakind1,stakind2,stakind3,"
                        + "stakind4,stakind5,staop1,staop2,staop3,staop4,"
                        + "staop5,stanumbers1,stanumbers2,stanumbers3,"
                        + "stanumbers4,stanumbers5,stavalues1,stavalues2,"
                        + "stavalues3,stavalues4,stavalues5");

                rs = stmt.executeQuery("select * "
                        + "from pg_statistic where starelid = '" + table
                        + "'::regclass");

                int rowIndex = 0;
                while (rs.next()) {
                    int noOfColumns = rs.getMetaData().getColumnCount();
                    String cellValue = toCSVCell(rs.getString(1));
                    int columnNo = Integer.parseInt(rs.getString(2).trim());
                    writer.write(cellValue + "," + columnNames.get(rowIndex++));
                    for (int i = 3; i <= noOfColumns; i++) {
                        cellValue = toCSVCell(rs.getString(i));
                        writer.write("," + cellValue);
                    }
                    writer.writeln();
                }

                writer.close();
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (IOException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null,
                    ex);
        }
    }

    private static String toCSVCell(String cellValue) {
        if (cellValue == null) {
            return "";
        }
        return "\"" + cellValue.replace("\"", "\\\"") + "\"";
    }
}
