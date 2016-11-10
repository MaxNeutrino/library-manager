package neutrino.max.librarymanager;

import java.sql.*;

public class BookManagerDB {

    public static final BookManagerDB bookManagerDB = new BookManagerDB();

    private Connection connection;
    private Statement statement;

    private BookManagerDB() {
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (statement != null)
            statement.close();

        if (connection != null)
            closeConnection();

        Class.forName("org.hsqldb.jdbcDriver");
        String url = "jdbc:hsqldb:file:".concat(System.getenv("HOME")).concat("/.librarymanagerdb/books");
        connection = DriverManager.getConnection(url, "tux", "tux");
        statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        try {
            getAllBooks();
        } catch (SQLException e) {
            statement.executeQuery("CREATE TABLE books (name VARCHAR(255) NOT NULL, status VARCHAR(255));" +
                    "CREATE UNIQUE INDEX name_book_key ON BOOKS (name)");
        }
    }

    public ResultSet getAllBooks() throws SQLException {
        return statement.executeQuery("SELECT * FROM books ORDER BY name");
    }

    public ResultSet getStatusByName(String name) throws SQLException {
        return statement.executeQuery("SELECT status FROM books WHERE name = '" + name + "'");
    }

    public ResultSet getLikeName(String name) throws SQLException {
        return statement.executeQuery("SELECT * FROM books WHERE name LIKE '%" + name + "%' ORDER BY name");
    }

    public void delete(String name) throws SQLException {
        statement.executeQuery("DELETE FROM books WHERE name = '" + name + "'");
    }

    public void edit(String name, String editedName, String status) throws SQLException {
        statement.executeQuery("UPDATE books SET name = '" +
                editedName + "', status = '" + status + "' WHERE name = '" + name + "'");
    }

    public void add(String name) throws SQLException {
        statement.execute("INSERT INTO books(name) VALUES '" + name + "'");
    }

    public ResultSet getByStatus(String status) throws SQLException {
        return statement.executeQuery("SELECT name FROM books WHERE status = '" + status + "' ORDER BY name");
    }

    public void addWithStatus(String name, String status) throws SQLException {
        statement.execute("INSERT INTO books(name, status) VALUES '" + name + "', '" + status + "'");
    }

    public void closeConnection() {
        try {
            statement.execute("SHUTDOWN");
            statement.close();
        } catch (Exception ignored) {

        } finally {
            try {
                connection.close();
            } catch (SQLException ignored) {
            }
        }
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
