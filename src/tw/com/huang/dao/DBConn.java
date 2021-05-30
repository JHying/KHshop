/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 *
 * @author H_yin
 */
public final class DBConn {

    private Connection dbConn;//宣告建立連線物件
    private String connMsg;//連線訊息
    private boolean isConn;//連線狀態
    private String strDriver;//MySQL JDBC連線驅動程式
    private String strURL;//資料庫連線參數
    private String strUser;//連線AFCISDB資料庫之使用者帳號
    private String strPass;//連線AFCISDB資料庫之使用者密碼
/* 連線參數預設值
     private String strDriver = "com.mysql.jdbc.Driver";//MySQL JDBC連線驅動程式
     private String strURL = "jdbc:mysql://localhost:3306/afcisdb?useUnicode=true&characterEncoding=UTF8";//資料庫連線參數
     private String strUser = "root";//連線資料庫之使用者帳號
     private String strPass = "password";//連線資料庫之使用者密碼
     */
/* 連線方式範例
    方法一:
    DBConn dbc = new DBConn();//使用預設值連線
    Connection dbconn = dbc.getConnection();//取得資料庫連線物件, 代入SQL查詢物件SQLProcess
    方法二:
    DBConn dbc = new DBConn(dbconnParam);//代入連線參數連線
    Connection dbconn = dbc.getConnection();//取得資料庫連線物件, 代入SQL查詢物件SQLProcess
    */
    public DBConn() {//使用預設值, 自動連線至AFCISTDB
        this.strDriver = "com.mysql.jdbc.Driver";//MySQL JDBC連線驅動程式
        this.strURL = "jdbc:mysql://localhost:3306/hmhouse_hmhouse?useUnicode=true&characterEncoding=UTF8";//資料庫連線參數
        this.strUser = "root";//連線資料庫之使用者帳號
        this.strPass = "password";//連線資料庫之使用者密碼
        this.createConnection();
    }

    public DBConn(String[] dbconnParam) {//輸入連線參數, 手動連線至AFCISTDB
        this.strDriver = dbconnParam[0];
        this.strURL = dbconnParam[1];
        this.strUser = dbconnParam[2];
        this.strPass = dbconnParam[3];
        this.createConnection();
    }

    //連線狀態參數
    public boolean getIsConn() {
        return this.isConn;
    }

    public String getConnMsg() {
        return this.connMsg;
    }

    //手動設定資料庫連線參數
    public void setDBConnParam(String[] dbconnParam) {
        this.strDriver = dbconnParam[0];
        this.strURL = dbconnParam[1];
        this.strUser = dbconnParam[2];
        this.strPass = dbconnParam[3];
    }

    //取得目前資料庫連線參數
    public String[] getDBConnParam() {
        String[] dbconnParam = new String[4];
        dbconnParam[0] = this.strDriver;
        dbconnParam[1] = this.strURL;
        dbconnParam[2] = this.strUser;
        dbconnParam[3] = this.strPass;
        return dbconnParam;
    }

    //手動輸入資料庫參數建立資料庫連線
    public void createConnection(String strDriver, String strURL, String strUser, String strPass) {//可自定義連線參數的連線
        this.dbConn = null;//宣告連線物件
        try {
            Class.forName(strDriver);//由前方指定連線驅動程式
            this.dbConn = DriverManager.getConnection(strURL, strUser, strPass);//由前方指定連線位置，帳號，密碼
            this.connMsg = "Connection is success！Connect to " + strURL;//如果連線成功則顯示連線位置的訊息
            this.isConn = true;//布林判斷為連線成功
        } catch (ClassNotFoundException | SQLException e) {
            this.connMsg = "Connection is failed";
            this.isConn = false;
        }
    }

    //以預設參數建立資料庫連線
    public void createConnection() {//直接連MySQL預設驅動與帳密
        this.dbConn = null;//宣告建立連線物件
        try {
            Class.forName(this.strDriver);//指定連線驅動程式
            this.dbConn = DriverManager.getConnection(this.strURL, this.strUser, this.strPass);//指定連線位置
            this.connMsg = "<p>Connect to " + this.strURL + "</p>";//如果連線成功則印出連線位置的訊息
            this.isConn = true;
        } catch (ClassNotFoundException | SQLException e) {
            this.connMsg = "<p>Connection is failed: " + e.getMessage() + "</p>";
            this.isConn = false;
        }
    }

    //傳回資料庫連線物件
    public Connection getConnection() {
        return this.dbConn;
    }

    //關閉連線
    public void closeConnection() {
        try {
            if (this.dbConn != null) {
                this.dbConn.close();
            }
        } catch (SQLException e) {
            this.connMsg = "<p>SQLException=" + e + "</p>";
        }
    }

}
