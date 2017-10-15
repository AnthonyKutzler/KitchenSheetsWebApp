package KitchenSheets.Interface;

public interface SqlStatements {

    String TABLE_NAME = "menus";

    String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE date = ?";
    String updateSQLPrefix = "UPDATE " + TABLE_NAME + " SET ";
    String updateSQLSuffix = " = ? WHERE date = ?";
    String insertSQLPrefix = "INSERT INTO " + TABLE_NAME + "(date, ";
    String insertSQLSuffix = ") VALUES (?, ?)";
    String selectHotSQL = "SELECT * FROM hotmenus";
    String selectHotPreSQL = "SELECT * FROM hotprefix";
    String insertHotSQL = "INSERT INTO hotmenus (item) VALUES (?)";
}
