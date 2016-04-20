package knarp;

import java.util.LinkedList;
import java.util.Random;
import java.util.Vector;

/* Classe contenant une liste d'adresse (émetteur et récepteurs) */
public class AdressList {
    private static final int MIN_ADDRESSES = 3;
    private String _sender;
    private LinkedList<String> _receivers;

    /* Constructeurs */
    public AdressList(){
        _receivers = new LinkedList();
        _sender = null;
    }

    public AdressList(String sender){
        _sender = sender;
        _receivers = new LinkedList();
    }

    public AdressList(String sender, String[] receivers){
        _sender = sender;
        _receivers = new LinkedList();

        for(String receiver: receivers){
            _receivers.add(receiver);
        }
    }

    /* Ajout d'un récepteur */
    public void addReceiver(String receiver){
        _receivers.add(receiver);
    }

    /* Obtention de la liste des récepteurs */
    public LinkedList<String> getReceivers(){
        return _receivers;
    }

    /* Ajout d'un émetteur */
    public String getSender(){
        return _sender;
    }

    /* Obtention de l'émetteur */
    public void setSender(String sender){
        _sender = sender;
    }

    /* Affichage de la liste */
    public void show(){
        System.out.println("Sender:"+_sender);
        System.out.println("Receivers:");
        for(String receiver: _receivers){
            System.out.println(" - " + receiver);
        }
    }

    /* Méthode permettant de générer des groupes d'envoi aléatoires à partir d'une liste d'adresses */
    public static Vector<AdressList> generateGroups(Vector<String> mails, int number) {
        if(number < 1){
            System.err.println("[Knarp] Cannot create groups: bad groups number!");
            System.exit(-1);
        }

        if (mails.size() / number < MIN_ADDRESSES) {
            System.err.println("[Knarp] Cannot create groups: not enough adresses!");
            System.exit(-1);
        }

        // Duplication de la liste passée en paramètre (sinon sera vidée)
        Vector<String> temp = (Vector<String>)mails.clone();

        Vector<AdressList> lists = new Vector();
        Random rand = new Random();

        // Remplissage de la liste (création de listes d'adresses)
        for (int i = 0; i < number; i++) {
            lists.add(new AdressList());
        }

        int index = 0;
        while (temp.size() > 0) {
            AdressList current = lists.elementAt(index);
            int i = rand.nextInt(temp.size());

            // Pour chaque adresse trouvée, on l'ajoute en tant que récepteur, ou en tant qu'émetteur
            // s'il n'a pas encore été généré
            if (current.getSender() == null) {
                current.setSender(temp.remove(i));
            } else {
                current.addReceiver(temp.remove(i));
            }

            index = (index + 1) % number; // Passe de groupe en groupe
        }

        return lists;
    }
}
