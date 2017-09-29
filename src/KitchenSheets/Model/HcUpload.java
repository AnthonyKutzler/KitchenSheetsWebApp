package KitchenSheets.Model;


import KitchenSheets.Controller.DbController;
import KitchenSheets.Controller.XSSFWorkbookParser;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Named(value = "hcUpload")
@ViewScoped
public class HcUpload implements Serializable {

    private static final String uploadPath = "/home/gob/webSheets/";
    //upload vars
    private Part file;
    private String returnFileName;
    private String dateString = "Todays Date";
    private Date date = new Date();//= today's date

    private String lunch,sub,vegetarian,adult,breakfast, uBreakfast, snack, uSnack, hsSnack;

    public HcUpload(){
        updateMenus();
    }
    public void runSheetParser() throws IOException{
        Map<String, Set<String>> map = new DbController().getMenus(dateString, true);
        File workingFile;
        InputStream iStream = file.getInputStream();
        workingFile = new File(uploadPath, file.getSubmittedFileName());
        Files.copy(iStream, workingFile.toPath());
        Set<String> totalLunch = map.get("lunch");
        totalLunch.addAll(map.get("adult"));
        SimpleDateFormat format = new SimpleDateFormat("EEEE");
        String day = format.format(date);
        returnFileName = new XSSFWorkbookParser(workingFile , day).parseCopy(new String[][]{
                totalLunch.toArray(new String[totalLunch.size()]),
                        map.get("breakfast").toArray(new String[map.get("breakfast").size()]),
                        map.get("snack").toArray(new String[map.get("snack").size()])} ,
                new int[] {map.get("lunch").size(), totalLunch.size()});
    }

    public void updateMenus(){
        Map<String, Set<String>> map = new DbController().getMenus(dateString, false);
        if(map.size() < 9){
            lunch = "No Data";
            sub = "No Data";
            vegetarian = "No Data";
            adult = "No Data";
            breakfast = "No Data";
            uBreakfast = "No Data";
            snack = "No Data";
            uSnack = "No Data";
            hsSnack = "No Data";
            return;
        }
        lunch = parseSet(map.get("lunch"));
        sub = parseSet(map.get("sub"));
        vegetarian = parseSet(map.get("vegetarian"));
        adult = parseSet(map.get("adult"));
        breakfast = parseSet(map.get("breakfast"));
        uBreakfast = parseSet(map.get("ubreakfast"));
        snack = parseSet(map.get("snack"));
        uSnack = parseSet(map.get("usnack"));
        hsSnack = parseSet(map.get("hssnack"));
    }

    private String parseSet(Set<String> set){
        StringBuilder value = new StringBuilder("");
        for(String item : set.toArray(new String[set.size()])){
            value.append(item + "&lt;br/&gt;");
        }
        return value.toString();
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

    public String getAdult() {
        return adult;
    }

    public void setAdult(String adult) {
        this.adult = adult;
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
