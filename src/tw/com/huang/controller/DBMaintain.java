/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tw.com.huang.controller.DBMaintain;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

/**
 *
 * @author G551
 */
@WebServlet(name = "DBMaintain", urlPatterns = { "/DBMaintain.do" })
public class DBMaintain extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
	 * methods.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException      if a servlet-specific error occurs
	 * @throws IOException           if an I/O error occurs
	 * @throws java.sql.SQLException
	 */
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		if (session.isNew()) {
			response.sendRedirect("dberror_page.jsp");
			return;
		}
		String[] connParam = (String[]) session.getAttribute("dbconnParam");
		DBConn dbc = new DBConn(connParam);
		SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
		String table = (String) session.getAttribute("table");// 改成彈性化
		@SuppressWarnings("unchecked")
		List<String[]> Querylist = (List<String[]>) session.getAttribute("tablelist");
		String[] colname = (String[]) Querylist.get(0);// 第一行陣列是COLNAME
		String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
		if (session.getAttribute("AC") != null) {
			String action = (String) session.getAttribute("AC");
			switch (action) {
			case "I": {
				String data = "";
				for (int i = 0; i < colname.length - 1; i++) {// 每個欄內容
					if (request.getParameter(colname[i]).equals("")) {
						data = data + "null" + "|";
					} else {
						String input = request.getParameter(colname[i]);
						data = data + input + "|";
					}
				}
				if (request.getParameter(colname[colname.length - 1]).equals("")) {
					data = data + "null";
				} else {
					String input = request.getParameter(colname[colname.length - 1]);
					data = data + input;
				}
				data = new String(data.getBytes("8859_1"), "UTF-8");
				String[] newdata = data.split("\\|");
				String success = sq.insertDataToDB(table, newdata);
				String dbfile = rootpath + "datafile/table_" + table + ".xls";// 存檔路徑
				String success2 = sq.exportTableToExcel(table, dbfile);
				String success3 = sq.selectDataFromTable(table);
				session.setAttribute("success", success + success2 + success3);
				break;
			}
			case "U": {
				String[] selected = (String[]) session.getAttribute("select");
				String success = "";
				for (int j = 0; j < selected.length; j++) {
					String data = "";
					for (int i = 0; i < colname.length - 1; i++) {// 每個欄內容
						if (request.getParameter(j + colname[i]).equals("")) {
							data = data + "null" + "|";
						} else {
							String input = request.getParameter(j + colname[i]);
							data = data + input + "|";
						}
					}
					if (request.getParameter(j + colname[colname.length - 1]).equals("")) {
						data = data + "null";
					} else {
						String input = request.getParameter(j + colname[colname.length - 1]);
						data = data + input;
					}
					data = new String(data.getBytes("8859_1"), "UTF-8");// 統整欄內容
					String row0 = (String) selected[j];
					String[] newdata = data.split("\\|");
					success = sq.updateDataByCondition(table, colname, newdata, colname[0], new String(row0.getBytes("8859_1"), "UTF-8"));
				}
				String dbfile = rootpath + "datafile/table_" + table + ".xls";// 存檔路徑
				String success2 = sq.exportTableToExcel(table, dbfile);
				String success3 = sq.selectDataFromTable(table);
				session.setAttribute("success", success + success2 + success3);
				break;
			}
			case "D": {
				String[] selected = request.getParameterValues("row");
				String success = "";
				for (String selected1 : selected) {
					String data = new String(selected1.getBytes("8859_1"), "UTF-8");
					if (table.equals("product") || table.equals("introduction")) { // 如果是產品或介紹刪除，則刪除相關照片
						String[] imgcol = { "img1_name", "img2_name" };
						sq.selectDataFromTable(table, imgcol, colname[0], data);
						List<String[]> list = sq.getQueryRow();
						String[] liststr = (String[]) list.get(0);
						File file = new File(rootpath + "datafile/" + liststr[0]);
						file.delete();
						file = new File(rootpath + "datafile/" + liststr[1]);
						file.delete();
					}
					success = sq.deleteDataByCondition(table, colname[0], data);
				}
				String dbfile = rootpath + "datafile/table_" + table + ".xls";// 存檔路徑
				String success2 = sq.exportTableToExcel(table, dbfile);
				String success3 = sq.selectDataFromTable(table);
				session.setAttribute("success", success + success2 + success3);
			}
			}
		} else {
			dbc.closeConnection();// 關閉連線
			response.sendRedirect("dbtableMaintain.jsp");
			return;
		}
		List<String[]> tablelist = sq.getQueryList();
		session.setAttribute("tablelist", tablelist);

		dbc.closeConnection();// 關閉連線
		response.sendRedirect("dbtableMaintain.jsp");

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet DBMaintain</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet DBMaintain at " + request.getContextPath() + "</h1>");
			out.println("</body>");
			out.println("</html>");
		}
	}

	// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the
	// + sign on the left to edit the code.">
	/**
	 * Handles the HTTP <code>GET</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (SQLException ex) {
			Logger.getLogger(DBMaintain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Handles the HTTP <code>POST</code> method.
	 *
	 * @param request  servlet request
	 * @param response servlet response
	 * @throws ServletException if a servlet-specific error occurs
	 * @throws IOException      if an I/O error occurs
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			processRequest(request, response);
		} catch (SQLException ex) {
			Logger.getLogger(DBMaintain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * Returns a short description of the servlet.
	 *
	 * @return a String containing servlet description
	 */
	@Override
	public String getServletInfo() {
		return "Short description";
	}// </editor-fold>

}
