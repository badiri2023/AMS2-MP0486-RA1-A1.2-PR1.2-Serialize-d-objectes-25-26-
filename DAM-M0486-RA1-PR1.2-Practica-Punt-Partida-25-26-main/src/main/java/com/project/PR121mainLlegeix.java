package com.project;

import java.io.File;

import com.project.excepcions.IOFitxerExcepcio;
import com.project.objectes.PR121hashmap;

public class PR121mainLlegeix {
    private static String filePath = System.getProperty("user.dir") + "/data/PR121HashMapData.ser";

    public static void main(String[] args) {
        try {
            PR121hashmap hashMap = deserialitzarHashMap();
            hashMap.getPersones().forEach((nom, edat) -> System.out.println(nom + ": " + edat + " anys"));
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error al llegir l'arxiu: " + e.getMessage());
        }
    }

    public static PR121hashmap deserialitzarHashMap() throws IOFitxerExcepcio {
        File file = new File(filePath);
        if (!file.exists()) {
            throw new IOFitxerExcepcio("Fichero inexistente: " + filePath);
        }

        PR121hashmap hashMap = new PR121hashmap();

        try (java.io.ObjectInputStream ois = new java.io.ObjectInputStream(
                    new java.io.FileInputStream(filePath))) {

                // Leemos el objeto serializado
                PR121hashmap temp = (PR121hashmap) ois.readObject();

                // Copiamos los datos del hasmap
                hashMap.getPersones().putAll(temp.getPersones());

        } catch (java.io.IOException | ClassNotFoundException e) {
                throw new IOFitxerExcepcio("Error llegint l'objecte del fitxer: " + e.getMessage());
            }

        return hashMap;
    }

    // Getter
    public static String getFilePath() {
        return filePath;
    }

    // Setter
    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }    
}