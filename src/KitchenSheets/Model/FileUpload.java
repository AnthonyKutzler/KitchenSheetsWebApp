package KitchenSheets.Model;


import KitchenSheets.Controller.XSSFWorkbookParser;

import javax.enterprise.context.ApplicationScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Named(value = "fileUpload")
@ViewScoped
public class FileUpload implements Serializable {


    private FileUpload[] daysOfTheWeek = new FileUpload[5];
    //file path's
    private static final String tempPath = "/a/b/c";
    private static final String uploadPath = "/home/gob/webSheets/";
    //upload vars
    private Part file;

    private static final String[] FINAL_ITEMS = {"Cold Trays", "Bread", "Fruit", "Allergy", "Protein", "Starch", "Veggie"};

    private static List<String> formList = Arrays.asList("lunch", "brk", "snk");
    //item input Strings
        //Lunch
    private String item0;
    private String item1;
    private String item2;
    private String item3;
    private String item4;
    private String item5;
    private String item6;
    private String item7;
    private String item8;
    private String item9;
    private String[] lunchArray;
        //Breakfast
    private String brkItem0, brkItem1, brkItem2, brkItem3, brkItem4, brkItem5, brkItem6;
    private String[] brkArray;
        //Snack
    private String snkItem0, snkItem1, snkItem2, snkItem3, snkItem4, snkItem5,snkItem6;
    private String[] snkArray;
    //2D items Array
    private String[][] items;
    private String day;
    //private String downloadFileName;
    //last index of [0] = kids meals // [1] = lunch meals
    private int[] columnAbsolutes = new int[2];
    private int lunch = 0, adult = 0;

    private List<String> days = Arrays.asList("Select Day", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday");

    public FileUpload(){

    }


    // after save run Calcs
    public void upload() throws IOException{
        setArrays();
        File workingFile;
        InputStream iStream = file.getInputStream();
        workingFile = new File(uploadPath, file.getSubmittedFileName());
        Files.copy(iStream, workingFile.toPath());
        String downloadFileName;
        try {
            downloadFileName = new XSSFWorkbookParser(workingFile, day).parseCopy(items,
                    new int[]{columnAbsolutes[0], columnAbsolutes[0] + columnAbsolutes[1]});
            workingFile.delete();
            download(FacesContext.getCurrentInstance(), downloadFileName);
        }catch (Exception e){
        workingFile.delete();
        }
    }

    protected static void download(FacesContext fContext, String downloadFileName) throws IOException {
        //FacesContext fContext = FacesContext.getCurrentInstance();
        ExternalContext eContext = fContext.getExternalContext();
        File downloadFile = new File(downloadFileName);
        eContext.responseReset();
        eContext.setResponseContentType(eContext.getMimeType(downloadFile.getName()));
        eContext.setResponseContentLength((int)downloadFile.length());
        eContext.setResponseHeader("Content-Disposition", "attachment; filename=\"" + downloadFile.getName() + "\"");
        OutputStream outStream = eContext.getResponseOutputStream();
        Files.copy(downloadFile.toPath(), outStream);
        //fContext.responseComplete();
        downloadFile.delete();
    }

    private void setArrays(){
        lunchArray = new String [] {item0, item1, item2, item3, item4, item5, item6, item7, item8, item9};
        brkArray = new String[] {brkItem0, brkItem1, brkItem2, brkItem3, brkItem4, brkItem5, brkItem6};
        snkArray = new String[] {snkItem0, snkItem1, snkItem2, snkItem3, snkItem4, snkItem5, snkItem6};
        items = new String[][] {getArray(lunchArray, true), getArray(brkArray, false),
                getArray(snkArray, false)};
    }

    private String[] getArray(String[] array, boolean lunch){
        int count;
        if(lunch)
            count = 10;
        else
            count = 8;
        for(int z = array.length - 1; z >= 0; z--){
            if(array[z] == null)
                count--;
        }
        if(lunch)
            count += FINAL_ITEMS.length;
        String[] returnArray = new String[count];
        for(int z = 0; z < count; z++){
            if(lunch) {
                returnArray[z] = z < 4 ? FINAL_ITEMS[z] : z < returnArray.length + 4 - FINAL_ITEMS.length
                        ? array[z - 4] : FINAL_ITEMS[z - (returnArray.length - FINAL_ITEMS.length)];
            }else{
                returnArray[z] = z < 1 ? FINAL_ITEMS[z] : array[z - 1];
            }
        }
        return returnArray;
    }

    //Upload Getters/ Setters
    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        if(file.getContentType().equals("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
            this.file = file;
    }



    //Breakfast, Lunch, and Snack Setters


    public void setItem0(String item0) {
        this.item0 = item0;
    }

    public void setItem1(String item1) {
        this.item1 = item1;
    }

    public void setItem2(String item2) {
        this.item2 = item2;
    }

    public void setItem3(String item3) {
        this.item3 = item3;
    }

    public void setItem4(String item4) {
        this.item4 = item4;
    }

    public void setItem5(String item5) {
        this.item5 = item5;
    }

    public void setItem6(String item6) {
        this.item6 = item6;
    }

    public void setItem7(String item7) {
        this.item7 = item7;
    }

    public void setItem8(String item8) {
        this.item8 = item8;
    }

    public void setItem9(String item9) {
        this.item9 = item9;
    }

    public void setBrkItem0(String brkItem0) {
        this.brkItem0 = brkItem0;
    }

    public void setBrkItem1(String brkItem1) {
        this.brkItem1 = brkItem1;
    }

    public void setBrkItem2(String brkItem2) {
        this.brkItem2 = brkItem2;
    }

    public void setBrkItem3(String brkItem3) {
        this.brkItem3 = brkItem3;
    }

    public void setBrkItem4(String brkItem4) {
        this.brkItem4 = brkItem4;
    }

    public void setBrkItem5(String brkItem5) {
        this.brkItem5 = brkItem5;
    }

    public void setBrkItem6(String brkItem6) {
        this.brkItem6 = brkItem6;
    }

    public void setSnkItem0(String snkItem0) {
        this.snkItem0 = snkItem0;
    }

    public void setSnkItem1(String snkItem1) {
        this.snkItem1 = snkItem1;
    }

    public void setSnkItem2(String snkItem2) {
        this.snkItem2 = snkItem2;
    }

    public void setSnkItem3(String snkItem3) {
        this.snkItem3 = snkItem3;
    }

    public void setSnkItem4(String snkItem4) {
        this.snkItem4 = snkItem4;
    }

    public void setSnkItem5(String snkItem5) {
        this.snkItem5 = snkItem5;
    }

    public void setSnkItem6(String snkItem6) {
        this.snkItem6 = snkItem6;
    }

    public String getItem0() {
        return item0;
    }

    public String getItem1() {
        return item1;
    }

    public String getItem2() {
        return item2;
    }

    public String getItem3() {
        return item3;
    }

    public String getItem4() {
        return item4;
    }

    public String getItem5() {
        return item5;
    }

    public String getItem6() {
        return item6;
    }

    public String getItem7() {
        return item7;
    }

    public String getItem8() {
        return item8;
    }

    public String getItem9() {
        return item9;
    }

    public String getBrkItem0() {
        return brkItem0;
    }

    public String getBrkItem1() {
        return brkItem1;
    }

    public String getBrkItem2() {
        return brkItem2;
    }

    public String getBrkItem3() {
        return brkItem3;
    }

    public String getBrkItem4() {
        return brkItem4;
    }

    public String getBrkItem5() {
        return brkItem5;
    }

    public String getBrkItem6() {
        return brkItem6;
    }

    public String getSnkItem0() {
        return snkItem0;
    }

    public String getSnkItem1() {
        return snkItem1;
    }

    public String getSnkItem2() {
        return snkItem2;
    }

    public String getSnkItem3() {
        return snkItem3;
    }

    public String getSnkItem4() {
        return snkItem4;
    }

    public String getSnkItem5() {
        return snkItem5;
    }

    public String getSnkItem6() {
        return snkItem6;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public int getLunch() {
        return lunch;
    }

    public int getAdult() {
        return adult;
    }

    public void setLunch(int lunch) {
        columnAbsolutes[0] = lunch + 6;
    }

    public void setAdult(int adult) {
        columnAbsolutes[1] = adult;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }
}
