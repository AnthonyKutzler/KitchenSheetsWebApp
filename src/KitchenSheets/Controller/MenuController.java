package KitchenSheets.Controller;

import org.apache.poi.hwpf.HWPFDocument;

import javax.servlet.http.Part;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
            doc = new HWPFDocument(new FileInputStream(workingFile));
            String docText = doc.getText().toString();
            String[] initialSplit = docText.split("\u0007");
            for (int z = 0; z < initialSplit.length; z++) {
                initialSplit[z] = initialSplit[z].trim();
                if (!initialSplit[z].equals("")) {
                    if (Character.isDigit(initialSplit[z].charAt(0))) {
                        String[] dates = initialSplit[z].split("   ");
                        for (int y = 0; y < dates.length; y++) {
                            if (!dates[y].contains("(closed)")) {
                                new DbController().insertOrUpdate(dates[y], menu,
                                        initialSplit[z + MenuDateOffset].replace("\r", ","));
                            }
                        }
                    }
                }
            }
        }catch (Exception e){}
    }
}
