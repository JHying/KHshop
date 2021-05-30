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

import tw.com.huang.controller.Cart;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;

/**
 *
 * @author G551
 */
public class Cart extends HttpServlet {
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
		String[] dbconnParam = (String[]) session.getAttribute("dbconnParam");// 資料庫連線參數
		DBConn dbc = new DBConn(dbconnParam);
		SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
		List<String[]> cart = new ArrayList<String[]>();
		List<String> price = new ArrayList<String>();
		String pid = "";// 產品ID
		int x = 0;// 已存在的產品編號
		boolean Exist = false;
		int count = 0;// 總價
		String discount_code = "660915213";//優惠代碼

		if (session.getAttribute("cartproduct") != null) {
			cart = (List<String[]>) session.getAttribute("cartproduct");
			price = (List<String>) session.getAttribute("cartprice");
			count = Integer.parseInt(session.getAttribute("cartcount").toString());
		}

		if (request.getParameter("add") != null) {// 新增產品
			pid = request.getParameter("addproduct");
			for (int i = 0; i < cart.size(); i++) {
				String[] product = (String[]) cart.get(i);
				if (product[0].equals(pid)) {
					x = i;
					Exist = true;
					break;
				}
			}
		} else if (request.getParameter("deleteAll") != null) {// 一次清空購物車產品
			session.removeAttribute("cartproduct");
			session.removeAttribute("cartprice");
			session.removeAttribute("cartcount");
			session.removeAttribute("discount_code");
			sq.closeSQLStatement();
			dbc.closeConnection();
			response.sendRedirect("cart.jsp");
			return;
		} else if (request.getParameter("update") != null) {// 更新購物車
			for (int i = 0; i < cart.size(); i++) {
				String[] product = (String[]) cart.get(i);
				String singlePrice = product[2];
				//優惠代碼正確則算成本價
				if (request.getParameter("discount").equals(discount_code)) {
					singlePrice = product[11];
					session.setAttribute("discount_code", "11");
				}
				String quant = request.getParameter(product[0] + "Q");
				int updatecount = Integer.parseInt(singlePrice) * Integer.parseInt(quant);
				String updatecountstr = String.valueOf(updatecount);
				price.set(i, updatecountstr);
			}
			int updatecount = 0;
			for (int i = 0; i < price.size(); i++) {
				int p = Integer.parseInt(price.get(i).toString());
				updatecount = updatecount + p;
			}
			// 低於4000加上運費160
			if (updatecount < 4000) {
				updatecount = updatecount + 160;
			}
			session.setAttribute("cartprice", price);// 重設購物車價格session
			session.setAttribute("cartcount", updatecount);// 重設購物車總價session
			sq.closeSQLStatement();
			dbc.closeConnection();
			response.sendRedirect("cart.jsp");
			return;
		} else {// 個別刪除購物車產品
			for (int i = 0; i < cart.size(); i++) {// 個別刪除購物車產品
				String[] product = (String[]) cart.get(i);
				if (request.getParameter("delete" + product[0]) != null) {
					cart.remove(i);// 移除該產品
					price.remove(i);// 移除該價格
					break;
				}
			}
			if (cart.isEmpty()) {
				session.removeAttribute("cartproduct");
				session.removeAttribute("cartprice");
				session.removeAttribute("cartcount");
				session.removeAttribute("discount_code");
			} else {
				int updatecount = 0;
				for (int i = 0; i < price.size(); i++) {
					int p = Integer.parseInt(price.get(i).toString());
					updatecount = updatecount + p;
				}
				// 低於4000加上運費160
				if (updatecount < 4000) {
					updatecount = updatecount + 160;
				}
				session.setAttribute("cartproduct", cart);// 重設購物車session
				session.setAttribute("cartprice", price);// 重設購物車價格session
				session.setAttribute("cartcount", updatecount);// 重設購物車總價session
			}
			sq.closeSQLStatement();
			dbc.closeConnection();
			response.sendRedirect("cart.jsp");
			return;
		}

		if (!Exist) {// 如果購物車中不存在，則加入
			sq.selectDataFromTable("product", "product_id", pid);
			List<String[]> add = sq.getQueryRow();
			cart.add((String[]) add.get(0));
			String[] product = (String[]) add.get(0);
			String singlePrice = product[2];
			String subprice = singlePrice;
			if (request.getParameter("quantity") != null) {
				int q = Integer.parseInt(request.getParameter("quantity"));
				int subtotal = q * Integer.parseInt(subprice);
				subprice = String.valueOf(subtotal);
				price.add(subprice);
				count = count + subtotal;
			} else {
				price.add(subprice);
				count = count + Integer.parseInt(subprice);
			}
			// 低於4000加上運費160
			if (count < 4000) {
				count = count + 160;
			}
			session.setAttribute("cartproduct", cart);// 重設購物車session
			session.setAttribute("cartprice", price);// 重設購物車價格session
			session.setAttribute("cartcount", count);// 重設購物車總價session
			sq.closeSQLStatement();
			dbc.closeConnection();
		} else {
			if (request.getParameter("quantity") != null) {
				String[] product = (String[]) cart.get(x);
				String quant = request.getParameter("quantity");
				String singlePrice = product[2];
				int updateprice = Integer.parseInt(singlePrice) * Integer.parseInt(quant);
				String updatepricestr = String.valueOf(updateprice);
				int updatecount = count - Integer.parseInt(price.get(x).toString()) + updateprice;
				price.set(x, updatepricestr);
				// 低於4000加上運費160
				if (updatecount < 4000) {
					updatecount = updatecount + 160;
				}
				session.setAttribute("cartprice", price);// 重設購物車價格session
				session.setAttribute("cartcount", updatecount);// 重設購物車總價session
			}
			sq.closeSQLStatement();
			dbc.closeConnection();
		}

		try (PrintWriter out = response.getWriter()) {
			/* TODO output your page here. You may use following sample code. */
			out.println("<!DOCTYPE html>");
			out.println("<html>");
			out.println("<head>");
			out.println("<title>Servlet Cart</title>");
			out.println("</head>");
			out.println("<body>");
			out.println("<script language='javascript'>");
			out.println("alert('添加成功，可至右上方購物車確認')");
			out.println("window.location.href = document.referrer;");
			out.println("</script>");
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
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
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
			Logger.getLogger(Cart.class.getName()).log(Level.SEVERE, null, ex);
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
