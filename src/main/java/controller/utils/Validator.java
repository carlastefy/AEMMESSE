package controller.utils;

import model.utenteService.Utente;

public class Validator {
    public static boolean checkIfUserAdmin(final Utente utente){
        return utente != null && utente.getTipo().startsWith("Gestore");
    }
}
