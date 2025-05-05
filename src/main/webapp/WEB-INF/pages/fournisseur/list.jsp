<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    List<Fournisseur> fournisseurs = (List<Fournisseur>) request.getAttribute("fournisseurs");
%>
<html>
<head>
    <title>Fournisseur</title>
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/datatables.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/style.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/components.css">
    <!-- Custom style CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/custom.css">
    <link rel='shortcut icon' type='image/x-icon' href='<%=contextPath%>/assets/img/favicon.ico' />
</head>
<body>
<div class="loader"></div>
<div id="app">
    <div class="main-wrapper main-wrapper-1">
        <%--   HEADER --%>
        <jsp:include page="../static/header.jsp"></jsp:include>
        <%--   SIDEBAR    --%>
        <jsp:include page="../static/sidebar.jsp"></jsp:include>

        <!-- Main Content -->
        <div class="main-content">
            <section class="section">
                <div class="section-body">
                    <div class="row">
                        <div class="col-12">
                            <% if (fournisseurs.size() > 0 && fournisseurs!= null) {%>
                            <div class="card">
                                <div class="card-header">
                                    <h4>Fournisseurs</h4>
                                </div>
                                <div class="card-body">
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb">
                                            <li class="breadcrumb-item active" aria-current="page"><i class="fas fa-user-friends"></i> Fournisseurs</li>
                                        </ol>
                                    </nav>
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover" id="tableExport" style="width:100%;">
                                            <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Nom</th>
                                                <th>Country</th>
                                                <th>Type</th>
                                                <th>Group</th>
                                                <th>Devis Fournissers</th>
                                                <th>Bon de comamnde</th>
                                                <th>Demmande de devis</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <% for (Fournisseur fournisseur : fournisseurs) {%>
                                                <tr>
                                                    <td><%=fournisseur.getName()%></td>
                                                    <td><%=fournisseur.getSupplier_name()%></td>
                                                    <td><%=fournisseur.getCountry()%></td>
                                                    <td><%=fournisseur.getSupplier_type()%></td>
                                                    <td><%=fournisseur.getSupplier_group()%></td>
                                                    <td><a href="<%=contextPath%>/fournisseur/devis-fournisseur?name=<%=fournisseur.getName()%>" class="btn btn-info">Details</a></td>
                                                    <td><a href="<%=contextPath%>/fournisseur/bon-commandes?name=<%=fournisseur.getName()%>" class="btn btn-info">Details</a></td>
                                                    <td><a href="<%=contextPath%>/fournisseur/demandeDevis?name=<%=fournisseur.getName()%>" class="btn btn-info">Details</a></td>
                                                </tr>
                                            <% } %>
                                            </tbody>
                                        </table>
                                    </div>
                                </div>
                            </div>
                            <% } else {%>
                                <div class="alert alert-light alert-has-icon">
                                    <div class="alert-icon"><i class="far fa-lightbulb"></i></div>
                                    <div class="alert-body">
                                        <div class="alert-title">Aucun fournisseur</div>
                                        <a href="http://erpnext.localhost:8000/app/supplier" class="btn btn-link">Creer un nouveau fournisseur</a>
                                    </div>
                                </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </section>
            <%--     SETTINGS      --%>
            <jsp:include page="../static/settings.jsp"></jsp:include>
        </div>

        <%--   FOOTER     --%>
        <jsp:include page="../static/footer.jsp"></jsp:include>
    </div>
</div>
</body>
<script src="<%=contextPath%>/assets/js/app.min.js"></script>
<!-- JS Libraies -->
<!-- Page Specific JS File -->
<script src="<%=contextPath%>/assets/bundles/datatables/datatables.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/js/dataTables.bootstrap4.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/dataTables.buttons.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/buttons.flash.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/jszip.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/pdfmake.min.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/vfs_fonts.js"></script>
<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/buttons.print.min.js"></script>
<script src="<%=contextPath%>/assets/js/page/datatables.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
