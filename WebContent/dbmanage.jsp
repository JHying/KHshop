<%-- 
    Document   : DBmanage
    Created on : 2016/2/17, 下午 11:03:43
    Author     : JJH
--%>

<%@page import="java.io.File"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HMhouse | DB manage</title>
</head>
<body>
	<font face="標楷體"> <%
 	if (session.getAttribute("success") != null) {
 		String Msg = (String) session.getAttribute("success");
 %> <script type="text/javascript" language="javascript">
            window.alert("<%=Msg%>");//跳出訊息框顯示成功</script> 
  <%
 	session.removeAttribute("success");
 	}
 %>
		<style>
html {
	height: 100%;
}

body {
	background-color: #f3f3fa;
	background-repeat: no-repeat;
	background-position: center;
	background-attachment: fixed;
	background-size: cover;
}
</style>
		<h1>
			登入成功！已連結資料庫 --- <a href='dbfirst.jsp'>登出</a>
		</h1> <%
 	if (session.getAttribute("dbconnParam") == null) {
 		RequestDispatcher dispatch = request.getRequestDispatcher("dberror_page.jsp");
 		dispatch.forward(request, response);
 	} else {
 		if (session.getAttribute("table") == null) {
 			out.println("<h3>請選擇要管理的資料表</h3>");
 			List tablel = (List) session.getAttribute("tablel");
 %>
		<form action="AdjustData.do" method="POST">
			<%
				String[] tables = (String[]) tablel.get(0);
						String table = tables[0];
			%>
			<p>
				<input type="radio" name="table" value="<%=table%>"
					checked="checked" /><%=table%></p>
			<%
				for (int i = 1; i < tablel.size(); i++) {
							tables = (String[]) tablel.get(i);
							table = tables[0];
			%>
			<p>
				<input type="radio" name="table" value="<%=table%>" /><%=table%></p>
			<%
				}
			%>
			<br>
			<button type="submit" name="next">下一步</button>
			&nbsp;
			<button type="reset" name="reset">重置</button>
		</form>
		<hr>
		<h5>直接輸入SQL語法</h5>
		<form action="AdjustData.do" method="POST" name="SQL">
			<textarea name="sql" rows="8" cols="80" value="SQL語法" required></textarea>
			<p>
				<button type="submit" name="sqlvalue">送出</button>
				&nbsp;
				<button type="reset">重設</button>
			</p>
		</form> <%
 	if (session.getAttribute("SQLQuery") != null) {
 				List QueryList = (List) session.getAttribute("SQLQuery");
 				session.removeAttribute("SQLQuery");
 				String[] Querycol = (String[]) QueryList.get(0);
 %>
		<table border="1">
			<thead bgcolor='#FFCC99'>
				<tr>
					<%
						for (int i = 0; i < Querycol.length; i++) {
					%>
					<th><%=Querycol[i]%></th>
					<%
						}
					%>
				</tr>
			</thead>
			<tbody bgcolor='#FFFFE6'>
				<%
					for (int i = 1; i < QueryList.size(); i++) {
									String[] data = (String[]) QueryList.get(i);//i=0時為欄位資訊, i>=1之後為各列資料
				%>
				<tr>
					<%
						for (int j = 0; j < data.length; j++) {//每個欄內容
											if (data[j].equals("null")) {
												data[j] = "";
											}
					%>
					<td><%=data[j]%></td>
					<%
						}
					%>
				</tr>
				<%
					}
				%>
			</tbody>
		</table> <%
 	}
 %>
		<hr>
		<h4>可下載的資料表：</h4> <%
 	for (int i = 0; i < tablel.size(); i++) {
 				tables = (String[]) tablel.get(i);
 				table = tables[0];
 				String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
 				String dbfile = rootpath + "datafile/table_" + table + ".xls";
 				File fdir = new File(dbfile);
 				if (fdir.exists()) {
 					String filename = fdir.getName();
 %>
		<p><%=filename%>(<a href="datafile/<%=filename%>" target="_blank">點擊下載</a>)
		</p> <%
 	}
 			}
 %>
		<hr> <%
 	} else {
 			String table = (String) session.getAttribute("table");
 			List dataList = (List) session.getAttribute("tablelist");
 			String[] cols = (String[]) dataList.get(0);
 			out.println("<h3>當前資料表：" + table + "</h3>");
 %>
		<p>
			<a href="dbimport.jsp">匯入或上傳資料</a>
		</p>
		<form action="AdjustData.do" method="POST" name='backform'>
			<input type="hidden" name="back" value="back" /> <a
				href="javascript: document.forms['backform'].submit()"><strong><font
					color='#FF0202'>重新選擇資料表</font></strong></a>
		</form>
		<hr> <%
			String rootpath = request.getServletContext().getRealPath("/");// 網站根目錄
 			String dbfile = rootpath + "datafile/table_" + table + ".xls";
 			File fdir = new File(dbfile);
 			if (fdir.exists()) {
 				String filename = fdir.getName();
 %>
		<p><%=filename%>(<a href="datafile/<%=filename%>" target="_blank">點擊下載</a>)
		</p> <%
 	}
 %>
		<form action="dbtableMaintain.jsp">
			<strong>目前資料</strong>
			<button type="submit">前往更改</button>
		</form> <br>
		<table border="1">
			<thead bgcolor='#FFCC99'>
				<tr>
					<%
						for (int i = 0; i < cols.length; i++) {
					%>
					<th><%=cols[i]%></th>
					<%
						}
					%>
				</tr>
			</thead>
			<tbody bgcolor='#FFFFE6'>
				<%
					for (int i = 1; i < dataList.size(); i++) {
								String[] data = (String[]) dataList.get(i);//i=0時為欄位資訊, i>=1之後為各列資料
				%>
				<tr>
					<%
						for (int j = 0; j < data.length; j++) {//每個欄內容
										if (data[j].equals("null")) {
											data[j] = "";
										}
					%>
					<td><%=data[j]%></td>
					<%
						}
					%>
				</tr>
				<%
					}
				%>
			</tbody>
		</table> <%
 	}
 	}
 %>
	</font>
</body>
</html>
