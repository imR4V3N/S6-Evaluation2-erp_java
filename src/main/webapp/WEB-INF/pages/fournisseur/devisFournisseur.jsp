<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %>
<%@ page import="mg.erp.entities.DemandeDevis" %>
<%@ page import="mg.erp.entities.DemandeDevis" %>
<%@ page import="mg.erp.entities.DevisFournisseur" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    List<DevisFournisseur> devisFournisseurs = (List<DevisFournisseur>) request.getAttribute("devisFournisseurs");
    String fournisseur = (String) request.getAttribute("fournisseur");
%>
<html>
<head>
    <title>Demande de devis</title>
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
                            <% if (devisFournisseurs.size() > 0 && devisFournisseurs!= null) {%>
                            <div class="card">
                                <div class="card-header">
                                    <h4>Devis du fournisseurs <%=fournisseur%></h4>
                                </div>
                                <div class="card-body">
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb">
                                            <li class="breadcrumb-item"><a href="<%=contextPath%>/fournisseur"><i class="fas fa-user-friends"></i> Fournisseurs</a></li>
                                            <li class="breadcrumb-item active" aria-current="page"><i class="fas fa-file-alt"></i>Devis fournisseur</li>
                                        </ol>
                                    </nav>
                                    <% if (request.getAttribute("error") != null) { %>
                                    <div class="alert alert-danger alert-dismissible show fade">
                                        <div class="alert-body">
                                            <button class="close" data-dismiss="alert">
                                                <span>&times;</span>
                                            </button>
                                            <%= request.getAttribute("error") %>
                                        </div>
                                    </div>
                                    <% } %>
                                    <% if (request.getAttribute("message") != null) { %>
                                    <div class="alert alert-success alert-dismissible show fade">
                                        <div class="alert-body">
                                            <button class="close" data-dismiss="alert">
                                                <span>&times;</span>
                                            </button>
                                            <%= request.getAttribute("message") %>
                                        </div>
                                    </div>
                                    <% } %>
                                    <div class="table-responsive">
                                        <table class="table table-striped table-hover" id="tableExport" style="width:100%;">
                                            <thead>
                                            <tr>
                                                <th>Nom</th>
                                                <th>Status</th>
                                                <th>Entreprise</th>
                                                <th>Date de Transaction</th>
                                                <th>Valide Jusqu'à</th>
                                                <th>Devise</th>
                                                <th>Quantité Totale</th>
                                                <th>Total</th>
                                                <th>Total Général</th>
                                                <th>Adresse du Fournisseur</th>
                                                <th>Adresse de Livraison</th>
                                                <th>Adresse de Facturation</th>
                                                <th>Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <% for (DevisFournisseur devisFournisseur : devisFournisseurs) { %>
                                            <tr>
                                                <td><%= devisFournisseur.getName() %></td>
                                                <td><%= devisFournisseur.getStatus().equalsIgnoreCase("Submitted") ? "<p class=\"text text-info\">"+ devisFournisseur.getStatus()+"</p>" : "<p class=\"text text-danger\">"+ devisFournisseur.getStatus()+"</p>" %></td>
                                                <td><%= devisFournisseur.getCompany() %></td>
                                                <td><%= devisFournisseur.getTransactionDate() %></td>
                                                <td><%= devisFournisseur.getValidTill() %></td>
                                                <td><%= devisFournisseur.getCurrency() %></td>
                                                <td><%= devisFournisseur.getTotalQty() %></td>
                                                <td><%= devisFournisseur.getTotal() %> $</td>
                                                <td><%= devisFournisseur.getGrandTotal() %> $</td>
                                                <td><%= devisFournisseur.getSupplierAddress() %></td>
                                                <td><%= devisFournisseur.getShippingAddress() %></td>
                                                <td><%= devisFournisseur.getBillingAddress() %></td>
                                                <td><a href="<%=contextPath%>/fournisseur/devis-fournisseur/<%=devisFournisseur.getName()%>/items" class="btn btn-info">Details</a></td>
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
                                        <div class="alert-title">Aucun devis de fournisseur</div>
                                        <a href="http://erpnext.localhost:8000/app/supplier-quotation" class="btn btn-link">Faire une nouvelle devis de fournisseur</a>
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
