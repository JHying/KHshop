/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tw.com.huang.controller.Search;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;
import tw.com.huang.util.DataUtil;

/**
 *
 * @author G551
 */
public class Search extends HttpServlet {

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
	@SuppressWarnings("unchecked")
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException, SQLException {
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		if (session.isNew()) {
			response.sendRedirect("error.jsp");
			return;
		}
		String[] dbconnParam = (String[]) session.getAttribute("dbconnParam");
		DBConn dbc = new DBConn(dbconnParam);
		SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
		List<String[]> searchList = new ArrayList<String[]>();
		List<String[]> productl = (List<String[]>) session.getAttribute("product");
		if (request.getParameter("keyword") != null) {
			String keyword = new String(request.getParameter("keyword").getBytes("8859_1"), "UTF-8");
			sq.selectContainFromTable("product", "name", keyword);
			searchList = sq.getQueryRow();
			sq.selectContainFromTable("product", "price", keyword);
			searchList.addAll(sq.getQueryRow());
			sq.selectContainFromTable("product", "description", keyword);
			searchList.addAll(sq.getQueryRow());
			sq.selectContainFromTable("product", "class", keyword);
			searchList.addAll(sq.getQueryRow());
			// 刪除重複資料
			searchList = DataUtil.repeatRemove(searchList);

		} else if (request.getParameter("class") != null) {
			if (request.getParameter("class").equals("nps")) {
				sq.selectContainFromTable("product", "new", "y");
			} else {
				String c = new String(request.getParameter("class").getBytes("8859_1"), "UTF-8");
				sq.selectContainFromTable("product", "class", c);
			}
			searchList = sq.getQueryRow();
		} else if (request.getParameter("ob") != null) {
			searchList = (List<String[]>) session.getAttribute("searchList");
			String[] orderset = request.getParameter("ob").split("\\|");
			// 排序
			searchList = sq.getSortList("product", "product_id", searchList, orderset[0], orderset[1]);
		} else if (request.getParameter("single") != null) {
			String singleid = request.getParameter("single");
			sq.selectContainFromTable("product", "product_id", singleid);
			searchList = sq.getQueryList();
			session.setAttribute("single", searchList);
			sq.closeSQLStatement();
			dbc.closeConnection();
			response.sendRedirect("single.jsp");
			return;
		}

		if (searchList.isEmpty()) {
			searchList = productl;
		}

		session.setAttribute("searchList", searchList);
		sq.closeSQLStatement();
		dbc.closeConnection();
		response.sendRedirect("products.jsp");

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet search</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet search at " + request.getContextPath() + "</h1>");
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
			Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(Search.class.getName()).log(Level.SEVERE, null, ex);
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
