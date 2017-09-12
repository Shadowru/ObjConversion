package org.shadow.livecity.files;

import org.shadow.livecity.files.fileformat.BuildingFileV1;

public class FileConvertor {
    public static BuildingFileV1 getNativeFile(ObjFileLoader objFileLoader) {

        BuildingFileV1 buildingFile = new BuildingFileV1();

        buildingFile.setIndexBuffer(objFileLoader.getIndices());
        buildingFile.setVertextCoordBuffer(objFileLoader.getVertex());
        buildingFile.setNormalsCoordBuffer(objFileLoader.getNormal());
        buildingFile.setTextureCoordBuffer(objFileLoader.getUv());

        return buildingFile;
    }
}
