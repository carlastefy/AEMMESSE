package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Autore;
import model.libroService.Libro;
import model.libroService.LibroDAO;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/insert-libro")
public class NuovoLibroServlet extends HttpServlet {
    private LibroDAO libroDAO = new LibroDAO();

    public void setLibroDAO(LibroDAO libroDAO){
        this.libroDAO = libroDAO;
    }

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final Libro libro = new Libro();
        final LibroDAO libroService = libroDAO;

        final String isbn = request.getParameter("isbn");
        final String titolo = request.getParameter("titolo");
        final String genere = request.getParameter("genere");
        final String annoPubblicazioni = request.getParameter("annoPubb");
        final String price=request.getParameter("prezzo");
        final String sconto1 = request.getParameter("sconto");
        final String trama = request.getParameter("trama");
        final String immagine = request.getParameter("immagine");

        if(isbn == null || isbn.length() != 13 || titolo == null || titolo.isEmpty() || genere == null || genere.isEmpty() ||
                annoPubblicazioni == null || annoPubblicazioni.isEmpty() || !isAnnoPubblicazioneValid(annoPubblicazioni) ||
                price == null || price.isEmpty() ||
                sconto1 == null || trama == null || immagine == null){
                final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                dispatcher.forward(request, response);
        }else {
            try {
                final double prezzo = Double.parseDouble(price);
                int sconto = 0;
                if (!sconto1.isEmpty() && isScontoValid(sconto1) && prezzo > 0) {
                    sconto = Integer.parseInt(sconto1);
                }else{
                    final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                    dispatcher.forward(request, response);
                }

                final String[] nomiAutori = request.getParameterValues("nome");
                final String[] cognomiAutori = request.getParameterValues("cognome");
                final String[] cfAutori = request.getParameterValues("cf");

                final List<Autore> autori = new ArrayList<>();

                if (nomiAutori != null && cognomiAutori != null && cfAutori != null) {
                    for (int i = 0; i < nomiAutori.length; ++i) {
                        if (nomiAutori[i].isEmpty() || cognomiAutori[i].isEmpty() || cfAutori[i].isEmpty()) {
                            final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                            dispatcher.forward(request, response);
                        }
                        final Autore autore = new Autore();
                        autore.setNome(nomiAutori[i]);
                        autore.setCognome(cognomiAutori[i]);
                        autore.setCf(cfAutori[i]);
                        autori.add(autore);
                    }
                }

                libro.setIsbn(isbn);
                libro.setTitolo(titolo);
                libro.setGenere(genere);
                libro.setAnnoPubblicazioni(annoPubblicazioni);
                libro.setPrezzo(prezzo);
                libro.setSconto(sconto);
                libro.setTrama(trama);
                libro.setImmagine(immagine);
                libro.setDisponibile(true);
                libro.setAutori(autori);

                final List<Libro> libri = libroService.doRetriveAll();
                boolean flag = true;
                for(final Libro l: libri) {
                    if(l.getIsbn().equals(isbn)) {
                        request.setAttribute("esito", "non riuscito");//per poter mostrare un errore nell'inserimento
                        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/prodotti/nuovoProdotto.jsp");
                        dispatcher.forward(request, response);
                        flag = false;
                    }
                }
                if(flag) {
                    libroService.doSave(libro);
                    response.sendRedirect("gestisci-prodotti");
                }

            } catch (final NumberFormatException e) {
                final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/errorJsp/erroreForm.jsp");
                dispatcher.forward(request, response);
            }
        }
    }

    public boolean isScontoValid(final String str){
        if(str.matches("\\d+")){
            final int strInt = Integer.parseInt(str);
            return strInt > 0 && strInt <= 100;
        }
        return false;
    }

    public boolean isAnnoPubblicazioneValid(final String str){
        if(str.matches("\\d+")){
            final int strInt = Integer.parseInt(str);
            return strInt <= LocalDateTime.now().getYear() && strInt > 0;
        }
        return false;
    }

    @Override
    protected void doPost(final HttpServletRequest req, final HttpServletResponse resp) throws ServletException, IOException {
        this.doGet(req, resp);
    }
}
