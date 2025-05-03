<%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 10:50
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>Login</title>
    <!-- General CSS Files -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/bootstrap-social/bootstrap-social.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/style.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/components.css">
    <!-- Custom style CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/custom.css">
    <link rel='shortcut icon' type='image/x-icon' href='<%=contextPath%>/assets/img/favicon.ico' />
</head>
<body>
<div class="loader"></div>
<div id="app">
    <section class="section">
        <div class="container mt-5">
            <div class="row">
                <div class="col-12 col-sm-8 offset-sm-2 col-md-6 offset-md-3 col-lg-6 offset-lg-3 col-xl-4 offset-xl-4">
                    <div class="card card-primary">
                        <div class="card-header">
                            <h4>Login</h4>
                        </div>
                        <div class="card-body">
                            <% if (error != null) {%>
                                <div class="alert alert-danger alert-dismissible show fade">
                                    <div class="alert-body">
                                        <button class="close" data-dismiss="alert">
                                            <span>&times;</span>
                                        </button>
                                        <%=error%>
                                    </div>
                                </div>
                            <% }  %>
                            <form method="POST" action="<%=contextPath%>/login" class="needs-validation" novalidate="">
                                <div class="form-group">
                                    <label for="email">Email</label>
                                    <input id="email" type="text"  class="form-control" name="username" value="Administrator" tabindex="1" required autofocus>
                                </div>
                                <div class="form-group">
                                    <div class="d-block">
                                        <label for="password" class="control-label">Password</label>
                                    </div>
                                    <input id="password" type="password" class="form-control"  name="password" value="admin" tabindex="2" required>
                                </div>
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary btn-lg btn-block" tabindex="4">
                                        Login
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>
</body>
<!-- General JS Scripts -->
<script src="<%=contextPath%>/assets/js/app.min.js"></script>
<!-- JS Libraies -->
<!-- Page Specific JS File -->
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
