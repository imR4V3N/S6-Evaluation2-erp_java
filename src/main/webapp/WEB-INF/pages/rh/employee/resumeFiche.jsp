<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %>
<%@ page import="mg.erp.entities.rh.Employee" %>
<%@ page import="mg.erp.entities.rh.Genre" %>
<%@ page import="mg.erp.entities.rh.Designation" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="mg.erp.entities.rh.SalarySummary" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.LinkedHashSet" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    String mois = (String) request.getAttribute("mois");
    List<SalarySummary> summaries = (List<SalarySummary>) request.getAttribute("summaries");
%>
<html>
<head>
    <title>Fiche de paie</title>
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/datatables.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/style.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/components.css">
    <link rel="stylesheet" href="assets/bundles/select2/dist/css/select2.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/custom.css">
    <link rel='shortcut icon' type='image/x-icon' href='<%=contextPath%>/assets/img/favicon.ico' />
</head>
<body>
<div class="loader"></div>
<div id="app">
    <div class="main-wrapper main-wrapper-1">
        <%--   HEADER --%>
        <jsp:include page="../../static/header.jsp"></jsp:include>
        <%--   SIDEBAR    --%>
        <jsp:include page="../../static/sidebar.jsp"></jsp:include>

        <!-- Main Content -->
        <div class="main-content">
            <section class="section">
                <div class="section-body">
                    <div class="row">
                        <div class="col-12">
                            <div class="col-12">
                                <div class="card col-12">
                                    <div class="card-header">
                                        <h4>Filtre</h4>
                                    </div>
                                    <div class="card-body col-12">
                                        <form class="col-12" action="<%=contextPath%>/employee/fiche" method="get" style="display: flex; gap: 1.5em; justify-content: center; align-items: center;">

                                            <div class="form-group" style="width: 80%;">
                                                <label>Mois</label>
                                                <input class="form-control" type="month" name="mois" value="<%=mois%>"/>
                                            </div>


                                            <button type="submit" style="width: 15%;" class="btn btn-primary p-2 col-3">Filtrer</button>
                                        </form>

                                    </div>
                                </div>
                            </div>

                            <% if (summaries != null && !summaries.isEmpty()) {
                                Set<String> allComponents = new LinkedHashSet<>();
                                for (SalarySummary summary : summaries) {
                                    allComponents.addAll(summary.getComponentTotals().keySet());
                                }
                            %>
                            <div class="card">
                                <div class="card-header">
                                    <h4>Employee</h4>
                                </div>
                                <div class="card-body">
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb">
                                            <li class="breadcrumb-item active" aria-current="page"><i class="fas fa-user-friends"></i> Employees</li>
                                        </ol>
                                    </nav>
                                    <div class="table-responsive">
                                        <table class="table table-bordered table-striped table-hover" id="tableExport" style="width:100%;">
                                            <thead class="table-dark">
                                            <tr>
                                                <th>Employé</th>
                                                <th>Mois</th>
                                                <th>Paie brute</th>
                                                <th>Paie nette</th>
                                                <% for (String comp : allComponents) { %>
                                                <th><%= comp %></th>
                                                <% } %>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <% for (SalarySummary summary : summaries) { %>
                                            <tr>
                                                <td><%= summary.getNomEmployee() %></td>
                                                <td><%= summary.getMonth() %></td>
                                                <td><%= String.format("%.2f", summary.getTotalPayBrut()) %> $</td>
                                                <td><%= String.format("%.2f", summary.getTotalPayNet()) %> $</td>

                                                <% for (String comp : allComponents) {
                                                    Double value = summary.getComponentTotals().getOrDefault(comp, 0.0);
                                                %>
                                                <td><%= String.format("%.2f", value) %> $</td>
                                                <% } %>
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
                                    <div class="alert-title">Aucun employee</div>
                                    <a href="http://erpnext.localhost:8000/app/employee" class="btn btn-link">Ajouter un nouveau employee</a>
                                </div>
                            </div>
                            <% } %>
                        </div>
                    </div>
                </div>
            </section>
            <%--     SETTINGS      --%>
            <jsp:include page="../../static/settings.jsp"></jsp:include>
        </div>

        <%--   FOOTER     --%>
        <jsp:include page="../../static/footer.jsp"></jsp:include>
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

<script src="assets/bundles/select2/dist/js/select2.full.min.js"></script>
<!-- Page Specific JS File -->
<script src="assets/js/page/forms-advanced-forms.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
