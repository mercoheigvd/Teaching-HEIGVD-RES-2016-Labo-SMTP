package knarp;

/* Classe contenant les différentes informations d'un mail (sujet et contenu) */
public class Mail {
    private String _subject;
    private String _content;

    public Mail(String subject, String content) {
        _subject = subject;
        _content = content;
    }

    public String getSubject() {
        return _subject;
    }

    public String getContent() {
        return _content;
    }

    public void show(){
        System.out.println("Subject : " + _subject);
        System.out.println(_content);
    }
}
