package KitchenSheets.Controller;

import KitchenSheets.Interface.DatabaseCreds;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class DatabaseConnection implements DatabaseCreds {

    public Connection connection;

    DatabaseConnection() {
        try {
            connection = new MariaDbDataSource(HOSTNAME, PORT, DATABASE).getConnection(USERNAME, PASSWORD);
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
