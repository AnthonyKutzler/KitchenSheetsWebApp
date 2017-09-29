package KitchenSheets.Model;



import KitchenSheets.Controller.MenuController;

import javax.faces.view.ViewScoped;
import javax.inject.Named;
import javax.servlet.http.Part;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.*;

@Named(value = "menuUpload")
@ViewScoped
public class MenuUpload implements Serializable {

    private File menuListFile = new File("/home/gob/webSheets/menu-list.txt");
    private Part lunch, sub, vegetarian, adult, breakfast, uBreakfast, snack, uSnack, hsSnack;
    private String uploadedMenus;

    enum MENU {
        LUNCH,
        SUB,
        VEGETARIAN,
        ADULT,
        BREAKFAST,
        UBREAKFAST,
        SNACK,
        USNACK,
        HSSNAK
    }


    public MenuUpload(){}

    public void uploadMenus(){
        Map<String, Part> fileMap = new HashMap<>();
        if(lunch != null){
            fileMap.put("lunch", lunch);
            writeToFile(MENU.LUNCH, lunch.getName());
        }
        if(sub != null){
            fileMap.put("sub", sub);
            writeToFile(MENU.SUB, sub.getName());
        }
        if(vegetarian != null){
            fileMap.put("vegetarian", vegetarian);
            writeToFile(MENU.VEGETARIAN, vegetarian.getName());
        }
        if(adult != null){
            fileMap.put("adult", adult);
            writeToFile(MENU.ADULT, adult.getName());
        }
        if(breakfast != null){
            fileMap.put("breakfast", breakfast);
            writeToFile(MENU.BREAKFAST, breakfast.getName());
        }
        if (uBreakfast != null){
            fileMap.put("ubreakfast", uBreakfast);
            writeToFile(MENU.UBREAKFAST, uBreakfast.getName());
        }
        if(snack != null){
            fileMap.put("snack", snack);
            writeToFile(MENU.SNACK, snack.getName());
        }
        if(uSnack != null){
            fileMap.put("usnack", uSnack);
            writeToFile(MENU.USNACK, uSnack.getName());
        }
        if(hsSnack != null){
            fileMap.put("hssnack", hsSnack);
            writeToFile(MENU.HSSNAK, hsSnack.getName());
        }
        new MenuController(fileMap);
    }

    private void writeToFile(MENU menu, String fileName){
        int line = 10;
        switch (menu){
            case LUNCH:
                line = 0;
                break;
            case SUB:
                line = 1;
                break;
            case VEGETARIAN:
                line = 2;
                break;
            case ADULT:
                line = 3;
                break;
            case BREAKFAST:
                line = 4;
                break;
            case UBREAKFAST:
                line = 5;
                break;
            case SNACK:
                line = 6;
                break;
            case USNACK:
                line = 7;
                break;
            case HSSNAK:
                line = 8;
                break;
        }
        try {
            List<String> fileLines = Files.readAllLines(menuListFile.toPath());
            fileLines.set(line, fileName);
            Files.write(menuListFile.toPath(), fileLines);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private List<String> getMenuNames() throws Exception{
        return Files.readAllLines(menuListFile.toPath());
    }

    public String getUploadedMenus() throws Exception {
        return uploadedMenus;
    }

    public void setUploadedMenus(String uploadedMenus) throws Exception {
        List<String> menuList = getMenuNames();
        StringBuilder menuNames = new StringBuilder("");
        for(String name : menuList)
            menuNames.append(name).append("&lt;br/&gt;");
        this.uploadedMenus = menuNames.toString();
    }

    public Part getLunch() {
        return lunch;
    }

    public void setLunch(Part lunch) {
        this.lunch = lunch;
    }

    public Part getSub() {
        return sub;
    }

    public void setSub(Part sub) {
        this.sub = sub;
    }

    public Part getVegetarian() {
        return vegetarian;
    }

    public void setVegetarian(Part vegetarian) {
        this.vegetarian = vegetarian;
    }

    public Part getAdult() {
        return adult;
    }

    public void setAdult(Part adult) {
        this.adult = adult;
    }

    public Part getBreakfast() {
        return breakfast;
    }

    public void setBreakfast(Part breakfast) {
        this.breakfast = breakfast;
    }

    public Part getSnack() {
        return snack;
    }

    public void setSnack(Part snack) {
        this.snack = snack;
    }

    public Part getuBreakfast() {
        return uBreakfast;
    }

    public void setuBreakfast(Part uBreakfast) {
        this.uBreakfast = uBreakfast;
    }

    public Part getuSnack() {
        return uSnack;
    }

    public void setuSnack(Part uSnack) {
        this.uSnack = uSnack;
    }

    public Part getHsSnack() {
        return hsSnack;
    }

    public void setHsSnack(Part hsSnack) {
        this.hsSnack = hsSnack;
    }
}
