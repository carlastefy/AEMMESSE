package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.RepartoDAO;

import java.io.IOException;
@WebServlet("/aggiungiLibro-reparto")
public class AggiungiLibroReparto extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String[] idReparti = request.getParameterValues("repartoSelezionato");
        final String isbn = request.getParameter("isbn");

        final RepartoDAO repartoService = new RepartoDAO();
        if(idReparti!=null) {
            for (final String idReparto : idReparti) {
                final int id= Integer.parseInt(idReparto);
                repartoService.doSaveAppartenenza(id,isbn);
            }
        }
        final RequestDispatcher dispatcher = request.getRequestDispatcher("modifica-libro");
        dispatcher.forward(request, response);

    }
}
