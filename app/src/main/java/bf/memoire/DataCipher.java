package bf.memoire;
import android.security.keystore.*;
import android.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.nio.charset.StandardCharsets;

final class DataCipher {
    static final String ALIAS="memoire-notes-v1";
    static synchronized SecretKey key()throws Exception {
        KeyStore store=KeyStore.getInstance("AndroidKeyStore");store.load(null);if(store.containsAlias(ALIAS))return (SecretKey)store.getKey(ALIAS,null);
        KeyGenerator generator=KeyGenerator.getInstance("AES","AndroidKeyStore");generator.init(new KeyGenParameterSpec.Builder(ALIAS,KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT).setBlockModes(KeyProperties.BLOCK_MODE_GCM).setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE).setRandomizedEncryptionRequired(true).build());return generator.generateKey();
    }
    static String seal(String text)throws Exception {Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,key());byte[] iv=cipher.getIV(),payload=cipher.doFinal(text.getBytes(StandardCharsets.UTF_8));byte[] encoded=new byte[iv.length+payload.length];System.arraycopy(iv,0,encoded,0,iv.length);System.arraycopy(payload,0,encoded,iv.length,payload.length);return "enc1:"+Base64.encodeToString(encoded,Base64.NO_WRAP);}
    static String open(String text)throws Exception {if(!text.startsWith("enc1:"))return text;byte[] encoded=Base64.decode(text.substring(5),Base64.NO_WRAP);if(encoded.length<28)throw new GeneralSecurityException("Données chiffrées incomplètes");Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,key(),new GCMParameterSpec(128,encoded,0,12));return new String(cipher.doFinal(encoded,12,encoded.length-12),StandardCharsets.UTF_8);}
}
