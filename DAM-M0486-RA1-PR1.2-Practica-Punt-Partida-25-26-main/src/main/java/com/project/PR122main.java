package com.project;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.project.excepcions.IOFitxerExcepcio;
import com.project.objectes.PR122persona;

public class PR122main {
    private static String filePath = System.getProperty("user.dir") + "/data/PR122persones.dat";

    public static void main(String[] args) {
        List<PR122persona> persones = new ArrayList<>();
        persones.add(new PR122persona("Maria", "López", 36));
        persones.add(new PR122persona("Gustavo", "Ponts", 63));
        persones.add(new PR122persona("Irene", "Sales", 54));

        try {
            serialitzarPersones(persones);
            List<PR122persona> deserialitzades = deserialitzarPersones();
            deserialitzades.forEach(System.out::println);  // Mostra la informació per pantalla
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    // Mètode per serialitzar la llista de persones
    public static void serialitzarPersones(List<PR122persona> persones) throws IOFitxerExcepcio {
        // simplemente mediante trycatch utilizamos el objectutput y escribimos y guardamos.
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(persones); 
            } catch (IOException e) {
                throw new IOFitxerExcepcio("Error escrivint l'objecte al fitxer: " + e.getMessage());
            }

    }

    // Mètode per deserialitzar la llista de persones
    public static List<PR122persona> deserialitzarPersones() throws IOFitxerExcepcio {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOFitxerExcepcio("Fichero inexistente: " + filePath);
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            return (List<PR122persona>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new IOFitxerExcepcio("Error llegint l'objecte del fitxer: " + e.getMessage());
        }
    }


    // Getter i Setter per a filePath (opcional)
    public static String getFilePath() {
        return filePath;
    }

    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }
}
