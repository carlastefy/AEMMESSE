package controller.admin.gestisciProdotti;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Libro;
import model.libroService.LibroDAO;

import java.io.IOException;

@WebServlet("/disponibile")
public class Disponibile extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final String isbn= request.getParameter("isbn");

        final LibroDAO service = new LibroDAO();
        final Libro libro = service.doRetrieveById(isbn);
        libro.setDisponibile(true);

        service.updateDisponibile(libro);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("gestisci-prodotti");
        dispatcher.forward(request, response);
    }
}
