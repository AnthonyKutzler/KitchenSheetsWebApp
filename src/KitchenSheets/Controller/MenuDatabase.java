package KitchenSheets.Controller;

import KitchenSheets.Interface.SqlStatements;
import com.sun.istack.internal.NotNull;
import org.DB.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;



public class MenuDatabase extends DatabaseConnection implements SqlStatements {

    static private final String[] excelMenus = {"lunch", "adult", "breakfast", "snack"};
    static private final String[] menus = {"lunch", "sub", "vegetarian", "adult1", "adult2", "breakfast", "snack", "hssnack"};

    Connection dbconn = null;
    PreparedStatement ps = null;

    public MenuDatabase() {
        super("QualityHcSheet");
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
        String[] items = new String[8];
        try {
            ps = dbconn.prepareStatement(selectSQL);
            ps.setString(1, date);
            ResultSet rs = ps.executeQuery();
            if (rs.first()) {
                do{
                    items[0] = rs.getString("lunch");
                    items[1] = rs.getString("sub");
                    items[2] = rs.getString("vegetarian");
                    items[3] = rs.getString("adult1");
                    items[4] = rs.getString("adult2");
                    items[5] = rs.getString("breakfast");
                    items[6] = rs.getString("snack");
                    items[7] = rs.getString("hssnack");
                }while (rs.next());
            }
            ps.close();
            dbconn.close();
            if(forExcell){
                List<String> hotItems = getHotItems(selectHotSQL);
                String[] excellItems = {"Cold Trays/" + items[0] + "/Allergy/" + items[1] + "/" + items[2], items[3] + "/" + items[4],"Cold Trays/" + items[5],
                                        "Cold Trays/" + items[6] + "/" + items[7] + "/"};
                for(int z = 0; z < excellItems.length; z++) {
                    Set<String> set = new LinkedHashSet<>();
                    for (String item : excellItems[z].split("/")) {
                        boolean contains = false;
                        item = item.replaceAll("[^a-zA-Z0-9]", "");
                        if(!hotItems.contains(item.trim())) {
                            if(item.equals("No Data"))
                                continue;
                            else if(item.equalsIgnoreCase("corn"))
                                continue;
                            for(String temp : set){
                                for(String temp2 : temp.split(" ")) {
                                    if (set.contains(temp2))
                                        contains = true;
                                }
                            }
                            if(!contains) {
                                //TODO: Implement
                                /*String renamedItem = renameMenuItems(item.trim());
                                if(renamedItem != null)
                                    set.add(renamedItem.trim());*/
                                set.add(item.trim());
                            }
                        }
                    }
                    itemMap.put(excelMenus[z], set);
                }

            }else {
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

    private String renameMenuItems(String item){
        //Broad Category
        List<String> broadCat = getSpecificItems("bread");
        if(broadCat.parallelStream().anyMatch(item::contains))
            return "Bread";
        broadCat = getSpecificItems("fruit");
        if(broadCat.parallelStream().anyMatch(item::contains))
            return "Fruit";
        broadCat = getSpecificItems("salad");
        if(broadCat.parallelStream().anyMatch(item::contains))
            return "Salad";
        broadCat = getSpecificItems("cereal");
        if(broadCat.parallelStream().anyMatch(item::contains))
            return "Cereal";
        return null;
    }

    void insertOrUpdate(String date, String column, String items){
        connectToDb();
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
                    prepareMenuStatement(updateSQLPrefix + column + updateSQLSuffix, items, date);
                    ps.executeUpdate();
                } else {
                    prepareMenuStatement(insertSQLPrefix + column + insertSQLSuffix, date, items);
                    ps.executeUpdate();
                }

        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                dbconn.close();
                dbconn = null;
            }catch (Exception e){}
        }
    }


    private List<String> getSpecificItems(String category){
        prepareSpecificStatement(selectSpecificPrefix + category + selectSpecificSuffix);
        List<String> items = new ArrayList<>();
        try {
            ResultSet rs = ps.executeQuery();
            if(rs.first()){
                items = Arrays.asList(rs.getString(category.toLowerCase()).split("/"));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }

        return items;
    }

    private void prepareSpecificStatement(String sql){
        try {
            connectToDb();
            ps = dbconn.prepareStatement(sql);
        }catch (Exception e){}
    }

    private void prepareMenuStatement(String sql, String date, String items){
        try {
            connectToDb();
            ps = dbconn.prepareStatement(sql);
            ps.setString(1, date);
            ps.setString(2, items);
        }catch (Exception e){}
    }
}
