package controller.utente.ordine;

import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ordineService.Ordine;
import model.tesseraService.TesseraDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.time.LocalDate;

@WebServlet("/pagamento-effettuato")
public class PagamentoEffettuato extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        if (Validator.checkIfUserAdmin(utente)) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }
        final TesseraDAO tesseraDAO = new TesseraDAO();
        final Ordine ordine = new Ordine();
        String address = null;
        //sto salvando sempre sulla request questi parametri poichè li devo mantenere fino a salvataggio ordine
        //dubbio sul metterlo direttamente in sessione...anche se non credo sia giusto salvarlo in sessione.
        ordine.setCitta(request.getParameter("citta"));
        ordine.setIndirizzoSpedizione(request.getParameter("indirizzo"));
        ordine.setCosto(Double.parseDouble(request.getParameter("costo")));

        int punti = 0;
        if(utente.getTipo().equalsIgnoreCase("premium")){
            final String puntiString = request.getParameter("punti");

            if(isNumeric(puntiString)) {
                punti = Integer.parseInt(puntiString);
                if (punti < 0 || punti > tesseraDAO.doRetrieveByEmail(utente.getEmail()).getPunti()) {
                    final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                    dispatcher.forward(request, response);
                    return;
                }
            }else if(!(puntiString.isEmpty())){
                final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                dispatcher.forward(request, response);
                return;
            }


        }
        ordine.setPuntiSpesi(punti);

        //effettuare controlli su dati dell'utente che acquista
        final String cardName = request.getParameter("cardName");
        final String cardNumber = request.getParameter("cardNumber");
        final String expiryDate = request.getParameter("expiryDate");
        final String cvv = request.getParameter("cvv");

        if(cardName==null || cardName.isEmpty() || !isNumeric(cardNumber) || expiryDate==null || /*isValidDate(expiryDate) ||*/ !isNumeric(cvv)){
            //pagina di errore per inserimento parametri errato
            address = "/WEB-INF/errorJsp/erroreForm.jsp";
            //response.sendRedirect("/WEB-INF/errorJsp/erroreForm.jsp");//forse

        }else{
            address = "/WEB-INF/results/ordineEffettuato.jsp";
            request.setAttribute("ordine", ordine);
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);
    }

    private static boolean isNumeric(final String str) {//metodo che utilizza espressione regolare per verificare che una stringa contenga solo numeri
        return str != null && !str.isEmpty() && str.matches("\\d+");
    }

    private boolean isValidDate(final String dateStr) {
        try {
            LocalDate.parse(dateStr); // Prova a fare il parsing della stringa come LocalDate
            return true;
        }catch(final Exception e){
            return false; // Se l'eccezione viene lanciata, la stringa non è una data valida
        }
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}