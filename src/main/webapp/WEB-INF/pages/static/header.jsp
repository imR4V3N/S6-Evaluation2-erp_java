<%@ page import="mg.erp.entities.Auth" %><%
    String contextPath = request.getContextPath();
    Auth auth = (Auth) request.getSession().getAttribute("user");
%>

<div class="navbar-bg"></div>
<nav class="navbar navbar-expand-lg main-navbar sticky">
    <div class="form-inline mr-auto">
        <ul class="navbar-nav mr-3">
            <li><a href="#" data-toggle="sidebar" class="nav-link nav-link-lg
									collapse-btn"> <i data-feather="align-justify"></i></a></li>
            <li><a href="#" class="nav-link nav-link-lg fullscreen-btn">
                <i data-feather="maximize"></i>
            </a></li>
        </ul>
    </div>
    <ul class="navbar-nav navbar-right">
        <li class="dropdown"><a href="#" data-toggle="dropdown"
                                class="nav-link dropdown-toggle nav-link-lg nav-link-user"> <img alt="image" src="<%=contextPath%>/assets/img/user.jpeg"
                                                                                                 class="user-img-radious-style"> <span class="d-sm-none d-lg-inline-block"></span></a>
            <div class="dropdown-menu dropdown-menu-right pullDown">
                <div class="dropdown-title"><%=auth.getFull_name()%></div>

                <div class="dropdown-divider"></div>
                <a href="http://erpnext.localhost:8000" class="dropdown-item has-icon text-dark" target="_blank" rel="noopener noreferrer"> <i class="fas fa-cogs text-info"></i>
                    ERPNext
                </a>
                <a href="<%=contextPath%>/logout" class="dropdown-item has-icon text-danger"> <i class="fas fa-sign-out-alt"></i>
                    Logout
                </a>
            </div>
        </li>
    </ul>
</nav>
