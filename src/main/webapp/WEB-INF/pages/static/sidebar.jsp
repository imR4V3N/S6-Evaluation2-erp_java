<%@ page import="java.time.YearMonth" %>
<%@ page import="java.time.Year" %>
<%
    String contextPath = request.getContextPath();
%>

<div class="main-sidebar sidebar-style-2">
    <aside id="sidebar-wrapper">
        <div class="sidebar-brand">
            <a href="<%=contextPath%>/employee"> <img alt="image" src="<%=contextPath%>/assets/img/logo.png" class="header-logo" /> <span
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
        <ul class="sidebar-menu">
            <li class="menu-header">RH</li>
            <li class="dropdown">
                <a href="#" class="menu-toggle nav-link has-dropdown"><i class="fas fa-user-friends"></i><span>Employees</span></a>
                <ul class="dropdown-menu">
                    <li><a class="nav-link" href="<%=contextPath%>/employee">Listes</a></li>
                    <li><a class="nav-link" href="<%=contextPath%>/employee/fiche?mois=<%=YearMonth.now()%>">Fiche de paie</a></li>
                    <li><a class="nav-link" href="<%=contextPath%>/salaire/element?element=null&comparaison=null&montant=-1">Salaire par elemment</a></li>
                </ul>
            </li>
            <li class="dropdown">
                <a href="#" class="menu-toggle nav-link has-dropdown"><i class="fas fa-chart-bar"></i><span>Salaires</span></a>
                <ul class="dropdown-menu">
                    <li><a class="nav-link" href="<%=contextPath%>/salaire/statistique?annee=<%=Year.now()%>">Statistiques</a></li>
                    <li><a class="nav-link" href="<%=contextPath%>/salaire/data">Generer</a></li>
                    <li><a class="nav-link" href="<%=contextPath%>/salaire/modif">Modification</a></li>
                </ul>
            </li>
            <li class="dropdown">
                <a href="<%=contextPath%>/data/page-import" class="nav-link"><i class="fas fa-file-export"></i><span>Import</span></a>
            </li>
        </ul>
    </aside>
</div>
