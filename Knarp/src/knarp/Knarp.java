package knarp;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Random;
import java.util.Vector;

/* Classe principale du générateur de blagues */
public class Knarp {
    private Socket _socket;
    private PrintWriter _writer;
    private BufferedReader _reader;
    private String _server;
    private String _email_adresses;
    private String _email_contents;
    private DocumentBuilderFactory _dbf;
    private DocumentBuilder _db;

    /* Constructeur du générateur */
    public Knarp(String server, String emails_adresses, String emails_contents) {
        try {
            _server = server;
            _email_adresses = emails_adresses;
            _email_contents = emails_contents;
            _dbf = DocumentBuilderFactory.newInstance();
            _db = _dbf.newDocumentBuilder();
        } catch (ParserConfigurationException p) {
            p.printStackTrace();
        }
    }

    /* Méthode de connexion au serveur SMTP (obtient les informations de connexion depuis le fichier de configuration) */
    private void connect() {
        InetAddress address;
        int port;

        try {
            File inputFile = new File(_server);

            // Lecture du fichier XML
            Document doc = _db.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Liste des éléments "address"
            NodeList a = doc.getElementsByTagName("address");
            NodeList p = doc.getElementsByTagName("port");

            // Lecture des informations
            address = InetAddress.getByName(a.item(0).getTextContent());
            port = Integer.parseInt(p.item(0).getTextContent());

            if (port != 25) {
                throw new Exception("[Knarp] The SMTP port must be 25!");
            }

            // Création des objets nécessaires à la connexion
            _socket = new Socket(address, port);
            _writer = new PrintWriter(_socket.getOutputStream(), true);
            _reader = new BufferedReader(new InputStreamReader(_socket.getInputStream()));

            _reader.readLine();
            if (!_writer.checkError()) {
                do {
                    _writer.println(SMTP.HELO + " client");
                    System.out.println(SMTP.HELO + " client");
                } while (!waitForAnswer());
            }
        } catch (Exception e) {
            System.err.println("[Knarp] Error while connecting: " + e.getMessage());
            System.exit(-1);
        }
    }

    /* Méthode de déconnexion */
    private void disconnect() {
        if (!_writer.checkError()) {
            do {
                _writer.println(SMTP.QUIT);
                System.out.println(SMTP.QUIT);
            } while (!waitForAnswer());
        }

        try {
            _socket.close();
            _writer.close();
            _reader.close();
        } catch (IOException e) {
            System.err.println("[Knarp] Error while disconnecting: " + e.getMessage());
            System.exit(-1);
        }
    }

    /*
    *  Méthode d'envoi de messages:
    *  Cette méthode choisit un mail au hasard dans la liste en paramètre et l'envoie à toutes les adresses
    */
    private void sendMessages(Group a, Mail mail) {

        if (!_writer.checkError()) {
            for (String ad : a.getReceivers()) {
                do {
                    _writer.println(SMTP.MAIL_FROM + a.getSender());
                    System.out.println(SMTP.MAIL_FROM + a.getSender());
                } while (!waitForAnswer());

                do {
                    _writer.println(SMTP.RCPT_TO + ad);
                    System.out.println(SMTP.RCPT_TO + ad);
                } while (!waitForAnswer());

                do {
                    _writer.println(SMTP.DATA);
                    System.out.println(SMTP.DATA);
                } while (!waitForAnswer());

                do {
                    _writer.println("Subject: " + mail.getSubject());
                    System.out.println("Subject: " + mail.getSubject());
                    _writer.println(mail.getContent());
                    System.out.println(mail.getContent());
                    _writer.println(".");
                    System.out.println(".");
                } while (!waitForAnswer());
            }
        }
    }

    /*
    *  Lecture du fichier de configuration contenant les adresses
    *  Retourne un vecteur de String contenant toutes les adresses
    */
    public Vector<String> getAdresses() {
        Vector<String> addresses = new Vector();
        try {
            File inputFile = new File(_email_adresses);

            // Lecture du fichier XML
            Document doc = _db.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Liste des éléments "address"
            NodeList list = doc.getElementsByTagName("address");

            // Lecture du contenu de chaque élément "address"
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);

                addresses.add(n.getTextContent());
            }
        } catch (Exception e) {
            System.err.println("[Knarp] Error getting email adresses: " + e.getMessage());
            System.exit(-1);
        }

        return addresses;
    }

    /*
    *  Lecture du fichier de configuration contenant les mails (plaisanteries)
    *  Retourne un vecteur de Mail (chaque objet contenant le sujet et le contenu du message)
    */
    public Vector<Mail> getEmails() {
        Vector<Mail> mails = new Vector();
        try {
            File inputFile = new File(_email_contents);

            // Lecture du fichier XML
            Document doc = _db.parse(inputFile);
            doc.getDocumentElement().normalize();

            // Liste des éléments "mail"
            NodeList list = doc.getElementsByTagName("mail");

            // Lecture des sous-éléments
            for (int i = 0; i < list.getLength(); i++) {
                Node n = list.item(i);

                String subject = ((Element) n).getElementsByTagName("subject").item(0).getTextContent();
                String content = ((Element) n).getElementsByTagName("content").item(0).getTextContent();

                mails.add(new Mail(subject, content));
            }
        } catch (Exception e) {
            System.err.println("[Knarp] Error getting email contents: " + e.getMessage());
            System.exit(-1);
        }

        return mails;
    }

    /* Méthode principale de la classe, permet de lancer l'envoi des blagues */
    public void prank(int nbgroups) {
        Random rand = new Random();

        // Obtention des informations nécessaires
        Vector<String> adresses = getAdresses();
        Vector<Mail> mails = getEmails();

        // Génération des groupes
        Vector<Group> groups = Group.generateGroups(adresses, nbgroups);

        for (Group grp : groups) {
            Mail mail = mails.elementAt(rand.nextInt(mails.size()));
            connect();
            sendMessages(grp, mail);
            disconnect();
        }
    }

    /*
    *  Méthode d'attente de réponse du serveur (attends 10ms, puis lit ce que le serveur a envoyé)
    *  Cette méthode retourne false si le serveur a retourné une erreur, true sinon
    */
    private boolean waitForAnswer() {
        String s = String.valueOf(5);
        int errcode;

        try {
            s = _reader.readLine();
            System.out.println("[SMTP Server] " + s);
        } catch (Exception e) {
            System.err.println("[Knarp] Error waiting for an answer: " + e.getMessage());
            System.exit(-1);
        }

        errcode = Integer.parseInt(s.substring(0, 1));

        if(errcode == 5){
            System.err.println("[Knarp] Fatal error from SMTP Server: " + s);
            System.exit(-1);
        }

        return errcode < 4;
    }
}
