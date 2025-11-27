package controller.admin.gestisciProdotti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/modifica-libro")
public class ModificaLibroServlet extends HttpServlet {

    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        final String isbn = request.getParameter("isbn");
        final LibroDAO service = new LibroDAO();
        final Libro libro= service.doRetrieveById(isbn);
        final SedeDAO sedeService = new SedeDAO();
        final RepartoDAO repartoService = new RepartoDAO();

        final List<Sede> sedi= service.getPresenzaSede(libro.getIsbn());
        final List<Sede> sediNonPresenti = sedeService.doRetrivedAll();
        final int n= sediNonPresenti.size();
        for(int i=0; i<n; ++i){
            final Sede sede=sediNonPresenti.get(i);
            if(sedi.contains(sede)) {
                sediNonPresenti.remove(i);
            }
        }


        final List<Reparto> reparti= service.getAppartenenzaReparto(libro.getIsbn());
        final List<Reparto> repartiNonPresenti = repartoService.doRetrivedAll();
        final int m= repartiNonPresenti.size();
        for(int i=0; i<m; ++i){
            final Reparto reparto=repartiNonPresenti.get(i);
            if(reparti.contains(reparto)) {
                repartiNonPresenti.remove(i);
            }
        }

        request.setAttribute("libro", libro);
        request.setAttribute("sedi", sedi);
        request.setAttribute("reparti", reparti);
        request.setAttribute("sediNonPresenti", sediNonPresenti);
        request.setAttribute("repartiNonPresenti", repartiNonPresenti);

        final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/prodotti/modificaLibro.jsp");
        dispatcher.forward(request, response);
    }

    public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
