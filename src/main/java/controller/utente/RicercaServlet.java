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

import java.io.IOException;
import java.util.List;
@WebServlet("/ricerca-servlet")
public class RicercaServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final String query = request.getParameter("q");
        final LibroDAO libroService = new LibroDAO();
        String address = null;
        if(query==null){
            address = "/WEB-INF/errorJsp/erroreForm.jsp";
        }
        else if(query.isEmpty()){
            address="index.html";
        }
        else {
            address="/WEB-INF/results/ricerca.jsp";
            /*final List<Libro> results = libroService.Search(query);
            request.setAttribute("results", results);
            request.setAttribute("q", query);*/
            // NEW: gestione paginazione
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

            final int pageSize = 15; // scegli tu: 12, 16, 24...
            final int offset = (page - 1) * pageSize;

            // NEW: numero totale risultati + pagina corrente
            final int totalResults = libroService.countSearch(query);
            final int totalPages = (int) Math.ceil((double) totalResults / pageSize);

            final List<Libro> results = libroService.searchPaged(query, offset, pageSize);

            request.setAttribute("results", results);
            request.setAttribute("q", query);
            request.setAttribute("page", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalResults", totalResults);
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);

    }
}
