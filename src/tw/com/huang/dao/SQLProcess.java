/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.dao;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 沒使用
 * @author H_yin
 */
public class SQLProcess {

	private ResultSet rs;// ResultSet物件, 資料處理結果物件
	private String sql;// 結構化查詢語言
	private Statement stmt;// 需先經過dbconn產生
	private String deputy;// 產生檔案之副檔名

	// 建構子
	public SQLProcess() {
	}

	public SQLProcess(Connection dbConn) throws SQLException {
		this.stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}

	public void setSQLStatement(Connection dbConn) throws SQLException {// 手動代入資料庫連線物件產生SQL物件
		this.stmt = dbConn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
	}

	public void closeSQLStatement() {
		if (this.stmt != null) {
			try {
				this.stmt.close();
			} catch (SQLException ex) {
				Logger.getLogger(SQLProcess.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
	}

	public Statement getSQLStatement() {
		return this.stmt;
	}

	// 執行資料查詢SQL語句(select ... from ...)
	public void setSQLQuery(String SQL) throws SQLException {
		this.rs = this.stmt.executeQuery(SQL);
	}

	// 執行資料存取SQL語句(insert, update, delete)
	public void setSQLUpdate(String SQL) throws SQLException {
		this.stmt.executeUpdate(SQL);
	}

	// 取得現在執行的SQL語法
	public String getSQL() {
		return this.sql;
	}

	// 取得查詢結果集合
	public ResultSet getResultSet() {
		return this.rs;
	}

	// 取得副檔名
	public String getDeputy() {
		return this.deputy;
	}

	// 取得MySQL資料庫綱要內所有表格名稱
	public List<String[]> getAllTables(String DBname) throws SQLException {
		// 從log資料，取問卷與資料表之關係
		this.sql = "SELECT table_name FROM information_schema.tables where table_schema='" + DBname + "';";
		setSQLQuery(this.sql);
		List<String[]> dataList = new ArrayList<String[]>();
		ResultSetMetaData rsmd = this.rs.getMetaData();
		String[] col = new String[rsmd.getColumnCount()];
		for (int i = 0; i < col.length; i++) {
			col[i] = rsmd.getColumnName(i + 1);
		}
		while (this.rs.next()) {
			String[] row = new String[rsmd.getColumnCount()];
			for (int i = 0; i < row.length; i++) {// 將各個欄位資料均轉成字串型態-->取出後再依系統與資料庫設計需求做調整
				if (this.rs.getObject(col[i]) != null) {// 有取到資料
					row[i] = this.rs.getObject(col[i]).toString();
				} else {
					row[i] = null;// 未取得資料時均設為空值
				}
			}
			dataList.add(row);
		}
		return dataList;
	}

	// 用POI讀取excel，只讀第一個sheet
	public void importExceltoDB(String tablename, String filePath) throws SQLException, IOException {
		File excelFile = new File(filePath);
		FileInputStream fiput = new FileInputStream(excelFile);
		POIFSFileSystem fs = new POIFSFileSystem(fiput);
		HSSFWorkbook wb = new HSSFWorkbook(fs);
		HSSFSheet sheet = wb.getSheetAt(0);// 取第一個sheet
		int rowCount = sheet.getLastRowNum() + 1;
		for (int i = 1; i < rowCount; i++) {// 從該sheet下scan所有資料
			// 0為 title,i從 1 開始
			System.out.println(i);// debug
			HSSFRow row = sheet.getRow(i);
			if (row != null) {
				// System.out.println("n" + i);// debug
				// System.out.println("n" + row.getLastCellNum());// debug
				String[] RowStr = new String[row.getLastCellNum()];
				// getPhysicalNumberOfCells() 取得"內有資料"的欄數
				// getLastCellNum() 取得最後一欄的欄數
				for (int j = 0; j < RowStr.length; j++) {
					if (row.getCell(j) == null) {
						RowStr[j] = "";
					} else {
						// System.out.println("je" + i);// debug
						RowStr[j] = row.getCell(j).toString();
					}
				}
				insertDataToDB(tablename, RowStr);
			}
		}
		wb.close();
		fiput.close();
		fs.close();
	}

	// 以分隔符號讀取資料，CSV以逗號分隔或其他分隔方式
	public void importFileToDB(String tablename, String resultfilepath, String regex)
			throws SQLException, FileNotFoundException, UnsupportedEncodingException {// 將大批資料從資料檔檔寫入資料庫方法，須給欲增加欄或資料庫&檔名
		// INSERT INTO tablename VALUES ('a','b','c','d');
		if (regex.equals("|")) {
			regex = "\\|";
		}
		File f = new File(resultfilepath);
		Scanner sc = new Scanner(f);
		List<String> dataset = new ArrayList<String>();
		while (sc.hasNextLine()) {
			String next = sc.nextLine();
			dataset.add(next);
		}
		sc.close();
		for (int i = 1; i < dataset.size(); i++) {
			String newdata = dataset.get(i).toString();
			if (newdata.endsWith(regex)) {
				newdata = newdata + "null";
			}
			String[] data = newdata.split(regex);
			insertDataToDB(tablename, data);
		}
	}

	// 增加一筆資料
	public void insertDataToDB(String tablename, String[] data) throws SQLException {
		// 增加一筆資料方法，須給資料庫、欲增加資料用逗號分開(會轉成陣列)
		// INSERT INTO tablename VALUES ('a','b','c','d');
		this.sql = "INSERT INTO " + tablename + " VALUES(";
		for (int i = 0; i < data.length; i++) {// 把前面欄內容一一加入
			if (data[i] == null || data[i].equals("") || data[i].equals("null")) {
				this.sql += "DEFAULT";
			} else {
				this.sql += "'" + data[i] + "'";
			}
			if (i < data.length - 1) {
				this.sql += ",";
			} else {
				this.sql += ");";// 最後加上括號分號
			}
		}
		this.stmt.executeUpdate(this.sql);
	}

	// 增加一筆資料至指定欄位
	public void insertDataToDB(String tablename, String[] colname, String[] data) throws SQLException {
		// 增加資料方法，須給欲增加欄、資料庫、欲增加資料陣列
		// INSERT INTO tablename(c1,c2) VALUES ('a','b');
		this.sql = "INSERT INTO " + tablename + " (";
		for (int i = 0; i < colname.length; i++) {// 把欄位一一加入
			this.sql += colname[i];
			if (i < colname.length - 1) {
				this.sql += ",";
			} else {
				this.sql += ") VALUES (";
			}
		}
		for (int i = 0; i < data.length; i++) {// 把前面欄內容一一加入
			if (data[i] == null || data[i].equals("") || data[i].equals("null")) {
				this.sql += "DEFAULT";
			} else {
				this.sql += "'" + data[i] + "'";
			}
			if (i < data.length - 1) {
				this.sql += ",";
			} else {
				this.sql += ");";// 最後加上括號分號
			}
		}
		this.stmt.executeUpdate(this.sql);
	}

	// 修改指定欄位所有資料
	public void updateDataByCondition(String tablename, String colname, String newdata) throws SQLException {
		// 修改資料，須給資料庫、修改欄、修改前資料(修改欄)、修改後資料
		// UPDATE test SET c1='avc' WHERE c1 IS 'NULL' ;
		// UPDATE test SET c1 = null;//刪除c1欄位所有資料
		this.sql = "UPDATE " + tablename + " SET " + colname + " = '" + newdata + "';";
		this.stmt.executeUpdate(this.sql);
	}

	// 依控制條件修改指定單一欄位部分資料
	public void updateDataByCondition(String tablename, String colname, String newdata, String ctrl_col,
			String ctrl_data) throws SQLException {
		// 修改資料，須給資料庫、修改欄、修改前資料(修改欄)、修改後資料
		// UPDATE test SET c1='avc' WHERE c1 IS 'NULL' ;
		this.sql = "UPDATE " + tablename + " SET " + colname + " = '" + newdata + "' WHERE " + ctrl_col + " = '"
				+ ctrl_data + "';";
		this.stmt.executeUpdate(this.sql);
	}

	// 依控制條件修改指定數個欄位部分資料
	public void updateDataByCondition(String tablename, String[] colname, String[] newdata, String ctrl_col,
			String ctrl_data) throws SQLException {
		// 修改資料，須給資料庫、修改欄、修改前資料(修改欄)、修改後資料
		// UPDATE test SET c1='avc' WHERE c1 IS 'NULL' ;
		this.sql = "UPDATE " + tablename + " SET ";
		for (int i = 0; i < colname.length; i++) {// 把欄位一一加入
			if (newdata[i].equals("null")) {
				this.sql += colname[i] + "=" + newdata[i];
			} else {
				this.sql += colname[i] + "='" + newdata[i] + "'";
			}
			if (i < colname.length - 1) {
				this.sql += ",";
			}
		}
		this.sql += " WHERE " + ctrl_col + " = '" + ctrl_data + "';";
		this.stmt.executeUpdate(this.sql);
	}

	// 依數個控制條件修改指定數個欄位部分資料
	public void updateDataByCondition(String tablename, String[] colname, String[] newdata, String[] ctrl_col,
			String[] ctrl_data) throws SQLException {
		// 修改資料，須給資料庫、修改欄、修改前資料(修改欄)、修改後資料
		// UPDATE test SET c1='avc' WHERE c1 IS 'NULL' ;
		this.sql = "UPDATE " + tablename + " SET ";
		for (int i = 0; i < colname.length; i++) {// 把欄位一一加入
			this.sql += colname[i] + "='" + newdata[i] + "'";
			if (i < colname.length - 1) {
				this.sql += ",";
			} else {
				this.sql += " WHERE ";
			}
		}
		for (int i = 0; i < ctrl_col.length; i++) {// 把欄位一一加入
			this.sql += ctrl_col[i] + "='" + ctrl_data[i] + "'";
			if (i < ctrl_col.length - 1) {
				this.sql += " and ";
			} else {
				this.sql += " ;";
			}
		}
		this.stmt.executeUpdate(this.sql);
	}

	// 刪除資料庫
	public void deleteAllData(String tablename) throws SQLException, UnsupportedEncodingException {
		// DELETE FROM test ;
		this.sql = "DELETE FROM " + tablename + ";";
		this.stmt.executeUpdate(this.sql);
	}

	// 刪除資料方法，須給資料庫、刪除欄、欲刪除格資料
	public void deleteDataByCondition(String tablename, String colname, String olddata) throws SQLException {
		// DELETE FROM test WHERE c1 = 'NULL';
		this.sql = "DELETE FROM " + tablename + " WHERE " + colname + " = '" + olddata + "';";
		this.stmt.executeUpdate(this.sql);
	}

	// 刪除資料方法，須給資料庫、刪除欄、欲刪除格資料(多條件)
	public void deleteDataByCondition(String tablename, String[] ctrl_col, String[] ctrl_data) throws SQLException {
		// DELETE FROM test WHERE c1 = 'NULL';
		this.sql = "DELETE FROM " + tablename + " WHERE ";
		for (int i = 0; i < ctrl_col.length; i++) {// 把欄位一一加入
			this.sql += ctrl_col[i] + "='" + ctrl_data[i] + "'";
			if (i < ctrl_col.length - 1) {
				this.sql += " and ";
			} else {
				this.sql += ";";
			}
		}
		this.stmt.executeUpdate(this.sql);
	}

	// 累加查詢資料至目前的查詢結果
	public void selectAddtoResult(Connection dbConn) throws SQLException {
		Statement stmt2 = dbConn.createStatement();
		ResultSet newresult = stmt2.executeQuery(this.sql);
		List<String[]> newQuery = getQueryList(newresult);
		String[] col = (String[]) newQuery.get(0);
		for (int i = 1; i < newQuery.size(); i++) {
			String[] row = (String[]) newQuery.get(i);
			this.rs.moveToInsertRow();
			for (int j = 0; j < col.length; j++) {
				this.rs.updateString(col[j], row[j]);
			}
			this.rs.insertRow();
		}
	}

	// 查詢全部資料，須給資料表
	public void selectDataFromTable(String tablename) throws SQLException, UnsupportedEncodingException {
		// SELLECT * FROM test
		this.sql = "SELECT * FROM " + tablename + ";";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename) throws SQLException, UnsupportedEncodingException {
		// 查詢全部資料，須給資料表
		// SELLECT * FROM test
		this.sql = "SELECT * FROM " + tablename + ";";
	}

	// 查詢某欄位(資料不重複)，須給資料庫、資料表、欄位
	public void selectDistinctFromTable(String tablename, String colname)
			throws SQLException, UnsupportedEncodingException {
		// select distinct(level) from test
		this.sql = "SELECT DISTINCT " + colname + " FROM " + tablename + ";";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDistinctFromTableSQL(String tablename, String colname)
			throws SQLException, UnsupportedEncodingException {
		// select distinct(level) from test
		this.sql = "SELECT DISTINCT " + colname + " FROM " + tablename + ";";
	}

	// 查詢某欄全部資料，須給資料表、查詢欄、欲查詢格資料
	public void selectDataFromTable(String tablename, String colname)
			throws SQLException, UnsupportedEncodingException {
		// SELLECT c1,c2 FROM test
		this.sql = "SELECT " + colname + " FROM " + tablename + ";";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String colname)
			throws SQLException, UnsupportedEncodingException {
		// 查詢某欄全部資料，須給資料表、查詢欄、欲查詢格資料
		// SELLECT c1,c2 FROM test
		this.sql = "SELECT " + colname + " FROM " + tablename + ";";
	}

	// 查詢某欄全部資料，須給資料表、查詢欄、欲查詢格的相對格(別欄)資料
	public void selectDataFromTable(String tablename, String ctrl_col, String ctrl_data) throws SQLException {
		// SELECT c1 FROM test WHERE c1 IS NULL
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data + "';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String ctrl_col, String ctrl_data) throws SQLException {
		// SELECT c1 FROM test WHERE c1 IS NULL
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data + "';";
	}

	// 查詢某欄全部資料，須給資料表、查詢欄、欲查詢格的相對格(別欄)資料
	public void selectContainFromTable(String tablename, String ctrl_col, String Keyword) throws SQLException {
		// SELECT c1 FROM test WHERE c1 IS NULL
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + Keyword + "%';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectContainFromTableSQL(String tablename, String ctrl_col, String Keyword) throws SQLException {
		// 查詢某欄全部資料，須給資料表、查詢欄、欲查詢格的相對格(別欄)資料
		// SELECT c1 FROM test WHERE c1 IS NULL
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + Keyword + "%';";
	}

	// SELECT c1 FROM test WHERE c1 LIKE '%keyword%';
	public void selectContainFromTable(String tablename, String colname, String ctrl_col, String Keyword)
			throws SQLException {
		this.sql = "SELECT " + colname + " FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + Keyword + "%';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectContainFromTableSQL(String tablename, String colname, String ctrl_col, String Keyword)
			throws SQLException {
		this.sql = "SELECT " + colname + " FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + Keyword + "%';";
	}

	// SELECT c1,c2... FROM test WHERE c1 LIKE '%keyword%';
	public void selectContainFromTable(String tablename, String colname[], String ctrl_col, String keyword)
			throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + keyword
				+ "%';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectContainFromTableSQL(String tablename, String colname[], String ctrl_col, String keyword)
			throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + " WHERE " + ctrl_col + " LIKE '%" + keyword
				+ "%';";
	}

	// SELECT c1 FROM test WHERE c2=data;
	public void selectDataFromTable(String tablename, String colname, String ctrl_col, String ctrl_data)
			throws SQLException {
		this.sql = "SELECT " + colname + " FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data + "';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String colname, String ctrl_col, String ctrl_data)
			throws SQLException {
		this.sql = "SELECT " + colname + " FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data + "';";
	}

	// SELECT c1,c2... FROM test WHERE c2=data;
	public void selectDataFromTable(String tablename, String colname[], String ctrl_col, String ctrl_data)
			throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data
				+ "';";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String colname[], String ctrl_col, String ctrl_data)
			throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + " WHERE " + ctrl_col + " = '" + ctrl_data
				+ "';";
	}

	// SELECT c1,c2... FROM test;
	public void selectDataFromTable(String tablename, String colname[]) throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + ";";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String colname[]) throws SQLException {
		this.sql = "SELECT ";
		for (int i = 0; i < colname.length - 1; i++) {
			this.sql += colname[i] + ",";
		}
		this.sql += colname[colname.length - 1] + " FROM " + tablename + ";";
	}

	// SELECT c1 FROM test WHERE ctrl_col IN ('data1','data2',...);
	public void selectDataFromTable(String tablename, String ctrl_col, String[] ctrl_data) throws SQLException {
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " IN (";
		for (int i = 0; i < ctrl_data.length - 1; i++) {
			this.sql += "'" + ctrl_data[i] + "',";
		}
		this.sql += "'" + ctrl_data[ctrl_data.length - 1] + "');";
		this.rs = this.stmt.executeQuery(this.sql);
	}

	public void selectDataFromTableSQL(String tablename, String ctrl_col, String[] ctrl_data) throws SQLException {
		this.sql = "SELECT * FROM " + tablename + " WHERE " + ctrl_col + " IN (";
		for (int i = 0; i < ctrl_data.length - 1; i++) {
			this.sql += "'" + ctrl_data[i] + "',";
		}
		this.sql += "'" + ctrl_data[ctrl_data.length - 1] + "');";
	}

	/**
	 * 取得時間區間專用 (包含頭尾)
	 * 
	 * @param tablename 表格名稱
	 * @param ctrl_col  時間欄位名稱 (yyyy-mm-ddThh:mm:ss.sssZ)
	 * @param start     起始時間 (mm/dd/YY)
	 * @param end       結束時間 (mm/dd/YY)
	 * @throws SQLException
	 */
	// SELECT * FROM test WHERE ctrl_col BETWEEN 'start' AND 'end';
	public void selectBetweenFromTable(String tablename, String ctrl_col, String start, String end) {
		this.sql = "SELECT * FROM " + tablename + " WHERE CONVERT_TZ(" + ctrl_col + ",'+00:00','+00:00') BETWEEN ";
		this.sql += "DATE_FORMAT(STR_TO_DATE('" + start
				+ "','%m/%d/%Y'),'%Y-%m-%d') AND date_add(DATE_FORMAT(STR_TO_DATE('" + end
				+ "','%m/%d/%Y'),'%Y-%m-%d'),interval 1 day);";
		try {
			this.rs = this.stmt.executeQuery(this.sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// 產品資料排序
	public List<String[]> getSortList(String teblename, String ctrl_Col, List<String[]> TargetRow, String sort_Col,
			String rule) {
		// orderList--targetRow of database; orderCol--sort column, rule--DESC or ASC
		// 排序
		String[] search_pid = new String[TargetRow.size()];
		for (int i = 0; i < TargetRow.size(); i++) {
			String[] searchstr = (String[]) TargetRow.get(i);
			search_pid[i] = searchstr[0];
		}
		this.sql = "SELECT * FROM " + teblename + " WHERE " + ctrl_Col + " IN (";
		for (int i = 0; i < search_pid.length - 1; i++) {
			this.sql += "'" + search_pid[i] + "',";
		}
		this.sql += "'" + search_pid[search_pid.length - 1] + "') ORDER BY CAST(" + sort_Col + " AS UNSIGNED) " + rule;
		try {
			this.setSQLQuery(this.sql);
			TargetRow = this.getQueryRow();
		} catch (SQLException ex) {
		}
		return TargetRow;
	}

	/**
	 * 資料排序
	 * 
	 * @param tablename 資料表名稱
	 * @param Colset    排序指定欄
	 * @param rule      DESC or ASC
	 * @return
	 */
	public List<String[]> getSortList(String tablename, String Colset, String rule) {
		// orderList--targetRow of database; orderCol--sort column, rule--DESC or ASC
		// 排序
		List<String[]> sortList = new ArrayList<String[]>();
		this.sql = "SELECT * FROM " + tablename + " ORDER BY CAST(" + Colset + " AS UNSIGNED) " + rule;
		try {
			this.setSQLQuery(this.sql);
			sortList = this.getQueryList();
		} catch (Exception ex) {
		}
		return sortList;
	}

	// 將結果集合資料轉成List輸出, 第 0 筆資料為欄位名稱
	public List<String[]> getQueryList() {
		List<String[]> dataList = new ArrayList<String[]>();
		ResultSetMetaData rsmd;
		try {
			rsmd = this.rs.getMetaData();
			String[] col = new String[rsmd.getColumnCount()];
			for (int i = 0; i < col.length; i++) {
				col[i] = rsmd.getColumnName(i + 1);
			}
			dataList.add(col);
			while (this.rs.next()) {
				String[] row = new String[rsmd.getColumnCount()];
				for (int i = 0; i < row.length; i++) {// 將各個欄位資料均轉成字串型態-->取出後再依系統與資料庫設計需求做調整
					if (rs.getObject(col[i]) != null) {// 有取到資料
						row[i] = rs.getObject(col[i]).toString();
					} else {
						row[i] = "null";// 未取得資料時均設為空值
					}
					row[i] = row[i].replaceAll("\r\n", "<br>");
					row[i] = row[i].replaceAll("\n", "<br>");
				}
				dataList.add(row);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			String[] col = { "查無資料", "不要亂搞" };
			dataList.add(col);
			e.printStackTrace();
		}
		return dataList;
	}

	// 代入結果集合資料轉乘List輸出, 第 0 筆資料為欄位名稱
	public List<String[]> getQueryList(ResultSet rs) throws SQLException {
		List<String[]> dataList = new ArrayList<String[]>();
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] col = new String[rsmd.getColumnCount()];
		for (int i = 0; i < col.length; i++) {
			col[i] = rsmd.getColumnName(i + 1);
		}
		dataList.add(col);
		while (rs.next()) {
			String[] row = new String[rsmd.getColumnCount()];
			for (int i = 0; i < row.length; i++) {// 將各個欄位資料均轉成字串型態-->取出後再依系統與資料庫設計需求做調整
				if (rs.getObject(col[i]) != null) {// 有取到資料
					row[i] = rs.getObject(col[i]).toString();
				} else {
					row[i] = "null";// 未取得資料時均設為空值
				}
				row[i] = row[i].replaceAll("\r\n", "<br>");
				row[i] = row[i].replaceAll("\n", "<br>");
			}
			dataList.add(row);
		}
		return dataList;
	}

	// 代入結果集合資料轉乘List輸出, 自己設定欄名(欄數和分開的row對好)
	public List<String[]> getQueryList(String[] col) throws SQLException {
		List<String[]> dataList = new ArrayList<String[]>();
		dataList.add(col);
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] colorg = new String[rsmd.getColumnCount()];
		for (int i = 0; i < colorg.length; i++) {
			colorg[i] = rsmd.getColumnName(i + 1);
		}
		while (this.rs.next()) {
			String[] row = new String[rsmd.getColumnCount()];
			for (int i = 0; i < row.length; i++) {// 將各個欄位資料均轉成字串型態-->取出後再依系統與資料庫設計需求做調整
				if (rs.getObject(colorg[i]) != null) {// 有取到資料
					row[i] = rs.getObject(colorg[i]).toString();
				} else {
					row[i] = "null";// 未取得資料時均設為空值
				}
				row[i] = row[i].replaceAll("\r\n", "<br>");
				row[i] = row[i].replaceAll("\n", "<br>");
			}
			dataList.add(row);
		}
		return dataList;
	}

	public List<String[]> getQueryRow() throws SQLException {
		List<String[]> dataList = new ArrayList<String[]>();
		ResultSetMetaData rsmd = rs.getMetaData();
		String[] colorg = new String[rsmd.getColumnCount()];
		for (int i = 0; i < colorg.length; i++) {
			colorg[i] = rsmd.getColumnName(i + 1);
		}
		while (this.rs.next()) {
			String[] row = new String[rsmd.getColumnCount()];
			for (int i = 0; i < row.length; i++) {// 將各個欄位資料均轉成字串型態-->取出後再依系統與資料庫設計需求做調整
				if (rs.getObject(colorg[i]) != null) {// 有取到資料
					row[i] = rs.getObject(colorg[i]).toString();
				} else {
					row[i] = "null";// 未取得資料時均設為空值
				}
				row[i] = row[i].replaceAll("\r\n", "<br>");
				row[i] = row[i].replaceAll("\n", "<br>");
			}
			dataList.add(row);
		}
		return dataList;
	}

	// 用POI將DBtable資料匯出成excel, 包含所有欄位名稱
	public void exportTableToExcel(String tablename, String filename) throws SQLException {
		// 寫入資料
		POIFSFileSystem fs = new POIFSFileSystem();
		HSSFWorkbook wb = new HSSFWorkbook();
		try {
			selectDataFromTable(tablename);// 取得表格所有資料產生ResultSet
			List<String[]> dataList = getQueryList();
			HSSFSheet sheet = wb.createSheet();
			HSSFCell cell;
			for (int i = 0; i < dataList.size(); i++) {
				HSSFRow row = sheet.createRow(i);
				String[] dataStr = (String[]) dataList.get(i);
				for (int j = 0; j < dataStr.length; j++) {
					dataStr[j] = dataStr[j].replaceAll("<br>", "\n");
					cell = row.createCell(j);
					if (dataStr[j].equals("null") || dataStr[j] == null) {
						dataStr[j] = "";
					}
					cell.setCellValue(dataStr[j].toString());
				}
			}
			this.deputy = ".xls";
			FileOutputStream fout = new FileOutputStream(filename + this.deputy);
			wb.write(fout);
			fout.flush();
			fout.close();
			wb.close();
			fs.close();
		} catch (IOException e) {
		}
	}

	// 將表格資料匯出至檔案, 包含所有欄位名稱
	public void exportTableToFileCSV(String tablename, String FilePath)
			throws SQLException, UnsupportedEncodingException, IOException {
		selectDataFromTable(tablename);// 取得表格所有資料產生ResultSet
		List<String[]> Querylist = getQueryList();// 將ResultSet轉換成List
		Iterator<String[]> it = Querylist.iterator();
		String[] col = (String[]) it.next();// 第一行欄名
		try {
			try (FileWriter fw = new FileWriter(FilePath, false)) {
				for (String col1 : col) {
					fw.write(col1 + ",");
				} // 先寫完欄位名稱
				fw.write("\r\n");// 換行
				while (it.hasNext()) {
					String[] row = (String[]) it.next();
					for (String row1 : row) {
						if (row1.equals("null") || row1 == null) {
							row1 = "";
						}
						// 寫一筆資料
						fw.write(row1 + ",");
					}
					fw.write("\r\n");// 換行
				}
				fw.close();
			}
		} catch (IOException e) {

		}
	}

	public void exportTableToFileTXT(String tablename, String FilePath)
			throws SQLException, UnsupportedEncodingException, IOException {
		this.selectDataFromTable(tablename);// 取得表格所有資料產生ResultSet
		List<String[]> Querylist = this.getQueryList();// 將ResultSet轉換成List
		Iterator<String[]> it = Querylist.iterator();
		String[] col = (String[]) it.next();// 第一行欄名
		try {
			try (FileWriter fw = new FileWriter(FilePath, false)) {
				for (String col1 : col) {
					fw.write(col1 + "|");
				} // 先寫完欄位名稱
				fw.write("\r\n");// 換行
				while (it.hasNext()) {
					String[] row = (String[]) it.next();
					for (String row1 : row) {
						if (row1.equals("null") || row1 == null) {
							row1 = "";
						}
						// 寫一筆資料
						fw.write(row1 + "|");
					}
					fw.write("\r\n");// 換行
				}
				fw.close();
			}
		} catch (IOException e) {

		}
	}

	public void exportTableToFileTXT(String FilePath, List<String[]> Querylist)
			throws SQLException, UnsupportedEncodingException, IOException {
		Iterator<String[]> it = Querylist.iterator();
		String[] col = (String[]) it.next();// 第一行欄名
		try {
			try (FileWriter fw = new FileWriter(FilePath, false)) {
				for (String col1 : col) {
					fw.write(col1 + "|");
				} // 先寫完欄位名稱
				fw.write("\r\n");// 換行
				while (it.hasNext()) {
					String[] row = (String[]) it.next();
					for (String row1 : row) {
						if (row1.equals("null") || row1 == null) {
							row1 = "";
						}
						// 寫一筆資料
						fw.write(row1 + "|");
					}
					fw.write("\r\n");// 換行
				}
				fw.close();
			}
		} catch (IOException e) {

		}
	}

	public void exportTableToFileTXT(String FilePath, String col, List<String[]> Querylist)
			throws SQLException, UnsupportedEncodingException, IOException {// 自設欄位
		Iterator<String[]> it = Querylist.iterator();
		it.next();// 第一行欄名
		try {
			try (FileWriter fw = new FileWriter(FilePath, false)) {
				fw.write(col); // 先寫完欄位名稱(自設)
				fw.write("\r\n");// 換行
				while (it.hasNext()) {
					String[] row = (String[]) it.next();
					for (String row1 : row) {
						if (row1.equals("null") || row1 == null) {
							row1 = "";
						}
						// 寫一筆資料
						fw.write(row1 + "|");
					}
					fw.write("\r\n");// 換行
				}
				fw.close();
			}
		} catch (IOException e) {
		}
	}

	public boolean isUserPass(String username, String password, String table) {
		boolean isValidated = true;
		try {
			sql = "SELECT ACCOUNT,PASSWORD FROM " + table + " WHERE ACCOUNT='" + username + "' AND PASSWORD='"
					+ password + "';";
			setSQLQuery(sql);
			List<String[]> data = getQueryRow();
			if (data.isEmpty()) {
				isValidated = false;
			}
		} catch (SQLException ex) {
			isValidated = false;
		}
		return isValidated;
	}

	public String[] getAddress(String code) throws SQLException {
		String[] loc = new String[3];
		String[] codes = code.split("-");
		String target = codes[0] + codes[1] + codes[2];
		String[] address = { "city", "town", "village" };
		selectDataFromTable("assessloc", address, "ZIP", target);
		List<String[]> dataList = getQueryList();
		String[] row = (String[]) dataList.get(1);
		for (int i = 0; i < row.length; i++) {
			loc[i] = row[i];
		}
		return loc;
	}

	public String getLocation(String code) throws SQLException {
		String loc = "";
		String[] codes = code.split("-");
		String target = codes[0] + codes[1] + codes[2];
		String[] address = { "city", "town", "village" };
		selectDataFromTable("assessloc", address, "ZIP", target);
		List<String[]> dataList = getQueryList();
		String[] row = (String[]) dataList.get(1);
		for (int i = 0; i < row.length; i++) {
			loc += row[i];
		}
		return loc;
	}
}
