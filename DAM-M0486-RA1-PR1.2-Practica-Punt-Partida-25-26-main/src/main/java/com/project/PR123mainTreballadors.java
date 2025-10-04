package com.project;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import com.project.excepcions.IOFitxerExcepcio;
import com.project.utilitats.UtilsCSV;

public class PR123mainTreballadors {
    private String filePath = System.getProperty("user.dir") + "/data/PR123treballadors.csv";
    private Scanner scanner = new Scanner(System.in);

    // Getters i setters per a filePath
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void iniciar() {
        boolean sortir = false;

        while (!sortir) {
            try {
                // Mostrar menú
                mostrarMenu();

                // Llegir opció de l'usuari
                int opcio = Integer.parseInt(scanner.nextLine());

                switch (opcio) {
                    case 1 -> mostrarTreballadors();
                    case 2 -> modificarTreballadorInteractiu();
                    case 3 -> {
                        System.out.println("Sortint...");
                        sortir = true;
                    }
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IOFitxerExcepcio e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    // Mètode que mostra el menú
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió de Treballadors");
        System.out.println("1. Mostra tots els treballadors");
        System.out.println("2. Modificar dades d'un treballador");
        System.out.println("3. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    // Mètode per mostrar els treballadors llegint el fitxer CSV
    public void mostrarTreballadors() throws IOFitxerExcepcio {
        // Comprobamos que el archivo en si existe primeramente 
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOFitxerExcepcio("El fitxer CSV no existeix: " + filePath);
        }
        // en caso de que pase el filtro, guardamos el contenido en una lista
        List<String> treballadorsCSV = llegirFitxerCSV();

        // hacemos comprobacion de qu etenga datos dentro
        if (treballadorsCSV.isEmpty()) {
            System.out.println("No data inside.");
            return;
        }  
        //Como sabemos que siempre las primeras lineas es la cabecera d elos datos como norma general la manipulamos de la siguiente forma
        // para que quede esteticamente bien
        String[] headers = treballadorsCSV.get(0).split(",");
        System.out.printf("%-5s %-10s %-10s %-12s %-8s\n", headers[0], headers[1], headers[2], headers[3], headers[4]);
        System.out.println("-".repeat(60));

        for (int i =1;i<treballadorsCSV.size();i++){
            String[] campos= treballadorsCSV.get(i).split(",");
            System.out.printf("%-5s %-10s %-10s %-12s %-8s\n",
            campos[0], campos[1], campos[2], campos[3], campos[4]);
        }

    }

    // Mètode per modificar un treballador (interactiu)
    public void modificarTreballadorInteractiu() throws IOFitxerExcepcio {
        
        // Leemos los datos para comprobar que no este vacio
        List<String> treballadorsCSV = llegirFitxerCSV();
        if (treballadorsCSV.isEmpty()) {
            throw new IOFitxerExcepcio("El fitxer està buit.");
        }
        // guardamos el header 
        String[] headers = treballadorsCSV.get(0).split(",");

        // variable string para guardar el id que usaremos luego, y para iniciar el bucle
        String id="";
        boolean existeix = false;

        while (!existeix) {
            System.out.print("\nIntrodueix l'ID del treballador que vols modificar: ");
            id = scanner.nextLine().trim();

            // Comprobar si existeix
            for (int i = 1; i < treballadorsCSV.size(); i++) {
                String[] camps = treballadorsCSV.get(i).split(",");
                if (camps[0].trim().equals(id)) {
                    existeix = true;
                    break;
                }
            }
            if (!existeix) {
                System.out.println("Id de trabajador inexistente o no valido.");
            }
        }

        boolean columnaValida=false;
        int colIndex =-1;
        String nouValor="";
        while (!columnaValida) {
            System.out.print("Quina dada vols modificar (Nom, Cognom, Departament, Salari)? ");
            String columna = scanner.nextLine().trim();

            for (int i = 0; i < headers.length; i++) {
                if (headers[i].equalsIgnoreCase(columna)) {
                    colIndex = i;
                    break;
                }
            }

            // Comprobamos si es una columna válida
            if (colIndex == 0) {
                System.out.println("No se puede modificar el id del trabjador.");
                colIndex = -1; // forzamos repetir
            }

            if (colIndex != -1) {
                boolean valorValido = false;   
                
                while (!valorValido) {
                    System.out.print("Introdueix el nou valor per a " + headers[colIndex] + ": ");
                    nouValor = scanner.nextLine().trim();

                    if (nouValor.isEmpty()) {
                        System.out.println("No se admiten valores vacios");
                        continue;
                    }

                // Si es salario, comprobamos que sea un numero
                    if (headers[colIndex].equalsIgnoreCase("Salari")) {
                        try {
                            Double.parseDouble(nouValor); 
                        } catch (NumberFormatException e) {
                            System.out.println("Salario dee de ser Numero valido.");
                            continue;
                        }
                        // comprobamos que el departamento sea un int
                    }else if (headers[colIndex].equalsIgnoreCase("Departament")) {
                        try {
                            Integer.parseInt(nouValor); 
                        } catch (NumberFormatException e) {
                            System.out.println("Id de departamento Invalido.");
                            continue; 
                        }}
                    valorValido = true; 
                }
                columnaValida = true; 
            } else {
                System.out.println("Columna no valida, columna inexistente");
            }
            }
        // Guardamos el valor ya que devolvemos un int y queremos el nombre la columna
        String columnaNombre = headers[colIndex];
        // llamamos al modificador
        modificarTreballador(id, columnaNombre, nouValor);
        }

    // Mètode que modifica treballador (per a tests i usuaris) llegint i escrivint sobre disc
    public void modificarTreballador(String id, String columna, String nouValor) throws IOFitxerExcepcio {
        // Leer todos los datos del CSV
        List<String> treballadorsCSV = llegirFitxerCSV();
      
      // Obtener índice de la columna (ya sabemos que existe)
        String[] headers = treballadorsCSV.get(0).split(",");
        int colIndex = -1;
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equalsIgnoreCase(columna)) {
                colIndex = i;
                break;
            }
        }
         // Iteramos para encontrar el id del trajador, el campo y aplciar el cambio par aluego salir con el break
        for (int i = 1; i < treballadorsCSV.size(); i++) {
            String[] campos = treballadorsCSV.get(i).split(",");
            if (campos[0].trim().equalsIgnoreCase(id.trim())) {
                campos[colIndex] = nouValor.trim();  
                treballadorsCSV.set(i, String.join(",", campos));  
                break;  
            }
        }
        // Guardar los cambios en el CSV
        escriureFitxerCSV(treballadorsCSV);
        System.out.println("Guardado exitosamente.");
    }


    // Encapsulació de llegir el fitxer CSV
    private List<String> llegirFitxerCSV() throws IOFitxerExcepcio {
        List<String> treballadorsCSV = UtilsCSV.llegir(filePath);
        if (treballadorsCSV == null) {
            throw new IOFitxerExcepcio("Error en llegir el fitxer.");
        }
        return treballadorsCSV;
    }

    // Encapsulació d'escriure el fitxer CSV
    private void escriureFitxerCSV(List<String> treballadorsCSV) throws IOFitxerExcepcio {
        try {
            UtilsCSV.escriure(filePath, treballadorsCSV);
        } catch (Exception e) {
            throw new IOFitxerExcepcio("Error en escriure el fitxer.", e);
        }
    }

    // Mètode main
    public static void main(String[] args) {
        PR123mainTreballadors programa = new PR123mainTreballadors();
        programa.iniciar();
    }    
}
