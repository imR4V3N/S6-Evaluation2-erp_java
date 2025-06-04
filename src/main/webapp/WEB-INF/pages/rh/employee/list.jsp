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
    List<Genre> genres = (List<Genre>) request.getAttribute("genres");
    List<Designation> designations = (List<Designation>) request.getAttribute("designations");
%>
<html>
<head>
    <title>Employee</title>
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
                                        <h4>Filtre</h4>
                                    </div>
                                    <div class="card-body col-12">
                                        <form class="col-12" action="<%=contextPath%>/employee" method="get" style="display: flex; gap: 1.5em; justify-content: center; align-items: center;">

                                            <div class="form-group" style="width: 80%;">
                                                <label>Nom complet</label>
                                                <input class="form-control" type="text" name="nom"/>
                                            </div>

                                            <div class="form-group" style="width: 80%;">
                                                <label>Age min</label>
                                                <input class="form-control" min="0" maxlength="4" type="number" name="ageMin"/>
                                            </div>

                                            <div class="form-group" style="width: 80%;">
                                                <label>Age max</label>
                                                <input class="form-control" min="0" maxlength="4" type="number" name="ageMax"/>
                                            </div>

                                            <div class="form-group" style="width: 80%;">
                                                <div class="form-group">
                                                    <label>Genre</label>
                                                    <select name="genre" class="form-control select2">
                                                        <option value="">Choisir un genre</option>
                                                        <% if (genres != null && genres.size() >  0) {
                                                            for (Genre g : genres) {%>
                                                                <option value="<%=g.getName()%>"><%=g.getName()%></option>
                                                        <% } } %>
                                                    </select>
                                                </div>
                                            </div>

                                            <div class="form-group" style="width: 80%;">
                                                <div class="form-group">
                                                    <label>Poste</label>
                                                    <select name="poste" class="form-control select2">
                                                        <option value="">Choisir un poste</option>
                                                        <% if (designations != null && designations.size() >  0) {
                                                            for (Designation d : designations) {%>
                                                                <option value="<%=d.getName()%>"><%=d.getName()%></option>
                                                        <% } } %>
                                                    </select>
                                                </div>
                                            </div>

                                            <button type="submit" style="width: 15%;" class="btn btn-primary p-2 col-3">Filtrer</button>
                                        </form>

                                    </div>
                                </div>
                            </div>

                            <% if (employees.size() > 0 && employees!= null) {%>
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
                                        <table class="table table-striped table-hover" id="tableExport" style="width:100%;">
                                            <thead>
                                            <tr>
                                                <th>ID</th>
                                                <th>Nom</th>
                                                <th>Prenom</th>
                                                <th>Genre</th>
                                                <th>Date de naissance</th>
                                                <th>Date d'embauhe</th>
                                                <th>Status</th>
                                                <th>Compagnie</th>
                                                <th>Actions</th>
                                            </tr>
                                            </thead>
                                            <tbody>
                                            <% for (Employee emp : employees) {%>
                                                <tr>
                                                    <td><%=emp.getName()%></td>
                                                    <td><%=emp.getLast_name()%></td>
                                                    <td><%=emp.getFirst_name()%></td>
                                                    <td><%=emp.getGender()%></td>
                                                    <td><%=emp.getDate_of_birth()%></td>
                                                    <td><%=emp.getDate_of_joining()%></td>
                                                    <td><%=emp.getStatus()%></td>
                                                    <td><%=emp.getCompany()%></td>
                                                    <td><a href="<%=contextPath%>/employee/fiche-paye?idEmp=<%=emp.getName()%>&mois=<%=YearMonth.now()%>" class="btn btn-info">Fiche de paye</a></td>
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

<script src="<%=contextPath%>/assets/bundles/select2/dist/js/select2.full.min.js"></script>
<!-- Page Specific JS File -->
<script src="<%=contextPath%>/assets/js/page/forms-advanced-forms.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
