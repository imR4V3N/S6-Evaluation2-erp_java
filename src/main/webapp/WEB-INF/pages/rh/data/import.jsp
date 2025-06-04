<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %>
<%@ page import="mg.erp.entities.rh.Employee" %>
<%@ page import="mg.erp.entities.rh.Genre" %>
<%@ page import="mg.erp.entities.rh.Designation" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="java.util.Map" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
    Map<String, List<Object>> insertRecords = (Map<String, List<Object>>) request.getAttribute("insertedRecords");
    List<Map<String, Object>> validationErrors = (List<Map<String, Object>>) request.getAttribute("validationErrors");
%>
<html>
<head>
    <title>Import</title>
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/datatables.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/style.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/components.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/select2/dist/css/select2.min.css">
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

                            <div class="card">
                                <div class="card-body">
                                    <nav aria-label="breadcrumb">
                                        <ol class="breadcrumb">
                                            <li class="breadcrumb-item active" aria-current="page"><i class="fas fa-user-friends"></i> Import</li>
                                        </ol>
                                    </nav>
                                    <div class="card">
                                        <div class="card-header">
                                            <h4>Import csv</h4>
                                        </div>
                                        <div class="card-body" >
                                            <% if (success != null) {%>
                                                <div class="alert alert-success alert-dismissible show fade">
                                                    <div class="alert-body">
                                                        <button class="close" data-dismiss="alert">
                                                            <span>&times;</span>
                                                        </button>
                                                        <strong><i class="fas fa-check"></i>  <%=success%></strong>
                                                        <br>
                                                        <%-- HERE --%>
                                                        <% if (insertRecords != null && !insertRecords.isEmpty()) { %>
                                                            <div class="response-container mt-2">
                                                                <h5>Enregistrements insérés</h5>

                                                                <% for (Map.Entry<String, List<Object>> entry : insertRecords.entrySet()) { %>
                                                                <div class="card border-success mb-2 p-2">
                                                                    <strong class="text text-dark"><%= entry.getKey() %></strong>
                                                                    <ul>
                                                                        <% for (Object record : entry.getValue()) { %>
                                                                            <li class="text text-black-50"><%= record.toString() %></li>
                                                                        <% } %>
                                                                    </ul>
                                                                </div>
                                                                <% } %>
                                                            </div>
                                                        <% } %>
                                                    </div>
                                                </div>
                                            <%}%>
                                            <% if (error != null) {%>
                                                <div class="alert alert-danger alert-dismissible show fade">
                                                    <div class="alert-body">
                                                        <button class="close" data-dismiss="alert">
                                                            <span>&times;</span>
                                                        </button>
                                                        <strong><i class="fas fa-times"></i>  <%=error%></strong>
                                                        <br>
                                                        <%-- HERE --%>
                                                        <%if (validationErrors != null && !validationErrors.isEmpty()) { %>
                                                            <div class="error-list mt-2">
                                                                <ul>
                                                                    <% for (Map<String, Object> ve : validationErrors) { %>
                                                                    <li class="mb-2">
                                                                        <strong>Fichier:</strong> <%= ve.get("file") %><br/>
                                                                        <strong>Ligne:</strong> <%= ve.get("line") %><br/>
                                                                        <strong>Erreur:</strong> <%= ve.get("error_message") %><br/>
                                                                        <strong>Données:</strong> <%= ve.get("data") %>
                                                                    </li>
                                                                    <% } %>
                                                                </ul>
                                                            </div>
                                                        <% } %>
                                                    </div>
                                                </div>
                                            <%}%>
                                            <form method="post" action="<%=contextPath%>/data/import/api" enctype="multipart/form-data" class="col-12">
                                                <div class="section-title">Fichier 1 (Employee)</div>
                                                <div class="custom-file mb-3">
                                                    <input type="file" name="file1" class="custom-file-input" id="file1" accept=".csv">
                                                    <label class="custom-file-label" for="file1">Choisir un fichier</label>
                                                </div>

                                                <div class="section-title">Fichier 2 (Salary Structure & Salary Component)</div>
                                                <div class="custom-file mb-3">
                                                    <input type="file" name="file2" class="custom-file-input" id="file2" accept=".csv">
                                                    <label class="custom-file-label" for="file2">Choisir un fichier</label>
                                                </div>

                                                <div class="section-title">Fichier 3 (Salary Structure Assignment & Salary Slip)</div>
                                                <div class="custom-file mb-3">
                                                    <input type="file" name="file3" class="custom-file-input" id="file3" accept=".csv">
                                                    <label class="custom-file-label" for="file3">Choisir un fichier</label>
                                                </div>

                                                <button type="submit" style="width: 15%; margin-top: 2em" class="btn btn-primary p-2 col-3">Importer</button>
                                            </form>

                                        </div>
                                    </div>
                                </div>
                            </div>
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

<script src="<%=contextPath%>/assets/bundles/select2/dist/js/select2.full.min.js"></script>
<!-- Page Specific JS File -->
<script src="<%=contextPath%>/assets/js/page/forms-advanced-forms.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<script src="<%=contextPath%>/assets/js/file.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
