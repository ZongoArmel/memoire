package bf.memoire;
import java.text.Normalizer;
import java.util.*;
final class SearchEngine {
    static String normalize(String text){return Normalizer.normalize(text,Normalizer.Form.NFD).replaceAll("\\p{M}","").toLowerCase(Locale.ROOT);}
    static boolean match(String text,String query){String source=normalize(text),q=normalize(query).trim();if(q.isEmpty())return true;String[] words=source.split("[^\\p{L}\\p{N}_-]+");for(String token:q.split("\\s+")){if(source.contains(token))continue;boolean found=false;if(token.length()>4)for(String word:words)if(oneEdit(word,token)){found=true;break;}if(!found)return false;}return true;}
    static boolean oneEdit(String a,String b){if(Math.abs(a.length()-b.length())>1)return false;int i=0,j=0,errors=0;while(i<a.length()&&j<b.length()){if(a.charAt(i)==b.charAt(j)){i++;j++;continue;}if(++errors>1)return false;if(a.length()>b.length())i++;else if(b.length()>a.length())j++;else{i++;j++;}}return errors+(a.length()-i)+(b.length()-j)<=1;}
}
