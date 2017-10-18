package KitchenSheets.Model;


import KitchenSheets.Controller.MenuDatabase;
import KitchenSheets.Controller.XSSFWorkbookParser;

import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.*;

@Named(value = "hcUpload")
@ViewScoped
public class HcUpload implements Serializable {

    private static final String uploadPath = "/home/gob/webSheets/";
    //upload vars
    private Part file;
    //private String returnFileName;
    private String dateString;
    private Date date = new Date();//= today's date
    private String lunch,sub,vegetarian,adult1,adult2,breakfast, uBreakfast, snack, uSnack, hsSnack;
    private List<String> psv = Arrays.asList("Protein", "Starch", "Veggie");

    public HcUpload(){
        updateMenus();
    }
    public void runSheetParser() throws IOException{
        Map<String, Set<String>> map = new MenuDatabase().getMenus(dateString, true);
        File workingFile;
        InputStream iStream = file.getInputStream();
        workingFile = new File(uploadPath, file.getSubmittedFileName());
        Files.copy(iStream, workingFile.toPath());
        Set<String> totalLunch = map.get("lunch");
        totalLunch.addAll(map.get("adult"));
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String day = format.format(date);
        if(day.equals("Monday") || day.equals("Wednesday") || day.equals("Friday"))
            totalLunch.add("Dessert");
        totalLunch.addAll(psv);
        String returnFileName = new XSSFWorkbookParser(workingFile , day).parseCopy(new String[][]{
                totalLunch.toArray(new String[totalLunch.size()]),
                        map.get("breakfast").toArray(new String[map.get("breakfast").size()]),
                        map.get("snack").toArray(new String[map.get("snack").size()])} ,
                new int[] {map.get("lunch").size() - 2, totalLunch.size() - 5});
        workingFile.delete();
        FileUpload.download(FacesContext.getCurrentInstance(), returnFileName);

    }


    public void updateMenus(){
        Map<String, Set<String>> map = new MenuDatabase().getMenus(dateString, false);
        if(map.size() < 8){
            lunch = "No Data";
            sub = "No Data";
            vegetarian = "No Data";
            adult1 = "No Data";
            adult2 = "No Data";
            breakfast = "No Data";
            snack = "No Data";
            hsSnack = "No Data";
            return;
        }
        lunch = parseSet(map.get("lunch"));
        sub = parseSet(map.get("sub"));
        vegetarian = parseSet(map.get("vegetarian"));
        adult1 = parseSet(map.get("adult1"));
        adult2 = parseSet(map.get("adult2"));
        breakfast = parseSet(map.get("breakfast"));
        snack = parseSet(map.get("snack"));
        //Head start... should be Club For kids
        hsSnack = parseSet(map.get("hssnack"));
    }

    private String parseSet(Set<String> set){
        StringBuilder value = new StringBuilder("");
        for(String item : set.toArray(new String[set.size()])){
            value.append(item + ", "); //&lt;br /&gt;
        }
        return value.toString().substring(0, value.length() - 2);
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        SimpleDateFormat format = new SimpleDateFormat("M/d");
        dateString = format.format(date);
        updateMenus();
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getSub() {
        return sub;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public String getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(String vegetarian) {
        this.vegetarian = vegetarian;
    }

    public String getAdult1() {
        return adult1;
    }

    public void setAdult1(String adult1) {
        this.adult1 = adult1;
    }

    public String getAdult2() {
        return adult2;
    }

    public void setAdult2(String adult2) {
        this.adult2 = adult2;
    }

    public String getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(String breakfast) {
        this.breakfast = breakfast;
    }

    public String getSnack() {
        return snack;
    }

    public void setSnack(String snack) {
        this.snack = snack;
    }

    public String getuBreakfast() {
        return uBreakfast;
    }

    public void setuBreakfast(String uBreakfast) {
        this.uBreakfast = uBreakfast;
    }

    public String getuSnack() {
        return uSnack;
    }

    public void setuSnack(String uSnack) {
        this.uSnack = uSnack;
    }

    public String getHsSnack() {
        return hsSnack;
    }

    public void setHsSnack(String hsSnack) {
        this.hsSnack = hsSnack;
    }
}


/**
 *
 * AnthonyKutzler@gmail.com
 *
 *
 */
