<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.Fournisseur" %>
<%@ page import="mg.erp.entities.rh.Employee" %>
<%@ page import="mg.erp.entities.rh.Genre" %>
<%@ page import="mg.erp.entities.rh.Designation" %>
<%@ page import="mg.erp.entities.rh.FichePaye" %>
<%@ page import="java.time.YearMonth" %>
<%@ page import="com.google.gson.Gson" %>
<%@ page import="java.net.URLEncoder" %>
<%@ page import="mg.erp.utils.YearMonthAdapter" %>
<%@ page import="com.google.gson.GsonBuilder" %><%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String contextPath = request.getContextPath();
    Gson gson = new GsonBuilder()
            .registerTypeAdapter(YearMonth.class, new YearMonthAdapter())
            .create();

    FichePaye fichePaye = (FichePaye) request.getAttribute("fichePaye");
    String idEmp = (String) request.getAttribute("idEmp");
    YearMonth mois = (YearMonth) request.getAttribute("mois");
%>
<html>
<head>
    <title>Fiche de paie</title>
    <link rel="stylesheet" href="<%=contextPath%>/assets/css/app.min.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/datatables.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/datatables/DataTables-1.10.16/css/dataTables.bootstrap4.min.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/bootstrap-social/bootstrap-social.css">
    <link rel="stylesheet" href="<%=contextPath%>/assets/bundles/summernote/summernote-bs4.css">
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
                                        <form class="col-12" action="<%=contextPath%>/employee/fiche-paye" method="get" style="display: flex; gap: 1.5em; justify-content: center; align-items: center;">

                                            <input class="form-control" type="hidden" name="idEmp" value="<%=idEmp%>"/>

                                            <div class="form-group" style="width: 80%;">
                                                <label>Mois et Annee</label>
                                                <input class="form-control" type="month" name="mois" value="<%=mois%>"/>
                                            </div>


                                            <button type="submit" style="width: 15%;" class="btn btn-primary p-2 col-3">Filtrer</button>
                                        </form>

                                    </div>
                                </div>
                            </div>

                            <% if (fichePaye!= null) {
                                String json = URLEncoder.encode(gson.toJson(fichePaye), "UTF-8");
                            %>
                            <div class="card">
                                <nav aria-label="breadcrumb">
                                    <ol class="breadcrumb">
                                        <li class="breadcrumb-item"><a href="<%=contextPath%>/employee"><i class="fas fa-user-friends"></i> Employees</a></li>
                                        <li class="breadcrumb-item active" aria-current="page"><i class="far fa-newspaper"></i> Fiche de paie</li>
                                    </ol>
                                </nav>
                                <div class="card-header">
                                    <h4>Fiche de paie</h4>
                                </div>
                                <div class="card-body">
                                    <div class="py-4">
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Identifiant de l'employé
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getEmployee()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Nom de l'employé
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getEmployee_name()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Poste
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getDesignation()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Departement
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getDepartement()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Structure de salaire
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getSalary_structure()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Salaire net
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getNet_pay()%> $
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Salaire brut
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getGross_pay()%> $
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Periode
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getYearMonth()%>
                                            </span>
                                        </p>
                                        <p class="clearfix">
                                            <span class="float-left">
                                              Entreprise
                                            </span>
                                            <span class="float-right text-muted">
                                              <%=fichePaye.getCompany()%>
                                            </span>
                                        </p>


                                    </div>
                                    <a href="<%=contextPath%>/employee/fiche-paye/export/pdf?idEmp=<%=idEmp%>&mois=<%=mois%>&data=<%=json%>" class="btn btn-success"><i class="fas fa-file-export"></i> Exporter en pdf</a>
                                </div>
                            </div>
                            <% } else {%>
                            <div class="alert alert-light alert-has-icon">
                                <div class="alert-icon"><i class="far fa-lightbulb"></i></div>
                                <div class="alert-body">
                                    <div class="alert-title">Aucun fiche de paye</div>
                                    <a href="http://erpnext.localhost:8000/app/salary-slip" class="btn btn-link">Ajouter un nouveau fiche de paye</a>
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

<script src="<%=contextPath%>/assets/bundles/summernote/summernote-bs4.js"></script>
<script src="<%=contextPath%>/assets/bundles/select2/dist/js/select2.full.min.js"></script>
<!-- Page Specific JS File -->
<script src="<%=contextPath%>/assets/js/page/forms-advanced-forms.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
</html>
