package org.shadow.livecity.files;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class ObjFileLoader {
    private static final java.lang.String COMMENT_PREFIX = "#";

    private ArrayList<Vector3> vertexCoords = new ArrayList<>();
    private ArrayList<Vector3> vertexNormals = new ArrayList<>();
    private ArrayList<Vector2> textureCoord = new ArrayList<>();

    private int vertexCount;
    private int textureCoordsCount;
    private int normalsCount;
    private int faceCount;

    private ArrayList<ObjTriple> fileIndexOrder;
    private HashMap<ObjTriple, Integer> fileCash;

    private int arraysCoordSize;
    private int indexArraySize;

    private ArrayList<ObjTriple> transposedTriple;

    private float[] vertex;
    private float[] normal;
    private float[] uv;
    private short[] indices;

    public ObjFileLoader(String fileName) {

        vertexCount = 0;
        textureCoordsCount = 0;
        normalsCount = 0;
        faceCount = 0;

        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fileName);

            loadOBJModel(inputStream);

            inputStream.close();

            printInfo();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void loadOBJModel(InputStream inputStream) {

        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;

        fileIndexOrder = new ArrayList<>();
        fileCash = new HashMap<>();

        try {

            final ObjLineParser lineParser = new ObjLineParser();

            while ((line = bufferedReader.readLine()) != null) {


                line = line.trim();

                if (line.startsWith(COMMENT_PREFIX)) {
                    continue;
                }

                if (lineParser.parse(line)) {

                    if (lineParser.isVertex()) {
                        addVertex(lineParser);
                    } else if (lineParser.isNormals()) {
                        addNormal(lineParser);
                    } else if (lineParser.isTextureCoordinate()) {
                        addTextureCoordinate(lineParser);
                    } else if (lineParser.isFace()) {
                        addFace(lineParser);
                    }
                }

            }
        } catch (Exception e) {
            System.out.println("Load OBJ Model error. Line : " + line);
            e.printStackTrace();
        }

        arraysCoordSize = fileCash.keySet().size();

        indexArraySize = fileIndexOrder.size();

        transposeData();

    }


    public int getArraysCoordSize() {
        return arraysCoordSize;
    }

    public int getIndexArraySize() {
        return indexArraySize;
    }

    public int getTextureCoordsCount() {
        return textureCoordsCount;
    }

    private void addTextureCoordinate(ObjLineParser lineParser) {
        textureCoordsCount++;
        textureCoord.add(
                new Vector2(
                        lineParser.getNextFloat() ,
                        lineParser.getNextFloat() 
                )
        );
    }

    public int getNormalsCount() {
        return normalsCount;
    }

    private void addNormal(ObjLineParser lineParser) {
        normalsCount++;

        vertexNormals.add(
                new Vector3(
                        lineParser.getNextFloat(),
                        lineParser.getNextFloat(),
                        lineParser.getNextFloat()
                )
        );
    }

    public int getVertexCount() {
        return vertexCount;
    }

    private void addVertex(ObjLineParser lineParser) {
        vertexCount++;

        vertexCoords.add(
                new Vector3(
                        lineParser.getNextFloat(),
                        lineParser.getNextFloat(),
                        lineParser.getNextFloat()
                )
        );
    }

    private void printInfo() {

        System.out.println("Vertices count : " + getVertexCount());
        System.out.println("Normals count : " + getNormalsCount());
        System.out.println("Texture coords count : " + getTextureCoordsCount());
        System.out.println("Faces count : " + getFaceCount());
        System.out.println("Unique index : " + arraysCoordSize);
        System.out.println("Index array : " + indexArraySize);

    }

    public int getFaceCount() {
        return faceCount;
    }

    private void addFace(ObjLineParser lineParser) {

        faceCount++;

        ObjTriple objTriple = null;

        int vertexCount = 0;

        ArrayList<ObjTriple> triangleArray = new ArrayList<>();

        while ((objTriple = lineParser.getNextTriple()) != null) {
            if (vertexCount > 2) {
                addObjTriple(
                        triangleArray.get(vertexCount-3)
                );
                addObjTriple(
                        triangleArray.get(vertexCount-1)
                );
            }
            vertexCount++;
            triangleArray.add(objTriple);
            addObjTriple(objTriple);
        }

    }

    private void addObjTriple(ObjTriple objTriple) {
        fileIndexOrder.add(objTriple);
        fileCash.computeIfAbsent(objTriple, ObjTriple::getV);
    }


    private Integer translateVertexIndex(Integer v) {
        if (v < 0) {
            return vertexCoords.size() - v;
        }
        return v - 1;
    }

    private Integer translateNormalsIndex(Integer v) {
        if (v < 0) {
            return vertexNormals.size() - v;
        }
        return v - 1;
    }

    private Integer translateUVIndex(Integer v) {
        if (v < 0) {
            return textureCoord.size() - v;
        }
        return v - 1;
    }

    private void transposeData() {
        Set<ObjTriple> keys = fileCash.keySet();

        transposedTriple = new ArrayList<>();

        transposedTriple.addAll(keys);

        vertex = new float[getArraysCoordSize() * 3];
        normal = new float[getArraysCoordSize() * 3];
        uv = new float[getArraysCoordSize() * 2];

        int vertex_pointer = 0;
        int normal_pointer = 0;
        int uv_pointer = 0;

        for (ObjTriple transposedTriple : transposedTriple) {
                Vector3 vector3 = vertexCoords.get(
                        translateVertexIndex(transposedTriple.getV())
                );
                vertex[vertex_pointer++] = vector3.getX();
                vertex[vertex_pointer++] = vector3.getY();
                vertex[vertex_pointer++] = vector3.getZ();

                vector3 = vertexNormals.get(
                        translateNormalsIndex(transposedTriple.getVn())
                );
                normal[normal_pointer++] = vector3.getX();
                normal[normal_pointer++] = vector3.getY();
                normal[normal_pointer++] = vector3.getZ();

                Vector2 vector2 = textureCoord.get(
                        translateUVIndex(transposedTriple.getVt())
                );
                uv[uv_pointer++] = vector2.getX();
                uv[uv_pointer++] = 1 - vector2.getY();
        }

        indices = new short[indexArraySize];
        int indices_pointer = 0;

        for (ObjTriple objTriple : fileIndexOrder) {
            indices[indices_pointer++] =
                    (short) transposedTriple.indexOf(
                            objTriple
                    );
        }

    }

    public float[] getVertex() {
        return vertex;
    }

    public float[] getNormal() {
        return normal;
    }

    public float[] getUv() {
        return uv;
    }

    public short[] getIndices() {
        return indices;
    }
}
