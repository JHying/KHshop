<%-- 
    Document   : single
    Created on : 2018/9/30, 下午 06:34:18
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

        <title>HMhouse | Product detail</title>

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
        List searchList = (List) session.getAttribute("single");
        String[] single = (String[]) searchList.get(1);
    %>
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
                                <li class='menu-item'><a href="javascript: document.forms['<%=i%>'].submit()"><%=classname[0]%></a></li>
                            </form>
                            <%
                                }
                            %>
                        </ul> <!-- .menu -->
                        <form action="search.do" method="POST">
                            <div class="search-form">
                                <label><img src="images/icon-search.png"></label>
                                <input type="text" name="keyword" placeholder="搜尋..." required>
                                <button class="icon" name="send" onclick="submit()">Go</button>
                            </div> <!-- .search-form -->
                        </form>
                        <div class="mobile-navigation"></div> <!-- .mobile-navigation -->
                    </div> <!-- .main-navigation -->
                </div> <!-- .container -->
            </div> <!-- .site-header -->

            <main class="main-content">
                <div class="container">
                    <div class="page">
                        <div class="entry-content">
                            <div class="col-sm-6 col-md-4">
                                <div class="product-images">
                                    <figure class="large-image">
                                        <a href="datafile/<%=single[4]%>"><img src="datafile/<%=single[4]%>" alt=""></a>
                                    </figure>
                                    <div class="thumbnails">
                                        <a href="datafile/<%=single[5]%>"><img src="datafile/<%=single[5]%>" alt=""></a>
                                    </div>
                                </div>
                            </div>
                            <div class="col-sm-6 col-md-8">
                                <h2 class="entry-title"><%=single[1]%></h2>
                                <small class="price">NT$<%=single[2]%></small>
                                <font class="singledesc"><%=single[3]%></font>
                                <div class="addtocart-bar">
                                    <form action="Cart.do">
                                        <label for="#">數量</label>
                                        <input type="hidden" name="addproduct" value="<%=single[0]%>" />
                                        <input type="number" name="quantity" min="1" max="99999" value='1' step='1' required>
                                        <input type="submit" name="add" value="加到購物車">
                                    </form>
                                </div>
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
