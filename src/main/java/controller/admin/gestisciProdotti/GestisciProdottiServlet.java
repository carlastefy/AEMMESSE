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
import java.util.ArrayList;
import java.util.List;

@WebServlet("/gestisci-prodotti")
public class GestisciProdottiServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final LibroDAO service = new LibroDAO();
        // --- Gestione pagina corrente ---
        int page = 1;
        final String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }
        if (page < 1) {
            page = 1;
        }

        final int pageSize = 20; // numero di prodotti per pagina in admin
        final int offset = (page - 1) * pageSize;

        // --- Totale libri e numero di pagine ---
        final int totalResults = service.countAllLibri();
        final int totalPages = (int) Math.ceil((double) totalResults / pageSize);

        // --- Libri della pagina corrente ---
        final List<Libro> libri = service.doRetrieveAllPaged(offset, pageSize);

        request.setAttribute("libri", libri);
        request.setAttribute("page", page);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("totalResults", totalResults);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/prodotti/gestisciProdotti.jsp");
        dispatcher.forward(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
