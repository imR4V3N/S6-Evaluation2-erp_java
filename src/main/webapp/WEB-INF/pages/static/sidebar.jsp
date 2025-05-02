<%
    String contextPath = request.getContextPath();
%>

<div class="main-sidebar sidebar-style-2">
    <aside id="sidebar-wrapper">
        <div class="sidebar-brand">
            <a href="<%=contextPath%>/fournisseur"> <img alt="image" src="<%=contextPath%>/assets/img/logo.png" class="header-logo" /> <span
                    class="logo-name">ERP</span>
            </a>
        </div>
        <ul class="sidebar-menu">
            <li class="menu-header">Fournisseur</li>
            <li class="dropdown">
                <a href="<%=contextPath%>/fournisseur" class="nav-link"><i class="fas fa-user-friends"></i><span>Fournisseurs</span></a>
            </li>
            <li class="menu-header">Mode comptable</li>
            <li class="dropdown">
                <a href="#" class="menu-toggle nav-link has-dropdown"><i class="fas fa-university"></i><span>Comptable</span></a>
                <ul class="dropdown-menu">
                    <li><a class="nav-link" href="<%=contextPath%>/comptable/factures">Factures</a></li>
                </ul>
            </li>
        </ul>
    </aside>
</div>
