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
import model.wishList.WishList;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

@WebServlet("/aggiungi-ai-preferiti")
public class AggiungiAiPrefServlet extends HttpServlet {

    //Nuova servlet per aggiornare i preferiti (creata per utilizzare AJAX)
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isbn = request.getParameter("isbn"); // Ottieni l'ISBN dal parametro della richiesta
        final LibroDAO libroService = new LibroDAO();
        final Libro libro = libroService.doRetrieveById(isbn);
        final HttpSession session = request.getSession();
        final Utente utente= (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        } else if(utente==null){
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
        }
        else {
            final JSONObject jsonResponse = new JSONObject();
            //boolean
            jsonResponse.put("isInWishList", true); // Indica se il libro è ora nei preferiti

            // Aggiungi l'ISBN alla WishList (o al database)
            WishList wishList = (WishList) session.getAttribute("wishList");
            if (wishList == null) {
                wishList = new WishList();
                wishList.setLibri(new ArrayList<>());
            }

            java.util.List<Libro> libri = wishList.getLibri();
            if (libri == null) {
                libri = new ArrayList<>();
                wishList.setLibri(libri);
            }

            // rimuovo la prima occorrenza se presente, altrimenti aggiungo
            final boolean removed = libri.remove(libro);
            if (removed) {
                jsonResponse.put("isInWishList", false); // Indica che il libro non è più nei preferiti
            } else {
                libri.add(libro);
                jsonResponse.put("isInWishList", true);
            }

            session.setAttribute("wishList", wishList);
            System.out.println(wishList.getLibri());

            // Invia una risposta al client (ad esempio, un oggetto JSON)
            response.setContentType("application/json");
            response.getWriter().write(jsonResponse.toString());
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}

