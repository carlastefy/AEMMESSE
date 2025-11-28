package controller.admin.gestisciSedi;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.libroService.Sede;
import model.libroService.SedeDAO;

import java.io.IOException;
import java.util.List;

@WebServlet("/aggiungi-sede")
public class AggiungiSedeServlet extends HttpServlet {
    public void doGet(final HttpServletRequest request, final HttpServletResponse response) throws IOException, ServletException {

        final String citta = request.getParameter("citta");
        final String via = request.getParameter("via");
        final String civ = request.getParameter("civico");
        final String cap = request.getParameter("cap");
        //controllo paramentri del form
        if(citta==null || citta.length()==0 || via==null || via.length()==0|| civ==null || civ.length()==0 ||
                cap==null || cap.length()==0)
            response.sendRedirect("/WEB-INF/errorJsp/erroreForm.jsp");

        int civico;
        final Sede sede = new Sede();
        try {
            civico = Integer.parseInt(civ);
            sede.setCitta(citta);
            sede.setVia(via);
            sede.setCivico(civico);
            sede.setCap(cap);

            final SedeDAO sedeService = new SedeDAO();
            final List<Sede> sedi = sedeService.doRetrivedAll();
            boolean flag = true;
            for (final Sede s : sedi) {
                if (s.getCap().equals(sede.getCap()) && s.getCitta().equals(sede.getCitta()) && s.getVia().equals(sede.getVia())
                        && s.getCivico() == sede.getCivico()) {
                    request.setAttribute("esito", "non riuscito");//per poter mostrare un errore nell'inserimento
                    final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/results/admin/sedi/aggiungiSedi.jsp");
                    dispatcher.forward(request, response);
                    flag = false;
                }

            }
            if (flag){
                sedeService.doSave(sede);
            response.sendRedirect("gestisci-sedi");
            }

        }catch (final NumberFormatException e){
            response.sendRedirect("/WEB-INF/errorJsp/erroreForm.jsp");
        }








    }
}