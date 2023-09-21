import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConvertirTabla extends JFrame{
    private JTextField txtNomTabla;
    private JTextField txtNumeros;
    private JPanel panelMain;
    private JTextField txtBuscar;
    private JButton btnBuscar;
    private JButton btnEjecutar;
    private JCheckBox chbAgreAtri;
    private JRadioButton insertarRadioButton;
    private JRadioButton eliminarRadioButton;
    private JTextField txtNomHoja;
    private String nomTabla, campNumeros, direccion, nomHoja;
    private File directorio;



    public ConvertirTabla() {
        btnBuscar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fc = new JFileChooser();

                File dirActual = new File(System.getProperty("user.dir"));

                fc.setCurrentDirectory(dirActual);

                FileNameExtensionFilter filter = new FileNameExtensionFilter("Excel file", "xls","xlsx");

                fc.addChoosableFileFilter(filter);
                fc.setFileFilter(filter);

                int seleccion = fc.showOpenDialog(fc);


                if(seleccion == JFileChooser.APPROVE_OPTION){

                    File fichero = fc.getSelectedFile();
                    txtBuscar.setText(fichero.getAbsolutePath());
                    //direccion = fichero.getAbsolutePath();
                    direccion = fichero.toURI().toString();
                    directorio = fichero.getParentFile();

                }
            }
        });
        btnEjecutar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    nomTabla = txtNomTabla.getText().toUpperCase();
                    campNumeros = txtNumeros.getText();
                    boolean checkbox = chbAgreAtri.isSelected();
                    nomHoja = txtNomHoja.getText();

                    Date d = new Date();
                    DateFormat df = new SimpleDateFormat("yyyyMMdd");

                    //XSSFSheet hoja = null;
                    Sheet hoja = null;
                    FileWriter escribir = null;

                    //Validando que exista una direccion de archivo
                    if(direccion == null || nomTabla.equals("") || nomHoja.equals("")){
                        JOptionPane.showMessageDialog(null,"Debe llenar todos los " +
                                "campos.","Error Campos Vacios", JOptionPane.ERROR_MESSAGE);
                    }else {

//                        File file = new File(nomTabla+"-"+df.format(d)+".txt");
                        File file = new File(directorio, nomTabla+"-"+df.format(d)+".txt");
                        escribir = new FileWriter(file, false);

                        URI uri = new URI(direccion);
                        URL url = uri.toURL();

                        new ExcelReaderHandler2(file, escribir, campNumeros, nomTabla, nomHoja,checkbox,
                                insertarRadioButton.isSelected(),eliminarRadioButton.isSelected()).readExcelFile(new File(url.toURI()));


                        escribir.close();
                        System.gc();

                        JOptionPane.showMessageDialog(null,"Operacion realizada correctamente",
                                "Mensaje", JOptionPane.INFORMATION_MESSAGE);

                        txtBuscar.setText("");
                        txtNomTabla.setText("");
                        txtNumeros.setText("");
                        txtNomHoja.setText("");
                    }

                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        eliminarRadioButton.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                chbAgreAtri.setEnabled(!eliminarRadioButton.isSelected());
            }
        });
    }
    /*public boolean columnaExite(int j, String[] arreglo){
        boolean b = false;
        for (String s : arreglo) {
            if (Integer.parseInt(s) == j) {
                b = true;
            }
            if(b) break;
        }
        return b;
    }

    *//**
     * Crea un txt con formato de insert en sql
     *//*
    public void insertarSQL(Sheet hoja, boolean checkbox, FileWriter escribir) throws IOException {
        int numero_filas = hoja.getLastRowNum();
        int numero_columnas = 0;
        for(int i = 0; i <= numero_filas; i++) {
            Row fila = hoja.getRow(i);

            numero_columnas = fila.getLastCellNum();
            //break;
        }

        String[][] lista_campos = new String[numero_filas+1][numero_columnas];// quite +1
        String[] seleccionColum = campNumeros.replace(" ", "").split(",");
        String mensajeCampos = "", mensajeValor = "";
        boolean columnasVacia = campNumeros.equals("");

        //Columna -1
        if(!columnasVacia){
            int[] columna = new int[seleccionColum.length];
            for (int i = 0; i < columna.length; i++) {
                columna[i] = Integer.parseInt(seleccionColum[i]) -1;
                seleccionColum[i] = String.valueOf(columna[i]);
            }
        }


        // Leer filas
        for(int i = 0; i <= numero_filas; i++){// igual agregado//quite +1
            Row fila = hoja.getRow(i);

            // Leer columnas
            for(int j = 0; j < numero_columnas; j++){
                Cell celda = fila.getCell(j);
                DataFormatter formatter = new DataFormatter();
                val = formatter.formatCellValue(celda) + "";

                lista_campos[i][j] = val;

            }

        }

        int cont = 0;

        //Convirtiendo a sentencia sql
        for(int i = 1; i < lista_campos.length; i++){
            for(int j = 0; j < lista_campos[i].length; j++) {

                mensajeCampos += lista_campos[0][j] + ", ";

                if(columnasVacia){
                    mensajeValor += "'"+lista_campos[i][j]+ "', ";
                }else if(columnaExite(j, seleccionColum)){
                    mensajeValor += lista_campos[i][j]+ ", ";
                    mensajeValor = mensajeValor.replace("\"","");

                }else{
                    mensajeValor += "'"+lista_campos[i][j]+ "', ";
                    mensajeValor = mensajeValor.replace("\"","");
                }

            }

            cont += 1;
            mensajeCampos = mensajeCampos.substring(0,mensajeCampos.length() - 2);
            mensajeValor = mensajeValor.substring(0, mensajeValor.length() - 2);

            if(checkbox){
                mensaje = "INSERT INTO " + nomTabla +
                        "("+mensajeCampos.toUpperCase()+") \nVALUES ("+mensajeValor.toUpperCase()+");\n";
            }else {
                mensaje = "INSERT INTO " + nomTabla +"\nVALUES ("+mensajeValor.toUpperCase()+");\n";
            }

            mensajeValor = "";
            mensajeCampos = "";
            escribir.write(mensaje+"\n");

            if(cont == 50){
                escribir.write("COMMIT;\n\n");
                cont = 0;
            } else if (i == (lista_campos.length - 1) && cont > 0) {
                escribir.write("COMMIT;");
            }
        }
    }

    public void eliminarSQL(Sheet hoja, FileWriter escribir) throws IOException{
        int numero_filas = hoja.getLastRowNum();
        int numero_columnas = 0;
        for(int i = 0; i <= numero_filas; i++) {
            Row fila = hoja.getRow(i);

            numero_columnas = fila.getLastCellNum();
            //break;
        }
        if(numero_columnas > 13){
            numero_columnas = 13;
        }

        String[][] lista_campos = new String[numero_filas+1][numero_columnas];// String[numero_filas+1][13]
        String[] seleccionColum = campNumeros.replace(" ", "").split(",");
        String mensajeCampos = "", mensajeValor = "";
        boolean columnasVacia = campNumeros.equals("");

        //Columna -1
        if(!columnasVacia){
            int[] columna = new int[seleccionColum.length];
            for (int i = 0; i < columna.length; i++) {
                columna[i] = Integer.parseInt(seleccionColum[i]) -1;
                seleccionColum[i] = String.valueOf(columna[i]);
            }
        }


        // Leer filas
        for(int i = 0; i <= numero_filas; i++){// igual agregado//quite +1
            Row fila = hoja.getRow(i);

            // Leer columnas
            for(int j = 0; j < numero_columnas; j++){
                Cell celda = fila.getCell(j);
                DataFormatter formatter = new DataFormatter();
                val = formatter.formatCellValue(celda) + "";

                lista_campos[i][j] = val;

            }

        }

        int cont = 0;

        //Convirtiendo a sentencia sql
        for(int i = 1; i < lista_campos.length; i++){
            for(int j = 0; j < lista_campos[i].length; j++) {

                if(columnaExite(j,seleccionColum)){
                    mensajeValor += lista_campos[0][j] + "=" +lista_campos[i][j]+" AND ";
                    mensajeValor = mensajeValor.replace("\"","");
                }else {
                    mensajeValor += lista_campos[0][j] + "=" + "'"+lista_campos[i][j]+"' AND ";
                }

            }

            cont += 1;
            mensajeValor = mensajeValor.substring(0, mensajeValor.length() - 5);

            mensaje = "DELETE FROM " + nomTabla +
                    "\nWHERE "+mensajeValor.toUpperCase()+";\n";

            mensajeValor = "";
            escribir.write(mensaje+"\n");

            if(cont == 50){
                escribir.write("COMMIT;\n\n");
                cont = 0;
            } else if (i == (lista_campos.length - 1) && cont > 0) {
                escribir.write("COMMIT;");
            }
        }
    }*/

    public static void main(String[] args){
        JFrame frame = new JFrame("Convertir Tabla");
        frame.setContentPane(new ConvertirTabla().panelMain);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 200);
        frame.setLocationRelativeTo(null);
        frame.pack();
        frame.setVisible(true);
    }
}
