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
import model.utenteService.Utente;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/aggiorna-carrello")
public class AggiornaCartServlet extends HttpServlet {
    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if(Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Carrello non trovato");
            return;
        }

        // JSON parser object to parse read JSON data from request
        final JSONParser jsonParser = new JSONParser();

        try (final InputStreamReader reader = new InputStreamReader(request.getInputStream())) {
            // Parse JSON data
            final Object obj = jsonParser.parse(reader);
            final JSONObject item = (JSONObject) obj;

            // Ottieni i valori da JSON
            final String isbn = (String) item.get("isbn");
            final long quantityLong = (long) item.get("quantity");
            final int quantity = (int) quantityLong;

            // Trova l'elemento del carrello corrispondente e aggiorna la quantità
            boolean itemFound = false;
            for (final RigaCarrello riga : carrello.getRigheCarrello()) {
                if (riga.getLibro().getIsbn().equals(isbn)) {
                    riga.setQuantita(quantity);
                    itemFound = true;
                    break;
                }
            }

            if (!itemFound) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Elemento non trovato nel carrello.");
                return;
            }


            // Update cart in session
            session.setAttribute("carrello", carrello);

            // Invia una risposta di successo
            response.setStatus(HttpServletResponse.SC_OK);


        } catch (final ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage()); // "Formato JSON non valido"
            e.printStackTrace();
        }




        /*HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Carrello non trovato");
            return;
        }

        // JSON parser object to parse read JSON data from request
        JSONParser jsonParser = new JSONParser();

        try (InputStreamReader reader = new InputStreamReader(request.getInputStream())) {
            // Parse JSON data
            Object obj = jsonParser.parse(reader);
            JSONArray cartData = (JSONArray) obj;
            int i=0;
            // Itera l'array JSON e aggiorna cart
            for (Object cartItem : cartData) {
                JSONObject item = (JSONObject) cartItem;
                String isbn = (String) item.get("isbn");
                System.out.println(isbn);
                // Ottieni la stringa dalla chiave "quantity" nell'oggetto JSON
                String quantityStr = (String) item.get("quantity");
                // Converte la stringa in un intero
                int quantity = Integer.parseInt(quantityStr);
                System.out.println(quantity);
                carrello.getRigheCarrello().get(i).setQuantita(quantity);
                i++;
            }

            // Update cart in session
            session.setAttribute("carrello", carrello);


            // Send success response

        } catch (ParseException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());//"Formato JSON non valido"
            e.printStackTrace();
        }*/
    }

        @Override
        protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
