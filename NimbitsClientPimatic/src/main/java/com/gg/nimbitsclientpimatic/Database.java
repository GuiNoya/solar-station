package com.gg.nimbitsclientpimatic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Database {

    // É possível capturar todos os sensores, pegando `deviceId`, `attributeName`
    // e `type` de todas linhas da tabela `deviceAttribute` e mandando fazer o
    // update.
    
    //private static final String DB_LOCATION = "/home/pi/pimatic-app/pimatic-database.sqlite";
    private static final String DB_LOCATION = "/home/guilherme/NetBeansProjects/NimbitsClientPimatic/pimatic-database.sqlite";
    private static Connection c = null;

    public static void open() {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:" + DB_LOCATION);
            c.setAutoCommit(false);
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    public static void close() {
        try {
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
    }

    public static List<Tuple> getData(String deviceId, String attributeName) {
        return getData(deviceId, attributeName, new Date(0), false);
    }

    public static List<Tuple> getData(String deviceId, String attributeName, boolean dataIsString) {
        return getData(deviceId, attributeName, new Date(0), dataIsString);
    }

    public static List<Tuple> getData(String deviceId, String attributeName, Date initialDate) {
        return getData(deviceId, attributeName, initialDate, false);
    }

    public static List<Tuple> getData(String deviceId, String attributeName, Date initialDate, boolean dataIsString) {
        ResultSet rs;
        List<Tuple> result;
        PreparedStatement stmt;

        try {
            String sql;
            java.sql.Timestamp ts = new java.sql.Timestamp(initialDate.getTime());
            String table = dataIsString ? "attributeValueString" : "attributeValueNumber";
            sql = "SELECT attr.time, attr.value FROM " + table + " attr, "
                    + "deviceAttribute da WHERE da.deviceId = ? AND "
                    + "da.attributeName = ? AND "
                    + "datetime(attr.time/1000, 'unixepoch') > "
                    + "datetime(" + ts.getTime() + "/1000, 'unixepoch') "
                    + "AND attr.deviceAttributeId = da.id;";
            stmt = c.prepareStatement(sql);
            stmt.setString(1, deviceId);
            stmt.setString(2, attributeName);
            rs = stmt.executeQuery();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }

        try {
            result = new ArrayList<>();
            if (dataIsString) {
                while (rs.next()) {
                    result.add(new Tuple<>(rs.getLong("time"), rs.getString("value")));
                }
            } else {
                while (rs.next()) {
                    result.add(new Tuple<>(rs.getLong("time"), (Number) rs.getDouble("value")));
                }
            }
            stmt.close();
        } catch (SQLException e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }

        return result;
    }
}
