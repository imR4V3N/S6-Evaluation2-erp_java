<%
    String contextPath = request.getContextPath();
%>

<div class="main-sidebar sidebar-style-2">
    <aside id="sidebar-wrapper">
        <div class="sidebar-brand">
            <a href="index.html"> <img alt="image" src="assets/img/logo.png" class="header-logo" /> <span
                    class="logo-name">ERP</span>
            </a>
        </div>
        <ul class="sidebar-menu">
            <li class="menu-header">Main</li>
            <li class="dropdown">
                <a href="<%=contextPath%>" class="nav-link"><i data-feather="monitor"></i><span>Dashboard</span></a>
            </li>
            <li class="menu-header">UI Elements</li>
            <li class="dropdown">
                <a href="#" class="menu-toggle nav-link has-dropdown"><i data-feather="copy"></i><span>Basic
                  Components</span></a>
                <ul class="dropdown-menu">
                    <li><a class="nav-link" href="<%=contextPath%>">Alert</a></li>
                    <li><a class="nav-link" href="<%=contextPath%>">Badge</a></li>
                </ul>
            </li>
        </ul>
    </aside>
</div>
