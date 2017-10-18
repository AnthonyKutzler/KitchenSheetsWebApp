package KitchenSheets.Controller;

import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Map;

public class MenuController {

    private static final String tempPath = "/home/gob/webSheets/";
    private static final int MenuDateOffset = 6;

    public MenuController(Map<String, Part> fileMap){
        for(Map.Entry<String, Part> entry : fileMap.entrySet())
            upload(entry.getKey(), entry.getValue());
    }

    public void upload(String menu, Part file){

        HWPFDocument doc;
        try {
            InputStream iStream = file.getInputStream();
            File workingFile = new File(tempPath, file.getSubmittedFileName());
            Files.copy(iStream, workingFile.toPath());
            if(workingFile.getAbsolutePath().substring(workingFile.getAbsolutePath().length() - 3,
                    workingFile.getAbsolutePath().length()).equals("doc"))
                parseDoc(menu, new HWPFDocument(new FileInputStream(workingFile)).getText().toString());
            else {
                XWPFDocument docx = new XWPFDocument(iStream);
                parseDoc(menu, new XWPFWordExtractor(docx).getText());//new XWPFDocument(new FileInputStream(workingFile)));

            }
            Files.delete(workingFile.toPath());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void parseDoc(String menu, String text){
        String[] initialSplit = text.split("\u0007");
        for (int z = 0; z < initialSplit.length; z++) {
            initialSplit[z] = initialSplit[z].trim();
            if (!initialSplit[z].equals("")) {
                if (Character.isDigit(initialSplit[z].charAt(0))) {
                    String[] dates = initialSplit[z].split("   ");
                    if(dates.length < 2)
                        dates = initialSplit[z].split(" ");
                    for (int y = 0; y < dates.length; y++) {
                        if (!dates[y].contains("(closed)")) {
                            if (menu.equals("adult")) {
                                new MenuDatabase().insertOrUpdate(dates[y].trim(), "adult1",
                                        initialSplit[z + (MenuDateOffset*2)].replace("\r", "/")
                                                .replace("\u000B", "/"));
                                new MenuDatabase().insertOrUpdate(dates[y].trim(), "adult2",
                                        initialSplit[z + (MenuDateOffset*4)].replace("\r", "/")
                                                .replace("\u000B", "/"));

                            } else {
                                new MenuDatabase().insertOrUpdate(dates[y].trim(), menu,
                                        initialSplit[z + MenuDateOffset].replace("\r", "/")
                                                .replace("\u000B", "/"));
                            }
                        }
                    }
                }
            }
        }
    }
}
