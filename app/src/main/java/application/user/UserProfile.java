package application.user;

/**
 * Classe che contiene tutte le informazioni di un utente
 */

public class UserProfile {

    String email;
    String password;
    String nome;
    String cognome;
    String data_nascita;
    String luogo_nascita;
    String provincia;
    String stato;
    String telefono;
    String sesso;
    String cod_fis;

    //classe costruttore di un'istanza di un profilo di un utente
    public UserProfile(String email, String password, String nome, String cognome, String data_nascita, String luogo_nascita, String provincia,
                       String stato, String telefono, String sesso, String cod_fis){

        this.email = email;
        this.password = password;
        this.nome = nome;
        this.cognome = cognome;
        this.data_nascita = data_nascita;
        this.luogo_nascita = luogo_nascita;
        this.provincia = provincia;
        this.stato = stato;
        this.telefono = telefono;
        this.sesso = sesso;
        this.cod_fis = cod_fis;
    }

    public UserProfile(String email, String nome, String cognome){

        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
    }

    /**
     * getters and setters
     *
     */
    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public String getData_nascita() {
        return data_nascita;
    }

    public String getLuogo_nascita() {
        return luogo_nascita;
    }

    public String getProvincia() {
        return provincia;
    }

    public String getStato() {
        return stato;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getSesso() {
        return sesso;
    }

    public String getCod_fis() {
        return cod_fis;
    }
}
