/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.heikweber.clipboarder;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author David
 */
public class ExecuteSQL {

    /**
     *
     * @param sql
     */
    public static void executeQuerry(String sql) {

        Config config = new Config();
        String DB_URL = config.getDB_URL();
        String PASS = config.getPASS();
        String USER = config.getUSER();

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();
            stmt.execute(sql);
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     *
     * @param sql
     */
    public static void executeQuerryAndPrint(String sql) {

        Config config = new Config();
        String DB_URL = config.getDB_URL();
        String PASS = config.getPASS();
        String USER = config.getUSER();

        Connection conn = null;
        Statement stmt = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                //Retrieve by column name
                String id = rs.getString("ID");
                String BN = rs.getString("BenutzerName");

                System.out.print("ID: " + id);
                System.out.println(", Benutzername: " + BN);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    /**
     *
     * @param sBenutzername
     * @param sPasswort
     * @return
     */
    public static String getUserID(String sBenutzername, String sPasswort) {

        String sql = "SELECT `ID` FROM `benutzer` WHERE `EMail` = '" + sBenutzername + "' AND `Passwort` = '" + sPasswort + "'";

        Config config = new Config();
        String DB_URL = config.getDB_URL();
        String PASS = config.getPASS();
        String USER = config.getUSER();
        int Count = 0;
        String id = "";

        Connection conn = null;
        Statement stmt = null;

        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(DB_URL, USER, PASS);
            stmt = conn.createStatement();

            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                id = rs.getString("ID");
                Count++;
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (stmt != null) {
                    conn.close();
                }
            } catch (SQLException se) {
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
        if (Count > 1) {
            return "FEHLER";
        } else if (Count == 0) {
            return "Falsche Zugangsdaten";
        } else {
            return id;
        }
    }

}
