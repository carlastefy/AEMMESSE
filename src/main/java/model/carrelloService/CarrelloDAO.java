package model.carrelloService;

import model.ConPool;
import model.gestoreService.Gestore;
import model.tesseraService.Tessera;
import model.utenteService.Utente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CarrelloDAO {
    public void doSave(final Carrello carrello){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO carrello (idCarrello, totale, email) VALUES(?,?,?)");
            ps.setString(1, carrello.getIdCarrello());
            ps.setDouble(2, carrello.getTotale());
            ps.setString(3, carrello.getEmail());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteCarrello(final String idCarrello){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM carrello WHERE idCarrello=?");
            ps.setString(1, idCarrello);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateCarrello(final Carrello carrello){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE carrello SET totale = ? WHERE idCarrello = ?");
            ps.setDouble(1, carrello.getTotale());
            ps.setString(2, carrello.getIdCarrello());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public Carrello doRetriveById(final String idCarrello){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idCarrello, totale, email FROM carrello WHERE idCarrello=?");
            ps.setString(1, idCarrello);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Carrello carrello = new Carrello();
                final RigaCarrelloDAO rigaService = new RigaCarrelloDAO();
                carrello.setIdCarrello(idCarrello);
                carrello.setTotale(rs.getDouble(2));
                carrello.setEmail(rs.getString(3));
                carrello.setRigheCarrello(rigaService.doRetrieveByIdCarrello(idCarrello));
                return carrello;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public Carrello doRetriveByUtente(final String email){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idCarrello, totale, email FROM carrello WHERE email=?");
            ps.setString(1, email);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Carrello carrello = new Carrello();
                final RigaCarrelloDAO rigaService = new RigaCarrelloDAO();
                carrello.setIdCarrello(rs.getString(1));
                carrello.setTotale(rs.getDouble(2));
                carrello.setEmail(rs.getString(3));
                carrello.setRigheCarrello(rigaService.doRetrieveByIdCarrello(carrello.getIdCarrello()));
                return carrello;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public List<String> doRetrivedAllIdCarrelli(){
        final List<String>  idCarrello = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idCarrello, totale, email FROM carrello");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                idCarrello.add(rs.getString(1));
            }
            return idCarrello;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }
}
