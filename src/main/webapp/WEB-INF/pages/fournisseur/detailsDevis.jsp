<%--<%@ page import="java.util.List" %>--%>
<%--<%@ page import="mg.erp.entities.Fournisseur" %>--%>
<%--<%@ page import="mg.erp.entities.DemandeDevis" %>--%>
<%--<%@ page import="mg.erp.entities.DemandeDevis" %>--%>
<%--<%@ page import="mg.erp.entities.Produit" %>&lt;%&ndash;--%>
<%--  Created by IntelliJ IDEA.--%>
<%--  User: raven--%>
<%--  Date: 01/05/2025--%>
<%--  Time: 11:10--%>
<%--  To change this template use File | Settings | File Templates.--%>
<%--&ndash;%&gt;--%>
<%--<%@ page contentType="text/html;charset=UTF-8" language="java" %>--%>
<%--<%--%>
<%--    String contextPath = request.getContextPath();--%>
<%--    List<Produit> produits = (List<Produit>) request.getAttribute("produits");--%>
<%--    String demandeDevis = (String) request.getAttribute("devis");--%>
<%--%>--%>
<%--<html>--%>
<%--<head>--%>
<%--    <title>Demande de devis</title>--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">--%>
<%--    <!-- Template CSS -->--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/datatables.min.css">--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css">--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/css/style.css">--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/css/components.css">--%>
<%--    <!-- Custom style CSS -->--%>
<%--    <link rel="stylesheet" href="<%=contextPath%>/assets/css/custom.css">--%>
<%--    <link rel='shortcut icon' type='image/x-icon' href='<%=contextPath%>/assets/img/favicon.ico' />--%>
<%--</head>--%>
<%--<body>--%>
<%--<div class="loader"></div>--%>
<%--<div id="app">--%>
<%--    <div class="main-wrapper main-wrapper-1">--%>
<%--        &lt;%&ndash;   HEADER &ndash;%&gt;--%>
<%--        <jsp:include page="../static/header.jsp"></jsp:include>--%>
<%--        &lt;%&ndash;   SIDEBAR    &ndash;%&gt;--%>
<%--        <jsp:include page="../static/sidebar.jsp"></jsp:include>--%>

<%--        <!-- Main Content -->--%>
<%--        <div class="main-content">--%>
<%--            <section class="section">--%>
<%--                <div class="section-body">--%>
<%--                    <div class="row">--%>
<%--                        <div class="col-12">--%>
<%--                            <% if (produits.size() > 0 && produits!= null) {%>--%>
<%--                            <div class="card">--%>
<%--                                <div class="card-header">--%>
<%--                                    <h4>Details du demande de devis <%=demandeDevis%></h4>--%>
<%--                                </div>--%>
<%--                                <div class="card-body">--%>
<%--                                    <div class="table-responsive">--%>
<%--                                        <table class="table table-striped table-hover" id="tableExport" style="width:100%;">--%>
<%--                                            <thead>--%>
<%--                                            <tr>--%>
<%--                                                <th>Nom article</th>--%>
<%--                                                <th>Description</th>--%>
<%--                                                <th>Quantité</th>--%>
<%--                                                <th>Prix unitaire ($)</th>--%>
<%--                                                <th>Nom devis</th>--%>
<%--                                                <th>Fournisseur</th>--%>
<%--                                            </tr>--%>
<%--                                            </thead>--%>
<%--                                            <tbody>--%>
<%--                                            <% for (Produit produit : produits) { %>--%>
<%--                                            <tr>--%>
<%--                                                <td><%= produit.getItem_name() %></td>--%>
<%--                                                <td><%= produit.getDescription() %></td>--%>
<%--                                                <td><%= produit.getQty() %></td>--%>
<%--                                                <td>--%>
<%--                                                    <form action="modifier-prix" method="post" class="d-flex">--%>
<%--                                                        <input type="hidden" name="name" value="<%= produit.getName() %>" />--%>
<%--                                                        <input type="number" step="0.01" name="rate" value="<%= produit.getRate() %>" class="form-control me-2" style="width: 100px;" />--%>
<%--                                                        <button type="submit" class="btn btn-primary btn-sm">Modifier</button>--%>
<%--                                                    </form>--%>
<%--                                                </td>--%>
<%--                                                <td><%= produit.getDevis_name() %></td>--%>
<%--                                                <td><%= produit.getFournisseur() %></td>--%>
<%--                                            </tr>--%>
<%--                                            <% } %>--%>
<%--                                            </tbody>--%>
<%--                                        </table>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            </div>--%>
<%--                            <% } else {%>--%>
<%--                                <div class="alert alert-light alert-has-icon">--%>
<%--                                    <div class="alert-icon"><i class="far fa-lightbulb"></i></div>--%>
<%--                                    <div class="alert-body">--%>
<%--                                        <div class="alert-title">Aucun produit sur la demande de devis</div>--%>
<%--                                        <a href="http://erpnext.localhost:8000/app/request-for-quotation" class="btn btn-link">Faire une nouvelle demande</a>--%>
<%--                                    </div>--%>
<%--                                </div>--%>
<%--                            <% } %>--%>
<%--                        </div>--%>
<%--                    </div>--%>
<%--                </div>--%>
<%--            </section>--%>
<%--            &lt;%&ndash;     SETTINGS      &ndash;%&gt;--%>
<%--            <jsp:include page="../static/settings.jsp"></jsp:include>--%>
<%--        </div>--%>

<%--        &lt;%&ndash;   FOOTER     &ndash;%&gt;--%>
<%--        <jsp:include page="../static/footer.jsp"></jsp:include>--%>
<%--    </div>--%>
<%--</div>--%>
<%--</body>--%>
<%--<script src="<%=contextPath%>/assets/js/app.min.js"></script>--%>
<%--<!-- JS Libraies -->--%>
<%--<!-- Page Specific JS File -->--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/datatables.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/js/dataTables.bootstrap4.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/dataTables.buttons.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/buttons.flash.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/jszip.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/pdfmake.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/vfs_fonts.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/bundles/datatables/export-tables/buttons.print.min.js"></script>--%>
<%--<script src="<%=contextPath%>/assets/js/page/datatables.js"></script>--%>
<%--<!-- Template JS File -->--%>
<%--<script src="<%=contextPath%>/assets/js/scripts.js"></script>--%>
<%--<!-- Custom JS File -->--%>
<%--<script src="<%=contextPath%>/assets/js/custom.js"></script>--%>
<%--</html>--%>
