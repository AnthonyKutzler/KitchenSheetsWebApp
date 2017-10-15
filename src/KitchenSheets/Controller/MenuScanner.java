package KitchenSheets.Controller;

import java.io.File;
import java.util.*;

public class MenuScanner {

    private String date, day;
    private int lunchCount, adultCount;
    private List<String> lunch = Arrays.asList("Cold Trays", "Allergy");//Removed Bread, and Fruit
    private List<String> suffixLunch = Arrays.asList("Protein", "Starch", "Veggie");
    private List<String> breakfast = Arrays.asList("Cold Trays");
    private List<String> snack = Arrays.asList("Cold Trays");


    public MenuScanner(String date){
        readMenu(date);
    }

    public String parseMenu(File file){
        return new XSSFWorkbookParser(file, day).parseCopy(new String[][] {lunch.toArray(new String[lunch.size()]),
                        breakfast.toArray(new String[breakfast.size()]), snack.toArray(new String[snack.size()])},
                new int[]{lunchCount, adultCount});
    }

    private void readMenu(String date){
        Map<String, Set<String>> menuMap = new MenuDatabase().getMenus(date, true);
        //Create tempset to get lunch and adult count separately
        Set<String> tempSet = menuMap.get("lunch");
        lunchCount = tempSet.size();
        tempSet.addAll(menuMap.get("adult"));
        adultCount = tempSet.size();
        //then add all to lunch
        lunch.addAll(tempSet);
        //then add suffix
        lunch.addAll(suffixLunch);
        breakfast.addAll(menuMap.get("breakfast"));
        snack.addAll(menuMap.get("snack"));
    }

    private void setupMenu(String date){

    }
}
