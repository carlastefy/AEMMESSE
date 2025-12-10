package controller.utente;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.carrelloService.RigaCarrello;
import model.libroService.Libro;
import model.libroService.LibroDAO;
import model.utenteService.Utente;
import org.json.simple.JSONObject;

import java.io.IOException;
import java.util.List;

@WebServlet("/rimuovi-dal-carrello")
public class RimuoviDalCartServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isbn = request.getParameter("isbn");

        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if (Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
            return;
        }

        final Carrello carrello = (Carrello) session.getAttribute("carrello");

        final LibroDAO libroService = new LibroDAO();
        final Libro libro = libroService.doRetrieveById(isbn);

        boolean success = false;
        if (carrello != null) {
            List<RigaCarrello> righeCarrello = carrello.getRigheCarrello();
            if (righeCarrello == null) {
                righeCarrello = new java.util.ArrayList<>();
                carrello.setRigheCarrello(righeCarrello);
            }

            // usa un Iterator per rimuovere in modo sicuro senza chiamare size()
            final java.util.Iterator<RigaCarrello> it = righeCarrello.iterator();
            while (it.hasNext()) {
                final RigaCarrello riga = it.next();
                if (riga.getLibro() != null && riga.getLibro().equals(libro)) {
                    it.remove(); // rimuove la riga corrente in modo sicuro
                    success = true;
                }
            }
            // Crea una risposta JSON per indicare lo stato della rimozione
            final JSONObject jsonResponse = new JSONObject();
            jsonResponse.put("success", success);

            // Imposta il tipo di contenuto della risposta
            response.setContentType("application/json");

            // Invia la risposta JSON al client
            response.getWriter().write(jsonResponse.toJSONString());
        } else {
            // Se il carrello non esiste, restituisci un errore
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Il carrello non è stato trovato");
        }


    }
    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
