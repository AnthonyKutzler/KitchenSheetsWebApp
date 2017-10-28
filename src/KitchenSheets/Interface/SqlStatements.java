package KitchenSheets.Interface;

public interface SqlStatements {

    String MENU_TABLE = "menus";
    String SPEC_TABLE = "specifics";

    String selectSQL = "SELECT * FROM " + MENU_TABLE + " WHERE date = ?";

    //Prefix/Suffix Statements to insert one of more strings into the middle of the statement
    String updateSQLPrefix = "UPDATE " + MENU_TABLE + " SET ";
    String updateSQLSuffix = " = ? WHERE date = ?";
    String insertSQLPrefix = "INSERT INTO " + MENU_TABLE + "(date, ";
    String insertSQLSuffix = ") VALUES (?, ?)";
    String selectHotSQL = "SELECT * FROM hotmenus";
    String selectHotPreSQL = "SELECT * FROM hotprefix";
    String insertHotSQL = "INSERT INTO hotmenus (item) VALUES (?)";
    String selectSpecificPrefix = "SELECT ";
    String selectSpecificSuffix = " FROM " + SPEC_TABLE;
}
