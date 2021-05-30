<%-- 
    Document   : order_search
    Created on : 2018/11/9, 上午 02:32:57
    Author     : H_yin
--%>

<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>

<%
    if (session.getAttribute("contact") == null) {
        RequestDispatcher dispatch = request.getRequestDispatcher("error.jsp");
        dispatch.forward(request, response);//委派回首頁
        return;
    }
    List classl = (List) session.getAttribute("class");
    List contactl = (List) session.getAttribute("contact");
    int n = 0;
    if (session.getAttribute("cartproduct") != null) {
        List cart = (List) session.getAttribute("cartproduct");
        n = cart.size();
    }
%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=8">

        <title>HMhouse | order searching</title>

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
    <body>
        <div id="site-content">
            <div class="site-header">
                <div class="container">
                    <a href="index.jsp" id="branding">
                        <div class="logo-text">
                            <h1 class="site-title">HMhouse</h1>
                            <small class="site-description">歡迎您的蒞臨！點此返回首頁</small>
                        </div>
                    </a> <!-- #branding -->

                    <div class="right-section pull-right">
                        <a href="order_search.jsp" class="cart">查詢訂單</a>
                        <a href="cart.jsp" class="cart"><i class="icon-cart"></i>(<%=n%>) 購物車</a>
                    </div> <!-- .right-section -->

                    <div class="main-navigation">
                        <button class="toggle-menu"><i class="fa fa-bars"></i></button>
                        <ul class="menu">
                            <li class="menu-item home current-menu-item"><a href="index.jsp"><i class="icon-home"></i></a></li>
                                    <%for (int i = 0; i < classl.size(); i++) {
                                            String[] classname = (String[]) classl.get(i);
                                    %>                         
                            <form name="classform<%=i%>" action = "search.do" method="POST" >
                                <input type="hidden" name="class" value="<%=classname[0]%>" />
                                <li class='menu-item'><a href="javascript: document.forms['classform<%=i%>'].submit()"><%=classname[0]%></a></li>
                            </form>               
                            <%
                                }
                            %>
                        </ul> <!-- .menu -->  
                        <form action="search.do" method="POST" name='search'>
                            <div class="search-form">
                                <label><img src="images/icon-search.png"></label>
                                <input type="text" name="keyword" placeholder="搜尋..." required>
                                <button class="icon" name="send">Go</button>
                            </div> <!-- .search-form -->
                        </form>
                        <div class="mobile-navigation"></div> <!-- .mobile-navigation -->
                    </div> <!-- .main-navigation -->
                </div> <!-- .container -->
            </div> <!-- .site-header -->

            <main class="main-content" style="text-align: center">
                <div class="container">
                    <div class="page">
                        <div class="entry-content">
                            <div class="row">
                                <%
                                    if (session.getAttribute("serialID") != null) {
                                        String[] searchdata = (String[]) session.getAttribute("serialID");
                                        String[] contact2 = (String[]) contactl.get(1);
                                %>
                                <font face="標楷體">
                                <table class="ordersearch">
                                    <thead>
                                        <tr>
                                            <th style="text-align: center;">訂單資料</th>
                                            <th style="text-align: center;">內容</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <tr>
                                            <td>編號</td>
                                            <td><%=searchdata[0]%></td>
                                        </tr>
                                        <tr>
                                            <td>訂購內容</td>
                                            <td><%=searchdata[8]%></td>
                                        </tr>
                                        <tr>
                                            <td>總價</td>
                                            <td>NT$<%=searchdata[9]%></td>
                                        </tr>
                                        <tr>
                                            <td>訂購時間</td>
                                            <td><%=searchdata[10]%></td>
                                        </tr>
                                        <tr>
                                            <td>狀態</td>
                                            <td><%=searchdata[11]%></td>
                                        </tr>
                                        <tr>
                                            <td>付款方式</td>
                                            <td><%=searchdata[5]%></td>
                                        </tr>
                                        <%
                                            if (!searchdata[7].equals("") && searchdata[7] != null && !searchdata[7].equals("null")) {
                                        %>
                                        <tr>
                                            <td>您填寫之付款帳戶（核對用）</td>
                                            <td><%=searchdata[7]%></td>
                                        </tr>
                                        <%
                                            }
                                        %>
                                    </tbody>
                                </table> <!-- ordersearch -->
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
                                <h2>（聯絡資料位於網頁最下方）</h2>
                                <br>
                                <a href="index.jsp"><h3>＊點此返回首頁＊</h3></a>
                                </font>
                                <%
                                    session.removeAttribute("serialID");
                                } else {
                                    if (session.getAttribute("searcherror") != null) {
                                        String errorMsg = (String) session.getAttribute("searcherror");
                                        out.println("<script>alert('" + errorMsg + "')</script>");
                                        session.removeAttribute("searcherror");
                                    }
                                %>
                                <form action="SetOrder.do" method="POST">
                                    <h2  align="center">訂單編號：<input class="ordersearchi" type="text" name="serialID" required onkeyup="value = value.replace(/[^\w\.\/]/ig, '')"/></h2>
                                    <button id="search" type="submit" name="search">送出</button>
                                    <button id="searchreset" type="reset" name="searchreset">清除</button>
                                </form>
                                <%
                                    }
                                %>
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
