package controller.admin.gestisciSedi;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.SedeDAO;

import java.io.IOException;

@WebServlet("/modifica-sede")
public class ModificaSedeServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException {

        final String isbn = request.getParameter("isbn");
        final int idSede = Integer.parseInt(request.getParameter("idSede"));

        final SedeDAO sedeDAO = new SedeDAO();
        sedeDAO.removeLibroSede(idSede, isbn);

        response.sendRedirect("gestisci-sedi");
    }
}
