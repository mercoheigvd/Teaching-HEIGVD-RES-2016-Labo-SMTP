package knarp;

/* Classe contenant les différentes informations d'un mail (sujet et contenu) */
public class Mail {
    private String _subject;
    private String _content;

    public Mail(String subject, String content) {
        _subject = subject;
        _content = content;
    }

    /* Obtention du sujet du mail */
    public String getSubject() {
        return _subject;
    }

    /* Obtention du contenu du mail */
    public String getContent() {
        return _content;
    }

    /* Affichage du mail */
    public void show(){
        System.out.println("Subject : " + _subject);
        System.out.println(_content);
    }
}
