package org.shadow.livecity;

import org.shadow.livecity.files.FileConvertor;
import org.shadow.livecity.files.ObjFileLoader;
import org.shadow.livecity.files.fileformat.BuildingFile;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {

        String fileName = "./files/in/Palace_Of_Soviets.obj";
        //fileName = "./files/in/MSK_Radisson_Royal_Hotel2.obj";
        //fileName = "./files/in/MFA building.obj";
        //fileName = "./files/in/MSK_Radisson_Royal_Hotel.obj";

        ObjFileLoader objFileLoader = new ObjFileLoader(fileName);

        BuildingFile buildingFile = FileConvertor.getNativeFile(objFileLoader);

        File ff = new File(fileName);
        String fileResult = ff.getName();

        fileResult = "./files/out/" + fileResult + ".conv";

        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileResult));

        oos.writeObject(buildingFile);

        oos.flush();
        oos.close();
    }
}
