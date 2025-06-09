<%@ page import="java.util.List" %>
<%@ page import="mg.erp.entities.rh.SalarySummary" %>
<%@ page import="java.util.Map" %>
<%@ page import="com.google.gson.Gson" %>
<%--
  Created by IntelliJ IDEA.
  User: raven
  Date: 01/05/2025
  Time: 11:10
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    Gson gson = new Gson();
    String contextPath = request.getContextPath();
    String annee = (String) request.getAttribute("annee");
    List<SalarySummary> summaries = (List<SalarySummary>) request.getAttribute("summaries");
    double totalPayBrut = (double) request.getAttribute("totalPayBrut");
    double totalPayNet = (double) request.getAttribute("totalPayNet");
    double totalPayDeduction = (double) request.getAttribute("totalPayDeduction");
%>
<html>
<head>
    <title>Statistique</title>
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
                                        <form class="col-12" action="<%=contextPath%>/salaire/statistique" method="get" style="display: flex; gap: 1.5em; justify-content: center; align-items: center;">

                                            <div class="form-group" style="width: 80%;">
                                                <label>Mois</label>
                                                <input class="form-control" type="number" name="annee" min="0" minlength="1" maxlength="4" value="<%=annee%>"/>
                                            </div>

                                            <button type="submit" style="width: 15%;" class="btn btn-primary p-2 col-3">Filtrer</button>
                                        </form>

                                    </div>
                                </div>
                            </div>
                            <div class="row d-flex justify-content-center align-items-center">
                                <div class="col-xl-4 col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                    <div class="card">
                                        <div class="card-statistic-4">
                                            <div class="align-items-center justify-content-between">
                                                <div class="row">
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pr-0 pt-3">
                                                        <div class="card-content">
                                                            <h5 class="font-15">Paie brute</h5>
                                                            <h2 class="mb-3 font-18"><%=String.format("%.2f", totalPayBrut)%> $</h2>
                                                        </div>
                                                    </div>
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pl-0">
                                                        <div class="banner-img">
                                                            <img src="<%=contextPath%>/assets/img/banner/brutte.png" width="140" alt="">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-xl-4 col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                    <div class="card">
                                        <div class="card-statistic-4">
                                            <div class="align-items-center justify-content-between">
                                                <div class="row ">
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pr-0 pt-3">
                                                        <div class="card-content">
                                                            <h5 class="font-15"> Paie nette</h5>
                                                            <h2 class="mb-3 font-18"><%=String.format("%.2f", totalPayNet)%> $</h2>
                                                        </div>
                                                    </div>
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pl-0">
                                                        <div class="banner-img">
                                                            <img src="<%=contextPath%>/assets/img/banner/nette.png" width="140" alt="">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="col-xl-4 col-lg-6 col-md-6 col-sm-6 col-xs-12">
                                    <div class="card">
                                        <div class="card-statistic-4">
                                            <div class="align-items-center justify-content-between">
                                                <div class="row ">
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pr-0 pt-3">
                                                        <div class="card-content">
                                                            <h5 class="font-15">Paie déduit</h5>
                                                            <h2 class="mb-3 font-18"><%=String.format("%.2f", totalPayDeduction)%> $</h2>
                                                        </div>
                                                    </div>
                                                    <div class="col-lg-6 col-md-6 col-sm-6 col-xs-6 pl-0">
                                                        <div class="banner-img">
                                                            <img src="<%=contextPath%>/assets/img/banner/deduit.png" width="140" alt="">
                                                        </div>
                                                    </div>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <div class="row clearfix col-12">
                                <div class="col-12">
                                    <div class="card">
                                        <div class="card-header">
                                            <h4>Graphe <%=annee%></h4>
                                        </div>
                                        <div class="card-body">
                                            <div class="recent-report__chart">
                                                <div id="chart6"></div>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                            <% if (summaries != null && !summaries.isEmpty()) { %>
                                <div class="card">
                                    <div class="card-header">
                                        <h4>Statistique des salaires</h4>
                                    </div>
                                    <div class="card-body">
                                        <nav aria-label="breadcrumb">
                                            <ol class="breadcrumb">
                                                <li class="breadcrumb-item active" aria-current="page">
                                                    <i class="fas fa-user-friends"></i> Statistique des salaires
                                                </li>
                                            </ol>
                                        </nav>
                                        <div class="table-responsive">
                                            <table class="table table-bordered table-striped table-hover" id="tableExport" style="width:100%;">
                                                <thead>
                                                <tr>
                                                    <th>Mois</th>
                                                    <th>Paie brute</th>
                                                    <th>Paie nette</th>
                                                    <th>Paie déduit</th>
                                                    <th>Elements</th>
                                                    <th>Détails</th>
                                                </tr>
                                                </thead>
                                                <tbody>
                                                <% int id = 0;
                                                    for (SalarySummary summary : summaries) {
                                                        String modalId = "detailsModal" + id;
                                                %>
                                                <tr>
                                                    <td><%= summary.getMonth() %></td>
                                                    <td><%= String.format("%.2f", summary.getTotalPayBrut()) %> $</td>
                                                    <td><%= String.format("%.2f", summary.getTotalPayNet()) %> $</td>
                                                    <td><%= String.format("%.2f", summary.getTotalPayDeduction()) %> $</td>
                                                    <td>
                                                        <a class="btn btn-primary" data-toggle="collapse" href="#<%=modalId%>" role="button"
                                                           aria-expanded="false" aria-controls="<%=modalId%>">Elements</a>
                                                        <div class="row">
                                                            <div class="col">
                                                                <div class="collapse multi-collapse" id="<%=modalId%>">
                                                                    <h6>Structure de salaire :</h6>
                                                                    <ul>
                                                                        <% for (Map.Entry<String, Double> entry : summary.getComponentTotals().entrySet()) { %>
                                                                        <li><strong><%= entry.getKey() %></strong> : <%= String.format("%.2f", entry.getValue()) %> $</li>
                                                                        <% } %>
                                                                    </ul>

                                                                    <hr>

                                                                    <h6>Composants : <span class="text-success">Earnings</span></h6>
                                                                    <ul>
                                                                        <% if (summary.getComponentEarnings() != null) {
                                                                            for (Map.Entry<String, Double> entry : summary.getComponentEarnings().entrySet()) { %>
                                                                        <li><%= entry.getKey() %> : <%= String.format("%.2f", entry.getValue()) %> $</li>
                                                                        <%   }
                                                                        } %>
                                                                    </ul>

                                                                    <h6>Composants : <span class="text-danger">Deductions</span></h6>
                                                                    <ul>
                                                                        <% if (summary.getComponentDeductions() != null) {
                                                                            for (Map.Entry<String, Double> entry : summary.getComponentDeductions().entrySet()) { %>
                                                                        <li><%= entry.getKey() %> : <%= String.format("%.2f", entry.getValue()) %> $</li>
                                                                        <%   }
                                                                        } %>
                                                                    </ul>
                                                                </div>
                                                            </div>
                                                        </div>
                                                    </td>
                                                    <td><a class="btn btn-primary" href="<%=contextPath%>/employee/fiche?mois=<%=summary.getMonth()%>">Détails</a></td>
                                                </tr>
                                                <% id++;} %>
                                                </tbody>
                                            </table>
                                        </div>
                                    </div>
                                </div>
                            <% } else {%>
                                <div class="alert alert-light alert-has-icon">
                                    <div class="alert-icon"><i class="far fa-lightbulb"></i></div>
                                    <div class="alert-body">
                                        <div class="alert-title">Aucun donnee</div>
                                        <a href="http://localhost:8080/data/page-import" class="btn btn-link">Importer des donnes</a>
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
<script src="<%=contextPath%>/assets/bundles/apexcharts/apexcharts.min.js"></script>

<script src="<%=contextPath%>/assets/bundles/select2/dist/js/select2.full.min.js"></script>
<!-- Page Specific JS File -->
<script src="<%=contextPath%>/assets/js/page/forms-advanced-forms.js"></script>
<!-- Template JS File -->
<script src="<%=contextPath%>/assets/js/scripts.js"></script>
<!-- Custom JS File -->
<script src="<%=contextPath%>/assets/js/custom.js"></script>
<script>
    'use strict';
    $(function () {
        chart6();
    });

    function chart6() {
        var seriesData = [
            {
                name: 'Paie Brute',
                data: <%= request.getAttribute("brut") %>,
            },
            {
                name: 'Paie Nette',
                data: <%= request.getAttribute("net") %>,
            },
            {
                name: 'Déductions',
                data: <%= request.getAttribute("deduction") %>,
            }
        ];

        <% Map<String, double[]> earningsMap = (Map<String, double[]>) request.getAttribute("earningsMap");
        for (Map.Entry<String, double[]> entry : earningsMap.entrySet()) { %>
            seriesData.push({
                name: '<%= "Gains - " + entry.getKey() %>',
                data: <%= gson.toJson(entry.getValue()) %>
            });
        <% } %>

        <% Map<String, double[]> deductionMap = (Map<String, double[]>) request.getAttribute("deductionMap");
        for (Map.Entry<String, double[]> entry : deductionMap.entrySet()) { %>
            seriesData.push({
                name: '<%= "Déductions - " + entry.getKey() %>',
                data: <%= gson.toJson(entry.getValue()) %>
            });
        <% } %>

        var options = {
            chart: {
                height: 450,
                type: 'area',
                stacked: false,
                toolbar: {
                    show: true
                }
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'smooth',
                width: 2
            },
            series: seriesData,
            xaxis: {
                categories: <%= request.getAttribute("dates") %>,
                labels: {
                    style: {
                        colors: '#9aa0ac',
                    }
                }
            },
            yaxis: {
                labels: {
                    style: {
                        colors: '#9aa0ac',
                    }
                }
            },
            tooltip: {
                shared: true,
                intersect: false,
            },
            legend: {
                position: 'top',
                horizontalAlign: 'left',
                labels: {
                    colors: '#9aa0ac',
                }
            }
        };

        var chart = new ApexCharts(document.querySelector("#chart6"), options);
        chart.render();
    }
</script>

</html>
