/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.controller;

import com.oreilly.servlet.MultipartRequest;

import tw.com.huang.controller.DBUpdateWithFile;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

import java.io.File;
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

/**
 *
 * @author G551
 */
@WebServlet(name = "DBUpdateWithFile", urlPatterns = { "/UpdateWithFile.do" })
public class DBUpdateWithFile extends HttpServlet {
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
		if (session.getAttribute("dbconnParam") == null) {
			response.sendRedirect("dberror_page.jsp");
			return;
		} else {
			String[] connParam = (String[]) session.getAttribute("dbconnParam");
			DBConn db = new DBConn(connParam);
			Connection dbConn = db.getConnection();
			SQLPrepareProcess sq = new SQLPrepareProcess(dbConn);
			String table = (String) session.getAttribute("table");
			String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
			String saveDirectory = rootpath + "datafile/";// 存檔路徑
			File uploadPath = new File(saveDirectory);
			if (!uploadPath.exists()) {// 沒有則創建
				uploadPath.mkdir();
			}
			int maxPostSize = 1 * 1024 * 1024 * 1024;// 允許上傳檔案最大容量(1GB)
			String encoding = "UTF-8";
			// 呼叫MultipartRequest取得檔案(產品)
			MultipartRequest mr = new MultipartRequest(request, saveDirectory, maxPostSize, encoding);

			if (mr.getFile("FILENAME") != null) {
				String filename = mr.getOriginalFileName("FILENAME");// 取得上傳檔名
				String filepath = saveDirectory + filename;
				String msg = sq.importExceltoDB(table, filepath);
				// 匯入後刪除上傳檔案
				File uf = new File(filepath);
				uf.delete();
				// 生成新的csv檔
				String dbfile = saveDirectory + "table_" + table + ".xls";// 存檔路徑
				String msg2 = sq.exportTableToExcel(table, dbfile);
				session.setAttribute("success", msg + msg2);
			} else if (mr.getFile("FILENAME2") != null) {
				String filename = mr.getOriginalFileName("FILENAME2");// 取得上傳檔名
				String filepath = saveDirectory + filename;
				String msg = sq.deleteAllData(table);
				String msg2 = sq.importExceltoDB(table, filepath);
				// 匯入後刪除上傳檔案
				File uf = new File(filepath);
				uf.delete();
				// 生成新的csv檔
				String dbfile = saveDirectory + "table_" + table + ".xls";// 存檔路徑
				String msg3 = sq.exportTableToExcel(table, dbfile);
				session.setAttribute("success", msg + msg2 + msg3);
			} else {
				// 呼叫MultipartRequest取得檔案(圖片)
				// 上傳多圖
				try {
//                    Enumeration filesname = mr.getFileNames();
					session.setAttribute("success", "圖片上傳成功");
				} catch (Exception e) {
					session.setAttribute("success", "圖片上傳發生錯誤");
				}
			}

			sq.selectDataFromTable(table);
			List<String[]> tablelist = sq.getQueryList();
			session.setAttribute("tablelist", tablelist);

			db.closeConnection();
			response.sendRedirect("dbimport.jsp");
		}

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet UpdateWithFile</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<h1>Servlet UpdateWithFile at " + request.getContextPath() + "</h1>");
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
			Logger.getLogger(DBUpdateWithFile.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(DBUpdateWithFile.class.getName()).log(Level.SEVERE, null, ex);
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
