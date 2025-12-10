package controller.utente;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.tesseraService.Tessera;
import model.tesseraService.TesseraDAO;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Random;

@WebServlet("/cambia-tipo")
public class ModificaTipoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final HttpSession session = request.getSession();
        final Utente utente = (Utente) session.getAttribute("utente");
        final TesseraDAO tesseraService = new TesseraDAO();
        final UtenteDAO utenteDAO = new UtenteDAO();
        if(utente.getTipo().equalsIgnoreCase("premium")){
            utente.setTipo("Standard");
            final String numero = tesseraService.doRetrieveByEmail(utente.getEmail()).getNumero();
            tesseraService.deleteTessera(numero);

        }else if(utente.getTipo().equalsIgnoreCase("standard")){
            utente.setTipo("Premium");
            final Tessera tessera = new Tessera();
            tessera.setEmail(utente.getEmail());
            tessera.setDataCreazione(LocalDate.now());
            tessera.setDataScadenza(LocalDate.now().plusYears(2));
            final List<String> numeri = tesseraService.doRetrivedAllByNumero();
            String numero;
            final Random random =new Random();
            do {
                numero = "T" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
            }while(numeri.contains(numero));
            tessera.setNumero(numero);

            tesseraService.doSave(tessera);
        }

        utenteDAO.updateUtente(utente); //cambio nel db i parametri cambiati
        response.sendRedirect("area-personale");
    }
    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
