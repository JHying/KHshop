<%-- 
Document   : index
Created on : 2018/9/30, 下午 06:25:27
Author     : H_yin
--%>

<%@page import="java.util.UUID"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=8">

        <title>HMhouse | Cart</title>

        <!-- Loading third party fonts -->
        <link href="http://fonts.googleapis.com/css?family=Roboto:400,700|" rel="stylesheet" type="text/css">
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
        <script language=javascript>
            function setReadonly() {
                if (document.all.payment.value == "貨到付款") {
                    //若條件成立則將 txt 設為readonly 
                    document.all.guest_account.readOnly = true;
                } else {
                    document.all.guest_account.readOnly = false;
                    document.all.guest_account.required = true;
                }
            }
        </script>
    </head>
    <body>
        <%
            if (session.getAttribute("contact") == null) {
                RequestDispatcher dispatch = request.getRequestDispatcher("error.jsp");
                dispatch.forward(request, response);//委派回首頁
                return;
            }
            List classl = (List) session.getAttribute("class");
            List contactl = (List) session.getAttribute("contact");
            int n = 0;
            List cart = new ArrayList();
            List orderdata = new ArrayList();
            if (session.getAttribute("orderdata") != null) {
                orderdata = (List) session.getAttribute("orderdata");
            } else {
                for (int i = 0; i < 8; i++) {
                    orderdata.add("");
                }
            }
            if (session.getAttribute("cartproduct") != null) {
                cart = (List) session.getAttribute("cartproduct");
                n = cart.size();
            } else {
                response.sendRedirect("cart.jsp");
                return;
            }
        %>
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
                                <li class='menu-item'><a href="javascript: document.forms['<%=i%>'].submit()"><%=classname[0]%></a></li>
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
            <main class="main-content">
                <div class="container">
                    <div class="page">
                        <h1 style="text-align:center">結帳及送貨資料</h1>
                        <form action="SetOrder.do"  name="order_confirm" method="POST">
                            <p  align="center">  收件人：<input style="background-color: white;" type="text" name="guest_name" size="45%" value="<%=orderdata.get(1).toString()%>" required onkeyup="this.value = this.value.replace(/^\s+|\s+$/g, '')"/></p>
                            <p  align="center">收件地址：<input style="background-color: white;" type="text" name="guest_address" placeholder="請輸入完整地址..." size="45%" value="<%=orderdata.get(2).toString()%>" required onkeyup="this.value = this.value.replace(/^\s+|\s+$/g, '')"/></p>
                            <p  align="center">郵遞區號：<input style="background-color: white;" minlength="3" maxlength="3" type="text" name="guest_postalcode" placeholder="輸入 3 碼..." size="45%" value="<%=orderdata.get(3).toString()%>" required onkeyup="value = value.replace(/[^\d]/g, '')"/></p>
                            <p  align="center">聯絡電話：<input style="background-color: white;" minlength="10" maxlength="10" type="text" name="guest_phone" size="45%" value="<%=orderdata.get(4).toString()%>" required onkeyup="value = value.replace(/[^\d]/g, '')"/></p>
                            <p  align="center">電子郵件：<input style="background-color: white;" type="email" name="guest_email" placeholder="（選填）可接收訂單資料，建議填寫..." size="45%" value="<%=orderdata.get(6).toString()%>" onkeyup="value = value.replace(/[^@\w\.\/]/ig, '')"/></p>
                            <p  align="center">付款方式：<select id="payment" name="payment" style="width:32.5%" onchange="setReadonly()" required>
                                    <option value="">－－請選擇－－</option>
                                    <!--                                   <option value="貨到付款">貨到付款</option> -->
                                    <option value="宅配">宅配（需於五天內先行付款）</option>
                                </select></p>
                            <p  align="center">匯款帳戶：<input style="background-color: white;" id="guest_account" type="text" name="guest_account" placeholder="（宅配需填寫）核對用，請輸入您付款用帳戶" size="45%" value="<%=orderdata.get(7).toString()%>" onkeyup="value = value.replace(/[^\d]/g, '')" readonly/></p>
                            <div class="cart-total">
                                <p align="right">
                                    <a href="cart.jsp" class="button muted">返回購物車</a>    
                                    <button name="next" class="button">下一步</button>              
                                </p>
                            </div> <!-- .cart-total -->
                        </form>
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