package tw.com.huang.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
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
 * Servlet implementation class Filter
 */
@WebServlet(name = "DBFilter", urlPatterns = { "/DBFilter.do" })
public class DBFilter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
			throws IOException, SQLException {
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		if (session.isNew()) {
			response.sendRedirect("dberror_page.jsp");
			return;
		}

		String[] connParam = (String[]) session.getAttribute("dbconnParam");
		DBConn dbc = new DBConn(connParam);
		SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
		String table = (String) session.getAttribute("table");
		List<String[]> Querylist = new ArrayList<String[]>();
		if (request.getParameter("start") != null && request.getParameter("end") != null) {
			if (request.getParameter("start").equals("") && request.getParameter("end").equals("")) {
				sq.selectDataFromTable(table);
				Querylist = sq.getQueryList();
			} else {
				String start = request.getParameter("start");
				String end = request.getParameter("end");
				sq.selectBetweenFromTable(table, "ordertime", start, end);
				Querylist = sq.getQueryList();
			}
			session.setAttribute("tablelist", Querylist);
			response.sendRedirect("dbtableMaintain.jsp");
			return;
		} else {
			// 若沒選擇，顯示全部資料
			sq.selectDataFromTable(table);
			Querylist = sq.getQueryList();
			session.setAttribute("tablelist", Querylist);
			response.sendRedirect("dbtableMaintain.jsp");
			return;
		}
	}

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public DBFilter() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			processRequest(request, response);
		} catch (SQLException ex) {
			Logger.getLogger(DBMaintain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			processRequest(request, response);
		} catch (SQLException ex) {
			Logger.getLogger(DBMaintain.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

}
