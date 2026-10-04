package bf.memoire;
import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.security.*;
import java.util.*;

final class SecureArchive {
    static final byte[] MAGIC="MEMOIRE1".getBytes(java.nio.charset.StandardCharsets.US_ASCII);
    static SecretKey key(char[] password,byte[] salt)throws Exception {
        PBEKeySpec spec=new PBEKeySpec(password,salt,150000,256);
        try{return new SecretKeySpec(SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded(),"AES");}finally{spec.clearPassword();}
    }
    static void encrypt(File source,OutputStream destination,char[] password)throws Exception {
        byte[] salt=new byte[16],iv=new byte[12];SecureRandom random=new SecureRandom();random.nextBytes(salt);random.nextBytes(iv);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.ENCRYPT_MODE,key(password,salt),new GCMParameterSpec(128,iv));cipher.updateAAD(MAGIC);
        try(OutputStream raw=destination){raw.write(MAGIC);raw.write(salt);raw.write(iv);try(CipherOutputStream out=new CipherOutputStream(raw,cipher);InputStream in=new FileInputStream(source)){byte[] b=new byte[8192];int n;while((n=in.read(b))!=-1)out.write(b,0,n);}}
    }
    static boolean encrypted(InputStream source)throws IOException {byte[] header=new byte[8];int n=0,r;while(n<header.length&&(r=source.read(header,n,header.length-n))!=-1)n+=r;return Arrays.equals(header,MAGIC);}
    static void decrypt(InputStream source,File destination,char[] password)throws Exception {
        try(DataInputStream in=new DataInputStream(source);OutputStream out=new FileOutputStream(destination)){byte[] magic=new byte[8],salt=new byte[16],iv=new byte[12];in.readFully(magic);if(!Arrays.equals(magic,MAGIC))throw new IOException("Format chiffré incompatible");in.readFully(salt);in.readFully(iv);Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");cipher.init(Cipher.DECRYPT_MODE,key(password,salt),new GCMParameterSpec(128,iv));cipher.updateAAD(MAGIC);byte[] buffer=new byte[8192];int n;long size=0;while((n=in.read(buffer))!=-1){size+=n;if(size>1024L*1024*1024)throw new IOException("Archive trop volumineuse");byte[] decoded=cipher.update(buffer,0,n);if(decoded!=null)out.write(decoded);}out.write(cipher.doFinal());}catch(Exception e){destination.delete();throw e;}
    }
}
