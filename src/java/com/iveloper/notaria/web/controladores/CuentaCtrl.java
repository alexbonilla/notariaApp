/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.iveloper.notaria.web.controladores;

import com.iveloper.db.Conexion;
import com.iveloper.db.PasswordEncryptionService;
import com.iveloper.entidades.Cuenta;
import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author Alex
 */
@WebServlet(name = "CuentaCtrl", urlPatterns = {"/CuentaCtrl"})
public class CuentaCtrl extends HttpServlet {

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/xml;charset=UTF-8");
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        PrintWriter out = response.getWriter();
        String op = request.getParameter("op");

        if (op.equalsIgnoreCase("usuario")) {
            response.setContentType("text/html;charset=UTF-8"); // Esto es para poder escribir la respuesta en el iframe
            response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
            out = response.getWriter();
            out.print(devolverUsuario(request, response));

        } else if (op.equalsIgnoreCase("validar")) {
            System.out.println("Entrando a validar usuario");
            validarCuenta(request, response);

        } else if (op.equalsIgnoreCase("consultar")) {
            consultarCuentas(request, response);
        } else if (op.equalsIgnoreCase("consultaSimple")) {
            obtenerCuentasJSON(request, response);

        } else if (op.equalsIgnoreCase("editar")) {
            obtenerCuenta(request, response, request.getParameter("usuario"));
        } else if (op.equalsIgnoreCase("modificar")) {
            try {
                modificarCuenta(request, response);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(CuentaCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (op.equalsIgnoreCase("cerrar_sesion")) {
            cerrarSesion(request, response);
        } else if (op.equalsIgnoreCase("crear")) {
            System.out.println("Entrando a crear usuario");
            try {
                crearCuenta(request, response);
            } catch (NoSuchAlgorithmException | InvalidKeySpecException ex) {
                Logger.getLogger(CuentaCtrl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        out.close();
    }

    public void validarCuenta(HttpServletRequest request, HttpServletResponse response) {

        String usuario = request.getParameter("usuario");
        System.out.println("Usuario: " + usuario);
        String clave = request.getParameter("clave");
        String rol = request.getParameter("rol");
        System.out.println("Rol: " + rol);
        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String url = "404.html";

        try {
            c.conectar();
            PasswordEncryptionService pes = new PasswordEncryptionService();
            Cuenta cuentaIngreso = c.consultarCuenta(usuario);
            boolean claveValida = false;
            if (cuentaIngreso != null) {
                claveValida = pes.authenticate(clave, cuentaIngreso.getPwd(), cuentaIngreso.getSalt());
            }
            
//            if (cuentaIngreso != null && claveValida && cuentaIngreso.getActivo()==1 && c.cuentaValida(usuario, rol)) {
            if (cuentaIngreso != null && claveValida && cuentaIngreso.getActivo()==1) {
                url = "nuevo_tramite.jsp";
                HttpSession session = request.getSession(true);
                session.setAttribute("cuentaSesion", cuentaIngreso);

            } else {

                url = "login.html";
                if (cuentaIngreso == null) {
                    System.out.println("Usuario no existe");
                } else if (!claveValida) {
                    System.out.println("Clave incorrecta");
                } else if(cuentaIngreso.getActivo()==0){
                    System.out.println("El usuario " + usuario + " no está activo");
                } else {
                    System.out.println("Rol no admitido para usuario " + usuario);
                }
            }

        } catch (Exception e) {
            System.out.println("" + e);
            url = "registration.html";
        }

        c.desconectar();

        try {
            response.sendRedirect(url);
        } catch (IOException e) {
            System.out.println("ERROR: " + e);
        }

    }

    public String devolverUsuario(HttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpSession session = req.getSession(false);
        Cuenta cuenta = (session == null) ? null : (Cuenta) session.getAttribute("cuentaSesion");
        return (cuenta == null) ? null : cuenta.getUsuario();
    }

    public List consultarCuentas(HttpServletRequest request, HttpServletResponse response) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        ArrayList<Cuenta> cuentas = new ArrayList();
        String xml = "";
        DateFormat df = new SimpleDateFormat("MM/dd/yyyy");

        try {
            PrintWriter pw = response.getWriter();
            c.conectar();
            cuentas = c.consultarCuentas();

            Iterator<Cuenta> cuentasItr = cuentas.iterator();

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<usuarios>";

            while (cuentasItr.hasNext()) {

                Cuenta cuenta = cuentasItr.next();
                xml += "<usuario>";
                xml += "<id>" + cuenta.getId() + "</id>";
                xml += "<fechacreacion>" + cuenta.getFechacreacion() + "</fechacreacion>";
                xml += "<username>" + cuenta.getUsuario() + "</username>";
                xml += "<roles>" + cuenta.getRoles() + "</roles>";
                xml += "<email>" + cuenta.getMail() + "</email>";
                xml += "<activo>" + (cuenta.getActivo() == 1 ? "si" : "no") + "</activo>";
                xml += "</usuario>";
            }

            xml += "</usuarios>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        }

        c.desconectar();
        return cuentas;

    }

    public void obtenerCuentasJSON(HttpServletRequest request, HttpServletResponse response) {
        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        ArrayList cuentas = new ArrayList();
        PrintWriter pw = null;

        try {
            pw = response.getWriter();

            c.conectar();
            cuentas = c.consultarCuentas();

            pw.println(toSimpleJSON(cuentas));

        } catch (Exception e) {
            System.out.println("" + e);
        }

        c.desconectar();

    }

    public void obtenerCuenta(HttpServletRequest request, HttpServletResponse response, String usuario) {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        String xml = "";
        PrintWriter pw = null;

        try {
            pw = response.getWriter();

            c.conectar();
            Cuenta cuenta = c.consultarCuenta(usuario);

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<cuentas>";

            xml += "<registro>";
            xml += "<cuenta>" + cuenta.getUsuario() + "</cuenta>";
            xml += "<pwd>" + cuenta.getPwd() + "</pwd>";
            xml += "<mail>" + cuenta.getMail() + "</mail>";
            xml += "<tipo>" + cuenta.getRoles() + "</tipo>";
            xml += "</registro>";

            xml += "</cuentas>";
            pw.println(xml);

        } catch (Exception e) {
            String error = "OCURRIO UN ERROR EN LA CONEXION";
        }

        c.desconectar();

    }

    public void modificarCuenta(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        String username = request.getParameter("username");
        String nuevorol = request.getParameter("rol") != null && !request.getParameter("rol").equals("") ? request.getParameter("rol") : "";
        String nuevoemail = request.getParameter("email") != null && !request.getParameter("email").equals("") ? request.getParameter("email") : "";
        String nuevoestado = request.getParameter("estado") != null && !request.getParameter("estado").equals("") ? request.getParameter("estado") : "";
        String nuevaclave = request.getParameter("clave") != null && !request.getParameter("clave").equals("") ? request.getParameter("clave") : "";
        Conexion c = new Conexion(path);

        PrintWriter pw = null;
        String xml = "";

        try {
            pw = response.getWriter();
            c.conectar();

            Cuenta cuenta = c.consultarCuenta(username);

            if (!nuevaclave.equals("")) {
                PasswordEncryptionService pes = new PasswordEncryptionService();
                byte[] salt = pes.generateSalt();
                byte[] clave = pes.getEncryptedPassword(nuevaclave, salt);
                cuenta.setSalt(salt);
                cuenta.setPwd(clave);
            }
            if(!nuevorol.equals("")){
                cuenta.setRoles(nuevorol);
            }
            if(!nuevoemail.equals("")){
                cuenta.setMail(nuevoemail);
            }
            if(!nuevoestado.equals("")){    
                cuenta.setActivo(nuevoestado.equals("si")?1:0);
            }

            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<cuentas>";
            xml += "<registro>";
            xml += "<respuesta>" + c.modificarCuenta(cuenta) + "</respuesta>";
            xml += "</registro>";
            xml += "</cuentas>";
            pw.println(xml);

        } catch (Exception e) {
            System.out.println("" + e);
        }

        c.desconectar();

    }

    public void crearCuenta(HttpServletRequest request, HttpServletResponse response) throws NoSuchAlgorithmException, InvalidKeySpecException {

        String path = getServletContext().getRealPath("/WEB-INF/configuration.properties");
        Conexion c = new Conexion(path);
        PasswordEncryptionService pes = new PasswordEncryptionService();
        byte[] salt = pes.generateSalt();

        byte[] clave = pes.getEncryptedPassword(request.getParameter("password"), salt);

        System.out.println("salt: " + new String(salt));
        System.out.println("clave: " + new String(clave));

        Cuenta cuenta = new Cuenta(request.getParameter("username"), request.getParameter("rol"), request.getParameter("email"), clave, salt);
        PrintWriter pw = null;
        String xml = "";

        try {
            pw = response.getWriter();
            c.conectar();
            xml = "<?xml version='1.0' encoding='UTF-8' ?>";
            xml += "<cuentas>";
            xml += "<registro>";
            xml += "<respuesta>" + c.crearCuenta(cuenta) + "</respuesta>";
            xml += "</registro>";
            xml += "</cuentas>";
            pw.println(xml);

        } catch (Exception e) {
            String error = "OCURRIO UN ERROR EN LA CONEXION";
        }

        c.desconectar();

    }

    public void cerrarSesion(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.setAttribute("cuentaSesion", null);
            session.invalidate();
        }
        response.sendRedirect("login.html");
        System.out.println("La sesión ha sido cerrada");
    }

    public static JSONObject toJSON(List elementos) {
        JSONObject json = new JSONObject();
        try {
            JSONArray jsonItems = new JSONArray();
            Iterator it = elementos.iterator();
            do {
                if (!it.hasNext()) {
                    break;
                }
                Cuenta c = (Cuenta) it.next();

                if (c != null) {
                    jsonItems.put(c.toJSONObject());
                }
            } while (true);
            json.put("items", jsonItems);
        } catch (JSONException ex) {
            System.out.println("" + ex);
        }
        return json;
    }

    public static String toSimpleJSON(List elementos) {

        StringBuilder strBld = new StringBuilder();

        Iterator it = elementos.iterator();
        strBld.append("[");
        do {
            if (!it.hasNext()) {
                break;
            }
            Cuenta cuenta = (Cuenta) it.next();

            if (cuenta != null) {
                strBld.append("[\"");
                strBld.append(cuenta.getUsuario());
                strBld.append("\", \"");
                strBld.append(cuenta.getUsuario());
                strBld.append("\"]");
            }
            if (it.hasNext()) {
                strBld.append(",");
            }

        } while (true);
        strBld.append("]");

        return strBld.toString();
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
