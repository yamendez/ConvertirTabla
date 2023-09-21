import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.net.URL;

public class ReadExcelUsingSaxParserExample {
    public static void main(String[] args) throws Exception{
        URL url = ReadExcelUsingSaxParserExample.class
                .getClassLoader()
                .getResource("Java_demo.xlsx");

        String direccion ="file:/Cursos/Java/Prueba2.xlsx";

        URI uri = new URI(direccion);
        URL url2 = uri.toURL();

        //URL url = ReadExcelUsingSaxParserExample.class.getResource("TABLA PCDT082B.xlsx");

        File file = new File("D:/Cursos/Java/IdeaProjects/ConvertirTabla/src/prueba.txt");
        FileWriter escribir = new FileWriter(file);

        String camposNumericos = "";
        boolean nomCampos = true;
        String nomTabla = "PRUEBA";
        boolean insertRadioButton = false;
        boolean deleteRadioButton = true;
        String nomHoja="Hoja1";


        new ExcelReaderHandler2(file, escribir, camposNumericos, nomTabla, nomHoja, nomCampos,
                insertRadioButton, deleteRadioButton).readExcelFile(new File(url2.toURI()));

//        new ExcelReaderHandler2(file, escribir, camposNumericos, nomTabla, nomHoja, nomCampos,
//                insertRadioButton, deleteRadioButton).readExcelFile(new File(url.toURI()));

        //escribir.write("\n\nFin de la hoja");
        escribir.close();
    }
}
