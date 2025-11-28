package controller.admin.gestisciReparti;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Reparto;
import model.libroService.RepartoDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/aggiungi-reparto")
public class AggiungiRepartoServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {
        final Reparto reparto = new Reparto();
        final String nome = request.getParameter("nome");
        final String descrizione = request.getParameter("descrizione");
        final String immagine = request.getParameter("immagine");
        if(nome==null || nome.isEmpty() || descrizione==null || descrizione.isEmpty() || immagine==null || immagine.isEmpty()){
            //pagina di errore per inserimento parametri errato
            response.sendRedirect("/WEB-INF/errorJsp/erroreForm.jsp");//forse
        }

        reparto.setDescrizione(descrizione);
        reparto.setNome(nome);
        reparto.setImmagine(immagine);

        final RepartoDAO repartoService = new RepartoDAO();
        final List<Reparto> reparti= repartoService.doRetrivedAll();
        boolean flag=true;
        for (final Reparto rep:reparti){
            if(rep.getNome().equals(reparto.getNome())){
                request.setAttribute("esito", "non riuscito");//per poter mostrare un errore nell'inserimento
                final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/reparti/aggiungiReparto.jsp");
                dispatcher.forward(request, response);
                flag=false;
            }
        }
        if(flag) {
            repartoService.doSave(reparto);
            response.sendRedirect("gestisci-reparti");
        }

    }
}
