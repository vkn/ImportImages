package persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PersistenceService {

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String TABLE = "syncedimages";
    private final String dbPath;


    private Connection connection;

    public PersistenceService(String dbPath) {
        this.dbPath = dbPath;
    }

    private Connection getConnection() throws SQLException {
        if (null == connection) {
            try {
                Class.forName("org.sqlite.JDBC");
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(PersistenceService.class.getName()).log(Level.SEVERE, null, ex);
            }
            connection = DriverManager.getConnection("jdbc:sqlite:" + dbPath);
            createTable();
        }
        return connection;
    }

    private void createTable() throws SQLException {
        updateQuery(String.format("create table if not exists %s (id string PRIMARY KEY, created string)", TABLE));
    }

    private int updateQuery(String updateQuery) throws SQLException {
        Statement statement = getConnection().createStatement();
        statement.setQueryTimeout(30);
        return statement.executeUpdate(updateQuery);
    }
    
    private ResultSet query(String query) throws SQLException {
        Statement statement = getConnection().createStatement();
        statement.setQueryTimeout(30);
        return statement.executeQuery(query);
    }

    public boolean isImported(String name) throws SQLException {
        ResultSet r = query(String.format("select count(*) AS cnt from %s WHERE id = '%s';", TABLE, name));
        r.next();
        return r.getInt("cnt") > 0;
    }

    public int markAsImported(String name) throws SQLException {
        String date = LocalDateTime.now(ZoneId.of("UTC")).format(dateFormatter);
        return updateQuery("insert into " + TABLE + " values('" + name +"', '" + date + "')");
    }

}
