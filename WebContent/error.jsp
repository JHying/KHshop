<%-- 
    Document   : error
    Created on : 2015/4/27, 下午 10:24:01
    Author     : H_yin
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0,maximum-scale=5">

        <title>HMhouse | error page</title>

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
    <span style="font-size:18px;"></span><span style="font-size:24px;"><meta http-equiv="refresh" content="5;URL=Welcome.do"> </span>
</head>
<body>
    <div id="site-content">
        <div class="site-header">
            <div class="container">
                <a href="Welcome.do" id="branding">
                    <div class="logo-text">
                        <h1 class="site-title">HMhouse</h1>
                        <small class="site-description">歡迎您的蒞臨！點此返回首頁</small>
                    </div>
                </a> <!-- #branding -->
            </div> <!-- .container -->
        </div> <!-- .site-header -->

        <main class="main-content" style="text-align: center">
            <div class="container">
                <div class="page">
                    <div class="entry-content">
                        <div class="row">
                            <font face="標楷體" size="5">
                            <h1>網頁閒置過久或訂單已送出</h1>
                            <h1>5 秒後導回首頁（若沒跳轉，請點選左上角返回）</h1>
                            </font>
                        </div>
                    </div>
                </div>
            </div> <!-- .container -->
        </main> <!-- .main-content -->
        <div class="site-footer">
            <div class="container">
            </div> <!-- .container -->
        </div> <!-- .site-footer -->
    </div>

    <script src="js/jquery-1.11.1.min.js"></script>
    <script src="js/plugins.js"></script>
    <script src="js/app.js"></script>
</body>
</html>
