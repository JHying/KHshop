<%-- 
    Document   : import
    Created on : 2016/6/18, 下午 08:24:28
    Author     : G551
--%>

<%@page import="java.util.Comparator"%>
<%@page import="java.util.Collections"%>
<%@page import="java.util.Arrays"%>
<%@page import="java.io.File"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>HMhouse | DBimport Page</title>
    </head>
    <body><font face="標楷體">
        <%
            if (session.getAttribute("success") != null) {
                String Msg = (String) session.getAttribute("success");
        %>
        <script type="text/javascript" language="javascript">
            window.alert("<%=Msg%>");//跳出訊息框顯示成功
        </script>
        <%
                session.removeAttribute("success");
            }
            if (session.getAttribute("dbconnParam") == null) {
                RequestDispatcher dispatch = request.getRequestDispatcher("dberror_page.jsp");
                dispatch.forward(request, response);//如果有且錯則委派至結果頁
            } else {
                String table = (String) session.getAttribute("table");
                List dataList = (List) session.getAttribute("tablelist");
                out.println("<h3>當前資料表：" + table + "</h3>");
                String[] cols = (String[]) dataList.get(0);
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
        <p><a href="dbmanage.jsp">返回前頁</a></p>
        <%
            if (table.equals("product")) {
        %>
        <h3>上傳產品圖片（可批量，建議大小：< 5 MB/張）</h3>
        <p><small>說明：增加產品時，圖片務必一併上傳，檔名「呼應資料庫設置之圖片名稱」，jpg、jpeg、gif、png、bmp 格式。</small></p>
        <form action="UpdateWithFile.do" method="POST" enctype="multipart/form-data">
            請選擇檔案：<input type="file" name="IMGNAME[]" required multiple draggable/>
            <p><input type="submit" name="imgimport" value="上傳" />&nbsp;<input type="reset" value="重設" /></p>
        </form>
        <small><strong>已存在之圖片檔</strong></small><br>
        <%
            //顯示存在的圖片
            String rootpath = request.getServletContext().getRealPath("/");//網站根目錄
            String folder = rootpath + "datafile/";
            File newfolder = new File(folder);
            File[] files = newfolder.listFiles();
            List fileList = Arrays.asList(files);
            Collections.sort(fileList, new Comparator<File>() {//依名稱排序
                public int compare(File o1, File o2) {
                    if (o1.isDirectory() && o2.isFile()) {
                        return -1;
                    }
                    if (o1.isFile() && o2.isDirectory()) {
                        return 1;
                    }
                    return o1.getName().compareTo(o2.getName());
                }
            });
            for (File file1 : files) {
                String lowerName = file1.getName().toLowerCase();
                if (lowerName.contains(".bmp") || lowerName.contains(".jpg") || lowerName.contains(".jpeg") || lowerName.contains(".gif") || lowerName.contains(".png")) {
        %>
        <small><strong><%=file1.getName()%></strong></small><br>
        <%
                }
            }
        %>
        <hr>
        <%
            }
        %>
        <h3>匯入資料表</h3>
        <p><small>說明：此處為匯入新資料(不會刪除舊資料)，支援 excel。</small></p>
        <form action="UpdateWithFile.do" method="POST" enctype="multipart/form-data">
            請選擇檔案：<input type="file" name="FILENAME" required/>
            <p><input type="submit" name="import" value="上傳" />&nbsp;<input type="reset" value="重設" /></p>
        </form>
        <hr>
        <h3>更新資料表</h3>
        <p><small>說明：此處將覆蓋資料表(舊資料刪除)，支援 excel。</small></p>
        <form action="UpdateWithFile.do" method="POST" enctype="multipart/form-data">
            請選擇檔案：<input type="file" name="FILENAME2" required/>
            <p><input type="submit" name="update" value="上傳" />&nbsp;<input type="reset" value="重設" /></p>
        </form>
        <hr>
        <p><a href="dbmanage.jsp">返回前頁</a></p>
        <h3>目前資料</h3>
        <table border="1">
            <thead  bgcolor='#FFCC99'>
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
            <tbody  bgcolor='#FFFFE6'>
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
        </table>
        <%
            }
        %>
        </font></body>
</html>
