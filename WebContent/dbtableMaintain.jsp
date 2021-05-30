<%-- 
    Document   : DataManage
    Created on : 2016/2/18, 下午 04:36:13
    Author     : G551
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>HMhouse | DBtable Maintain</title>

<link rel="stylesheet" href="http://code.jquery.com/ui/1.11.4/themes/smoothness/jquery-ui.css">
<script src="http://code.jquery.com/jquery-1.10.2.js"></script>
<script src="http://code.jquery.com/ui/1.11.4/jquery-ui.js"></script>

<script>
        function GetAllCheckBox(CheckAll)
        {
            var items = document.getElementsByTagName("input");
            for (var i = 0; i < items.length; i++)
            {
                if (items[i].type === "checkbox") {
                    items[i].checked = CheckAll.checked;
                }
            }
        }
  		$(function() {
    		$( "#start" ).datepicker({
      			defaultDate: "+1w",
      			changeMonth: true,
	   			changeYear: true,
      			onClose: function( selectedDate ) {
        			$( "#to" ).datepicker( "option", "minDate", selectedDate );
      			}
    		});
    		$( "#end" ).datepicker({
      			defaultDate: "+1w",
      			changeMonth: true,
				changeYear: true,
      			onClose: function( selectedDate ) {
        			$( "#from" ).datepicker( "option", "maxDate", selectedDate );
      			}
    		});
 		});
</script>

</head>
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
<body>
<h2>日期篩選</h2>
<form action="DBFilter.do" method="POST">
<label for="start">從</label>
<input type="text" id="start" name="start">
<label for="end">至</label>
<input type="text" id="end" name="end">
<input type="submit" value="確定" />&nbsp;<input type="reset" value="重設" />
</form>

	<font face="標楷體"> <%
 	if (session.getAttribute("dbconnParam") == null) {
 		RequestDispatcher dispatch = request.getRequestDispatcher("dberror_page.jsp");
 		dispatch.forward(request, response);//如果有且錯則委派至結果頁
 	}
 	if (session.getAttribute("success") != null) {
 		String Msg = (String) session.getAttribute("success");
 %> <script type="text/javascript" language="javascript">
            window.alert("<%=Msg%>");//跳出訊息框顯示成功
								</script> <%
 	session.removeAttribute("success");
 		session.removeAttribute("AC");
 		session.removeAttribute("select");
 	}
 	String action = "N";
 	String msg = "";
 	String title = "目前資料";
 	String form = "dbtableMaintain.jsp";
 	int x = 0;
 	if (request.getParameter("action") != null) {
 		action = request.getParameter("action");
 		if (action.equals("I")) {//如果選擇是新增，則增加新一值都是null
 			title = "請輸入新內容";
 			form = "DBMaintain.do";
 		} else {
 			if (request.getParameterValues("row") != null) {
 				if (action.equals("U")) {
 					String[] rowname = request.getParameterValues("row");
 					session.setAttribute("SR", rowname);
 					title = "請修改內容";
 					form = "DBMaintain.do";
 				} else {
 					title = "確定要刪除勾選的資料嗎？";
 					form = "DBMaintain.do";
 				}
 			} else {//如果選擇新增以外沒勾checkbox
 				msg = "<b><font color='#FF0202'>請勾選目標列！</font></b>";
 				action = "N";
 			}
 		}
 		session.setAttribute("AC", action);
 	}
 	String table = (String) session.getAttribute("table");
 	List dataList = (List) session.getAttribute("tablelist");
 	String[] cols = (String[]) dataList.get(0);//轉換後的欄位名
 %>
		<h1>
			【<%=table%>】 Maintain Page
		</h1>
		<h2>資料更新</h2> <%=msg%>
		<h3><%=title%></h3>
	</font>
	<form action="<%=form%>" method="post">
		<font face="標楷體">
			<table border="1">
				<thead bgcolor='#FFCC99'>
					<tr>
						<%
							if (action.equals("N") || action.equals("D")) {
						%>
						<th><input type="checkbox" name='col'
							onclick="GetAllCheckBox(this)" /></th>
						<%
							}
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
							String[] row = (String[]) dataList.get(i);//i=0時為欄位資訊, i>=1之後為各列資料
							boolean tr_tag = false;//判斷是否出現<tr>的指標變數, 預設為不出現
							if (action.equals("D") || action.equals("U")) {//為了避免出現不必要的<tr>
								boolean selected = false;
								String[] rowname = request.getParameterValues("row");
								for (int n = 0; n < rowname.length; n++) {//如果找到符合的第一欄內容
									String rownamestr = new String(rowname[n].getBytes("8859_1"), "UTF-8");
									if (row[0].equals(rownamestr)) {
										selected = true;
										break;
									}
								}
								if (selected) {//需要印出<tr>
									tr_tag = true;
								}
							} else {//action不是D或U時需要印出<tr>
								tr_tag = true;
								;
							}
							if (tr_tag) {//指標為true時印出<tr>
								out.println("<tr>");
							}
							boolean selected = true;
							for (int j = 0; j < row.length; j++) {//每個欄內容
								if (row[j].equals("null")) {
									row[j] = "";
								}
								if (action.equals("N")) {//如果還沒選動作
									if (j == 0) {
					%>
					<td><input type="checkbox" name="row" value="<%=row[0]%>" /></td>
					<%
						}
								} else if (action.equals("D") || action.equals("U")) {
									String[] rowname = request.getParameterValues("row");
									session.setAttribute("select", rowname);
									selected = false;
									for (int n = 0; n < rowname.length; n++) {//找到符合的第一欄內容
										String rownamestr = new String(rowname[n].getBytes("8859_1"), "UTF-8");
										if (row[0].equals(rownamestr)) {
											selected = true;
											break;
										}
									}
									if (selected && action.equals("D")) {
										if (j == 0) {
					%>
					<td><input type="checkbox" name="row" value="<%=row[0]%>"
						checked="checked" /></td>
					<%
						}
									}
								}
								if (selected) {
									if (action.equals("U")) {
					%>
					<td><input type="text" name="<%=x + cols[j]%>"
						value="<%=row[j]%>" size='15' /></td>
					<%
						} else {
										out.println("<td>" + row[j] + "</td>");
									}
								}
							}
							if (selected) {
								x = x + 1; // 每次跑完一列，x + 1
							}
							if (tr_tag) {//指標為true時印出對應<tr>的</tr>
								out.println("</tr>");
							}
						}
						if (action.equals("I")) {
					%>
					<tr>
						<%
							for (int i = 0; i < cols.length; i++) {
						%>
						<td><input type="text" name="<%=cols[i]%>" value="" size='15' /></td>
						<%
							}
						%>
					</tr>
					<%
						}
					%>
				</tbody>
			</table>
		</font>
		<%
			if (action.equals("N")) {
		%>
		<font face="標楷體" size='5'>
			<p>請選擇您要進行的動作：</p>
			<p>
				<input type="radio" name="action" value="I" checked="checked" />新增資料
			</p>
			<p>
				<input type="radio" name="action" value="U" />修改資料
			</p>
			<p>
				<input type="radio" name="action" value="D" />刪除資料
			</p> <%
 	} else {
 %>
			<h4>
				<p>
					<a href="dbtableMaintain.jsp">回到更改頁面</a>
				</p>
			</h4> <%
 	}
 %>
			<p>
				<input type="submit" value="確定" />&nbsp;<input type="reset"
					value="重設" />
			</p>
		</font>
	</form>
	<hr>
	<font face="標楷體">
		<p>
			<a href="dbmanage.jsp">返回前頁</a>
		</p>
		<p>
			<a href="dbfirst.jsp">登出</a>
		</p>
	</font>
</body>
</html>
