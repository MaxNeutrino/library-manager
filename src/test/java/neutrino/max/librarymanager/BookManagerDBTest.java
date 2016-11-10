package neutrino.max.librarymanager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.sql.*;

import static neutrino.max.librarymanager.BookManagerDB.bookManagerDB;

public class BookManagerDBTest {

    private final String[] ALL = new String[]{"Bruce Eckel \"Thinking in Java\"", "J. Rowling “Harry Potter”",
            "Linus Torvalds \"Just for fun\"", "Unknown “Harry Potter”"};

    private final String[] HARRY_POTTER_BOOKS = new String[]{"J. Rowling “Harry Potter”", "Unknown “Harry Potter”"};

    private final String JAVA_BOOK = "Bruce Eckel \"Thinking in Java\"";

    private final String READ_STATUS = "read";

    private final String ADD = "Quantum mechanics";

    private final String[] ALL_WITH_ADD = new String[]{"Bruce Eckel \"Thinking in Java\"", "J. Rowling “Harry Potter”",
            "Linus Torvalds \"Just for fun\"", ADD, "Unknown “Harry Potter”"};

    @Before
    public void setUp() throws Exception {
        Connection connection = null;
        Statement statement = null;
        try {
            Class.forName("org.hsqldb.jdbcDriver");
            String url = "jdbc:hsqldb:file:".concat(System.getenv("HOME")).concat("/.librarymanagerdb/books");
            connection = DriverManager.getConnection(url, "tux", "tux");
            statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(getClass().getResourceAsStream("/initDB.sql")))) {

                StringBuilder script = new StringBuilder();
                while (reader.ready()) {
                    script.append(reader.readLine());
                }

                statement.execute(script.toString());
                statement.close();
                connection.close();
            }
        } finally {
            statement.close();
            connection.close();
        }

        bookManagerDB.openConnection();
    }

    @After
    public void close() {
        bookManagerDB.closeConnection();
    }

    @Test
    public void getAllBooksTest() throws SQLException {
        ResultSet resultSet = bookManagerDB.getAllBooks();
        checkArray(resultSet, ALL);
    }

    @Test
    public void getStatusByNameTest() throws SQLException {
        ResultSet resultSet = bookManagerDB.getStatusByName(JAVA_BOOK);
        resultSet.next();
        Assert.assertEquals(resultSet.getString("status"), READ_STATUS);
    }

    @Test
    public void getLikedNameTest() throws SQLException {
        ResultSet resultSet = bookManagerDB.getLikeName("Harry");
        checkArray(resultSet, HARRY_POTTER_BOOKS);
    }

    @Test
    public void addTest() throws SQLException {
        bookManagerDB.add(ADD);
        checkArray(bookManagerDB.getAllBooks(), ALL_WITH_ADD);
    }

    @Test
    public void delete() throws SQLException {
        bookManagerDB.delete(ADD);
        checkArray(bookManagerDB.getAllBooks(), ALL);
    }

    @Test
    public void addWithStatus() throws SQLException {
        bookManagerDB.addWithStatus("Zoo", READ_STATUS);
        ResultSet resultSet = bookManagerDB.getAllBooks();
        resultSet.last();
        Assert.assertEquals(resultSet.getString("name"), "Zoo");
        Assert.assertEquals(resultSet.getString("status"), READ_STATUS);
    }

    private void checkArray(ResultSet resultSet, String[] books) throws SQLException {
        int i = 0;
        while (resultSet.next()) {
            Assert.assertEquals(resultSet.getString("name"), books[i]);
            i++;
        }
    }
}
