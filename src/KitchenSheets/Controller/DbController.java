package KitchenSheets.Controller;

import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

public class DbController {

    static private final String HOSTNAME = "localhost";
    static private final String DATABASE = "QualityHcSheet";
    static private final String USERNAME = "";
    static private final String PASSWORD = "";
    static private final String TABLE_NAME = "menus";
    static private final int PORT = 3306;
    static private final String[] excellMenus = {"lunch", "adult", "breakfast", "snack"};
    static private final String[] menus = {"lunch", "sub", "vegetarian", "adult", "breakfast", "ubreakfast", "snack",
                                            "usnack", "hssnack"};

    //SQL Statements
    static private final String selectSQL = "SELECT * FROM " + TABLE_NAME + " WHERE date = ?";
    static private final String updateSQL = "UPDATE " + TABLE_NAME + " SET ? = ?" + " WHERE date = ?";
    static private final String insertSQL = "INSERT INTO " + TABLE_NAME + "(date, ?) VALUES (?, ?)";
    static private final String selectHotSQL = "SELECT * FROM hotmenus";
    static private final String selectHotPreSQL = "SELECT * FROM hotprefix";
    static private final String insertHotSQL = "INSERT INTO hotmenus (item) VALUES (?)";

    Connection dbconn = null;
    PreparedStatement ps = null;

    public DbController(){
        try {
            dbconn = new MariaDbDataSource(HOSTNAME, PORT, DATABASE).getConnection(USERNAME, PASSWORD);
        }catch (Exception e){}

    }


    private List<String> getHotItems(String sql){
        List<String> hotItems = new ArrayList<>();
        try{
            ps = dbconn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                while (rs.next()){
                    hotItems.add(rs.getString("item"));
                }
            }
        }catch (Exception e){}
        return hotItems;
    }

    public Map<String, Set<String>> getMenus(String date, boolean forExcell){
        Map<String, Set<String>> itemMap = new HashMap<>();
        String[] items = new String[4];
        try {
            ps = dbconn.prepareStatement(selectSQL);
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                while (rs.next()){
                    items[0] = rs.getString("lunch");
                    items[1] = rs.getString("sub");
                    items[2] = rs.getString("vegetarian");
                    items[3] = rs.getString("adult");
                    items[4] = rs.getString("breakfast");
                    items[5] = rs.getString("ubreakfast");
                    items[6] = rs.getString("snack");
                    items[7] = rs.getString("usnack");
                    items[8] = rs.getString("hssnack");
                }
            }
            if(forExcell){
                List<String> hotItems = getHotItems(selectHotSQL);
                String[] excellItems = {items[0] + "," + items[1] + "," + items[2], items[3], items[4] + "," + items[5],
                                        items[6] + "," + items[7] + "," + items[8]};
                for(int z = 0; z < excellItems.length; z++) {
                    Set<String> set = new HashSet<>();
                    for (String item : excellItems[z].split(",")) {
                        if(!hotItems.contains(item.trim()))
                            set.add(item.trim());
                    }
                    itemMap.put(excellMenus[z], set);
                }

            }else {
                for (int z = 0; z < items.length; z++) {
                    Set<String> set = new HashSet<>();
                    for (String item : items[z].split(",")) {
                        set.add(item.trim());
                    }
                    itemMap.put(menus[z], set);
                }
            }

        }catch (Exception e){}
        return itemMap;
    }

    public void insertOrUpdate(String date, String column, String items){
        String[] itemsArray = items.split(",");
        List<String> hotPrefixes = getHotItems(selectHotPreSQL);
        try{
            for(String item : itemsArray) {
                if (hotPrefixes.parallelStream().anyMatch(item::contains)){
                    ps = dbconn.prepareStatement(insertHotSQL);
                    ps.setString(1, item);
                    ps.executeUpdate();
                }
                ps = dbconn.prepareStatement(selectSQL);
                ps.setString(1, date);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {
                    preparedStatement(updateSQL, date, column, item);
                    ps.executeUpdate();
                } else {
                    preparedStatement(insertSQL, date, column, item);
                    ps.executeUpdate();
                }


            }
        }catch (Exception e){}
    }

    public void preparedStatement(String sql, String date, String column, String items){
        try {
            ps = dbconn.prepareStatement(sql);
            ps.setString(1, column);
            ps.setString(2, date);
            ps.setString(3, items);
        }catch (Exception e){}
    }
}
