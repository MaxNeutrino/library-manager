package neutrino.max.librarymanager;
/**
    I used embedded db HSQLDB for local book library
    Extended - user can set status for book, example: "Thinking in Java - read"
**/

public class Main {
    public static void main(String[] args) {
        InputListener listener = new InputListener();
        listener.listen();
    }
}
