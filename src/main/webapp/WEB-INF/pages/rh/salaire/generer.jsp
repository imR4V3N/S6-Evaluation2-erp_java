<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %>
<%@ page import="mg.erp.entities.rh.Employee" %>
<%@ page import="mg.erp.entities.rh.Genre" %>
<%@ page import="mg.erp.entities.rh.Designation" %>
<%@ page import="java.time.YearMonth" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    List<Employee> employees = (List<Employee>) request.getAttribute("employees");
    String success = (String) request.getAttribute("success");
    String error = (String) request.getAttribute("error");
%>
<html>
<head>
    <title>Generer salaire</title>
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
                            <div class="col-12">
                                <div class="card col-12">
                                    <div class="card-header">
                                        <h4>Generer salaire</h4>
                                    </div>
                                    <div class="card-body col-12">
                                        <% if (success != null) {%>
                                        <div class="alert alert-success alert-dismissible show fade">
                                            <div class="alert-body">
                                                <button class="close" data-dismiss="alert">
                                                    <span>&times;</span>
                                                </button>
                                                <strong><i class="fas fa-check"></i>  <%=success%></strong>
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
                                            </div>
                                        </div>
                                        <%}%>
                                        <form class="col-12" action="<%=contextPath%>/salaire/generer" method="post" >
                                            <div class="form-group" style="width: 80%;">
                                                <div class="form-group">
                                                    <label>Employee</label>
                                                    <select name="employee" class="form-control select2">
                                                        <% if (employees != null && employees.size() >  0) {
                                                            for (Employee g : employees) {%>
                                                            <option value="<%=g.getName()%>"><%=g.getName()%> - <%=g.getEmployee_name()%></option>
                                                        <% } } %>
                                                    </select>
                                                </div>
                                                <div class="form-group" style="width: 80%;">
                                                    <label>Mois debut</label>
                                                    <input class="form-control" type="month" name="debut"/>
                                                </div>

                                                <div class="form-group" style="width: 80%;">
                                                    <label>Mois fin</label>
                                                    <input class="form-control" type="month" name="fin"/>
                                                </div>

                                                <div class="form-group" style="width: 80%;">
                                                    <label>Montant</label>
                                                    <input class="form-control" type="text" name="montant" value="0"/>
                                                </div>
                                            </div>

                                            <button type="submit" style="width: 15%;" class="btn btn-primary p-2 col-3">Generer</button>
                                        </form>

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
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
