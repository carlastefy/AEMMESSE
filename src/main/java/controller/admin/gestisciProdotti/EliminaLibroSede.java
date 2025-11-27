package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.SedeDAO;

import java.io.IOException;
@WebServlet("/eliminaLibro-sede")
public class EliminaLibroSede extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String isbn= request.getParameter("isbn");
        final int idSede = Integer.parseInt(request.getParameter("idSede"));

        final SedeDAO service = new SedeDAO();
        service.deleteFromPresenzaLibro(idSede, isbn);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("modifica-libro");
        dispatcher.forward(request, response);
    }
}
