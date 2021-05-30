<%-- 
    Document   : index
    Created on : 2015/4/26, 下午 06:29:51
    Author     : K45V-MB
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Scanner"%>
<%@page import="java.io.File"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>HMhouse | DBuser Login</title>
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
        <font face="標楷體" size='5'>
        <h1>登入</h1>
        <form name="login" action="firstCheck.do" method="post">
            <p>帳號：<input type="text" name="user" required/></p>
            <p>密碼：<input type="password" name="pass" required/></p>
            <p><button type="submit"/>確認</button>
               <button type="reset"/>重置</button></p>
        </form>
        </font>
    </body>
</html>
