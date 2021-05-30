package tw.com.huang.service;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import javax.servlet.http.HttpServletRequest;

import lombok.Getter;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

public class WelcomeService {

	private DBConn dbc;
	private SQLPrepareProcess sq;
	@Getter
	private String[] dbconnParam = new String[4];

	public WelcomeService(HttpServletRequest request) {
		String rootpath = request.getServletContext().getRealPath("/") + "/";
		String path = "setting/";
		String dbcfile = "dbconn_param.txt";
		File f = new File(rootpath + path + dbcfile);
		Scanner sc;
		try {
			sc = new Scanner(f);
			int l = 0;
			while (sc.hasNextLine()) {
				this.dbconnParam[l] = sc.nextLine().toString();
				l = l + 1;
			}
			sc.close();
			this.dbc = new DBConn(this.dbconnParam);
			this.sq = new SQLPrepareProcess(dbc.getConnection());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public List<String[]> getTableData(String tablename) {
		List<String[]> dataList = null;
		try {
			sq.selectDataFromTable(tablename);
			dataList = sq.getQueryRow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

	public List<String[]> getDistinctCol(String tablename, String col) {
		List<String[]> dataList = null;
		try {
			sq.selectDistinctFromTable(tablename, col);
			dataList = sq.getQueryRow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

	public List<String[]> getContainFromTable(String tablename, String col, String keyword) {
		List<String[]> dataList = null;
		try {
			sq.selectContainFromTable(tablename, col, keyword);
			dataList = sq.getQueryRow();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

	/**
	 * 取得產品排序
	 * 
	 * @param rowlist 欲排序資料
	 * @param sortCol 排序指定欄
	 * @param rule    DESC or ASC
	 * @return
	 */
	public List<String[]> getSortList(List<String[]> rowlist, String sortCol, String rule) {
		List<String[]> dataList = null;
		try {
			dataList = sq.getSortList("product", "product_id", rowlist, sortCol, rule);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataList;
	}

}
