<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Facture" %>
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    List<Facture> factures = (List<Facture>) request.getAttribute("factures");
%>
<html>
<head>
    <title>Facture</title>
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
                            <% if (factures.size() > 0 && factures!= null) {%>
                            <div class="card">
                                <div class="card-header">
                                    <h4>Facture</h4>
                                </div>
                                <div class="card-body">
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb">
                                            <li class="breadcrumb-item active" aria-current="page"><i class="fas fa-clipboard-list"></i> Factures</li>
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
                                                <th>Fournisseur</th>
                                                <th>Date de facture</th>
                                                <th>Date d'échéance</th>
                                                <th>Montant dû</th>
                                                <th>Montant total</th>
                                                <th>Devise</th>
                                                <th>Status</th>
                                                <th>Action</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <% for (Facture facture : factures) { %>
                                            <tr>
                                                <td><%= facture.getName() %></td>
                                                <td><%= facture.getSupplier() %></td>
                                                <td><%= facture.getPosting_date() %></td>
                                                <td><%= facture.getDue_date() %></td>
                                                <td><%= facture.getOutstanding_amount() %></td>
                                                <td><%= facture.getGrand_total() %></td>
                                                <td><%= facture.getCurrency() %></td>
                                                <% if (!"Paid".equalsIgnoreCase(facture.getStatus())) { %>
                                                    <td class="text-danger"><%= facture.getStatus() %></td>
                                                <% } else { %>
                                                    <td class="text-success"><%= facture.getStatus() %></td>
                                                <% } %>

                                                <td>
                                                    <% if (!"Paid".equalsIgnoreCase(facture.getStatus())) { %>
                                                    <form method="post" action="<%=contextPath%>/comptable/facture/payer" style="display: flex">
                                                        <input type="hidden" name="factureName" value="<%=facture.getName() %>" />
                                                        <input type="hidden" id="montantDu" name="montantDu" value="<%=facture.getOutstanding_amount() %>" />
                                                        <input type="number" step="0.01" id="payement" class="form-control me-2" style="width: 100px;" name="payement" value="<%=facture.getOutstanding_amount() %>" />
                                                        <button type="submit" class="btn btn-info btn-sm">Payer</button>
                                                    </form>
                                                    <% } %>
                                                </td>
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
                                        <div class="alert-title">Aucun facture</div>
                                        <a href="http://erpnext.localhost:8000/app/purchase-invoice" class="btn btn-link">Faire une nouvelle facture</a>
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
<script>
    document.addEventListener('DOMContentLoaded', function () {
        const form = document.querySelector('form');
        form.addEventListener('submit', function (e) {
            const montantDu = parseFloat(document.getElementById('montantDu').value);
            const payement = parseFloat(document.getElementById('payement').value);

            if (isNaN(montantDu) || isNaN(payement)) {
                alert("Montant invalide.");
                e.preventDefault();
                return;
            }

            if (payement > montantDu) {
                alert("Le montant payé ne peut pas être inférieur au montant dû.");
                e.preventDefault(); // Empêche la soumission
            }
        });
    });
</script>

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
