<%-- 
    Document   : basedatos
    Created on : 07/07/2014, 06:06:37 PM
    Author     : Alex
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@ page import="com.iveloper.entidades.Cuenta" %>
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="utf-8">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <meta name="description" content="">
        <meta name="author" content="Alex">
        <link rel="shortcut icon" href="img/favicon.png">

        <title>Notaría App - Configuración de Base de Datos</title>

        <!-- Bootstrap core CSS -->
        <link href="css/bootstrap.min.css" rel="stylesheet">
        <link href="css/bootstrap-reset.css" rel="stylesheet">
        <!--external css-->
        <link href="assets/font-awesome/css/font-awesome.css" rel="stylesheet" />
        <!-- Custom styles for this template -->
        <link href="css/style.css" rel="stylesheet">
        <link href="css/style-responsive.css" rel="stylesheet" />

        <!-- HTML5 shim and Respond.js IE8 support of HTML5 tooltipss and media queries -->
        <!--[if lt IE 9]>
          <script src="js/html5shiv.js"></script>
          <script src="js/respond.min.js"></script>
        <![endif]-->
    </head>

    <body>
        <% Cuenta cuentaSesion = (Cuenta) session.getAttribute("cuentaSesion"); %>
        <% if (cuentaSesion != null) { %>  
        <section id="container" class="">
            <!--header start-->
            <header class="header white-bg">
                <div class="sidebar-toggle-box">
                    <div data-original-title="Toggle Navigation" data-placement="right" class="fa fa-bars tooltips"></div>
                </div>
                <!--logo start-->
                <a href="nuevo_tramite.jsp" class="logo" >Escritorio de <span>Notaría</span></a>
                <!--logo end-->

                <div class="top-nav ">
                    <ul class="nav pull-right top-menu">
                        <li>
                            <input type="text" class="form-control search" placeholder="Search">
                        </li>
                        <!-- user login dropdown start-->
                        <li class="dropdown">
                            <a data-toggle="dropdown" class="dropdown-toggle" href="#">
                                <span class="username">usuario</span>
                                <b class="caret"></b>
                            </a>
                            <ul class="dropdown-menu extended logout">
                                <div class="log-arrow-up"></div>
                                <li><a href="login.html"><i class="fa fa-key"></i> Cerrar sesión</a></li>
                            </ul>
                        </li>
                        <!-- user login dropdown end -->
                    </ul>
                </div>
            </header>
            <!--header end-->
            <!--sidebar start-->
            <aside>
                <div id="sidebar"  class="nav-collapse ">
                    <!-- sidebar menu start-->
                    <ul class="sidebar-menu" id="nav-accordion">                                     
                        <li class="sub-menu">
                            <a href="javascript:;">
                                <i class="fa fa-folder-open"></i>
                                <span>Trámites</span>
                            </a>
                            <ul class="sub">
                                <li><a  href="index.jsp">Nuevo Trámite</a></li>                                
                                <li><a  href="consultar_tramites.jsp">Consultar Trámites</a></li>                                
                            </ul>
                        </li>
                        <% if (cuentaSesion.getRoles().contains("admin")) {%>
                        <li class="sub-menu">
                            <a href="javascript:;" >
                                <i class=" fa fa-minus-square"></i>
                                <span>Gastos</span>
                            </a>
                            <ul class="sub">
                                <li><a  href="gastos.jsp">Registrar Gastos</a></li>
                                <li><a  href="consultar_gastos.jsp">Consultar Gastos</a></li>
                            </ul>
                        </li>
                        <%}%>  
                        <% if (cuentaSesion.getRoles().contains("admin")) {%>
                        <li class="sub-menu">
                            <a href="javascript:;" class="active">
                                <i class=" fa fa-cogs"></i>
                                <span>Configuración</span>
                            </a>
                            <ul class="sub">
                                <li class="active"><a  href="basedatos.jsp">Servidor</a></li>
                                <li><a  href="usuarios.jsp">Usuarios</a></li>
                                <li><a  href="#">Servicios</a></li>
                            </ul>
                        </li>
                        <%}%>                        
                        <% if (cuentaSesion.getRoles().contains("admin") || cuentaSesion.getRoles().contains("query")) {%>
                        <li class="sub-menu">
                            <a href="javascript:;" >
                                <i class=" fa fa-cogs"></i>
                                <span>Documentos</span>
                            </a>
                            <ul class="sub">
                                <li><a  href="consultar_documentos.jsp">Consultar Documentos</a></li>
                                    <% if (cuentaSesion.getRoles().contains("admin")) {%>
                                <li><a  href="documentos_anulados.jsp">Documentos Anulados</a></li>
                                    <%}%>                                                                                           
                            </ul>
                        </li>
                        <%}%>
                    </ul>
                    <!-- sidebar menu end-->
                </div>
            </aside>
            <!--sidebar end-->
            <!--main content start-->
            <section id="main-content">
                <section class="wrapper">
                    <!-- page start-->
                    <div class="row">
                        <div class="col-lg-6">
                            <section class="panel">
                                <header class="panel-heading">
                                    Configuración de Base de Datos
                                </header>
                                <div class="panel-body">
                                    <div class="form">
                                        <form class="cmxform form-horizontal tasi-form" id="bdSetupForm" method="post" action="DBCtrl">
                                            <input type="hidden" name="op" value="configurar" />
                                            <div class="form-group ">
                                                <label for="hostname" class="control-label col-lg-2">Host</label>
                                                <div class="col-lg-6">
                                                    <input class=" form-control" id="hostname" name="hostname" type="text" />
                                                </div>
                                            </div>
                                            <div class="form-group ">
                                                <label for="port" class="control-label col-lg-2">Puerto</label>
                                                <div class="col-lg-2">
                                                    <input class=" form-control" id="port" name="port" type="text" />
                                                </div>
                                            </div>
                                            <div class="form-group ">
                                                <label for="database" class="control-label col-lg-2">Base de Datos</label>
                                                <div class="col-lg-6">
                                                    <input class="form-control " id="database" name="database" type="text" />
                                                </div>
                                            </div>
                                            <div class="form-group ">
                                                <label for="username" class="control-label col-lg-2">Usuario</label>
                                                <div class="col-lg-6">
                                                    <input class="form-control " id="username" name="username" type="text" />
                                                </div>
                                            </div>
                                            <div class="form-group ">
                                                <label for="password" class="control-label col-lg-2">Password</label>
                                                <div class="col-lg-6">
                                                    <input class="form-control " id="password" name="password" type="password" />
                                                </div>
                                            </div>
                                            <div class="form-group">
                                                <label class="col-sm-2 control-label col-lg-2" for="dbvendor">Motor</label>
                                                <div class="col-lg-6">
                                                    <select class="form-control m-bot15" id="dbvendor" name="dbvendor">
                                                        <option value="0">MYSQL</option>
                                                        <option value="1">SQL SERVER</option>
                                                        <option value="2">POSTGRESQL</option>
                                                    </select>
                                                </div>
                                            </div>   

                                            <div class="form-group">
                                                <div class="col-lg-offset-4 col-lg-6">
                                                    <button class="btn btn-danger" type="submit">Enviar</button>
                                                </div>
                                            </div>
                                        </form>
                                    </div>
                                </div>
                            </section>
                        </div>
                    </div>
                    <!-- page end-->
                </section>
            </section>
            <!--main content end-->
            <!--footer start-->
            <footer class="site-footer">
                <div class="text-center">
                    2014 &copy; Comprobantes Electrónicos by RastreoTotal.
                    <a href="#" class="go-top">
                        <i class="fa fa-angle-up"></i>
                    </a>
                </div>
            </footer>
            <!--footer end-->
        </section>

        <!-- js placed at the end of the document so the pages load faster -->
        <script src="js/jquery.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script class="include" type="text/javascript" src="js/jquery.dcjqaccordion.2.7.js"></script>
        <script src="js/jquery.scrollTo.min.js"></script>
        <script src="js/jquery.nicescroll.js" type="text/javascript"></script>
        <script type="text/javascript" src="js/jquery.validate.min.js"></script>
        <script src="js/respond.min.js" ></script>

        <!--common script for all pages-->
        <script src="js/common-scripts.js"></script>

        <!--script for this page-->
        <script src="js/form-validation-script.js"></script>

        <%}%>
    </body>
</html>

