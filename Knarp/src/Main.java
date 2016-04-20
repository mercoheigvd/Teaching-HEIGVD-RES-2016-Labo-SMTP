import knarp.Knarp;

import java.util.Scanner;

/* Classe principale permettant de lancer la classe de création de mails forgés */
public class Main {
    /* Constantes des différents fichiers de configuration */
    private final static String SERVER = "server.xml";
    private final static String EMAIL_ADDRESSES = "email_addresses.xml";
    private final static String EMAIL_CONTENTS = "email_contents.xml";

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        Knarp k = new Knarp(SERVER, EMAIL_ADDRESSES, EMAIL_CONTENTS);
        int groups;

        // Lecture de l'entrée utilisateur
        System.out.println("Entrez le nombre de groupes à créer:");
        System.out.print("> ");
        groups = input.nextInt();

        k.prank(groups);
    }
}
