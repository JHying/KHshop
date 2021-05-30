<%-- 
Document   : index
Created on : 2018/9/30, 下午 06:25:27
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
<meta name="viewport"
	content="width=device-width, initial-scale=1.0,maximum-scale=8">

<title>HMhouse | Cart</title>

<!-- Loading third party fonts -->
<link href="http://fonts.googleapis.com/css?family=Roboto:400,700|"
	rel="stylesheet" type="text/css">
<link href="fonts/font-awesome.min.css" rel="stylesheet" type="text/css">
<link href="fonts/lineo-icon/style.css" rel="stylesheet" type="text/css">

<!-- Loading main css file -->
<link rel="stylesheet" href="style.css">

<!--[if lt IE 9]>
        <script src="js/ie-support/html5.js"></script>
        <script src="js/ie-support/respond.js"></script>
        <![endif]-->
<!-- Global site tag (gtag.js) - Google Analytics -->
<script async
	src="https://www.googletagmanager.com/gtag/js?id=UA-130412407-1"></script>
<script>
	window.dataLayer = window.dataLayer || [];
	function gtag() {
		dataLayer.push(arguments);
	}
	gtag('js', new Date());

	gtag('config', 'UA-130412407-1');
</script>
<script>
	function confirmclick(button) {
		var p = button.id;
		if (confirm("確定將「" + p + "」從購物車移除嗎")) {
			window.event.returnValue = true;
		} else {
			window.event.returnValue = false;
		}
	}
	function deleteclick() {
		if (confirm("確定清空購物車嗎")) {
			window.event.returnValue = true;
		} else {
			window.event.returnValue = false;
		}
	}
	function submitenter(e) {
		var keycode;
		if (window.event)
			keycode = window.event.keyCode;
		else if (e)
			keycode = e.which;
		else
			return true;
		if (keycode == 13) {
			update.click();
			return false;
		} else
			return true;
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
		List price = new ArrayList();
		int count = 0;
		String href = "cart_confirm.jsp";
		if (session.getAttribute("cartproduct") != null) {
			cart = (List) session.getAttribute("cartproduct");
			price = (List) session.getAttribute("cartprice");
			count = Integer.parseInt(session.getAttribute("cartcount").toString());
			n = cart.size();
		} else {
			href = "#";
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
				</a>
				<!-- #branding -->

				<div class="right-section pull-right">
					<a href="order_search.jsp" class="cart">查詢訂單</a> <a href="cart.jsp"
						class="cart"><i class="icon-cart"></i>(<%=n%>) 購物車</a>
				</div>
				<!-- .right-section -->

				<div class="main-navigation">
					<button class="toggle-menu">
						<i class="fa fa-bars"></i>
					</button>
					<ul class="menu">
						<li class="menu-item home current-menu-item"><a
							href="index.jsp"><i class="icon-home"></i></a></li>
						<%
							for (int i = 0; i < classl.size(); i++) {
								String[] classname = (String[]) classl.get(i);
						%>
						<form name="classform<%=i%>" action="search.do" method="POST">
							<input type="hidden" name="class" value="<%=classname[0]%>" />
							<li class='menu-item'><a
								href="javascript: document.forms['<%=i%>'].submit()"><%=classname[0]%></a></li>
						</form>
						<%
							}
						%>
					</ul>
					<!-- .menu -->
					<form action="search.do" method="POST" name='search'>
						<div class="search-form">
							<label><img src="images/icon-search.png"></label> <input
								type="text" name="keyword" placeholder="搜尋..." required>
							<button class="icon" name="send">Go</button>
						</div>
						<!-- .search-form -->
					</form>
					<div class="mobile-navigation"></div>
					<!-- .mobile-navigation -->
				</div>
				<!-- .main-navigation -->
			</div>
			<!-- .container -->
		</div>
		<!-- .site-header -->
		<main class="main-content">
		<div class="container">
			<div class="page">
				<form action="Cart.do" method="POST" name="updcart">
					<table class="cart">
						<thead>
							<tr>
								<th class="product-name">產品名稱</th>
								<th class="product-price">單價</th>
								<th class="product-qty">數量</th>
								<th class="product-total">小計</th>
								<th class="action"></th>
							</tr>
						</thead>
						<tbody>
							<%
								for (int i = 0; i < cart.size(); i++) {
									String[] cartproduct = (String[]) cart.get(i);
									String productprice = (String) price.get(i);
									if (session.getAttribute("discount_code") != null) {
										cartproduct[2] = cartproduct[Integer.parseInt((String)session.getAttribute("discount_code"))];
									}
									String amount = String.valueOf(Integer.parseInt(productprice) / Integer.parseInt(cartproduct[2]));
							%>
							<tr>
								<td class="product-name">
									<div class="product-thumbnail">
										<img src="datafile/<%=cartproduct[4]%>">
									</div>
									<div class="product-detail">
										<h3 class="product-title"><%=cartproduct[1]%></h3>
									</div>
								</td>
								<td class="product-price">NT$<%=cartproduct[2]%></td>
								<td class="product-qty"><input class="amountbutton"
									type="number" name="<%=cartproduct[0]%>Q" min="1" max="99999"
									value='<%=amount%>' step='1' onChange="update.click()"
									onKeyPress="return submitenter(event)" required></td>
								<td class="product-total">NT$<%=productprice%></td>
								<td class="action"><input type="hidden"
									name="deleteproduct<%=cartproduct[0]%>"
									value="<%=cartproduct[0]%>" /> <input class="deletebutton"
									id='<%=cartproduct[1]%>' name='delete<%=cartproduct[0]%>'
									type="submit" value="移除" onclick="confirmclick(this)">
								</td>
							</tr>
							<%
								}
							%>
						</tbody>
					</table>
					<!-- .cart -->
					<p align='right'>
						<strong>使用優惠代碼：</strong><input name="discount" style="background-color: white;"
							type="text" onChange="update.click()" onKeyPress="return submitenter(event)"
							onkeyup="value = value.replace(/[^\d]/g, '')">
					</p>
					<p align='right'>
						<button class="updatebutton" id="update" type="submit"
							name="update" style="display: none;">更新購物車</button>
						<input class="clearbutton" name="deleteAll" type="submit"
							value="清空全部" onclick="deleteclick()">
					</p>
				</form>
				<div class="cart-total">
					<p class="total" bgcolor='red'>
						<strong>總價</strong><span class="num">NT$<%=count%></span>
					</p>
					<p align="right">
						<small>-- 運費160，滿4000免運 --</small>
					</p>
					<p align="right">
						<a href="index.jsp" class="button muted">繼續購物</a> <a
							href="<%=href%>" class="button">確認結帳</a>
					</p>
				</div>
				<!-- .cart-total -->
			</div>
		</div>
		<!-- .container --> </main>
		<!-- .main-content -->
		<div class="site-footer">
			<div class="container">
				<div class="row">
					<%
						String[] contact = (String[]) contactl.get(0);
					%>
					<div class="col-md-2">
						<div class="widget">
							<h3 class="widget-title">聯絡我們</h3>
						</div>
						<!-- .widget -->
					</div>
					<!-- column -->
					<div class="col-md-3">
						<div class="widget">
							<h3 class="widget-title"><%=contact[0]%></h3>
						</div>
						<!-- .widget -->
					</div>
					<!-- column -->
					<div class="col-md-3">
						<div class="widget">
							<h3 class="widget-title"><%=contact[1]%></h3>
						</div>
						<!-- .widget -->
					</div>
					<!-- column -->
					<div class="col-md-4">
						<div class="widget">
							<h3 class="widget-title"><%=contact[2]%></h3>
						</div>
						<!-- .widget -->
					</div>
					<!-- column -->
				</div>
				<!-- .row -->
			</div>
			<!-- .container -->
		</div>
		<!-- .site-footer -->
	</div>

	<script src="js/jquery-1.11.1.min.js"></script>
	<script src="js/plugins.js"></script>
	<script src="js/app.js"></script>

</body>

</html>