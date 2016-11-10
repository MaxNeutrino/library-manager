package neutrino.max.librarymanager;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

import static neutrino.max.librarymanager.BookManagerDB.bookManagerDB;

public class BookManagerWrapper {

    private BufferedReader reader;

    public BookManagerWrapper(BufferedReader reader) {
        this.reader = reader;
    }

    public void openConnection() {
        try {
            bookManagerDB.openConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void printAllBooks(boolean isWithStatus) throws SQLException {
        ResultSet resultSet = bookManagerDB.getAllBooks();
        if (resultSet.isBeforeFirst()) {
            System.out.println("Our books :");
            print(resultSet, isWithStatus);
        } else {
            System.out.println("Empty library");
        }
    }

    public void addBook(String name) throws IOException {
        System.out.println("Write status of the book");
        String status = reader.readLine();
        try {
            if (status.length() < 1)
                bookManagerDB.add(name);
            else
                bookManagerDB.addWithStatus(name, status);
            System.out.println("Book ".concat(name).concat(" was added"));
        } catch (SQLException e) {
            System.err.println("Book exist");
        }
    }

    public void editBook(String name) throws SQLException, NotFoundException {
        try {
            name = checkBook(name);
            System.out.println("Typing a new name of the book - ".concat(name)
            .concat("\n If you don't want to change the name leave empty"));
            String newName = reader.readLine();

            System.out.println("Typing a new status of the book - ".concat(name)
                    .concat("\n If you don't want to change the name leave empty"));
            String newStatus = reader.readLine();

            if (newName.length() < 1)
                newName = name;
            if (newStatus.length() < 1) {
                ResultSet statusSet = bookManagerDB.getStatusByName(name);
                if (statusSet.isBeforeFirst()) {
                    statusSet.next();
                    newStatus = statusSet.getString("status");
                }
            }

            bookManagerDB.edit(name, newName, newStatus);
            System.out.println("Book changed");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void removeBook(String name) throws SQLException, NotFoundException {
        name = checkBook(name);
        bookManagerDB.delete(name);
        System.out.println("Book ".concat(name).concat(" was removed "));
    }

    public void findByStatus(String status) throws SQLException {
        if (status != null) {
            ResultSet resultSet = bookManagerDB.getByStatus(status);

            if (resultSet.isBeforeFirst()) {
                System.out.println("Books with status - ".concat(status));
                print(resultSet, false);
            } else
                System.err.println("Books does not exist with the status - ".concat(status));

        } else {
            System.err.println("Status can not be empty");
        }
    }

    public void findByName(String name) throws SQLException {
        if (name != null) {
            ResultSet resultSet = bookManagerDB.getLikeName(name);
            if (resultSet.isBeforeFirst()) {
                System.out.println("Found the books:");
                print(resultSet, true);
            } else
                System.err.println("Books not found");
        }
    }

    public void printHelp() {
        System.out.println(
                "\tadd [book name] - add a book to the library\n"
                .concat("\tedit [book name] - edit a book\n")
                .concat("\tremove [book name] - remove a book from the library\n")
                .concat("\tall books - print all the books from the library\n")
                .concat("\tall books status - print all the books with status from the library\n")
                .concat("\tfind name [book name] - find books by name\n")
                .concat("\tfind status [book status] - find books by status\n")
                .concat("\texit - exit from library manager")
        );
    }

    public void printHello() {
        System.out.println("Type help for more info");
    }

    private String checkBook(String name) throws SQLException, NotFoundException {
        try {
            ResultSet resultSet = bookManagerDB.getLikeName(name);

            if (resultSet.isBeforeFirst()) {
                resultSet.last();
                int lastRowIndex = resultSet.getRow();

                if (lastRowIndex > 1) {
                    System.out.println("We have few books with such name please choose one by typing a number of book:");
                    print(resultSet, false);

                    int rowNum = Integer.valueOf(reader.readLine());
                    if (lastRowIndex < rowNum)
                        throw new NumberFormatException();

                    resultSet.absolute(rowNum);
                    return resultSet.getString("name");

                } else {
                    return resultSet.getString("name");
                }
            } else {
                throw new NotFoundException("Book does not exist in the library");
            }
        } catch (NumberFormatException e) {
            System.err.println("Wrong line number");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void print(ResultSet resultSet, boolean isWithStatus) throws SQLException {
        resultSet.beforeFirst();
        while (resultSet.next()) {
            String name = resultSet.getString("name");
            if (name != null) {

                if (isWithStatus) {
                    String status = resultSet.getString("status");
                    if (status == null)
                        status = "";

                    System.out.println(
                            "\t".concat(String.valueOf(resultSet.getRow()))
                                    .concat(". ")
                                    .concat(name)
                                    .concat(" - ")
                                    .concat(status));
                } else {
                    System.out.println(
                            "\t".concat(String.valueOf(resultSet.getRow()))
                                    .concat(". ")
                                    .concat(name));
                }
            } else
                System.out.println("Name can not be empty");
        }
        System.out.println();
    }
}
