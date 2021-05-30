<%-- 
    Document   : product
    Created on : 2018/9/30, 下午 06:34:04
    Author     : H_yin
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<%
    if (session.getAttribute("contact") == null) {
        RequestDispatcher dispatch = request.getRequestDispatcher("error.jsp");
        dispatch.forward(request, response);//委派回首頁
        return;
    }
    List classl = (List) session.getAttribute("class");
    List contactl = (List) session.getAttribute("contact");
    List searchList = new ArrayList();
    int n = 0;
    if (session.getAttribute("cartproduct") != null) {
        List cart = (List) session.getAttribute("cartproduct");
        n = cart.size();
    }
    if (session.getAttribute("searchList") != null) {
        searchList = (List) session.getAttribute("searchList");
    }
%>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=8">

        <title>HMhouse | Products</title>

        <!-- Loading third party fonts -->
        <link href="http://fonts.googleapis.com/css?family=Roboto:100,400,700|" rel="stylesheet" type="text/css">
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

            <main class="main-content">
                <div class="container">
                    <div class="page">
                        <div class="filter-bar">
                            <div class="filter">
                                <span>
                                    <label>排序：</label>
                                    <form name="orderby" action="search.do" method="POST"> 
                                        <select name="ob" onChange='javascript:orderby.submit()'>
                                            <option value="#">－－ 請選擇 －－</option>
                                            <option value="product_id|ASC">上市時間-由舊到新</option>
                                            <option value="product_id|DESC">上市時間-由新到舊</option>
                                            <option value="price|ASC">價格-由低到高</option>
                                            <option value="price|DESC">價格-由高到低</option>
                                        </select>
                                    </form>
                                </span>
                            </div> <!-- .filter -->
                        </div> <!-- .filter-bar -->
                        <div class="product-list">

                            <%for (int i = 0; i < searchList.size(); i++) {
                                    String[] searchproduct = (String[]) searchList.get(i);%>

                            <div class="product">
                                <div class="inner-product">
                                    <div class="figure-image">
                                        <a href="javascript: document.forms['prosingle<%=searchproduct[0]%>'].submit()"><img src="datafile/<%=searchproduct[4]%>"></a>
                                    </div>
                                    <h3 class="product-title"><a href="javascript: document.forms['prosingle<%=searchproduct[0]%>'].submit()"><%=searchproduct[1]%></a></h3>
                                    <small class="price">NT$<%=searchproduct[2]%></small>
                                    <p><%=searchproduct[3]%></p>
                                    <form action="Cart.do" method="POST">
                                        <input type="hidden" name="addproduct" value="<%=searchproduct[0]%>" />
                                        <input name="add" type="submit" value="加到購物車" class = "button">
                                    </form>
                                    <form action="search.do" method="POST" name="prosingle<%=searchproduct[0]%>">
                                        <input type="hidden" name="single" value="<%=searchproduct[0]%>" />
                                        <a href="javascript: document.forms['prosingle<%=searchproduct[0]%>'].submit()" class="button muted">產品詳情</a>
                                    </form>
                                </div>
                            </div> <!-- .product -->
                            <%
                                }
                            %>
                        </div> <!-- .product-list -->

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
