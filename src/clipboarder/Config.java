/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clipboarder;

/**
 *
 * @author David
 */
public class Config {

    static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";
   //static final String DB_URL = "jdbc:mysql://localhoast/test";
    static final String DB_URL = "jdbc:mysql://localhost/test";
    //  Database credentials
    static final String USER = "root";
    static final String PASS = "";

    public String getDriver() {
        return JDBC_DRIVER;
    }
    public String getDB_URL() {
        return DB_URL;
    }
    public String getUSER() {
        return USER;
    }
    public String getPASS() {
        return PASS;
    }

}
