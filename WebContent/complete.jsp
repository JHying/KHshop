<%-- 
    Document   : error
    Created on : 2015/4/27, 下午 10:24:01
    Author     : H_yin
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=8">

        <title>HMhouse | Complete</title>

        <!-- Loading third party fonts -->
        <link href="http://fonts.googleapis.com/css?family=Roboto:100,300,400,700|" rel="stylesheet" type="text/css">
        <link href="fonts/font-awesome.min.css" rel="stylesheet" type="text/css">
        <link href="fonts/lineo-icon/style.css" rel="stylesheet" type="text/css">

        <!-- Loading main css file -->
        <link rel="stylesheet" href="style.css">

        <!--[if lt IE 9]>
        <script src="js/ie-support/html5.js"></script>
        <script src="js/ie-support/respond.js"></script>
        <![endif]-->
        <!-- Global site tag (gtag.js) - Google Analytics -->
        <script async src="https://www.googletagmanager.com/gtag/js?id=UA-130412407-1"></script>
        <script>
            window.dataLayer = window.dataLayer || [];
            function gtag() {
                dataLayer.push(arguments);
            }
            gtag('js', new Date());

            gtag('config', 'UA-130412407-1');
        </script>
    </head>
    <%
        String sn = "";
        List contactl = new ArrayList();
        if (session.getAttribute("sn") != null) {
            contactl = (List) session.getAttribute("contact");
            sn = session.getAttribute("sn").toString();
            if (session.getAttribute("sendMsg") != null) {
                String Msg = session.getAttribute("sendMsg").toString();
                out.println("<script>alert('" + Msg + "')</script>");
            }
            session.invalidate();
        } else {
            RequestDispatcher dispatch = request.getRequestDispatcher("Welcome.do");
            dispatch.forward(request, response);//委派回首頁
            return;
        }
        String[] contact2 = (String[]) contactl.get(1);
    %>
    <body>
        <div id="site-content">
            <div class="site-header">
                <div class="container">
                    <a href="Welcome.do" id="branding">
                        <div class="logo-text">
                            <h1 class="site-title">HMhouse</h1>
                            <small class="site-description">歡迎您的蒞臨！點此返回首頁</small>
                        </div>
                    </a> <!-- #branding -->
                </div> <!-- .container -->
            </div> <!-- .site-header -->

            <main class="main-content" style="text-align: center">
                <div class="container">
                    <div class="page">
                        <div class="entry-content">
                            <div class="row">
                                <font face="標楷體">
                                <h1>感謝訂購！您的訂單已送出！</h1>     
                                <font color="brown"><h1>訂單編號：<%=sn%></h1></font>
                                <br>
                                <h2>選擇<font color="red">宅配</font>請於「<font color="red">五天內</font>」以填寫之付款帳戶匯款至下列地址，確認後將盡速為您出貨~~</h2>
                                <font color="brown">
                                <h3><%=contact2[0]%></h3>
                                <h3><%=contact2[1]%></h3>
                                <h3><%=contact2[2]%></h3></font>
                                <h2>出貨約需 2~5 個工作天，請靜待簡訊或信箱通知~~感謝您！</h2>
                                <br>
                                <h2>若有任何疑問或建議，請隨時與我們聯繫，我們將盡力為您服務~~</h2>
                                <h2>再次感謝您的支持與配合 :)</h2>
                                <font color="red"><h3>（提醒您：訂單編號可查詢出貨狀態，離開此頁前記得將「訂單編號」記下唷！）</h3></font>
                                <br>
                                <a href="Welcome.do"><h3>＊點此返回首頁＊</h3></a>
                                </font>
                            </div>
                        </div>
                    </div>
                </div> <!-- .container -->
            </main> <!-- .main-content -->
            <div class="site-footer">
                <div class="container">
                    <div class="row">
                        <%
                            String[] contact = (String[]) contactl.get(0);%>
                        <div class="col-md-2">
                            <div class="widget">
                                <h3 class="widget-title">聯絡我們</h3>
                            </div> <!-- .widget -->
                        </div> <!-- column -->
                        <div class="col-md-3">
                            <div class="widget">
                                <h3 class="widget-title"><%=contact[0]%></h3>
                            </div> <!-- .widget -->
                        </div> <!-- column -->
                        <div class="col-md-3">
                            <div class="widget">
                                <h3 class="widget-title"><%=contact[1]%></h3>
                            </div> <!-- .widget -->
                        </div> <!-- column -->
                        <div class="col-md-4">
                            <div class="widget">
                                <h3 class="widget-title"><%=contact[2]%></h3>
                            </div> <!-- .widget -->
                        </div> <!-- column -->
                    </div><!-- .row -->
                </div> <!-- .container -->
            </div> <!-- .site-footer -->
        </div>

        <script src="js/jquery-1.11.1.min.js"></script>
        <script src="js/plugins.js"></script>
        <script src="js/app.js"></script>
    </body>
</html>
