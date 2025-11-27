package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.SedeDAO;

import java.io.IOException;

@WebServlet("/aggiungiLibro-sede")
public class AggiungiLibroSede extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
            final String[] idSedi= request.getParameterValues("sedeSelezionata");
            final String isbn = request.getParameter("isbn");

            final SedeDAO sedeService = new SedeDAO();
            if(idSedi!=null) {
                for (final String idSede : idSedi) {
                    final int id = Integer.parseInt(idSede);
                    sedeService.doSavePresenza(id, isbn);
                }
            }

        final RequestDispatcher dispatcher = request.getRequestDispatcher("modifica-libro");
        dispatcher.forward(request, response);

    }
}
