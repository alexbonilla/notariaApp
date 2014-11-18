/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.notaria.web.controladores;

import com.iveloper.comprobantes.modelo.Detalle;
import com.iveloper.comprobantes.modelo.Impuesto;
import com.iveloper.comprobantes.modelo.InfoTributaria;
import com.iveloper.comprobantes.modelo.TotalImpuesto;
import com.iveloper.comprobantes.modelo.factura.Factura;
import com.iveloper.comprobantes.modelo.factura.InfoFactura;
import com.iveloper.comprobantes.utils.ClaveAcceso;
import com.iveloper.db.Conexion;
import com.iveloper.entidades.Abono;
import com.iveloper.entidades.Cliente;
import com.iveloper.entidades.Cuenta;
import com.iveloper.entidades.Documento;
import com.iveloper.entidades.RetencionRecibida;
import com.iveloper.entidades.Servicio;
import com.iveloper.entidades.Tramite;
import com.iveloper.entidades.TramiteItems;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperRunManager;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

/**
 *
 * @author Alex
 */
@WebServlet(name = "TramiteCtrl", urlPatterns = {"/TramiteCtrl"})
public class TramiteCtrl extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        String op = request.getParameter("op");
        if (op.equalsIgnoreCase("guardarNuevoTramite")) {
            System.out.println("Entró a guardarNuevoTramite");
            response.setContentType("text/plain;charset=UTF-8"); // Esto es para poder escribir la respuesta en el iframe
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            guardarNuevoTramite(request, response);
        } else if (op.equalsIgnoreCase("buscarTramites")) {
            System.out.println("Entró a buscarTramites");
            buscarTramites(request, response);
        } else if (op.equalsIgnoreCase("anularTramite")) {
            System.out.println("Entró a anularTramite");
            anularTramite(request, response);
        } else if (op.equalsIgnoreCase("buscarTramitesHoy")) {
            System.out.println("Entró a buscarTramitesHoy");
            buscarTramitesHoy(request, response);
        } else if (op.equalsIgnoreCase("abonarTramite")) {
            System.out.println("Entró a abonarTramite");
            abonarTramite(request, response);
        } else if (op.equalsIgnoreCase("consultarDocumentos")) {
            System.out.println("Entró a consultarDocumentos");
            consultarDocumentos(request, response);
        } else if (op.equalsIgnoreCase("consultarDocumentosAnulados")) {
            System.out.println("Entró a consultarDocumentosAnulados");
            consultarDocumentosAnulados(request, response);
        } else if (op.equalsIgnoreCase("consultarAbonos")) {
            System.out.println("Entró a consultarAbonos");
            consultarAbonos(request, response);
        } else if (op.equalsIgnoreCase("facturarTramite")) {
            response.setContentType("application/pdf");
            System.out.println("Entró a facturarTramite");
            facturarTramite(request, response);
        } else if (op.equalsIgnoreCase("facturarTramiteHTML")) {
            response.setContentType("text/html");
            System.out.println("Entró a facturarTramiteHTML");
            facturarTramiteHTML(request, response);
        } else if (op.equalsIgnoreCase("cambiarNumFacturaTramite")) {
            System.out.println("Entró a cambiarNumFacturaTramite");
            cambiarNumFacturaTramite(request, response);
        } else if (op.equalsIgnoreCase("guardarRetencionRecibida")) {
            System.out.println("Entró a guardarRetencionRecibida");
            guardarRetencionRecibida(request, response);
        } else if (op.equalsIgnoreCase("borrarDocumento")) {
            System.out.println("Entró a borrarDocumento");
            anularDocumento(request, response);
            actualizarValoresTramite(request, response);
            consultarDocumentos(request, response);
        } else if (op.equalsIgnoreCase("reutilizarDocumento")) {
            System.out.println("Entró a reutilizarDocumento");
            reutilizarDocumento(request, response);
        }else if (op.equalsIgnoreCase("buscarDocumentos")) {
            System.out.println("Entró a buscarDocumentos");
            buscarDocumentos(request, response);
        }
    }

    public void guardarNuevoTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";
        String identificacion = request.getParameter("clienteIdentificacion") == null ? null : request.getParameter("clienteIdentificacion");

        if (identificacion != null) {
            try {
                c.conectar();
                Cliente cliente = c.consultarCliente(identificacion);
                Tramite tramite = new Tramite();
                tramite.setCliente(cliente);

                ArrayList<Documento> documentos = obtenerDocumentos(request, response);
                tramite.setDocumentos(documentos);
                tramite.setCantdoc(documentos.size());
                ArrayList<BigDecimal> totales = calcularTotales(documentos);

                tramite.setSubtotal(totales.get(0));
                tramite.setIva(totales.get(1));
                tramite.setTotal(totales.get(2));
                tramite.setTotaladicional(totales.get(3));

                tramite.setFechacreacion(new Date());
                tramite.setUltimamodificacion(new Date());

                String usuario = devolverUsuario(request, response);
                tramite.setUsuario(usuario);

                tramite = c.guardarTramite(tramite);
                System.out.println("Tramite " + tramite.getIdtramite() + " ha sido guardado");
                System.out.println("Valor de parametro abono es " + request.getParameter("abono"));
                BigDecimal valorabono = request.getParameter("abono") == null || request.getParameter("abono").equals("") ? BigDecimal.ZERO : new BigDecimal(request.getParameter("abono"));
                System.out.println("Valor de abono es " + valorabono);
                if (!valorabono.equals(BigDecimal.ZERO)) {
                    valorabono = valorabono.setScale(2, RoundingMode.HALF_EVEN);

                    Abono abono = new Abono();
                    abono.setIdtramite(tramite.getIdtramite());
                    abono.setValor(valorabono);
                    abono.setFechaabono(new Date());
                    c.guardarAbono(abono);
                }
                try (PrintWriter pw = response.getWriter()) {
                    pw.println(tramite.getIdtramite());
                }

            } catch (Exception e) {
                System.out.println("" + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void anularDocumento(HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        Cuenta cuenta = (session == null) ? null : (Cuenta) session.getAttribute("cuentaSesion");
        if (cuenta.getRoles().contains("admin")) {
            String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
            Conexion c = new Conexion(path);
            String iddocumento = request.getParameter("iddocumento") == null ? null : request.getParameter("iddocumento");

            if (iddocumento != null) {
                try {
                    PrintWriter pw = response.getWriter();

                    c.conectar();
                    int resultado = c.marcarDocumentoComoAnulado(iddocumento);

                    System.out.println("Resultado de anular documento " + iddocumento + ":" + resultado);

                } catch (Exception e) {
                    System.out.println("Error: Al ejecutar TramiteCtrl.anularTramite " + e);
                }
            }

            c.desconectar();
        }

    }

    public void reutilizarDocumento(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String iddocumento = request.getParameter("iddocumento") == null ? null : request.getParameter("iddocumento");

        if (iddocumento != null) {
            try {
                PrintWriter pw = response.getWriter();

                c.conectar();
                int resultado = c.marcarDocumentoComoReutilizado(iddocumento);

                System.out.println("Resultado de reutilizar documento " + iddocumento + ":" + resultado);

            } catch (Exception e) {
                System.out.println("Error: Al ejecutar TramiteCtrl.reutilizarDocumento " + e);
            }
        }

        c.desconectar();

    }

    public void actualizarValoresTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String idtramite_str = request.getParameter("idtramite") == null ? null : request.getParameter("idtramite");
        if (idtramite_str != null) {
            try {
                c.conectar();
                int idtramite = Integer.parseInt(idtramite_str);
                Tramite tramite = c.consultarTramite(idtramite);

                ArrayList<Documento> documentos = tramite.getDocumentos();

                tramite.setDocumentos(documentos);
                tramite.setCantdoc(documentos.size());
                ArrayList<BigDecimal> totales = calcularTotales(documentos);

                tramite.setSubtotal(totales.get(0));
                tramite.setIva(totales.get(1));
                tramite.setTotal(totales.get(2));
                tramite.setTotaladicional(totales.get(3));

                tramite.setUltimamodificacion(new Date());

                tramite = c.actualizarTramite(tramite);
                System.out.println("Tramite " + tramite.getIdtramite() + " ha sido actualizado");

            } catch (Exception e) {
                System.out.println("" + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void anularTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";
        String idtramite = request.getParameter("idtramite") == null ? null : request.getParameter("idtramite");
        int id = (idtramite == null ? 0 : Integer.parseInt(idtramite));

        if (id != 0) {
            try {
                PrintWriter pw = response.getWriter();

                c.conectar();
                int resultado = c.marcarTramiteComoAnulado(id);

                xml = "<?xml version='1.0' encoding='UTF-8' ?>";
                xml += "<resultado>";
                xml += resultado;

                xml += "</resultado>";
                pw.println(xml);

            } catch (Exception e) {
                System.out.println("Error: Al ejecutar TramiteCtrl.anularTramite " + e);
            }
        }

        c.desconectar();

    }

    private ArrayList<Documento> obtenerDocumentos(HttpServletRequest request, HttpServletResponse response) {

        ArrayList<Documento> documentos = new ArrayList();

        Map<String, String[]> parametrosMap = request.getParameterMap();
        TreeMap parametros = new TreeMap();
        parametros.putAll(parametrosMap);

        SortedMap iddocumentos = getByPreffix(parametros, "iddocumento");
        SortedMap idservicios = getByPreffix(parametros, "idservicio");
        SortedMap nombresbenef = getByPreffix(parametros, "benefnombre");
        SortedMap idsbenef = getByPreffix(parametros, "benefid");
        SortedMap valoresadicionales = getByPreffix(parametros, "valoradicional");
        SortedMap descripcionesadicionales = getByPreffix(parametros, "descripcionadicional");

        Collection<String[]> iddocumentos_values = iddocumentos.values();
        Collection<String[]> idservicios_values = idservicios.values();
        Collection<String[]> nombresbenef_values = nombresbenef.values();
        Collection<String[]> idsbenef_values = idsbenef.values();
        Collection<String[]> valoresadicionales_values = valoresadicionales.values();
        Collection<String[]> descripcionesadicionales_values = descripcionesadicionales.values();

        Iterator<String[]> iddocumentos_string = iddocumentos_values.iterator();
        Iterator<String[]> idservicios_string = idservicios_values.iterator();
        Iterator<String[]> nombresbenef_string = nombresbenef_values.iterator();
        Iterator<String[]> idsbenef_string = idsbenef_values.iterator();
        Iterator<String[]> valoresadicionales_string = valoresadicionales_values.iterator();
        Iterator<String[]> descripcionesadicionales_string = descripcionesadicionales_values.iterator();

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        try {
            c.conectar();

            while (iddocumentos_string.hasNext() && idservicios_string.hasNext() && nombresbenef_string.hasNext() && idsbenef_string.hasNext() && valoresadicionales_string.hasNext() && descripcionesadicionales_string.hasNext()) {

                String iddocumento = (iddocumentos_string.next())[0];
                String idservicio = (idservicios_string.next())[0];
                String nombrebenef = (nombresbenef_string.next())[0];
                String idbenef = (idsbenef_string.next())[0];
                String valoradicional = (valoresadicionales_string.next())[0];
                String descripcionadicional = (descripcionesadicionales_string.next())[0];

                Servicio servicio = c.consultarServicio(Integer.parseInt(idservicio));
                System.out.println(iddocumento + " " + servicio.getDescripcion() + " " + nombrebenef + " " + idbenef);

                BigDecimal valoradicionalBigDecimal = new BigDecimal(BigInteger.ZERO);
                try {
                    valoradicionalBigDecimal = new BigDecimal(valoradicional);
                } catch (Exception e) {
                    System.out.println("No se ingresó un valor adicional válido:" + valoradicional);
                    valoradicionalBigDecimal = new BigDecimal(BigInteger.ZERO);
                }

                valoradicionalBigDecimal = valoradicionalBigDecimal.setScale(2, RoundingMode.HALF_EVEN);

                Documento documento = new Documento();
                documento.setIddocumento(iddocumento);
                documento.setServicio(servicio);
                documento.setBenefnombre(nombrebenef);
                documento.setBenefid(idbenef);
                documento.setValoradicional(valoradicionalBigDecimal);
                documento.setDescripcionadicional(descripcionadicional);

                documentos.add(documento);
            }

        } catch (Exception ex) {
            Logger.getLogger(TramiteCtrl.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            c.desconectar();
        }
        return documentos;
    }

    private SortedMap<String, Object> getByPreffix(
            NavigableMap<String, Object> myMap,
            String preffix) {
        return myMap.subMap(preffix, preffix + Character.MAX_VALUE);
    }

    private ArrayList<BigDecimal> calcularTotales(ArrayList<Documento> documentos) {
        ArrayList<BigDecimal> totales = new ArrayList<BigDecimal>();

        Iterator<Documento> docIter = documentos.iterator();

        BigDecimal subtotal = new BigDecimal(BigInteger.ZERO);
        BigDecimal iva = new BigDecimal(BigInteger.ZERO);
        BigDecimal total = new BigDecimal(BigInteger.ZERO);
        BigDecimal totaladicional = new BigDecimal(BigInteger.ZERO);

        while (docIter.hasNext()) {
            Documento doc = docIter.next();
            BigDecimal precio = doc.getServicio().getPrecio();
            BigDecimal valoradicional = doc.getValoradicional();
            subtotal = subtotal.add(precio);
            totaladicional = totaladicional.add(valoradicional);
        }

        subtotal = subtotal.setScale(2, RoundingMode.HALF_EVEN);
        totaladicional = totaladicional.setScale(2, RoundingMode.HALF_EVEN);

        iva = subtotal.multiply(new BigDecimal("0.12"));
        iva = iva.setScale(2, RoundingMode.HALF_EVEN);
        total = subtotal.add(iva);
        total = total.setScale(2, RoundingMode.HALF_EVEN);

        System.out.println("Subtotal:" + subtotal);
        System.out.println("IVA:" + iva);
        System.out.println("Total:" + total);
        System.out.println("Total Adicional:" + totaladicional);

        totales.add(subtotal);
        totales.add(iva);
        totales.add(total);
        totales.add(totaladicional);

        return totales;
    }

    public String devolverUsuario(HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        Cuenta cuenta = (session == null) ? null : (Cuenta) session.getAttribute("cuentaSesion");
        return (cuenta == null) ? null : cuenta.getUsuario();
    }

    public void buscarTramites(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        Cuenta cuentaSesion = (Cuenta) session.getAttribute("cuentaSesion");
        String usuario = "";

        if (cuentaSesion != null && cuentaSesion.roles.equals("usuario")) {
            usuario = cuentaSesion.getUsuario();
        } else if (cuentaSesion != null && cuentaSesion.roles.equals("admin")) {
            usuario = request.getParameter("usuario") == null || request.getParameter("usuario").equals("") || request.getParameter("usuario").equals("null") ? null : request.getParameter("usuario");
        }

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";

        String idcliente = request.getParameter("idcliente") == null || request.getParameter("idcliente").equals("") || request.getParameter("idcliente").equals("null") ? null : request.getParameter("idcliente");
        String fechaInicioStr = request.getParameter("fechaInicio") == null || request.getParameter("fechaInicio").equals("") ? null : request.getParameter("fechaInicio");
        String fechaFinalStr = request.getParameter("fechaFinal") == null || request.getParameter("fechaFinal").equals("") ? null : request.getParameter("fechaFinal");
        String canceladoStr = request.getParameter("cancelado") == null || request.getParameter("cancelado").equals("") ? null : request.getParameter("cancelado");

        System.out.println("usuario: " + usuario);
        System.out.println("idcliente: " + idcliente);
        System.out.println("fechaInicioStr: " + fechaInicioStr);
        System.out.println("fechaFinalStr: " + fechaFinalStr);
        System.out.println("canceladoStr: " + canceladoStr);

        java.util.Date fechaInicio = null;
        java.util.Date fechaFinal = null;
        int cancelado = canceladoStr == null ? -1 : Integer.parseInt(canceladoStr);

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        try {
            // today    
            Calendar cal = new GregorianCalendar();

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            fechaInicio = fechaInicioStr == null ? cal.getTime() : df.parse(fechaInicioStr);
            //tomorrow
            cal.add(Calendar.DATE, 1);
            fechaFinal = fechaFinalStr == null ? cal.getTime() : df.parse(fechaFinalStr);
        } catch (ParseException e) {
            System.out.println("ERROR en TramiteCtrl.buscarTramites: " + e);
        }

        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Tramite> tramites = c.busquedaTramites(usuario, idcliente, fechaInicio, fechaFinal, cancelado);
            Iterator<Tramite> tramitesItr = tramites.iterator();
            BigDecimal ivaTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal subtotalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal totalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal totaladicionalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal abonosTramites = new BigDecimal(BigInteger.ZERO);

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<tramites>";

            while (tramitesItr.hasNext()) {
                Tramite tramite = tramitesItr.next();
                Iterator<Documento> documentosItr = tramite.getDocumentos().iterator();
                StringBuilder documentos = new StringBuilder();
                while (documentosItr.hasNext()) {
                    documentos.append(documentosItr.next().getIddocumento()).append(" ");
                }
                subtotalTramites = subtotalTramites.add(tramite.getSubtotal());
                totalTramites = totalTramites.add(tramite.getTotal());
                ivaTramites = ivaTramites.add(tramite.getIva());
                totaladicionalTramites = totaladicionalTramites.add(tramite.getTotaladicional());

                RetencionRecibida retencionRecibida = c.obtenerRetencionRecibida(tramite.getIdtramite());
                BigDecimal valorRetenido = retencionRecibida == null ? BigDecimal.ZERO : retencionRecibida.getValor();

                xml += "<tramite>";
                xml += "<idtramite>" + tramite.getIdtramite() + "</idtramite>";
                xml += "<numfactura>" + tramite.getNumfactura() + "</numfactura>";
                xml += "<fechacreacion>" + df.format(tramite.getFechacreacion()) + "</fechacreacion>";
                xml += "<usuario>" + tramite.getUsuario() + "</usuario>";
                xml += "<nombrecliente>" + tramite.getCliente().getRazonsocial() + "</nombrecliente>";
                xml += "<credito>" + tramite.getCliente().getCredito() + "</credito>";
                xml += "<cantdoc>" + tramite.getCantdoc() + "</cantdoc>";
                xml += "<docs>" + documentos.toString() + "</docs>";
                xml += "<subtotal>" + tramite.getSubtotal().subtract(valorRetenido) + "</subtotal>";
                xml += "<iva>" + tramite.getIva() + "</iva>";
                xml += "<total>" + tramite.getTotal() + "</total>";
                xml += "<totaladicional>" + tramite.getTotaladicional() + "</totaladicional>";
                BigDecimal abonos = tramite.totalAbonos();
                xml += "<abonos>" + abonos + "</abonos>";
                abonosTramites = abonosTramites.add(abonos);
                BigDecimal pendiente = tramite.getTotal().add(tramite.getTotaladicional()).subtract(abonos);
                System.out.println("Pendiente despues de abonos: " + pendiente);
                if (retencionRecibida != null) {
                    pendiente = pendiente.subtract(retencionRecibida.getValor());
                    System.out.println("Pendiente despues de retención: " + pendiente);
                }

                pendiente = pendiente.setScale(2, RoundingMode.HALF_EVEN);
                xml += "<pendiente>" + pendiente + "</pendiente>";
                String canceladoResp = tramite.getCancelado() == 0 ? "No" : "Si";
                xml += "<cancelado>" + canceladoResp + "</cancelado>";
                xml += "<facturado>" + (tramite.getFacturado() == 1 ? "Si" : "No") + "</facturado>";
                xml += "</tramite>";
            }

            xml += "<subtotalTramites>" + subtotalTramites + "</subtotalTramites>";
            xml += "<ivaTramites>" + ivaTramites + "</ivaTramites>";
            xml += "<totalTramites>" + totalTramites + "</totalTramites>";
            xml += "<totaladicionalTramites>" + totaladicionalTramites + "</totaladicionalTramites>";
            xml += "<abonosTramites>" + abonosTramites + "</abonosTramites>";

            xml += "</tramites>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }

    public void buscarTramitesHoy(HttpServletRequest request, HttpServletResponse response) {

        HttpSession session = request.getSession(false);
        Cuenta cuentaSesion = (Cuenta) session.getAttribute("cuentaSesion");
        String usuario = "";

        if (cuentaSesion != null && cuentaSesion.roles.equals("usuario")) {
            usuario = cuentaSesion.getUsuario();
        } else if (cuentaSesion != null && cuentaSesion.roles.equals("admin")) {
            usuario = request.getParameter("usuario") == null || request.getParameter("usuario").equals("") || request.getParameter("usuario").equals("null") ? null : request.getParameter("usuario");
        }

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";

        // today    
        Calendar cal = new GregorianCalendar();

        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        java.util.Date fechaInicio = cal.getTime();

        cal.setTime(fechaInicio);

        // tomorrow    
        cal.add(Calendar.DATE, 1);

        java.util.Date fechaFinal = cal.getTime();

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        System.out.println("fechaInicioStr: " + df.format(fechaInicio));
        System.out.println("fechaFinalStr: " + df.format(fechaFinal));

        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Tramite> tramites = c.busquedaTramitesHoy(usuario, fechaInicio, fechaFinal);
            Iterator<Tramite> tramitesItr = tramites.iterator();

            BigDecimal ivaTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal subtotalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal totalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal totaladicionalTramites = new BigDecimal(BigInteger.ZERO);
            BigDecimal abonosTramites = new BigDecimal(BigInteger.ZERO);

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<tramites>";

            while (tramitesItr.hasNext()) {

                Tramite tramite = tramitesItr.next();
                Iterator<Documento> documentosItr = tramite.getDocumentos().iterator();
                StringBuilder documentos = new StringBuilder();
                while (documentosItr.hasNext()) {
                    documentos.append(documentosItr.next().getIddocumento()).append(" ");
                }

                RetencionRecibida retencionRecibida = c.obtenerRetencionRecibida(tramite.getIdtramite());
                BigDecimal valorRetenido = retencionRecibida == null ? BigDecimal.ZERO : retencionRecibida.getValor();

                subtotalTramites = subtotalTramites.add(tramite.getSubtotal());
                totalTramites = totalTramites.add(tramite.getTotal());
                ivaTramites = ivaTramites.add(tramite.getIva());
                totaladicionalTramites = totaladicionalTramites.add(tramite.getTotaladicional());

                xml += "<tramite>";
                xml += "<idtramite>" + tramite.getIdtramite() + "</idtramite>";
                xml += "<numfactura>" + tramite.getNumfactura() + "</numfactura>";
                xml += "<fechacreacion>" + df.format(tramite.getFechacreacion()) + "</fechacreacion>";
                xml += "<usuario>" + tramite.getUsuario() + "</usuario>";
                xml += "<nombrecliente>" + tramite.getCliente().getRazonsocial() + "</nombrecliente>";
                xml += "<credito>" + tramite.getCliente().getCredito() + "</credito>";
                xml += "<cantdoc>" + tramite.getCantdoc() + "</cantdoc>";

                xml += "<docs>" + documentos.toString() + "</docs>";

                xml += "<subtotal>" + tramite.getSubtotal().subtract(valorRetenido) + "</subtotal>";
                xml += "<iva>" + tramite.getIva() + "</iva>";
                xml += "<total>" + tramite.getTotal() + "</total>";
                xml += "<totaladicional>" + tramite.getTotaladicional() + "</totaladicional>";
                BigDecimal abonos = tramite.totalAbonos();
                xml += "<abonos>" + abonos + "</abonos>";
                abonosTramites = abonosTramites.add(abonos);
                BigDecimal pendiente = tramite.getTotal().add(tramite.getTotaladicional()).subtract(abonos);
                System.out.println("Pendiente despues de abonos: " + pendiente);
                if (retencionRecibida != null) {
                    pendiente = pendiente.subtract(retencionRecibida.getValor());
                    System.out.println("Pendiente despues de retencion: " + pendiente);
                }

                pendiente = pendiente.setScale(2, RoundingMode.HALF_EVEN);
                xml += "<pendiente>" + pendiente + "</pendiente>";
                String canceladoResp = tramite.getCancelado() == 0 ? "No" : "Si";
                xml += "<cancelado>" + canceladoResp + "</cancelado>";
                xml += "<facturado>" + (tramite.getFacturado() == 1 ? "Si" : "No") + "</facturado>";
                xml += "</tramite>";
            }
            xml += "<subtotalTramites>" + subtotalTramites + "</subtotalTramites>";
            xml += "<ivaTramites>" + ivaTramites + "</ivaTramites>";
            xml += "<totalTramites>" + totalTramites + "</totalTramites>";
            xml += "<totaladicionalTramites>" + totaladicionalTramites + "</totaladicionalTramites>";
            xml += "<abonosTramites>" + abonosTramites + "</abonosTramites>";

            xml += "</tramites>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }

    public void abonarTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        int idtramite = request.getParameter("idtramite") == null ? -1 : Integer.parseInt(request.getParameter("idtramite"));

        if (idtramite != -1) {
            try {
                c.conectar();

                System.out.println("Valor de parametro abono es " + request.getParameter("valor"));

                BigDecimal valorabono = request.getParameter("valor") == null || request.getParameter("valor").equals("") ? BigDecimal.ZERO : new BigDecimal(request.getParameter("valor"));

                System.out.println("Valor de abono es " + valorabono);
                if (!valorabono.equals(BigDecimal.ZERO)) {
                    valorabono = valorabono.setScale(2, RoundingMode.HALF_EVEN);

                    Abono abono = new Abono();
                    abono.setIdtramite(idtramite);
                    abono.setValor(valorabono);
                    abono.setFechaabono(new Date());
                    abono = c.guardarAbono(abono);
                    System.out.println("Id de abono es " + abono.getIdabono());
                }
            } catch (Exception e) {
                System.out.println("Error en TramiCtrl.abonarTramite" + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void guardarRetencionRecibida(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        int idtramite = request.getParameter("idtramite") == null ? -1 : Integer.parseInt(request.getParameter("idtramite"));

        if (idtramite != -1) {
            try {
                c.conectar();
                int numfactura = c.obtenerNumFactura(idtramite);
                System.out.println("El numero de factura es " + numfactura);

                BigDecimal valorabono = request.getParameter("valor") == null || request.getParameter("valor").equals("") ? BigDecimal.ZERO : new BigDecimal(request.getParameter("valor"));

                System.out.println("Valor de retencion es " + valorabono);
                if (!valorabono.equals(BigDecimal.ZERO)) {
                    valorabono = valorabono.setScale(2, RoundingMode.HALF_EVEN);

                    RetencionRecibida retencion = new RetencionRecibida();
                    retencion.setNumfactura(numfactura);
                    retencion.setValor(valorabono);
                    retencion.setFecharegistro(new Date());
                    c.guardarRetencionRecibida(retencion, idtramite);

                }
            } catch (Exception e) {
                System.out.println("Error en TramiCtrl.guardarRetencionRecibida" + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void consultarDocumentos(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";

        int idtramite = request.getParameter("idtramite") == null || request.getParameter("idtramite").equals("") || request.getParameter("idtramite").equals("null") ? 0 : Integer.parseInt(request.getParameter("idtramite"));

        System.out.println("idtramite: " + idtramite);
        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Documento> documentos = c.consultarDocumentos(idtramite);
            Iterator<Documento> documentosItr = documentos.iterator();

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<documentos>";

            while (documentosItr.hasNext()) {

                Documento documento = documentosItr.next();
                xml += "<documento>";
                xml += "<iddocumento>" + documento.getIddocumento() + "</iddocumento>";
                xml += "<idtramite>" + documento.getTramite() + "</idtramite>";
                xml += "<benefnombre>" + documento.getBenefnombre() + "</benefnombre>";
                xml += "<benefid>" + documento.getBenefid() + "</benefid>";
                xml += "<idservicio>" + documento.getServicio().getId() + "</idservicio>";
                xml += "<descripcion>" + documento.getServicio().getDescripcion() + "</descripcion>";
                xml += "<tipo>" + documento.getServicio().getTipo() + "</tipo>";
                xml += "<precio>" + documento.getServicio().getPrecio() + "</precio>";
                xml += "<adicional>" + documento.getValoradicional() + "</adicional>";
                xml += "</documento>";
            }

            xml += "</documentos>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }
    
    public void buscarDocumentos(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);
        Cuenta cuentaSesion = (Cuenta) session.getAttribute("cuentaSesion");
        String usuario = "";

        if (cuentaSesion != null && cuentaSesion.roles.equals("usuario")) {
            usuario = cuentaSesion.getUsuario();
        } else if (cuentaSesion != null && cuentaSesion.roles.equals("admin")) {
            usuario = request.getParameter("usuario") == null || request.getParameter("usuario").equals("") || request.getParameter("usuario").equals("null") ? null : request.getParameter("usuario");
        }
        

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";

        String descripcion = request.getParameter("descripcion") == null || request.getParameter("descripcion").equals("") || request.getParameter("descripcion").equals("null") ? null : request.getParameter("descripcion");
        String tipo = request.getParameter("tipo") == null || request.getParameter("tipo").equals("") || request.getParameter("tipo").equals("null") ? null : request.getParameter("tipo");
        String fechaInicioStr = request.getParameter("fechaInicio") == null || request.getParameter("fechaInicio").equals("") ? null : request.getParameter("fechaInicio");
        String fechaFinalStr = request.getParameter("fechaFinal") == null || request.getParameter("fechaFinal").equals("") ? null : request.getParameter("fechaFinal");

        System.out.println("usuario: " + usuario);
        System.out.println("descripcion: " + descripcion);
        System.out.println("tipo: " + tipo);
        System.out.println("fechaInicioStr: " + fechaInicioStr);
        System.out.println("fechaFinalStr: " + fechaFinalStr);        

        java.util.Date fechaInicio = null;
        java.util.Date fechaFinal = null;        

        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        try {
            // today    
            Calendar cal = new GregorianCalendar();

            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            fechaInicio = fechaInicioStr == null ? cal.getTime() : df.parse(fechaInicioStr);
            //tomorrow
            cal.add(Calendar.DATE, 1);
            fechaFinal = fechaFinalStr == null ? cal.getTime() : df.parse(fechaFinalStr);
        } catch (ParseException e) {
            System.out.println("ERROR en TramiteCtrl.buscarDocumentos: " + e);
        }

        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Documento> documentos = c.busquedaDocumentos(descripcion, tipo, fechaInicio, fechaFinal);
            Iterator<Documento> documentosItr = documentos.iterator();            

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<documentos>";

            while (documentosItr.hasNext()) {

                Documento documento = documentosItr.next();
                Tramite tramite = c.consultarTramite(documento.getTramite());
                xml += "<documento>";
                xml += "<iddocumento>" + documento.getIddocumento() + "</iddocumento>";
                xml += "<idtramite>" + documento.getTramite() + "</idtramite>";
                xml += "<clientenombre>" + c.consultarTramite(documento.getTramite()).getCliente().getRazonsocial() + "</clientenombre>";                
                xml += "<fecha>" + c.consultarTramite(documento.getTramite()).getFechacreacion() + "</fecha>";                
                xml += "<benefnombre>" + documento.getBenefnombre() + "</benefnombre>";
                xml += "<benefid>" + documento.getBenefid() + "</benefid>";
                xml += "<idservicio>" + documento.getServicio().getId() + "</idservicio>";
                xml += "<descripcion>" + documento.getServicio().getDescripcion() + "</descripcion>";
                xml += "<descripcionAdicional>" + documento.getDescripcionadicional() + "</descripcionAdicional>";
                xml += "<tipo>" + documento.getServicio().getTipo() + "</tipo>";
                xml += "<precio>" + documento.getServicio().getPrecio() + "</precio>";
                xml += "<adicional>" + documento.getValoradicional() + "</adicional>";
                xml += "<numfactura>" + tramite.getNumfactura()+ "</numfactura>";
                xml += "<usuario>" + tramite.getUsuario() + "</usuario>";
                xml += "</documento>";
            }

            xml += "</documentos>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }

    public void consultarDocumentosAnulados(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";
        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Documento> documentos = c.consultarDocumentosAnulados();
            Iterator<Documento> documentosItr = documentos.iterator();

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<documentos>";

            while (documentosItr.hasNext()) {

                Documento documento = documentosItr.next();
                xml += "<documento>";
                xml += "<iddocumento>" + documento.getIddocumento() + "</iddocumento>";
                xml += "<idtramite>" + documento.getTramite() + "</idtramite>";
                xml += "<fechacreacion>" + documento.getFechacreacion() + "</fechacreacion>";
                xml += "<benefnombre>" + documento.getBenefnombre() + "</benefnombre>";
                xml += "<benefid>" + documento.getBenefid() + "</benefid>";
                xml += "<idservicio>" + documento.getServicio().getId() + "</idservicio>";
                xml += "<descripcion>" + documento.getServicio().getDescripcion() + "</descripcion>";
                xml += "<tipo>" + documento.getServicio().getTipo() + "</tipo>";
                xml += "<precio>" + documento.getServicio().getPrecio() + "</precio>";
                xml += "<adicional>" + documento.getValoradicional() + "</adicional>";
                xml += "</documento>";
            }

            xml += "</documentos>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }

    public static String DocumentosToJSON(List elementos) {

        StringBuilder strBld = new StringBuilder();

        Iterator it = elementos.iterator();
        strBld.append("[");
        do {
            if (!it.hasNext()) {
                break;
            }
            Documento documento = (Documento) it.next();

            if (documento != null) {
                strBld.append("[\"");
                strBld.append(documento.getIddocumento());
                strBld.append("\", \"");
                strBld.append(documento.getFechacreacion());
                strBld.append("\"]");
            }
            if (it.hasNext()) {
                strBld.append(",");
            }

        } while (true);
        strBld.append("]");

        return strBld.toString();
    }

    public void consultarAbonos(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";

        int idtramite = request.getParameter("idtramite") == null || request.getParameter("idtramite").equals("") || request.getParameter("idtramite").equals("null") ? 0 : Integer.parseInt(request.getParameter("idtramite"));

        System.out.println("idtramite: " + idtramite);
        try {
            PrintWriter pw = response.getWriter();

            c.conectar();
            ArrayList<Abono> abonos = c.consultarAbonos(idtramite);
            Iterator<Abono> abonosItr = abonos.iterator();

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<abonos>";

            while (abonosItr.hasNext()) {

                Abono abono = abonosItr.next();
                xml += "<abono>";

                xml += "<idabono>" + abono.getIdabono() + "</idabono>";
                xml += "<idtramite>" + abono.getIdtramite() + "</idtramite>";
                xml += "<fechaabono>" + abono.getFechaabono() + "</fechaabono>";
                xml += "<valor>" + abono.getValor() + "</valor>";
                xml += "</abono>";
            }

            xml += "</abonos>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        } finally {
            c.desconectar();
        }
    }

    public Factura construirFacturaElectronica(Tramite tramite) {
        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword(System.getProperty("pwdCrpt"));
        Properties props = new EncryptableProperties(encryptor);
        try {
            props.load(new FileInputStream(path));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TramiteCtrl.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(TramiteCtrl.class.getName()).log(Level.SEVERE, null, ex);
        }
        String razonsocial = props.getProperty("datosnotaria.razonsocial");
        String ruc = props.getProperty("datosnotaria.ruc");
        String direccionmatriz = props.getProperty("datosnotaria.direccionmatriz");
        String direccionestab = props.getProperty("datosnotaria.direccionestab");

        String tipoComprobante = "01";
        String ambiente = "1"; //1 pruebas, 2 producción
        String serie = String.format("%03d", 1) + String.format("%03d", 1); //serie arbitraria
        String numeroComprobante = String.format("%09d", tramite.getIdtramite()); //Iniciar con secuencial establecido
        String codigoNumerico = "12344321";
        String tipoEmision = "1"; //1 normal, 2 contingencia

        Factura f = new Factura();
        f.setVersion("1.1.0");
        f.setId("comprobante");
        InfoTributaria infoTributaria = new InfoTributaria();
        infoTributaria.setAmbiente(ambiente);
        infoTributaria.setRazonSocial(razonsocial);
        infoTributaria.setNombreComercial(razonsocial);
        infoTributaria.setRuc(ruc);
        Date hoy = new Date();
        infoTributaria.setClaveAcceso(ClaveAcceso.generarClaveAcceso(hoy, tipoComprobante, ruc, ambiente, serie, numeroComprobante, codigoNumerico, tipoEmision));

        infoTributaria.setCodDoc("01");
        infoTributaria.setEstab(String.format("%03d", 1));
        infoTributaria.setTipoEmision("1");
        infoTributaria.setPtoEmi(String.format("%03d", 1));
        infoTributaria.setSecuencial(numeroComprobante);
        infoTributaria.setDirMatriz(direccionmatriz);
        infoTributaria.setIdtramite(tramite.getIdtramite());

        f.setInfoTributaria(infoTributaria);

        TotalImpuesto totimp1 = new TotalImpuesto();
        totimp1.setBaseImponible(tramite.getSubtotal());
        totimp1.setCodigo("2"); //Este código indica que es IVA
        totimp1.setCodigoPorcentaje("2"); //Este código indica que el valor es 12% (0=0%;2=12%;6=No objeto de impuesto)
        totimp1.setValor(tramite.getIva()); //CORREGIR Y PONER EL RESULTADO DE LA OPERACION DE PORCENTAJE IVA CALCULADO PARA BASE IMPONIBLE

        List<TotalImpuesto> totalConImpuestos = new ArrayList();

        totalConImpuestos.add(totimp1);

        InfoFactura infoFactura = new InfoFactura();

        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String fechaDocumento = df.format(hoy);

        infoFactura.setFechaEmision(fechaDocumento);
        infoFactura.setDirEstablecimiento(direccionestab);
        infoFactura.setObligadoContabilidad("NO");

        infoFactura.setTipoIdentificacionComprador(tramite.getCliente().getTipoIdentificacion());

        infoFactura.setRazonSocialComprador(tramite.getCliente().getRazonsocial());
        infoFactura.setIdentificacionComprador(tramite.getCliente().getIdentificacion());
        infoFactura.setTotalSinImpuestos(tramite.getSubtotal());
        infoFactura.setTotalDescuento(BigDecimal.ZERO);
        infoFactura.setTotalConImpuesto(totalConImpuestos);
        infoFactura.setPropina(BigDecimal.ZERO);
        infoFactura.setImporteTotal(tramite.getTotal()); //CORREGIR Y COLOCAR SUMA DE TOTAL SIN IMPUESTOS MAS TOTAL CON IMPUESTOS
        infoFactura.setMoneda("DOLAR");

        f.setInfoFactura(infoFactura);

        Conexion c = new Conexion(path);

        try {
            c.conectar();
            ArrayList<TramiteItems> tramiteItems = c.consultarTramiteItems(tramite.getIdtramite());
            Iterator<TramiteItems> tramiteItemsItr = tramiteItems.iterator();
            List<Detalle> detalles = new ArrayList();

            while (tramiteItemsItr.hasNext()) {
                TramiteItems tramiteItem = tramiteItemsItr.next();

                System.out.println("Detalle: " + tramiteItem.getDescripcion() + " " + tramiteItem.getTotal());

                Detalle d = new Detalle();
                d.setCodigoPrincipal("pea-" + String.format("%03d", tramiteItem.getIdservicio()) + "-" + String.format("%02d", tramiteItem.getIdservicio()));
                d.setCodigoAuxiliar("tar-" + String.format("%03d", tramiteItem.getIdservicio()) + "-" + String.format("%02d", tramiteItem.getIdservicio()));
                d.setDescripcion(tramiteItem.getDescripcion());
                d.setCantidad(new BigDecimal(tramiteItem.getCantidad()));
                d.setPrecioUnitario(tramiteItem.getPrecio());
                d.setDescuento(new BigDecimal("0.00"));
                d.setPrecioTotalSinImpuesto(tramiteItem.getSubtotal());

                List<Impuesto> impuesto = new ArrayList();
                Impuesto i11 = new Impuesto();
                i11.setCodigo("2");
                i11.setCodigoPorcentaje("2");
                i11.setTarifa(new BigDecimal("12"));
                i11.setBaseImponible(tramiteItem.getSubtotal());
                BigDecimal totalIva = tramiteItem.getSubtotal().multiply(new BigDecimal("0.12"));
                totalIva = totalIva.setScale(2, RoundingMode.HALF_EVEN);
                i11.setValor(totalIva);
                impuesto.add(i11);
                d.setImpuesto(impuesto);
                detalles.add(d);

            }
            System.out.println("Longitud de detalles: " + detalles.size());
            f.setDetalle(detalles);
        } catch (Exception e) {
            System.out.println("ERROR en TramiteCtrl.construirFacturaElectronica" + e);
        }

        try {
            c.conectar();
            c.guardarFactura(infoTributaria.getClaveAcceso(), f);
        } catch (Exception ex) {
            System.out.println("" + ex);
        } finally {
            if (c.estaConectado()) {
                System.out.println("EVENTO: La conexión a la base de datos está siendo cerrada...");
                c.desconectar();
                System.out.println("EVENTO: La conexión a la base de datos ha sido cerrada");
            } else {
                System.out.println("EVENTO: La conexión de base de datos ya estaba cerrada");
            }
        }
        return f;
    }

    public void facturarTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        int idtramite = request.getParameter("idtramite") == null ? -1 : Integer.parseInt(request.getParameter("idtramite"));
        int numfactura = request.getParameter("numfactura") == null ? -1 : Integer.parseInt(request.getParameter("numfactura"));

        if (idtramite != -1) {
            try {
                c.conectar();
                Tramite tramite = c.consultarTramite(idtramite);

                String claveacceso = null;

                if (tramite.getFacturado() != 1) {
                    Factura factura = construirFacturaElectronica(tramite);
                    tramite.setFacturado(1);
                    tramite.setNumfactura(numfactura);
                    c.setTramiteFacturado(tramite);
                    claveacceso = factura.getInfoTributaria().getClaveAcceso();
                } else {
                    claveacceso = c.obtenerClaveAccesoTramite(tramite);
                }

                generarFacturaPDF(request, response, claveacceso);

            } catch (Exception e) {
                System.out.println("Error en TramiCtrl.facturarTramite " + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void facturarTramiteHTML(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        int idtramite = request.getParameter("idtramite") == null ? -1 : Integer.parseInt(request.getParameter("idtramite"));
        int numfactura = request.getParameter("numfactura") == null ? -1 : Integer.parseInt(request.getParameter("numfactura"));

        if (idtramite != -1) {
            try {
                c.conectar();
                Tramite tramite = c.consultarTramite(idtramite);

                String claveacceso = null;

                if (tramite.getFacturado() != 1) {
                    Factura factura = construirFacturaElectronica(tramite);
                    tramite.setFacturado(1);
                    tramite.setNumfactura(numfactura);
                    c.setTramiteFacturado(tramite);
                    claveacceso = factura.getInfoTributaria().getClaveAcceso();
                } else {
                    claveacceso = c.obtenerClaveAccesoTramite(tramite);
                }
                generarFacturaHtml(request, response, claveacceso);

            } catch (Exception e) {
                System.out.println("Error en TramiCtrl.facturarTramite " + e);
            } finally {
                c.desconectar();
            }
        }
    }

    public void cambiarNumFacturaTramite(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);

        int idtramite = request.getParameter("idtramite") == null ? -1 : Integer.parseInt(request.getParameter("idtramite"));
        int numfactura = request.getParameter("numfactura") == null ? -1 : Integer.parseInt(request.getParameter("numfactura"));

        if (idtramite != -1) {
            try {
                c.conectar();
                Tramite tramite = c.consultarTramite(idtramite);
                System.out.println("Cambiando numfactura de tramite " + tramite.getIdtramite() + " de " + tramite.getNumfactura() + " a " + numfactura);
                if (tramite.getFacturado() == 1) {
                    tramite.setNumfactura(numfactura);
                    c.setTramiteFacturado(tramite);

                    String claveacceso = c.obtenerClaveAccesoTramite(tramite);

                }

            } catch (Exception e) {
                System.out.println("Error en TramiCtrl.cambiarNumFacturaTramite " + e);
            } finally {
                c.desconectar();
            }
        }
    }

    protected void generarFacturaPDF(HttpServletRequest request, HttpServletResponse response, String claveacceso)
            throws ServletException, IOException {
        System.out.println("Entrando a generarPDF");
        System.out.println("La clave de acceso para generar la factura es: " + claveacceso);
        ServletOutputStream servletOutputStream = response.getOutputStream();
        File reportFile = new File(getServletConfig().getServletContext().getRealPath("/reports/facturaNotaria.jasper"));
        System.out.println("Hasta aqui no hay problemas con encontrar el archivo jasper");
        byte[] bytes = null;
        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        Map parameters = new HashMap();
        parameters.put("paramclave", claveacceso);

        try {

            c.conectar();
            bytes = JasperRunManager.runReportToPdf(reportFile.getPath(), parameters, c.getCon());

            response.setContentType("application/pdf");
            response.addHeader("Content-Disposition", "attachment; filename=" + claveacceso + ".pdf");
            response.setContentLength(bytes.length);

            servletOutputStream.write(bytes, 0, bytes.length);
            servletOutputStream.flush();
            servletOutputStream.close();
        } catch (JRException e) {
            // display stack trace in the browser
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            response.setContentType("text/plain");
            response.getOutputStream().print(stringWriter.toString());
            System.out.println("ERROR en generarPDF:" + e);
        } catch (Exception ex) {
            System.out.println("ERROR en generarPDF:" + ex);
        }
    }

    protected void generarFacturaHtml(HttpServletRequest request, HttpServletResponse response, String claveacceso)
            throws ServletException, IOException {
        System.out.println("Entrando a generarHtml");
        File reportFile = new File(getServletConfig().getServletContext().getRealPath("/reports/facturaNotaria.jasper"));
        String html;
        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        Map parameters = new HashMap();
        parameters.put("paramclave", claveacceso);
        PrintWriter pw = response.getWriter();
        try {

            c.conectar();
            html = JasperRunManager.runReportToHtmlFile(reportFile.getPath(), parameters, c.getCon());
            pw.println(html);

        } catch (JRException e) {
            // display stack trace in the browser
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            e.printStackTrace(printWriter);
            response.setContentType("text/html");
            response.getOutputStream().print(stringWriter.toString());
            System.out.println("ERROR en generarFacturaHtml:" + e);
        } catch (Exception ex) {
            System.out.println("ERROR en generarFacturaHtml:" + ex);
        }
    }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
