package controller.utente.ordine;


import controller.utils.Validator;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.carrelloService.Carrello;
import model.carrelloService.CarrelloDAO;
import model.carrelloService.RigaCarrello;
import model.carrelloService.RigaCarrelloDAO;
import model.gestoreService.Gestore;
import model.gestoreService.GestoreDAO;
import model.libroService.Libro;
import model.ordineService.Ordine;
import model.ordineService.OrdineDAO;
import model.ordineService.RigaOrdine;
import model.ordineService.RigaOrdineDAO;
import model.tesseraService.Tessera;
import model.tesseraService.TesseraDAO;
import model.utenteService.Utente;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@WebServlet("/do-ordine")
public class OrdineServlet extends HttpServlet {

    private OrdineDAO ordineDAO = new OrdineDAO();
    private CarrelloDAO carrelloDAO = new CarrelloDAO();

    private RigaCarrelloDAO rigaCarrelloDAO = new RigaCarrelloDAO();

    public void setRigaCarrelloDAO(RigaCarrelloDAO rigaCarrelloDAO) {
        this.rigaCarrelloDAO = rigaCarrelloDAO;
    }

    public void setCarrelloDAO(CarrelloDAO carrelloDAO) {
        this.carrelloDAO = carrelloDAO;
    }

    public void setOrdineDAO(OrdineDAO ordineDAO){
        this.ordineDAO = ordineDAO;
    }
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        //prendo ciò che ho in  sessione
        final HttpSession session= request.getSession();
        if(Validator.checkIfUserAdmin((Utente) session.getAttribute("utente"))) {
            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/homepageAdmin.jsp");
            dispatcher.forward(request, response);
        }

        //non faccio il controllo poichè lo faccio negli step prima...quindi se non ci fossero righe non dovrebbe
        //proprio arrivare a questo punto.
        final List<RigaCarrello> righeCarrello = (List<RigaCarrello>) session.getAttribute("righeDisponibili");

        final Carrello carrello = (Carrello) session.getAttribute("carrello");
        final Utente utente = (Utente) session.getAttribute("utente");

        //credo gli oggetti che mi serviranno e i relativi services
        final Ordine ordine = new Ordine();
        //RigaOrdineDAO rigaOrdineDAO = new RigaOrdineDAO();
        final TesseraDAO tesseraDAO = new TesseraDAO();
        final Carrello carrelloDB = carrelloDAO.doRetriveByUtente(utente.getEmail());

        //parametri passati da servlet a jsp...
        ordine.setIndirizzoSpedizione(request.getParameter("indirizzo"));
        ordine.setCitta(request.getParameter("citta"));

       // if(utente.getTipo().equalsIgnoreCase("premium")){
        //setto i punti, se è un utente standard sono a 0
            final String puntiString = request.getParameter("punti");
            int punti = 0;
            if(puntiString != null && !puntiString.isEmpty())
                punti = Integer.parseInt(puntiString);
            ordine.setPuntiSpesi(punti);
       // }



        //parametri calcolati
        double costo = 0;
        int puntiAcquisiti = 0;
        //creo idOrdine
        final List<String> idOrdini = ordineDAO.doRetrivedAllByIdOrdini(); //mock
        String idOrdine;
        final Random random =new Random();
        do {
            idOrdine = "T" + random.nextInt(10) + random.nextInt(10) + random.nextInt(10);
        }while(idOrdini.contains(idOrdine));
        ordine.setIdOrdine(idOrdine);


        final List<RigaOrdine> righeOrdine = new ArrayList<>();
        //prendo i libri selezionati dalle righe carrello e calcolo il costo e i punti

        for(final RigaCarrello rigaCarrello : righeCarrello){
            final Libro l = rigaCarrello.getLibro();
            final RigaOrdine riga = new RigaOrdine();
            riga.setIdOrdine(ordine.getIdOrdine());
            riga.setLibro(l);

            final double prezzoUnitarioPrima = (l.getPrezzo() - (l.getPrezzo()*l.getSconto()/100));
            final BigDecimal bd = new BigDecimal(prezzoUnitarioPrima).setScale(2, RoundingMode.HALF_UP);
            final double prezzoUnitario = bd.doubleValue();
            riga.setPrezzoUnitario(prezzoUnitario);
            riga.setQuantita(rigaCarrello.getQuantita());

        //    rigaOrdineDAO.doSave(riga); //salvo nel db la riga ordine
            righeOrdine.add(riga);

            costo += ((rigaCarrello.getQuantita() * prezzoUnitario) /*- (ordine.getPuntiSpesi() * 0.10)*/); //tolgo
            if(utente.getTipo().equalsIgnoreCase("premium"))
                puntiAcquisiti += 5* rigaCarrello.getQuantita();


            //quando scorro la lista delle righe del carrello che voglio acquistare
            //devo eventualmente cancellare la riga in sessione e nel db, o settare la quantità.
            int sizeRigheCarrello = carrelloDB.getRigheCarrello().size();//prima nel ciclo c'era i< della chaimata a funzione
            for(int i=0; i<sizeRigheCarrello; ++i){//creedengo preferisce ++i invece i++
                final RigaCarrello rc = carrelloDB.getRigheCarrello().get(i);
                final Libro libroRC = rc.getLibro();
                if(libroRC.equals(l)){
                    final int differenza = rc.getQuantita() - rigaCarrello.getQuantita();
                    if (differenza <= 0){
                        rigaCarrelloDAO.deleteRigaCarrello(l.getIsbn(), carrelloDB.getIdCarrello());

                    }
                }
            }
            sizeRigheCarrello = carrello.getRigheCarrello().size();
            // rimuovi in modo sicuro usando un iterator per evitare problemi di indice
            java.util.Iterator<RigaCarrello> itSession = carrello.getRigheCarrello().iterator();
            while (itSession.hasNext()) {
                final RigaCarrello rigaInSessione = itSession.next();
                if (rigaInSessione.getLibro().equals(l)) {
                    itSession.remove();
                    break;
                }
            }

        }

        ordine.setPuntiOttenuti(puntiAcquisiti);

        //aggiorno tessera
       if(utente.getTipo().equalsIgnoreCase("premium")){
           final Tessera tessera = tesseraDAO.doRetrieveByEmail(utente.getEmail());
           if(tessera.getDataScadenza().isAfter(LocalDate.now())){
               tessera.setPunti(tessera.getPunti() - ordine.getPuntiSpesi() + ordine.getPuntiOttenuti());
                tesseraDAO.updateTessera(tessera);
            } else {
                ordine.setPuntiSpesi(0); //non può spendere punti poichè la tessera è scaduta.
            }
       }

        // ordine.setCosto(Double.parseDouble(request.getParameter("costo")));
        final double costoAggiornato=costo - (ordine.getPuntiSpesi() * 0.10);
        final BigDecimal bd = new BigDecimal(costoAggiornato).setScale(2, RoundingMode.HALF_UP);
        final double costoArrotondato = bd.doubleValue();
        ordine.setCosto(costoArrotondato); //lo ricalcolo per sicurezza
            //utente che effettua l'ordine
        ordine.setEmail(utente.getEmail());
            //quando si fa l'ordine: nel momento di invocazione della servlet
        ordine.setDataEffettuazione(LocalDate.now());
            //righeOrdine
        ordine.setRigheOrdine(righeOrdine);

        ordine.setStato("In Lavorazione");
        final GestoreDAO gestoreDAO = new GestoreDAO();
        final Random rand = new Random();
        final List<Gestore> gestoriDispo = gestoreDAO.doRetrivedAll();
        ordine.setMatricola(gestoriDispo.get(rand.nextInt(gestoriDispo.size())).getMatricola());

        ordineDAO.doSave(ordine);

        response.sendRedirect("index.html");
    }
}

