package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.RepartoDAO;

import java.io.IOException;

@WebServlet("/eliminaLibro-reparto")
public class EliminaLibroReparto extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String isbn= request.getParameter("isbn");
        final int idReparto = Integer.parseInt(request.getParameter("idReparto"));

        final RepartoDAO service = new RepartoDAO();
        service.deleteFromAppartenenzaLibro(idReparto, isbn);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("modifica-libro");
        dispatcher.forward(request, response);
    }
}
