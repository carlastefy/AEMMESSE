package model.libroService;

import model.ConPool;
import model.carrelloService.RigaCarrello;
import model.gestoreService.Gestore;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SedeDAO {
    public void doSave(final Sede sede){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO sede (citta, via, numeroCivico, cap) VALUES(?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, sede.getCitta());
            ps.setString(2, sede.getVia());
            ps.setInt(3, sede.getCivico());
            ps.setString(4, sede.getCap());
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            final ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            final int id = rs.getInt(1);
            sede.setIdSede(id);
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteSede(final int idSede){
        try (final Connection con = ConPool.getConnection()) {
            //prima cancello da appartenenza (che ha riferimenti a reparto) solo se ci sono elementi
            final List<Libro> l = this.getPresenza(idSede);
            final Sede s = this.doRetrieveById(idSede);
            if (l!=null && !l.isEmpty()) {
                final PreparedStatement ps =
                        con.prepareStatement("DELETE FROM presenza WHERE idSede=?");
                ps.setInt(1, idSede);
                s.setLibri(null);
                if(ps.executeUpdate() < 1)
                    throw new RuntimeException("DELETE error from appartenenza.");
            }
            //poi elimino il reparto in questione
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM sede WHERE idSede=?");
            ps.setInt(1, idSede);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void updateSede(final Sede sede){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("UPDATE sede SET citta = ?, via = ?, numeroCivico = ?, cap = ? WHERE idSede = ?");
            ps.setString(1, sede.getCitta());
            ps.setString(2, sede.getVia());
            ps.setInt(3, sede.getCivico());
            ps.setString(4, sede.getCap());
            ps.setInt(5, sede.getIdSede());
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("UPDATE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void removeLibroSede(final int idSede, final String isbn){
        try(final Connection con = ConPool.getConnection()){
            final PreparedStatement ps = con.prepareStatement("DELETE FROM presenza WHERE idSede=? AND isbn = ?");
            ps.setInt(1,idSede);
            ps.setString(2, isbn);

            final Sede p = this.doRetrieveById(idSede);
            final LibroDAO libroService = new LibroDAO();
            final Libro l = libroService.doRetrieveById(isbn);
            p.getLibri().remove(l); //ho tolto il contains perchè credo lo faccia da solo.

            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void addLibroSede(final Sede sede, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO presenza (idSede, isbn) VALUES(?, ?)");
            ps.setInt(1, sede.getIdSede());
            ps.setString(2, isbn);

            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
            final LibroDAO libroService = new LibroDAO();
            sede.getLibri().add(libroService.doRetrieveById(isbn));

        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Sede> doRetrivedAll(){
        final List<Sede> sedi = new ArrayList<>();
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idSede, citta, via, numeroCivico, cap FROM sede");

            final ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                final Sede p = new Sede();
                p.setIdSede(rs.getInt(1));
                p.setCitta(rs.getString(2));
                p.setVia(rs.getString(3));
                p.setCivico(rs.getInt(4));
                p.setCap(rs.getString(5));
                p.setLibri(this.getPresenza(p.getIdSede()));
                sedi.add(p);
            }
            return sedi;
        } catch(final SQLException e){
            throw new RuntimeException(e);
        }
    }

    public Sede doRetrieveById(final int idSede) {
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT idSede, citta, via, numeroCIvico, cap FROM sede WHERE idSede=?");
            ps.setInt(1, idSede);
            final ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                final Sede p = new Sede();
                p.setIdSede(rs.getInt(1));
                p.setCitta(rs.getString(2));
                p.setVia(rs.getString(3));
                p.setCivico(rs.getInt(4));
                p.setCap(rs.getString(5));
                p.setLibri(this.getPresenza(p.getIdSede()));
                return p;
            }
            return null;
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Libro> getPresenza(final int idSede){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("SELECT isbn FROM presenza WHERE idSede=?");
            ps.setInt(1, idSede);
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
    public void deleteFromPresenzaLibro(final int idSede, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps =
                    con.prepareStatement("DELETE FROM presenza WHERE idSede=? AND isbn=?");
            ps.setInt(1, idSede);
            ps.setString(2, isbn);
            if(ps.executeUpdate() != 1)
                throw new RuntimeException("DELETE error.");
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void doSavePresenza(final int idSede, final String isbn){
        try (final Connection con = ConPool.getConnection()) {
            final PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO presenza (idSede, isbn) VALUES(?,?)");
            ps.setInt(1, idSede);
            ps.setString(2, isbn);
            if (ps.executeUpdate() != 1) {
                throw new RuntimeException("INSERT error.");
            }
        } catch (final SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
