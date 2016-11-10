package neutrino.max.librarymanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;

import static neutrino.max.librarymanager.BookManagerDB.bookManagerDB;

public class InputListener {

    public void listen() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {

            BookManagerWrapper bookWrapper = new BookManagerWrapper(reader);
            bookWrapper.openConnection();
            bookWrapper.printHello();

            while (true) {
                String line = reader.readLine();

                try {

                    if (line.startsWith("add "))
                        bookWrapper.addBook(line.substring(4));
                    else
                        if (line.startsWith("edit "))
                            bookWrapper.editBook(line.substring(5));
                    else
                        if (line.startsWith("remove "))
                            bookWrapper.removeBook(line.substring(7));
                    else
                        if (line.startsWith("find name "))
                            bookWrapper.findByName(line.substring(10));
                    else
                        if (line.startsWith("find status "))
                            bookWrapper.findByStatus(line.substring(12));
                    else
                        if (line.equals("all books"))
                            bookWrapper.printAllBooks(false);
                    else
                        if (line.equals("all books status"))
                            bookWrapper.printAllBooks(true);
                    else
                        if (line.equals("help"))
                            bookWrapper.printHelp();
                    else
                        if (line.equals("exit"))
                            break;

                } catch (NotFoundException e) {
                    System.err.println(e.getMessage());
                }
            }

        } catch (SQLException e) {
            System.err.println("Can't connect to the database");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            bookManagerDB.closeConnection();
        }
    }
}
