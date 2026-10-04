package bf.memoire;
public class SearchEngineTest {
 public static void main(String[] args){if(!SearchEngine.match("Résoudre un problème réseau DHCP","reseau probleme"))throw new AssertionError();if(!SearchEngine.match("Configuration VLAN","configuratio"))throw new AssertionError();if(SearchEngine.match("Configuration VLAN","configuraxxx"))throw new AssertionError();if(SearchEngine.match("Routeur","serveur"))throw new AssertionError();if(!SearchEngine.match("",""))throw new AssertionError();System.out.println("5 search checks passed");}
}
