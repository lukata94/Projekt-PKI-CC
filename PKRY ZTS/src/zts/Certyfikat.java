package zts;

import java.io.Serializable;
import java.security.PublicKey;

import javax.crypto.SealedObject;

/**
 * Klasa przedstawiajaca certyfikat dla uzytkownika.
 * @author £ukasz DŸwigulski Rafa³ Sosnowski
 *
 */
public class Certyfikat implements Serializable{
	private PublicKey klucz;
	private String uzytkownik;
	
	/**
	 * Inicjalizacja certyfikatu.
	 * @param pk klucz publiczny zapisany w certyfikacie
	 * @param user uzytkownik zapisany w certyfikacie
	 */
	public Certyfikat(PublicKey pk, String user){
		klucz = pk;
		uzytkownik = user;
	}
	
	public String zwrocUzytkownika(){
		return this.uzytkownik;
	}
	
	public PublicKey zwrocKlucz(){
		return this.klucz;
	}
	
}
