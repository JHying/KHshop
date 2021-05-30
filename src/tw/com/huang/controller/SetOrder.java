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
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import tw.com.huang.controller.SetOrder;
import tw.com.huang.dao.DBConn;
import tw.com.huang.dao.SQLPrepareProcess;
import tw.com.huang.service.MailService;

/**
 *
 * @author G551
 */
public class SetOrder extends HttpServlet {

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
    @SuppressWarnings("unchecked")
	protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException {
        response.setContentType("text/html;charset=UTF-8");
        HttpSession session = request.getSession();
        if (session.isNew()) {
            response.sendRedirect("error.jsp");
            return;
        }
        if (request.getParameter("next") != null) {
            List<String> orderdata = new ArrayList<String>();
            String name = new String(request.getParameter("guest_name").getBytes("8859_1"), "UTF-8");
            String address = new String(request.getParameter("guest_address").getBytes("8859_1"), "UTF-8");
            String postalcode = request.getParameter("guest_postalcode");
            String phone = request.getParameter("guest_phone");
            String payment = new String(request.getParameter("payment").getBytes("8859_1"), "UTF-8");
            String email = "";
            String account = "";
            if (!request.getParameter("guest_email").equals("")) {
                email = new String(request.getParameter("guest_email").getBytes("8859_1"), "UTF-8");
            }
            if (!request.getParameter("guest_account").equals("")) {
                account = request.getParameter("guest_account");
            }
            //流水號："年月日-時分秒-亂數-驗証值"，驗証值用md5("年月日-時分秒-亂數") 計算
            String sn = Long.toHexString(System.currentTimeMillis()).substring(10);
            orderdata.add(sn);
            orderdata.add(name);
            orderdata.add(address);
            orderdata.add(postalcode);
            orderdata.add(phone);
            orderdata.add(payment);
            orderdata.add(email);
            orderdata.add(account);
            session.setAttribute("orderdata", orderdata);
            response.sendRedirect("order_confirm.jsp");
            return;
        } else if (session.getAttribute("orderdata") != null && request.getParameter("check") != null) {
            String[] dbconnParam = (String[]) session.getAttribute("dbconnParam");
            DBConn dbc = new DBConn(dbconnParam);
            SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
            List<String> orderdata = (List<String>) session.getAttribute("orderdata");
            List<String[]> cart = (List<String[]>) session.getAttribute("cartproduct");
            List<String> price = (List<String>) session.getAttribute("cartprice");
            String count = session.getAttribute("cartcount").toString();
            //將訂單寫入資料庫
            //產生流水號 - 時間戳六進位後三碼 + UUIDhashcode + 初始時間戳後三碼
            int hashCode = UUID.randomUUID().hashCode();
            if (hashCode < 0) {
                hashCode = -hashCode;
            }
            String sn = Long.toHexString(System.currentTimeMillis()).substring(9) + hashCode + orderdata.get(0).toString();
            String orderproduct = "";
            for (int i = 0; i < cart.size(); i++) {
                String[] cartproduct = (String[]) cart.get(i);
                String productprice = (String) price.get(i);
                String amount = String.valueOf(Integer.parseInt(productprice) / Integer.parseInt(cartproduct[2]));
                orderproduct += cartproduct[1] + "*" + amount + "、";
            }
            orderproduct = orderproduct.substring(0, orderproduct.length() - 1);
            //設定訂單編號、order、總價、時間、處理狀態
            Date date = new Date();
            orderdata.set(0, sn);
            orderdata.add(orderproduct);
            orderdata.add(count);
            orderdata.add(date.toInstant().toString());
            orderdata.add("n");
            String[] orderform = (String[]) orderdata.toArray(new String[orderdata.size()]);
            sq.insertDataToDB("order_form", orderform);
            //有填email自動寄信
            if (!orderdata.get(6).toString().equals("")) {
            	MailService ms = new MailService();
                sq.selectDataFromTable("systemsender");
                List<String[]> systemsender = sq.getQueryRow();
                String[] systemsenderstr = (String[]) systemsender.get(0);
                String sender = systemsenderstr[0];
                String recipiant = orderdata.get(6).toString();
                String subject = systemsenderstr[2];
                String password = systemsenderstr[1];
                String host = systemsenderstr[4];
                String message = "<br>訂單編號：" + sn + "<br>訂購內容：" + orderproduct + "<br>總價：NT$ " + count + "<br>付款方式：" + orderdata.get(5).toString() + "<br><br>";
                if (!orderdata.get(7).toString().equals("")) {
                    message += "<br>匯款帳戶：" + orderdata.get(7).toString() + "（核對用，請以此帳戶進行付款）";
                    List<String[]> contactl = (List<String[]>) session.getAttribute("contact");
                    String[] contact2 = (String[]) contactl.get(1);
                    message += "<p>付款地址：</p>";
                    message += "<p>" + contact2[0] + "</p>";
                    message += "<p>" + contact2[1] + "</p>";
                    message += "<p>" + contact2[2] + "</p>";
                }
                message += systemsenderstr[3];
                ms.sendEmail(sender, recipiant, subject, message, password, host);
                session.setAttribute("sendMsg", ms.getSendMsg());
            }
            sq.closeSQLStatement();
            dbc.closeConnection();
            session.setAttribute("sn", sn);
            response.sendRedirect("complete.jsp");
            return;
        } else if (request.getParameter("serialID") != null) {//搜尋訂單
            String[] dbconnParam = (String[]) session.getAttribute("dbconnParam");
            DBConn dbc = new DBConn(dbconnParam);
            SQLPrepareProcess sq = new SQLPrepareProcess(dbc.getConnection());
            String serialID = request.getParameter("serialID");
            sq.selectDataFromTable("order_form", "serial_number", serialID);
            List<String[]> search = sq.getQueryRow();
            if (search.isEmpty()) {
                session.setAttribute("searcherror", "查無此訂單！提醒您，英文字母大小寫也有影響哦！");
            } else {
                String[] searchdata = (String[]) search.get(0);
                if (searchdata[11].equals("n")) {
                    searchdata[11] = "待處理";
                } else {
                    searchdata[11] = "已出貨";
                }
                session.setAttribute("serialID", searchdata);
            }
            sq.closeSQLStatement();
            dbc.closeConnection();
            response.sendRedirect("order_search.jsp");
        }

        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet SetOrder</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet SetOrder at " + request.getContextPath() + "</h1>");
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
            Logger.getLogger(SetOrder.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(SetOrder.class.getName()).log(Level.SEVERE, null, ex);
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
