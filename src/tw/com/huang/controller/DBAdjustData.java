/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
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

import tw.com.huang.controller.DBAdjustData;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

/**
 *
 * @author H_yin
 */
@WebServlet(name = "DBAdjustData", urlPatterns = { "/AdjustData.do" })
public class DBAdjustData extends HttpServlet {

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
		DBConn db = new DBConn(connParam);
		Connection dbConn = db.getConnection();
		SQLPrepareProcess sq = new SQLPrepareProcess(dbConn);
		if (request.getParameter("sql") != null) {
			String sql = request.getParameter("sql");
			sql = new String(sql.getBytes("8859_1"), "UTF-8");
			String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
			List<String[]> SQLQuery = null;
			sq.setSQLQuery(sql);
			SQLQuery = sq.getQueryList();
			session.setAttribute("SQLQuery", SQLQuery);
			String msg = "";
			if (SQLQuery == null) {
				sq.setSQLUpdate(sql);
				List<String[]> tablel = sq.getAllTables("khshop");
				for (int i = 1; i < tablel.size(); i++) {
					String[] tables = (String[]) tablel.get(i);
					String tablename = tables[0];
					String dbfile = rootpath + "datafile/table_" + tablename + ".xls";// 存檔路徑
					msg = sq.exportTableToExcel(tablename, dbfile);
				}
				session.setAttribute("success", msg);
			}
		} else if (session.getAttribute("table") == null || request.getParameter("table") != null) {
			String table = request.getParameter("table");
			session.setAttribute("table", table);
			sq.selectDataFromTable(table);
			List<String[]> tablelist = sq.getQueryList();
			session.setAttribute("tablelist", tablelist);
		} else if (request.getParameter("back") != null) {
			session.removeAttribute("table");
			session.removeAttribute("tablelist");
		} else {

		}
		db.closeConnection();
		response.sendRedirect("dbmanage.jsp");

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet ImportData</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet ImportData at " + request.getContextPath() + "</h1>");
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
			Logger.getLogger(DBAdjustData.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(DBAdjustData.class.getName()).log(Level.SEVERE, null, ex);
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
