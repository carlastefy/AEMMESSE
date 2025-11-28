package controller.admin.gestisciReparti;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.RepartoDAO;

import java.io.IOException;

@WebServlet("/modifica-reparto")
public class ModificaRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final String isbn = request.getParameter("isbn");
        final int idReparto = Integer.parseInt(request.getParameter("idReparto"));

        final RepartoDAO repartoDAO = new RepartoDAO();
        repartoDAO.removeLibroReparto(idReparto, isbn);

        response.sendRedirect("gestisci-reparti");
    }
}
