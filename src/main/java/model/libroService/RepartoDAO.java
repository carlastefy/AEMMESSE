package model.libroService;

import model.ConPool;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartoDAO {
    public void doSave(final Reparto reparto){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO reparto (nome,descrizione,immagine) VALUES(?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, reparto.getNome());
            ps.setString(2, reparto.getDescrizione());
            ps.setString(3, reparto.getImmagine());

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            final ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            final int id = rs.getInt(1);
            reparto.setIdReparto(id);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteReparto(final int idReparto){
        try (final Connection con = ConPool.getConnection()) {

            //prima cancello da appartenenza (che ha riferimenti a reparto) solo se ci sono elementi
            final List<Libro> l = this.getAppartenenza(idReparto);
            final Reparto r = this.doRetrieveById(idReparto);
            if (l!=null && !l.isEmpty()) {
                final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM appartenenza WHERE idReparto=?");
                ps.setInt(1, idReparto);
                r.setLibri(null);
                if(ps.executeUpdate() < 1)
                    throw new RuntimeException("DELETE error from appartenenza.");
            }
            //poi elimino il reparto in questione
            final PreparedStatement ps = con.prepareStatement("DELETE FROM reparto WHERE idReparto=?");
            ps.setInt(1, idReparto);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error from reparto.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateReparto(final Reparto reparto){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE reparto SET descrizione = ?, immagine = ? WHERE idReparto = ?");
            ps.setString(1, reparto.getDescrizione());
            ps.setString(2, reparto.getImmagine());
            ps.setInt(3, reparto.getIdReparto());

            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeLibroReparto(final int idReparto, final String isbn){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("DELETE FROM appartenenza WHERE idReparto=? AND isbn = ?");
            ps.setInt(1,idReparto);
            ps.setString(2, isbn);

            final Reparto p = this.doRetrieveById(idReparto);
            final LibroDAO libroService = new LibroDAO();
            final Libro l = libroService.doRetrieveById(isbn);
            p.getLibri().remove(l); //ho tolto il contains perchè credo lo faccia da solo.

            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }
    public void aggiungiLibroReparto(final Reparto reparto, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO appartenenza (idReparto, isbn) VALUES(?, ?)");
            ps.setInt(1, reparto.getIdReparto());
            ps.setString(2, isbn);

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            final LibroDAO libroService = new LibroDAO();
            reparto.getLibri().add(libroService.doRetrieveById(isbn));

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Reparto> doRetrivedAll(){
        final List<Reparto> reparti = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idReparto, nome, descrizione, immagine FROM reparto");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Reparto p = new Reparto();
                p.setIdReparto(rs.getInt(1));
                p.setNome(rs.getString(2));
                p.setDescrizione(rs.getString(3));
                p.setImmagine(rs.getString(4));
                p.setLibri(this.getAppartenenza(p.getIdReparto()));
                reparti.add(p);
            }
            return reparti;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Reparto doRetrieveById(final int idReparto) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idReparto, nome, descrizione, immagine FROM reparto WHERE idReparto=?");
            ps.setInt(1, idReparto);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Reparto p = new Reparto();
                p.setIdReparto(rs.getInt(1));
                p.setNome(rs.getString(2));
                p.setDescrizione(rs.getString(3));
                p.setImmagine(rs.getString(4));
                p.setLibri(this.getAppartenenza(idReparto));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Libro> getAppartenenza(final int idReparto){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT isbn FROM appartenenza WHERE idReparto=?");
            ps.setInt(1, idReparto);
            final List<Libro> lista=new ArrayList<>();
            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final String isbn = rs.getString(1);
                final LibroDAO p = new LibroDAO();
                final Libro libro=p.doRetrieveById(isbn);
                lista.add(libro);
            }
            return lista;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void deleteFromAppartenenzaLibro(final int idReparto, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM appartenenza WHERE idReparto=? AND isbn=?");
            ps.setInt(1, idReparto);
            ps.setString(2, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSaveAppartenenza(final int idReparto, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO appartenenza (idReparto, isbn) VALUES(?,?)");
            ps.setInt(1, idReparto);
            ps.setString(2, isbn);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
