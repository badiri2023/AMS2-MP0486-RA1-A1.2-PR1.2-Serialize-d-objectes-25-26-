package com.project;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.project.excepcions.IOFitxerExcepcio;

public class PR120mainPersonesHashmap {
    private static String filePath = System.getProperty("user.dir") + "/data/PR120persones.dat";

    public static void main(String[] args) {
        HashMap<String, Integer> persones = new HashMap<>();
        persones.put("Anna", 25);
        persones.put("Bernat", 30);
        persones.put("Carla", 22);
        persones.put("David", 35);
        persones.put("Elena", 28);

        try {
            escriurePersones(persones);
            llegirPersones();
        } catch (IOFitxerExcepcio e) {
            System.err.println("Error en treballar amb el fitxer: " + e.getMessage());
        }
    }

    // Getter per a filePath
    public static String getFilePath() {
        return filePath;
    }

    // Setter per a filePath
    public static void setFilePath(String newFilePath) {
        filePath = newFilePath;
    }

    // Mètode per escriure les persones al fitxer 
    public static void escriurePersones(HashMap<String, Integer> persones) throws IOFitxerExcepcio {
        // probamos de introducir los datos en el fichero, pero en casod e que el fichero no se encutnre vamos al catch
        try(DataOutputStream dos=new DataOutputStream(new FileOutputStream(filePath))){
            //iteramos mediante for en el hasmap para sacar los datos y introducir las llaves y los valores
            for (Map.Entry<String,Integer> entry:persones.entrySet()){
                dos.writeUTF(entry.getKey());
                dos.writeInt(entry.getValue());
            }
        }catch (IOException e){
            throw  new IOFitxerExcepcio("Error de localizacion de fichero"+e.getMessage());
        }

    }

    // Mètode per llegir les persones des del fitxer
    public static void llegirPersones() throws IOFitxerExcepcio {
        File file= new File(filePath);
        if (!file.exists()){
            throw  new IOFitxerExcepcio("Fihero inexitente"+filePath);
        }

        try (DataInputStream dis = new DataInputStream(new FileInputStream(filePath))) {
            while (dis.available() > 0) {     
                System.out.println(dis.readUTF() + ": " + dis.readInt() + " anys");
            }
        } catch (IOException e) {
            throw new IOFitxerExcepcio("Error de lectura de fichero: " + e.getMessage());
        }
    }
}
