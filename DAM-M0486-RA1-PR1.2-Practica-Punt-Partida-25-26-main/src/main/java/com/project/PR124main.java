package com.project;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class PR124main {

    // Constants que defineixen l'estructura d'un registre
    private static final int ID_SIZE = 4; // Número de registre: 4 bytes
    private static final int NAME_MAX_BYTES = 40; // Nom: màxim 20 caràcters (40 bytes en UTF-8)
    private static final int GRADE_SIZE = 4; // Nota: 4 bytes (float)

    // Posicions dels camps dins el registre
    private static final int NAME_POS = ID_SIZE; // El nom comença just després del número de registre
    private static final int GRADE_POS = NAME_POS + NAME_MAX_BYTES; // La nota comença després del nom

    // Atribut per al path del fitxer
    private String filePath;

    private Scanner scanner = new Scanner(System.in);

    // Constructor per inicialitzar el path del fitxer
    public PR124main() {
        this.filePath = System.getProperty("user.dir") + "/data/PR124estudiants.dat"; // Valor per defecte
    }

    // Getter per al filePath
    public String getFilePath() {
        return filePath;
    }

    // Setter per al filePath
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public static void main(String[] args) {
        PR124main gestor = new PR124main();
        boolean sortir = false;

        while (!sortir) {
            try {
                gestor.mostrarMenu();
                int opcio = gestor.getOpcioMenu();

                switch (opcio) {
                    case 1 -> gestor.llistarEstudiants();
                    case 2 -> gestor.afegirEstudiant();
                    case 3 -> gestor.consultarNota();
                    case 4 -> gestor.actualitzarNota();
                    case 5 -> sortir = true;
                    default -> System.out.println("Opció no vàlida.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Si us plau, introdueix un número vàlid.");
            } catch (IOException e) {
                System.out.println("Error en la manipulació del fitxer: " + e.getMessage());
            }
        }
    }

    // Mostrar menú d'opcions
    private void mostrarMenu() {
        System.out.println("\nMenú de Gestió d'Estudiants");
        System.out.println("1. Llistar estudiants");
        System.out.println("2. Afegir nou estudiant");
        System.out.println("3. Consultar nota d'un estudiant");
        System.out.println("4. Actualitzar nota d'un estudiant");
        System.out.println("5. Sortir");
        System.out.print("Selecciona una opció: ");
    }

    // Obtenir la selecció del menú
    private int getOpcioMenu() {
        return Integer.parseInt(scanner.nextLine());
    }

    // Mètode per llistar tots els estudiants
    public void llistarEstudiants() throws IOException {
        llistarEstudiantsFitxer();
    }

    // Mètode per afegir un nou estudiant
    public void afegirEstudiant() throws IOException {
        int registre = demanarRegistre();
        String nom = demanarNom();
        float nota = demanarNota();
        afegirEstudiantFitxer(registre, nom, nota);
    }

    // Mètode per consultar la nota
    public void consultarNota() throws IOException {
        int registre = demanarRegistre();
        consultarNotaFitxer(registre);
    }

    // Mètode per actualitzar la nota
    public void actualitzarNota() throws IOException {
        int registre = demanarRegistre();
        float novaNota = demanarNota();
        actualitzarNotaFitxer(registre, novaNota);
    }

     // Funcions per obtenir input de l'usuari
     private int demanarRegistre() {
        System.out.print("Introdueix el número de registre (enter positiu): ");
        int registre = Integer.parseInt(scanner.nextLine());
        if (registre < 0) {
            throw new IllegalArgumentException("El número de registre ha de ser positiu.");
        }
        return registre;
    }

    private String demanarNom() {
        System.out.print("Introdueix el nom: ");
        return scanner.nextLine();
    }

    private float demanarNota() {
        System.out.print("Introdueix la nota (valor entre 0 i 10): ");
        float nota = Float.parseFloat(scanner.nextLine());
        if (nota < 0 || nota > 10) {
            throw new IllegalArgumentException("Nota debe ser del 0 al 10.");
        }
        return nota;
    }

    // Mètode per trobar la posició d'un estudiant al fitxer segons el número de registre
    private long trobarPosicioRegistre(RandomAccessFile raf, int registreBuscat) throws IOException {
        //Nos posiionamos al principo del fichero
        raf.seek(0);
        // Mientras no lleguemos al final del fichero sguiremos con el bucle
        while (raf.getFilePointer() < raf.length()) {
                // leemos y guardamos los datos
                long pos = raf.getFilePointer();
                int registreActual = raf.readInt();
                // Si es igual, guardamoso el valor y lo pamos con el retunr
                if (registreActual == registreBuscat) {
                    return pos;
                }
                // Si no coincide, saltamos 
                raf.skipBytes(NAME_MAX_BYTES + GRADE_SIZE);
            }

            throw new IOException("Estudiante inexistente: " + registreBuscat);
        }


    // Operacions amb fitxers
    // Mètode que manipula el fitxer i llista tots els estudiants (independent per al test)
    public void llistarEstudiantsFitxer() throws IOException {
         // Abrimos el fichero en modo lectura/escritura (rw) por si en el futuro queremos actualizar
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            raf.seek(0);
            System.out.printf("%-10s %-20s %-6s\n", "ID", "Nom", "Nota");
            System.out.println("--------------------------------------");
        // estamos en el bucle hasta que lleguemos al final del archivo
        while (raf.getFilePointer() < raf.length()) {
            int registre = raf.readInt();
            String nom = llegirNom(raf);
            float nota = raf.readFloat();

            // Mostramos los datos guardados de cada alumno en las variables
            System.out.printf("%-10d %-20s %-6.2f\n", registre, nom, nota);
        }
        }
    }

    // Mètode que manipula el fitxer i afegeix l'estudiant
    public void afegirEstudiantFitxer(int registre, String nom, float nota) throws IOException {
    // Abrimos el fichero en modo lectura/escritura
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) {
            // Nos posicionamos al final del fichero para añadir un nuevo registro
            raf.seek(raf.length());
            raf.writeInt(registre);
            escriureNom(raf, nom);
            raf.writeFloat(nota);
        }  
      }

    // Mètode que manipula el fitxer i consulta la nota d'un estudiant
    public void consultarNotaFitxer(int registre) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "r")) { // Solo lectura
                long totalRegistres = raf.length() / (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE);
                boolean trobat = false;
                for (int i = 0; i < totalRegistres; i++) {
                    raf.seek(i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE));
                    int registreActual = raf.readInt(); 
                    String nom = llegirNom(raf);         
                    float nota = raf.readFloat();       
                    if (registreActual == registre) {
                        System.out.printf("Estudiant trobat: %d - %s - Nota: %.2f\n", registreActual, nom, nota);
                        trobat = true;
                        break;
                    }
                }
                if (!trobat) {
                    System.out.println("Estudiante inecistente.");
                }
            }
    }

    // Mètode que manipula el fitxer i actualitza la nota d'un estudiant
    public void actualitzarNotaFitxer(int registre, float novaNota) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(filePath, "rw")) { 
                long totalRegistres = raf.length() / (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE);
                boolean trobat = false;

                for (int i = 0; i < totalRegistres; i++) {
                    raf.seek(i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE));
                    int registreActual = raf.readInt(); 
                    if (registreActual == registre) {
                        raf.seek(i * (ID_SIZE + NAME_MAX_BYTES + GRADE_SIZE) + GRADE_POS);
                        raf.writeFloat(novaNota); 
                        System.out.println("Nota actualizada " + registre);
                        trobat = true;
                        break;
                    }
                }
                if (!trobat) {
                    System.out.println("Alumno inexitente.");
                }
        }   
    }

    // Funcions auxiliars per a la lectura i escriptura del nom amb UTF-8
    private String llegirNom(RandomAccessFile raf) throws IOException {
        byte[] nomBytes = new byte[NAME_MAX_BYTES];   
        raf.readFully(nomBytes);                    
        // Convertimos los bytes a String UTF-8 y eliminamos posibles caracteres de relleno al final
        String nom = new String(nomBytes, StandardCharsets.UTF_8).trim();
        return nom;
    }

    private void escriureNom(RandomAccessFile raf, String nom) throws IOException {
        byte[] nomBytes = nom.getBytes(StandardCharsets.UTF_8);

        if (nomBytes.length > NAME_MAX_BYTES) {
            // Si el nombre ocupa más de 40 bytes, recortamos
            raf.write(nomBytes, 0, NAME_MAX_BYTES);
        } else {
            // Si ocupa menos, escribimos y rellenamos con ceros
            raf.write(nomBytes);
            for (int i = nomBytes.length; i < NAME_MAX_BYTES; i++) {
                raf.writeByte(0); 
            }
        } 
    }
}
