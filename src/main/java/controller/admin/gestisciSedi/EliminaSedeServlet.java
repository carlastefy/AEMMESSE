package controller.admin.gestisciSedi;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.SedeDAO;

import java.io.IOException;

@WebServlet("/elimina-sede")
public class EliminaSedeServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final SedeDAO sedeService = new SedeDAO();
        final int idSede = Integer.parseInt(request.getParameter("idSede"));
        sedeService.deleteSede(idSede);

        response.sendRedirect("gestisci-sedi");
    }
}