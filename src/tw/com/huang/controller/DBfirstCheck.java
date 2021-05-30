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
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tw.com.huang.controller.DBfirstCheck;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

/**
 *
 * @author G551
 */
@WebServlet(name = "DBfirstCheck", urlPatterns = { "/firstCheck.do" })
public class DBfirstCheck extends HttpServlet {

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
		String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
		String filepath = "setting/";
		String dbcfile = "dbconn_param.txt";
		File f = new File(rootpath + filepath + dbcfile);
		Scanner sc = new Scanner(f);
		String[] dbconnParam = new String[4];// 資料庫連線參數
		int l = 0;
		while (sc.hasNextLine()) {
			dbconnParam[l] = sc.nextLine().toString();
			l = l + 1;
		}
		sc.close();
		dbconnParam[2] = request.getParameter("user").trim();
		dbconnParam[3] = request.getParameter("pass").trim();
		DBConn dbc = new DBConn(dbconnParam);
		if (dbc.getIsConn()) {
			SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
			List<String[]> tablel = sq.getAllTables("hmhouse_hmhouse");// 取得所有有權限的資料表
			String folderpath = rootpath + "datafile/";
			File folder = new File(folderpath);
			if (!folder.exists()) {// 不存在則創建
				folder.mkdir();
			}

			for (int k = 0; k < tablel.size(); k++) {
				String[] table = (String[]) tablel.get(k);
				String dbfile = folderpath + "table_" + table[0] + ".xls";// 存檔路徑
				sq.exportTableToExcel(table[0], dbfile);
			}

			if (session.getAttribute("table") != null) {
				session.removeAttribute("table");
			}
			session.setAttribute("user", dbconnParam[2]);
			session.setAttribute("tablel", tablel);
			session.setAttribute("dbconnParam", dbconnParam);
			response.sendRedirect("dbmanage.jsp");
			return;
		} else {
			RequestDispatcher dispatch = request.getRequestDispatcher("dberror_page.jsp");
			dispatch.forward(request, response);
		}

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet firstCheck</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet firstCheck at " + request.getContextPath() + "</h1>");
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
			Logger.getLogger(DBfirstCheck.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(DBfirstCheck.class.getName()).log(Level.SEVERE, null, ex);
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
