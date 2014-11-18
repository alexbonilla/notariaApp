<%-- 
    Document   : index
    Created on : 22/05/2014, 09:14:33 AM
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

        <title>Notaría WebApp - Escritorio</title>



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
        <script>

            window.dhx_globalImgPath = "img/";

        </script>
        <link rel="STYLESHEET" type="text/css" href="css/dhtmlxcombo.css">
        <script  src="js/dhtmlxcommon.js"></script>
        <script  src="js/dhtmlxcombo.js"></script> 
        <script src="js/ext/dhtmlxcombo_whp.js" type="text/javascript"></script>

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
<!--                                <li><a href="#"><i class=" fa fa-suitcase"></i>Perfil</a></li>
                                <li><a href="#"><i class="fa fa-cog"></i> Configuracion</a></li>
                                <li><a href="#"><i class="fa fa-bell-o"></i> Notificaciones</a></li>-->
                                <li><a href="CuentaCtrl?op=cerrar_sesion"><i class="fa fa-key"></i> Cerrar sesión</a></li>
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
                            <a href="javascript:;" class="active" >
                                <i class="fa fa-tasks"></i>
                                <span>Trámites</span>
                            </a>
                            <ul class="sub">
                                <li class="active"><a  href="nuevo_tramite.jsp">Nuevo Trámite</a></li>                                                                
                                <li><a  href="consultar_tramites.jsp">Consultar Trámites</a></li> 
                            </ul>
                        </li>
                        <% if (cuentaSesion.getRoles().contains("admin")) {%>
<!--                        <li class="sub-menu">
                            <a href="javascript:;" >
                                <i class="fa fa-bar-chart-o"></i>
                                <span>Ingresos/Egresos</span>
                            </a>
                            <ul class="sub">
                                <li><a  href="product_list.html">Registrar Egreso</a></li>
                                <li><a  href="product_details.html">Consultas</a></li>
                            </ul>
                        </li>-->
                        <%}%>
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
                            <a href="javascript:;" >
                                <i class=" fa fa-cogs"></i>
                                <span>Configuración</span>
                            </a>
                            <ul class="sub">
                                <li><a  href="basedatos.jsp">Servidor</a></li>
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
                    <!--multiple select start-->
                    <div class="row">
                        <div class="col-md-3">
                            <section class="panel col-md-12">
                                <header class="panel-heading">
                                    Servicios
                                </header>
                                <div class="panel-body">
                                    <div class="form-group">
                                        <label for="comboTipoServicio">Tipo</label>
                                        <select id="comboTipoServicio" name="comboTipoServicios" onchange="obtenerServicios()" class="form-control">
                                            <option value="diligencias" selected>Diligencias</option>
                                            <option value="escrituras">Escrituras</option>
                                        </select>
                                    </div>
                                    <div class="form-group">
                                        <label for="comboServicios">Servicio</label>
                                        <div id="comboServicios" name="comboServicios"></div>
                                    </div>
                                    <div class="form-group">
                                        <label class="control-label">Precio</label>
                                        <div>
                                            <input id="precio" name="precio" class="form-control two-digits" type="text" placeholder="0.00" style="text-align: right;" disabled="">
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="control-label">Cantidad</label>
                                        <div>
                                            <div id="spinner4">
                                                <div class="input-group" style="width:150px;">
                                                    <div class="spinner-buttons input-group-btn">
                                                        <button type="button" class="btn spinner-up btn-warning" onclick="actualizarTotal()">
                                                            <i class="fa fa-plus"></i>
                                                        </button>
                                                    </div>
                                                    <input id="cantidad" name="cantidad" type="text" class="spinner-input form-control" maxlength="3" readonly>
                                                    <div class="spinner-buttons input-group-btn">
                                                        <button type="button" class="btn spinner-down btn-danger" onclick="actualizarTotal()">
                                                            <i class="fa fa-minus"></i>
                                                        </button>
                                                    </div>
                                                </div>
                                            </div>                                                
                                        </div>
                                    </div>
                                    <div class="form-group">
                                        <label class="control-label">Total</label>
                                        <div>
                                            <input id="total" name="total" class="form-control" type="text" placeholder="0.00" style="text-align: right;" disabled="">
                                        </div>
                                    </div>
                                    <button type="submit" class="btn btn-info" onclick="agregarTramite()">Agregar a trámite >></button>

                                </div>
                                <div aria-hidden="true" aria-labelledby="myModalLabel" role="dialog" tabindex="-1" id="myModal" class="modal fade">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button aria-hidden="true" data-dismiss="modal" class="close" type="button">×</button>
                                                <h4 class="modal-title">Nuevo Servicio</h4>
                                            </div>
                                            <div class="modal-body">


                                                <div class="form-group">
                                                    <label for="nuevoServicioDescripcion">Descripcion</label>
                                                    <input type="text" class="form-control" id="nuevoServicioDescripcion" name="nuevoServicioDescripcion" placeholder="descripción">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoServicioTipo">Tipo</label>
                                                    <select class="form-control" id="nuevoServicioTipo" name="nuevoServicioTipo">
                                                        <option value="diligencias">Diligencias</option>
                                                        <option value="escrituras">Escrituras</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoServicioPrecio">Precio</label>
                                                    <input type="text" class="form-control" id="nuevoServicioPrecio" name="nuevoServicioPrecio" placeholder="0.00">
                                                </div>
                                                <div class="form-group">
                                                    <button class="btn btn-default" onclick="guardarNuevoServicio()">Guardar</button>
                                                </div>

                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </section>                            
                        </div>
                        <div class="col-md-9">
                            <section class="panel col-md-12" >                                
                                <header class="panel-heading">
                                    Trámite
                                </header>
                                <div class="form-group col-md-4">
                                    <label for="comboClientes">Cliente</label>
                                    <div id="comboClientes" name="comboClientes"></div>
                                </div>
                                <div class="form-group col-md-6">
                                    <label for="nombreCliente">Nombre</label>
                                    <div>
                                        <input id="nombreCliente" name="nombreCliente" class="form-control" type="text" placeholder="Nombre de cliente" disabled="">
                                    </div>                                    
                                </div>
                                <div class="form-group col-md-2">
                                    <label for="usarCliente">A favor de</label>
                                    <div>
                                        <input id="usarCliente" name="usarCliente" class="form-control" type="checkbox" onchange="actualizarFavorecido();">
                                    </div>                                    
                                </div>                                                                
                                <table id="servicios_tramite" name="servicios_tramite" class="table table-striped table-advance table-hover col-md-12" width="100%">
                                    <thead>
                                        <tr>
                                            <th></th>
                                            <th><i class="fa"></i> #Serie</th>
                                            <th class="hidden-phone"><i class="fa"></i> Descripcion</th>
                                            <th class="hidden-phone"><i class="fa"></i> Info de Documento</th>
                                            <th><i class="fa"></i> Valor</th>
                                            <th></th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <!--Aquí se agregan los servicios al trámite-->
                                    </tbody>
                                </table>
                                <div class="form-group col-md-12">
                                    <label for="valorSubtotalTramite" class="control-label col-md-4">SubTotal</label>
                                    <input type="text" class="col-md-8" id="valorSubtotalTramite" name="valorSubtotalTramite" disabled="" style="text-align: right;" value="0.00">
                                </div>
                                <div class="form-group col-md-12">
                                    <label for="valorIvaTramite" class="control-label col-md-4">IVA</label>
                                    <input type="text" class="col-md-8" id="valorIvaTramite" name="valorIvaTramite" disabled="" style="text-align: right;" value="0.00">
                                </div>
                                <div class="form-group col-md-12">
                                    <label for="valorTotalTramite" class="control-label col-md-4">Total</label>
                                    <input type="text" class="col-md-8" id="valorTotalTramite" name="valorTotalTramite" disabled="" style="text-align: right;" value="0.00">
                                </div>
                                <div class="form-group col-md-12">
                                    <!--<label for="credito" class="control-label col-md-4">Credito</label>-->
                                    <input type="hidden" class="col-md-8" id="credito" name="credito" placeholder="0.00" style="text-align: right;" disabled="">
                                </div> 
                                <div class="form-group col-md-12">
                                    <!--<label for="abono" class="control-label col-md-4">Abonar</label>-->
                                    <input type="hidden" class="col-md-8" id="abono" name="abono" placeholder="0.00" style="text-align: right;" onkeyUp="actualizarAbonoTotal()">
                                </div> 
                                <div class="form-group col-md-12">
                                    <!--<label for="abonoTotal" class="control-label col-md-4">Abono Total</label>-->
                                    <input type="hidden" class="col-md-8" id="abonoTotal" name="abonoTotal" placeholder="0.00" style="text-align: right;" disabled="">
                                </div> 
                                <div class="form-group col-md-12">
                                    <button id="sendServer" name="sendServer" class="btn btn-default" onclick="crearTramite()">Guardar</button>
                                </div>
                                <div aria-hidden="true" aria-labelledby="myModalLabel" role="dialog" tabindex="-1" id="nuevoCliente" class="modal fade">
                                    <div class="modal-dialog">
                                        <div class="modal-content">
                                            <div class="modal-header">
                                                <button aria-hidden="true" data-dismiss="modal" class="close" type="button">×</button>
                                                <h4 class="modal-title">Nuevo Cliente</h4>
                                            </div>
                                            <div class="modal-body">


                                                <div class="form-group">
                                                    <label for="nuevoClienteIdentificacion">Identificación</label>
                                                    <input type="text" class="form-control" id="nuevoClienteIdentificacion" name="nuevoClienteIdentificacion" placeholder="Identificación">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteTipoIdentificacion">Tipo</label>
                                                    <select class="form-control" id="nuevoClienteTipoIdentificacion" name="nuevoClienteTipoIdentificacion">
                                                        <option value="02">Cédula</option>
                                                        <option value="01">RUC</option>
                                                        <option value="03">Pasaporte</option>
                                                    </select>
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteRazonSocial">Nombre</label>
                                                    <input type="text" class="form-control" id="nuevoClienteRazonSocial" name="nuevoClienteRazonSocial" placeholder="Nombre">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteDireccion">Direccion</label>
                                                    <input type="text" class="form-control" id="nuevoClienteDireccion" name="nuevoClienteDireccion" placeholder="Direccion">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteTelefonoFijo">Tel. Fijo</label>
                                                    <input type="text" class="form-control" id="nuevoClienteTelefonoFijo" name="nuevoClienteTelefonoFijo" placeholder="Teléfono fijo">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteTelefonoMovil">Tel. Movil</label>
                                                    <input type="text" class="form-control" id="nuevoClienteTelefonoMovil" name="nuevoClienteTelefonoMovil" placeholder="Teléfono móvil">
                                                </div>
                                                <div class="form-group">
                                                    <label for="nuevoClienteEmail">Email</label>
                                                    <input type="text" class="form-control" id="nuevoClienteEmail" name="nuevoClienteEmail" placeholder="Email">
                                                </div>
                                                <div class="form-group">
                                                    <button class="btn btn-default" onclick="guardarNuevoCliente()">Guardar</button>
                                                </div>
                                            </div>
                                        </div>
                                    </div>
                                </div>                                
                            </section>
                        </div>
                    </div>
                    <!--multiple select end-->

                    <!-- page end-->
                </section>
            </section>
            <!--main content end-->
            <!--footer start-->
            <footer class="site-footer">
                <div class="text-center">
                    2014 &copy; notariaApp by Iveloper.
                    <a href="#" class="go-top">
                        <i class="fa fa-angle-up"></i>
                    </a>
                </div>
            </footer>
            <!--footer end-->
        </section>

        <!-- js placed at the end of the document so the pages load faster -->
        <!--<script src="js/jquery.js"></script>-->
        <script type="text/javascript" language="javascript" src="assets/advanced-datatable/media/js/jquery.js"></script>
        <script src="js/bootstrap.min.js"></script>
        <script class="include" type="text/javascript" src="js/jquery.dcjqaccordion.2.7.js"></script>
        <script src="js/jquery.scrollTo.min.js"></script>
        <script src="js/jquery.nicescroll.js" type="text/javascript"></script>
        <script src="js/respond.min.js" ></script>


        <script type="text/javascript" language="javascript" src="assets/advanced-datatable/media/js/jquery.dataTables.js"></script>
        <script type="text/javascript" src="assets/data-tables/DT_bootstrap.js"></script>        

        <!--this page plugins-->

        <script type="text/javascript" src="assets/fuelux/js/spinner.min.js"></script>

        <!--common script for all pages-->
        <script src="js/common-scripts.js"></script>
        <!--this page  script only-->
        <script src="js/ajax.js"></script>
        <script src="js/nuevotramite.js"></script>
        <script type="text/javascript">
                                                        /* Formating function for row details */
                                                        function fnFormatDetails(oTable, nTr)
                                                        {
                                                            var aData = oTable.fnGetData(nTr);
                                                            var sOut = '<table cellpadding="5" cellspacing="0" border="0" style="padding-left:50px;">';
                                                            sOut += '<tr><td>A favor de:</td><td><input id="' + aData[1] + '-nuevo-benef" type="text" placeholder="A favor de" value="' + $('#benefnombre-' + aData[1]).val() + '"></input></tr>';
                                                            sOut += '<tr><td>Cédula/RUC/Pasaporte:</td><td><input id="' + aData[1] + '-nuevo-benef-id" type="text" placeholder="Identificacion" value="' + $('#benefid-' + aData[1]).val() + '"></input></tr>';
                                                            sOut += '<tr><td>Valor adicional:</td><td><input id="' + aData[1] + '-valor-adicional" type="text" placeholder="0.00" value="' + $('#valoradicional-' + aData[1]).val() + '"></input></tr>';
                                                            sOut += '<tr><td>Descripción de adicional:</td><td><input id="' + aData[1] + '-descripcion-adicional" type="text" placeholder="descripción" value="' + $('#descripcionadicional-' + aData[1]).val() + '"></input></tr>';
                                                            sOut += '<tr><td><div id="mensajeGuardar-' + aData[1] + '"></div></td><td><button onclick="asignarNuevoBeneficiario(\'' + aData[1] + '\');">Guardar</button></td></tr>';
                                                            sOut += '</table>';

                                                            return sOut;
                                                        }

                                                        function asignarNuevoBeneficiario(numdocumento) {
                                                            if ($('#' + numdocumento + '-nuevo-benef').val() === null || $('#' + numdocumento + '-nuevo-benef').val() === '') {
                                                                $('#benefinfo-' + numdocumento).val('pendiente');
                                                                $('#benefnombre-' + numdocumento).val($('#' + numdocumento + '-nuevo-benef').val());
                                                                $('#benefid-' + numdocumento).val($('#' + numdocumento + '-nuevo-benef-id').val());
                                                                $('#valoradicional-' + numdocumento).val($('#' + numdocumento + '-valor-adicional').val());
                                                                $('#descripcionadicional-' + numdocumento).val($('#' + numdocumento + '-descripcion-adicional').val());                                                                
                                                            } else {
                                                                $('#benefinfo-' + numdocumento).val('ok');
                                                                $('#benefnombre-' + numdocumento).val($('#' + numdocumento + '-nuevo-benef').val());
                                                                $('#benefid-' + numdocumento).val($('#' + numdocumento + '-nuevo-benef-id').val());
                                                                $('#valoradicional-' + numdocumento).val($('#' + numdocumento + '-valor-adicional').val());
                                                                $('#descripcionadicional-' + numdocumento).val($('#' + numdocumento + '-descripcion-adicional').val());
                                                            }
                                                            $('#mensajeGuardar-' + numdocumento).text("Información guardada");
                                                        }

                                                        function agregarDetalles(oTable, idrow) {
                                                            /*
                                                             * Insert a 'details' column to the table
                                                             */
//                                                                var nCloneTh = document.createElement('th');
                                                            var nCloneTd = document.createElement('td');
                                                            nCloneTd.innerHTML = '<img src="assets/advanced-datatable/examples/examples_support/details_open.png">';
                                                            nCloneTd.className = "center";

//                                                                $('#servicios_tramite thead tr').each(function() {
//                                                                    this.insertBefore(nCloneTh, this.childNodes[0]);
//                                                                });

                                                            $('#' + idrow).prepend('<td><img src="assets/advanced-datatable/examples/examples_support/details_open.png"></td>');
//                                                            $('#servicios_tramite tbody tr').each(function() {
//                                                                this.insertBefore(nCloneTd.cloneNode(true), this.childNodes[0]);
//                                                            });

                                                            /*
                                                             * Initialse DataTables, with no sorting on the 'details' column
                                                             */
                                                            oTable = $('#servicios_tramite').dataTable({
                                                                "bDestroy": true,
                                                                "bPaginate": false,
                                                                "bFilter": false,
                                                                "bInfo": false,
                                                                "aoColumnDefs": [
                                                                    {"bSortable": false, "aTargets": [0]}
                                                                ],
                                                                "aaSorting": [[1, 'asc']],
                                                                "aoColumns": [
                                                                    /*index*/ null,
                                                                    /* Subject Name */ null,
                                                                    /* Address */ null,
                                                                    /* LinkedWithCompany */ null,
                                                                    /* Work Tel */ null,
//                                                                    /* LinkedWithCompany */ {"bVisible": false}
                                                                    /* LinkedWithCompany */ null
                                                                ]
                                                            });
                                                            /* Add event listener for opening and closing details
                                                             * Note that the indicator for showing which row is open is not controlled by DataTables,
                                                             * rather it is done here
                                                             */

                                                            $('#' + idrow + ' td img').click(function() {
//                                                                alert('Primero: ' + $('#' + idrow).html());
                                                                var nTr = $(this).parents('tr')[0];
//                                                                var nTr = $(this).parent().parent()[0];
//                                                                var nTr = oTable.$('#' + idrow);
//                                                                alert('Segundo: ' + oTable.$('#' + idrow));
                                                                if (oTable.fnIsOpen(nTr))
                                                                {
                                                                    /* This row is already open - close it */
                                                                    this.src = "assets/advanced-datatable/examples/examples_support/details_open.png";
                                                                    oTable.fnClose(nTr);
                                                                }
                                                                else
                                                                {
                                                                    /* Open this row */
                                                                    this.src = "assets/advanced-datatable/examples/examples_support/details_close.png";
                                                                    oTable.fnOpen(nTr, fnFormatDetails(oTable, nTr), 'details');
                                                                }
                                                            });
                                                        }
        </script>
        <%}%>
    </body>
</html>
