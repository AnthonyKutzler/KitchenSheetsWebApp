package KitchenSheets.Controller;

import KitchenSheets.Interface.DatabaseCreds;
import KitchenSheets.Interface.SqlStatements;
import org.mariadb.jdbc.MariaDbDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class MenuDatabase extends DatabaseConnection implements SqlStatements {

    static private final String[] excellMenus = {"lunch", "adult", "breakfast", "snack"};
    static private final String[] menus = {"lunch", "sub", "vegetarian", "adult", "breakfast", "ubreakfast", "snack",
                                            "usnack", "hssnack"};

    Connection dbconn = null;
    PreparedStatement ps = null;


    //TODO: Be sure to test.
    public MenuDatabase() {
        super();
        connectToDb();
    }

    private void connectToDb(){
        dbconn = super.connection;
    }

    private List<String> getHotItems(String sql){
        List<String> hotItems = new ArrayList<>();
        try{
            ps = dbconn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                do{
                    hotItems.add(rs.getString("item"));
                }while (rs.next());
            }
        }catch (Exception e){}
        return hotItems;
    }

    public Map<String, Set<String>> getMenus(String date, boolean forExcell){
        Map<String, Set<String>> itemMap = new HashMap<>();
        String[] items = new String[9];
        try {
            ps = dbconn.prepareStatement(selectSQL);
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                do{
                    items[0] = rs.getString("lunch");
                    items[1] = rs.getString("sub");
                    items[2] = rs.getString("vegetarian");
                    items[3] = rs.getString("adult");
                    items[4] = rs.getString("breakfast");
                    items[5] = rs.getString("ubreakfast");
                    items[6] = rs.getString("snack");
                    items[7] = rs.getString("usnack");
                    items[8] = rs.getString("hssnack");
                }while (rs.next());
            }
            if(forExcell){
                List<String> hotItems = getHotItems(selectHotSQL);
                String[] excellItems = {"Cold Trays/" + items[0] + "/Allergy/" + items[1] + "/" + items[2], items[3],"Cold Trays/" + items[4] + "/" + items[5],
                                        "Cold Trays/" + items[6] + "/" + items[7] + "/" + items[8]};
                for(int z = 0; z < excellItems.length; z++) {
                    Set<String> set = new LinkedHashSet<>();
                    for (String item : excellItems[z].split("/")) {
                        if(!hotItems.contains(item.trim())) {
                            if(item.equals("No Data"))
                                continue;
                            set.add(item.trim());
                        }
                    }
                    itemMap.put(excellMenus[z], set);
                }

            }else {
                //TODO: Change all to LinkedHashSet
                for (int z = 0; z < items.length; z++) {
                    Set<String> set = new LinkedHashSet<>();
                    for (String item : items[z].split("/")) {
                        set.add(item.trim());
                    }
                    itemMap.put(menus[z], set);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                ps.close();
                dbconn.close();
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if(dbconn != null)
                    dbconn = null;
            }
        }
        return itemMap;
    }

    void insertOrUpdate(String date, String column, String items){
        String[] itemsArray = items.split("/");
        List<String> hotPrefixes = getHotItems(selectHotPreSQL);
        try{
            for(String item : itemsArray) {
                if (hotPrefixes.parallelStream().anyMatch(item::contains)) {
                    if(item.contains("CV:") || item.contains("buns"))
                        continue;
                    try {
                        ps = dbconn.prepareStatement(insertHotSQL);
                        ps.setString(1, item.trim());
                        ps.executeUpdate();
                    } catch (Exception e) {
                    }//Nested Try block for possibility of duplicates.
                }
            }
            if (dbconn == null){
                connectToDb();
            }
            ps = dbconn.prepareStatement(selectSQL);
                ps.setString(1, date);
                ResultSet rs = ps.executeQuery();
                if (rs.first()) {
                    preparedStatement(updateSQLPrefix + column + updateSQLSuffix, items, date);
                    ps.executeUpdate();
                } else {
                    preparedStatement(insertSQLPrefix + column + insertSQLSuffix, date, items);
                    ps.executeUpdate();
                }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                dbconn.close();
            }catch (Exception e){}
        }
    }

    public void preparedStatement(String sql, String date, String items){
        try {
            ps = dbconn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, items);
        }catch (Exception e){}
    }
}
