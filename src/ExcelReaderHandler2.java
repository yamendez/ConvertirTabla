import java.io.*;

public class ExcelReaderHandler2 extends SheetHandler2{

    File file;
    int contador;
    boolean checkbox, insertRButton, deleteRButton;
    FileWriter escribir;
    String campos, mensaje, valor, campNumeros, nomTabla, nomHoja;
    String[] arrayCampos;
    String[] arrayValor;
    StringBuilder builder = new StringBuilder();

    public ExcelReaderHandler2(File file, FileWriter escribir, String camposNumericos, String nomTabla,
                               String nomHoja, boolean checkbox, boolean insertRButton, boolean deleteRButton){
        this.file = file;
        this.escribir = escribir;
        this.campNumeros = camposNumericos;
        this.checkbox = checkbox;
        this.nomTabla = nomTabla;
        this.insertRButton = insertRButton;
        this.deleteRButton = deleteRButton;
        this.nomHoja = nomHoja;
    }

    @Override
    protected void processRow() {
        //Get specific values here
        /*String a = rowValues.get("A");
         * String b = rowValues.get("B");*/
        try {
            if (insertRButton){
                insertarSQL();
            }else if (deleteRButton){
                eliminarSQL();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    @Override
    protected boolean processSheet(String sheetName) {
        //Decide which sheet to read; Return true for all sheets
        // return "Sheet 1".equals(sheetName);
        //*System.out.println("Processing start for sheet : " + sheetName);

            //escribir.write("Processing start for sheet : " + sheetName);
        return nomHoja.equals(sheetName);
    }

    @Override
    protected void startSheet() {
        //Any custom logic when a new sheet starts
        //*System.out.println("Sheet starts");

            //escribir.write("\nSheet stars");
    }

    @Override
    protected void endSheet() {
        //Any custom logic when a new sheet ends
        //*System.out.println("Sheet ends");
        try {
            escribir.write("COMMIT;\n");
            builder.delete(0, builder.length());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //escribir.close();
    }

    public boolean columnaExite(int j, String[] arreglo){
        boolean b = false;
        for (String s : arreglo) {
            if (Integer.parseInt(s) == j) {
                b = true;
            }
            if(b) break;
        }
        return b;
    }
    public void insertarSQL() throws IOException{
        // Obtiene la primera fila
        if(rowNumber == 1 && !header.isEmpty()){

            arrayCampos = header.values().toArray(new String[0]);

            for (String arrayCampo : arrayCampos) {
                builder.append(arrayCampo).append(", ");
            }
            campos = String.valueOf(builder).substring(0,builder.length()-2);

        } else if (rowNumber > 1 && !rowValues.isEmpty()) {

            //Print whole row

            arrayValor = rowValues.values().toArray(new String[0]);
            builder.delete(0, builder.length());

            String[] seleccionColum = campNumeros.replace(" ", "").split(",");
            boolean columnasVacia = campNumeros.equals("");

            // Crea un arreglo de los campos numericos;
            if(!columnasVacia){
                int[] columna = new int[seleccionColum.length];
                for (int i = 0; i < columna.length; i++) {
                    columna[i] = Integer.parseInt(seleccionColum[i]) -1;
                    seleccionColum[i] = String.valueOf(columna[i]);
                }
            }
            int cont = 0;

            // Agregando comillas a los campos string
            for (String valor : arrayValor) {

                if (columnasVacia) {
                    builder.append("'").append(valor.toUpperCase()).append("'").append(", ");
                } else if(columnaExite(cont ,seleccionColum)){
                    builder.append(valor.toUpperCase()).append(", ");
                } else {
                    builder.append("'").append(valor.toUpperCase()).append("'").append(", ");
                }
                cont++;
            }


//            for (String valor : arrayValor) {
//                builder.append("'").append(valor.toUpperCase()).append("'").append(", ");
//            }
            valor = String.valueOf(builder).substring(0,builder.length()-2);

            if (checkbox) {
                mensaje ="INSERT INTO "+nomTabla+" ("+campos+") \nVALUES ("+valor+");\n";
            } else {
                mensaje ="INSERT INTO "+nomTabla+" \nVALUES ("+valor+");\n";
            }

            //mensaje ="INSERT INTO TABLA PRUEBA "+campos+" \nVALUES ("+valor+");\n";

            escribir.write(mensaje.toUpperCase()+"\n");
            contador++;

            if (contador == 50){
                escribir.write("COMMIT;\n\n");
                contador = 0;
            }

        }
    }

    public void eliminarSQL() throws IOException{
        if(rowNumber == 1 && !header.isEmpty()){

            // Obtiene los nombres de las columnas
            arrayCampos = header.values().toArray(new String[0]);

            for (String arrayCampo : arrayCampos) {
                builder.append(arrayCampo).append(", ");
            }
            campos = String.valueOf(builder).substring(0,builder.length()-2);

        } else if (rowNumber > 1 && !rowValues.isEmpty()) {

            //Print whole row

            arrayValor = rowValues.values().toArray(new String[0]);
            builder.delete(0, builder.length());

            String[] seleccionColum = campNumeros.replace(" ", "").split(",");
            boolean columnasVacia = campNumeros.equals("");

            // Crea un arreglo de los campos numericos;
            if(!columnasVacia){
                int[] columna = new int[seleccionColum.length];
                for (int i = 0; i < columna.length; i++) {
                    columna[i] = Integer.parseInt(seleccionColum[i]) -1;
                    seleccionColum[i] = String.valueOf(columna[i]);
                }
            }
            //int cont = 0;

            // Agregando comillas a los campos string
            for(int i = 0; i < arrayCampos.length; i++){
                if (columnasVacia) {
                    builder.append(arrayCampos[i]).append("='".concat(arrayValor[i].concat("' and ")));
                } else if(columnaExite(i, seleccionColum)){
                    builder.append(arrayCampos[i]).append("=".concat(arrayValor[i].concat(" and ")));
                } else {
                    builder.append(arrayCampos[i]).append("='".concat(arrayValor[i].concat("' and ")));
                }
                //cont++;
                if (i == 12) break;
            }

            valor = String.valueOf(builder).substring(0,builder.length() - 5);

            mensaje = "DELETE FROM " + nomTabla
                    +"\nWHERE " + valor.toUpperCase() +";\n";

            escribir.write(mensaje.toUpperCase()+"\n");
            contador++;

            if (contador == 50){
                escribir.write("COMMIT;\n\n");
                contador = 0;
            }

        }
    }

}
