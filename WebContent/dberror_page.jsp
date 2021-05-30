<%-- 
    Document   : error
    Created on : 2015/4/27, 下午 10:24:01
    Author     : H_yin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>HMhouse | DBlogin ERROR</title>
        <span style="font-size:18px;"></span><span style="font-size:24px;"><meta http-equiv="refresh" content="5;URL=dbfirst.jsp"> </span>
    </head>
    <body>
        <%
            if (!session.isNew()) {
                session.invalidate();
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
        <font face="標楷體" size="5">
        <h1>ERROR！ERROR！</h1>
        <h3>您輸入的帳號密碼不正確</h3>
        <p><a href="dbfirst.jsp"><font face="標楷體" size="4">＊5秒後自行跳轉，若沒跳轉請點選此處＊</font></a></p>
        </font>
    </body>
</html>
