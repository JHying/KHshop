/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tw.com.huang.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tw.com.huang.controller.Welcome;
import tw.com.huang.service.WelcomeService;

/**
 *
 * @author H_yin
 */
public class Welcome extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     * @throws java.sql.SQLException
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        
        //第一次進入，資料庫連線並取得參數
        WelcomeService ws = new WelcomeService(request);

        //取得DB所有的table資料
        //接收介紹資料
        String intro = "introduction";
        List<String[]> introl = ws.getTableData(intro);

        //接收聯絡資料
        String tcontact = "contact";
        List<String[]> contactl = ws.getTableData(tcontact);

        //接收種類資料
        String tproduct = "product";
        List<String[]> classl = ws.getDistinctCol(tproduct, "class");

        //接收產品資料
        List<String[]> productl = ws.getTableData(tproduct);

        //接收熱銷產品
        List<String[]> hotl = ws.getContainFromTable(tproduct, "hot", "y");

        //接收新品上市
        List<String[]> newl = ws.getContainFromTable(tproduct, "new", "y");
        List<String[]> newl2 = ws.getSortList(newl, "product_id", "DESC");

        //不同種類產品分別接收
        for (int i = 0; i < classl.size(); i++) {
            String[] data = (String[]) classl.get(i);
            List<String[]> Pobc = ws.getContainFromTable(tproduct, "class", data[0]);
            session.setAttribute("class" + i, Pobc);
        }

        session.setAttribute("class", classl);
        session.setAttribute("product", productl);
        session.setAttribute("hot", hotl);
        session.setAttribute("new", newl2);
        session.setAttribute("contact", contactl);
        session.setAttribute("intro", introl);
        session.setAttribute("dbconnParam", ws.getDbconnParam());
        
        //導向首頁
        response.sendRedirect("index.jsp");

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet FirstSetting</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet FirstSetting at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (SQLException ex) {
            Logger.getLogger(Welcome.class.getName()).log(Level.SEVERE, null, ex);
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
