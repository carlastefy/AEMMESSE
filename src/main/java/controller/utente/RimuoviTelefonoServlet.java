package controller.utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@WebServlet("/rimuovi-telefono")
public class RimuoviTelefonoServlet extends HttpServlet {
    protected void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        // JSON parser object to parse read JSON data from request
        final JSONParser jsonParser = new JSONParser();
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        try (final InputStreamReader reader = new InputStreamReader(request.getInputStream())) {
            // Parse JSON data
            final Object obj = jsonParser.parse(reader);
            final JSONObject item = (JSONObject) obj;

            // Ottieni i valori da JSON
            final String email = (String) item.get("email");
            final String telefono = (String) item.get("telefono");

            // Chiamata alla funzione del DAO per rimuovere il telefono
            final UtenteDAO service = new UtenteDAO();
            final List<String> telefoni = service.cercaTelefoni(email);
            if (telefoni != null && telefoni.contains(telefono)) {
                service.deleteTelefono(email, telefono);
            }

            if (utente == null) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not logged in");
                return;
            }

            List<String> telefoniSession = utente.getTelefoni();
            if (telefoniSession == null) {
                telefoniSession = new java.util.ArrayList<>();
                utente.setTelefoni(telefoniSession);
            }

            // Usa un iterator per rimuovere in modo sicuro senza chiamare size()
            final java.util.Iterator<String> it = telefoniSession.iterator();
            while (it.hasNext()) {
                final String t = it.next();
                if (t != null && t.equals(telefono)) {
                    it.remove();
                    break;
                }
            }

            session.setAttribute("utente", utente);


            // Invia una risposta al client
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write("Telefono rimosso con successo.");
        } catch (final ParseException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Errore nella lettura del JSON.");
        }
    }
}