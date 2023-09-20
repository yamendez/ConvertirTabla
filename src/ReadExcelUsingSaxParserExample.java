import java.io.File;
import java.io.FileWriter;
import java.net.URL;

public class ReadExcelUsingSaxParserExample {
    public static void main(String[] args) throws Exception{
        URL url = ReadExcelUsingSaxParserExample.class
                .getClassLoader()
                .getResource("Java_demo.xlsx");

        //URL url = ReadExcelUsingSaxParserExample.class.getResource("TABLA PCDT082B.xlsx");

        File file = new File("D:/Cursos/Java/IdeaProjects/ConvertirTabla/src/prueba.txt");
        FileWriter escribir = new FileWriter(file);

        String camposNumericos = "1";
        boolean nomCampos = true;
        String nomTabla = "PRUEBA";
        boolean insertRadioButton = true;
        boolean deleteRadioButton = false;


        new ExcelReaderHandler2(file, escribir, camposNumericos, nomCampos, nomTabla).readExcelFile(new File(url.toURI()));

        //escribir.write("\n\nFin de la hoja");
        escribir.close();
    }
}
