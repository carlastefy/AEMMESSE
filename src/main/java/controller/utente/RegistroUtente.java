package controller.utente;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;
import model.carrelloService.Carrello;
import model.carrelloService.CarrelloDAO;
import model.tesseraService.Tessera;
import model.tesseraService.TesseraDAO;
import model.utenteService.Utente;
import model.utenteService.UtenteDAO;

@WebServlet(name = "insertUtente", value = "/insert-utente")
public class RegistroUtente extends HttpServlet{
    private UtenteDAO utenteDAO = new UtenteDAO();
    private TesseraDAO tesseraDAO = new TesseraDAO();
    public void setUtenteDAO(UtenteDAO utenteDAO){
        this.utenteDAO = utenteDAO;
    }
    public void setTesseraDAO(TesseraDAO tesseraDAO){
        this.tesseraDAO = tesseraDAO;
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final Utente utente = new Utente();
        final String nomeUtente = request.getParameter("nomeUtente");
        final String email = request.getParameter("email");
        final String codiceSicurezza = request.getParameter("pw");
        final String tipo = request.getParameter("tipo");
        String address = null;

        if (nomeUtente == null || nomeUtente.isEmpty() || (email == null || email.isEmpty() || !email.contains("@")) ||
                codiceSicurezza == null || codiceSicurezza.isEmpty()  || codiceSicurezza.length()>16 || tipo == null) {
            //pagina di errore per inserimento parametri errato
            address = "/WEB-INF/errorJsp/erroreForm.jsp";
        }else{
            final String[] numeriTelefono = request.getParameterValues("telefono");
            final List<String> telefoni = new ArrayList<>();
            for (final String telefono : numeriTelefono) {
                if (!isNumeric(telefono) || telefono.length() != 10) {
                    //pagina dai errore per inserimento parametri errato
                    address = "/WEB-INF/errorJsp/erroreForm.jsp";
                } else telefoni.add(telefono);
            }

            //se un telefono è già esistente nel DB va in errore
            final UtenteDAO utenteService = utenteDAO;

            final List<String> telefoniDB = utenteService.doRetrieveAllTelefoni();
            if(!telefoniDB.isEmpty()){
                for(final String telefono: telefoni){
                    if (telefoniDB.contains(telefono)) {
                        address = "/WEB-INF/errorJsp/erroreTelefonoDB.jsp";
                        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
                        dispatcher.forward(request, response);
                        return;
                    }
                }
            }

            utente.setNomeUtente(nomeUtente);
            utente.setEmail(email);
            utente.setTipo(tipo);
            utente.setCodiceSicurezza(codiceSicurezza);
            utente.setTelefoni(telefoni);

            final Utente utenteRetrieved = utenteService.doRetrieveById(utente.getEmail());
            if (utenteRetrieved == null) {
                utenteService.doSave(utente);
                if (utente.getTipo().equalsIgnoreCase("premium")) {
                    final TesseraDAO tesseraService = tesseraDAO;
                    final Tessera tessera = new Tessera();
                    tessera.setEmail(utente.getEmail());
                    tessera.setDataCreazione(LocalDate.now());
                    tessera.setDataScadenza(LocalDate.now().plusYears(2));
                    final List<String> numeri = tesseraService.doRetrivedAllByNumero();
                    String numT;
                    Random random = new Random();
                    do {
                        numT = "T" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
                    } while (numeri.contains(numT));
                    tessera.setNumero(numT);
                    tesseraService.doSave(tessera);
                }
                //request.getSession().setAttribute("utente", utente);
                final CarrelloDAO carrelloService = new CarrelloDAO();
                final Carrello carrello = new Carrello();
                carrello.setEmail(utente.getEmail());
                carrello.setTotale(0);
                final List<String> id = carrelloService.doRetrivedAllIdCarrelli();
                String newId;
                Random random = new Random();
                do {
                    char a = (char) (65 + random.nextInt(90 - 65));
                    char b = (char) (65 + random.nextInt(90 - 65));
                    char c = (char) (65 + random.nextInt(90 - 65));
                    newId = a + b + c + String.valueOf(10 + random.nextInt(100 - 10));
                } while (id.contains(newId));

                carrello.setIdCarrello(newId);
                carrelloService.doSave(carrello);

                if(address == null)
                  address = "/WEB-INF/results/login.jsp";

            } else {
                if(address == null)
                //pagina di errore nel caso l'email fosse già presente nel db
                    address = "/WEB-INF/errorJsp/utentePresente.jsp";

            }
        }

        final RequestDispatcher dispatcher = request.getRequestDispatcher(address);
        dispatcher.forward(request, response);

    }

    private static boolean isNumeric(final String str) {//metodo che utilizza espressione regolare per verificare che una stringa contenga solo numeri
        return str != null && !str.isEmpty() && str.matches("\\d+");
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}