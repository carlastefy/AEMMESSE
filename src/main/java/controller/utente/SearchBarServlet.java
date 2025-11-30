package controller.utente;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.utenteService.Utente;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet ("/search")
public class SearchBarServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
            return;
        }
        final String query = request.getParameter("q");
        final LibroDAO libroService = new LibroDAO();
        final JSONArray jsonArray = new JSONArray();

        if (query != null && !query.trim().isEmpty()) {
            List<Libro> results = libroService.Search(query);
            if (results != null && !results.isEmpty()) {
                // cache size and limit to 10 to avoid repeated size() calls
                final int n = Math.min(results.size(), 10);
                for (int i = 0; i < n; ++i) {
                    final JSONObject jsonObject = new JSONObject();
                    final Libro r = results.get(i);
                    jsonObject.put("isbn", r.getIsbn());
                    jsonObject.put("titolo", r.getTitolo());
                    jsonArray.add(jsonObject);
                }
            }
        }

        // Impostare il tipo di contenuto della risposta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Inviare la risposta JSON al client
        final PrintWriter out = response.getWriter();
        out.print(jsonArray);
        out.flush();


        // Simula una ricerca nei dati
        /*List<Libro> results = new ArrayList<>();
        if (query != null && !query.trim().isEmpty()) {
            List<Libro> risultati = libroService.Search(query);
            for (int i = 0; i< risultati.size() || i < 10; i++) {
                results.add(risultati.get(i));
            }
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        if (!results.isEmpty()) {
            for (String result : results) {
                out.println("<div class='search-result-item'>" + result + "</div>");
            }
        } else {
            out.println("<div class='search-result-item'>Nessun risultato trovato</div>");
        }*/
    }
}
