/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package util;

import dao.AlumnowebJpaController;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author SASHA
 */
@WebServlet(name = "loginAlumno", urlPatterns = {"/loginAlumno"})
public class loginAlumno extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private final EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_Preg01_war_1.0-SNAPSHOTPU");
    private final AlumnowebJpaController alumnoDAO = new AlumnowebJpaController(emf);
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setContentType("application/json;charset=UTF-8");

        try (PrintWriter out = response.getWriter()) {

            // Leer el cuerpo de la solicitud
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = request.getReader()) {
                String linea;
                while ((linea = reader.readLine()) != null) {
                    sb.append(linea);
                }
            }

            JSONObject json;
            try {
                json = new JSONObject(sb.toString());
            } catch (Exception e) {
                out.println("{\"resultado\":\"error\",\"mensaje\":\"JSON inválido\"}");
                return;
            }

            if (!json.has("user") || !json.has("pass")) {
                out.println("{\"resultado\":\"error\",\"mensaje\":\"Faltan parámetros\"}");
                return;
            }

            String userParam = json.getString("user");
            String pass = json.getString("pass");

            int codiAlum;
            try {
                codiAlum = Integer.parseInt(userParam);
            } catch (NumberFormatException e) {
                out.println("{\"resultado\":\"error\", \"mensaje\":\"Usuario inválido\"}");
                return;
            }

            String hashedPassword = alumnoDAO.obtenerPasswordPorCodigo(codiAlum); // Asegúrate de tener este método
            if (hashedPassword == null) {
                out.println("{\"resultado\":\"error\",\"mensaje\":\"Usuario no encontrado\"}");
                return;
            }

            String passConUser = pass + userParam;
            boolean valido = BCrypt.checkpw(passConUser, hashedPassword);

            if (valido) {
                try {
                    String token = JwtUtil.generarToken(userParam);
                    request.getSession().setAttribute("usuario", userParam);
                    out.println("{\"resultado\":\"ok\",\"token\":\"" + token + "\"}");
                } catch (Exception e) {
                    e.printStackTrace();
                    out.println("{\"resultado\":\"error\",\"mensaje\":\"Error al generar token\"}");
                }
            } else {
                out.println("{\"resultado\":\"error\",\"mensaje\":\"Usuario o contraseña incorrectos\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().println("{\"resultado\":\"error\",\"mensaje\":\"Error interno del servidor\"}");
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
