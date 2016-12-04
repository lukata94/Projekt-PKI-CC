package zts;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;
import javax.crypto.spec.SecretKeySpec;
import javax.swing.JOptionPane;

import org.bouncycastle.jcajce.provider.digest.SHA3.DigestSHA3;

/**
 * Klasa generatora certyfikatow.
 * @author £ukasz DŸwigulski Rafa³ Sosnowski
 *
 */
public class Generator {

	private String uzytkownik;
	private Cipher koder;
	private PrivateKey prywatnyCC;
	private String path = "src/bin";
	
	/**
	 * Inicjalizator generatora.
	 * @param u przekazujemy nazwe uzytkownika
	 * @throws Exception
	 */
	public Generator(String u) throws Exception{
		uzytkownik = u;
		this.koder = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		prywatnyCC = wczytajPrywatnyKlucz();
	}
	
	/**
	 * Metoda wczytania z pliku prywatnego klucza CC.
	 * @return prywatny klucz CC
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeySpecException
	 */
	public PrivateKey wczytajPrywatnyKlucz() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException{
		File filePrivateKey = new File("src/bin/CCprivate.key");
		FileInputStream fis = new FileInputStream("src/bin/CCprivate.key");
		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
		fis.read(encodedPrivateKey);
		fis.close();
 
		KeyFactory keyFactory = KeyFactory.getInstance("RSA");
 
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
				encodedPrivateKey);
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		
		return privateKey;
	}
	
	/**
	 * Metoda generacji certyfikatu.
	 * @param h haslo przekazywane do szyfrowania klucza prywatnego
	 */
	public void GeneracjaCertyfikatu(String h) {
		try {
 
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
 
			keyGen.initialize(2048);
			KeyPair generatedKeyPair = keyGen.genKeyPair();
			
	        String hasloSHA3 = sha3(h);
			byte[] prywatnyklucz  = encryptPrivate(generatedKeyPair.getPrivate(), hasloSHA3);
			
			Certyfikat cert = new Certyfikat(generatedKeyPair.getPublic(), uzytkownik);
			
			SealedObject so = ZakodowanyUzytkownik(cert, prywatnyCC);
 
			ZapiszPrywatny(prywatnyklucz);
			ZapiszCertyfikat(so, cert);
 
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Funkcja szyfrujaca prywatny klucz uzytkownika.
	 * @param pk prywatny klucz wygenerowany dla uzytkownika
	 * @param haslo haslo ktore posluzy za klucz
	 * @return zaszyfrowane bajty klucza
	 */
	public byte[] encryptPrivate(final PrivateKey pk, final String haslo) { 
		try {
	        final Key key = generateKeyFromString(haslo); //bierzemy klucz zrobiony na podstawie hasla
	        final Cipher c = Cipher.getInstance("AES");
	        c.init(Cipher.ENCRYPT_MODE, key);
	        byte[] prywatnybajty = pk.getEncoded(); //prywatny klucz na bajty
	        final byte[] encValue = c.doFinal(prywatnybajty); //szyfrujemy
	        return encValue;
	    } catch(Exception ex) {
	    	JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
	        return null;
	    }
	}
	
	/**
	 * Funkcja hashujaca SHA3 wejsciowego stringa.
	 * @param input wejsciowy string
	 * @return polowa funkcji skrotu ktora posluzy do wygenerowania 192-bitowego klucza
	 */
    public String sha3(String input){
        String hash="";
        
        try {
          DigestSHA3 md=new DigestSHA3(256);
          md.update(input.getBytes("UTF-8"));
          byte[] mdbytes = md.digest();
          byte[] key = new byte[mdbytes.length /2];

          for(int I = 0; I < key.length; I++){
              // bierzemy polowe hashu
              key[I] = mdbytes[I];
          }         
          hash = bytesToHex(key);
        }
        catch (Exception e) {
          e.printStackTrace();
        }        
        return hash;
    }
    
    /**
     * Metoda zamieniajaca bajty na postac heksadecymalna.
     * @param bytes bajty do zamiany
     * @return string w postaci HEX
     */
    public String bytesToHex(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        for (byte byt : bytes) result.append(Integer.toString((byt & 0xff) + 0x100, 16).substring(1));
        return result.toString();
    }
 
    /**
     * Generacja klucza AES na podstawie wejsciowego stringa.
     * @param secKey string ktory posluzy do generacji klucza
     * @return klucz AES 192-bitowy
     * @throws Exception
     */
	private Key generateKeyFromString(final String secKey) throws Exception {
		Base64.Decoder dekoder = Base64.getDecoder();
	    final byte[] keyVal = dekoder.decode(secKey);
	    final Key key = new SecretKeySpec(keyVal, "AES");
	    return key;
	}

	/**
	 * Tworzymy zaszyfrowana wiadomosc zawierajaca hasz z bajtow klucza i nazwy uzytkownika z certyfikatu
	 * @param cer certyfikat do zakodowania
	 * @param key Klucz prywatny CC
	 * @return zakodowana wiadomosc
	 * @throws InvalidKeyException
	 */
	public SealedObject ZakodowanyUzytkownik(Certyfikat cer, PrivateKey key) throws InvalidKeyException{
        //inicjujemy szyfrowanie kluczem przekazanym
        koder.init(Cipher.ENCRYPT_MODE, key);    
        String hash="";
        
        SealedObject myEncryptedMessage = null;
        
        byte[] kluczBajty = cer.zwrocKlucz().getEncoded();
        byte[] uzytkownikBajty = cer.zwrocUzytkownika().getBytes();
        // tworzymy tablice ktora bedzie zawierala bajty klucza i uzytkownika
        byte[] destination = new byte[kluczBajty.length + uzytkownikBajty.length];
        System.arraycopy(kluczBajty, 0, destination, 0, kluczBajty.length);
        // kopiujemy bajty klucza i uzytkownika do naszej stworzonej specjalnej tablicy
        System.arraycopy(uzytkownikBajty, 0, destination, kluczBajty.length, uzytkownikBajty.length);
        
        try {
        	DigestSHA3 md=new DigestSHA3(256);
            md.update(destination);
            byte[] mdbytes = md.digest();
            hash = bytesToHex(mdbytes);
            
            myEncryptedMessage= new SealedObject(hash, koder); //zakodowana wiadomosc
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
        } catch (IllegalBlockSizeException ex) {
            JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                                    JOptionPane.ERROR_MESSAGE);
        }
        
        return myEncryptedMessage;
    }
	
	/**
	 * Metoda zapisu prywatnego klucza do pliku
	 * @param doZapisu bajty klucza do zapisu
	 */
	public void ZapiszPrywatny(byte[] doZapisu){
		FileOutputStream fos;
		try {
			fos = new FileOutputStream(path + "/" + uzytkownik + "private.key");
			fos.write(doZapisu);
			fos.close();
		} catch (FileNotFoundException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
		} catch (IOException ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Zapis certyfikatu, czyli obiektu certyfikat i jego zaszyfrowanego haszu (jako podpis)
	 * @param doZapisu zakodowana wiadomosc
	 * @param c certyfikat
	 */
	public void ZapiszCertyfikat(SealedObject doZapisu, Certyfikat c){
		ObjectOutputStream oos = null;
		FileOutputStream fout = null;
		try{
		    fout = new FileOutputStream(path + "/" + uzytkownik + ".cer");
		    oos = new ObjectOutputStream(fout);
		    oos.writeObject(doZapisu);
		    oos.writeObject(c);
		    
		    oos.close();
		} catch (Exception ex) {
			JOptionPane.showMessageDialog(null, ex.toString(), "Error",
                    JOptionPane.ERROR_MESSAGE);
		}
	}
}
