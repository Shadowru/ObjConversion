package org.shadow.livecity.files;

import java.util.StringTokenizer;

class ObjTriple {


    private final Integer v;
    private final Integer vt;
    private final Integer vn;

    public ObjTriple(Integer v, Integer vt, Integer vn) {
        this.v = v;
        this.vt = vt;
        this.vn = vn;
    }

    public Integer getV() {
        return v;
    }

    public Integer getVt() {
        return vt;
    }

    public Integer getVn() {
        return vn;
    }

    public static ObjTriple parseTriple(String lineSection) {

        //TDOO:Rewrite "0//", "0/0/", etc ...
        final StringTokenizer stringTokenizer = new StringTokenizer(lineSection, "/", true);
        String sV = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        String sVt = stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        String sVn = stringTokenizer.nextToken();

        Integer vt = null;
        if(sVt.length() > 0){
            vt = Integer.parseInt(sVt);
        }

        Integer vn = null;
        if(sVn.length() > 0){
            vn = Integer.parseInt(sVn);
        }

        return new ObjTriple(
                Integer.parseInt(sV),
                vt,
                vn
        );
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ObjTriple objTriple = (ObjTriple) o;

        if (!v.equals(objTriple.v)) return false;
        if (!vt.equals(objTriple.vt)) return false;
        return vn.equals(objTriple.vn);
    }

    @Override
    public int hashCode() {
        int result = v.hashCode();
        result = 31 * result + vt.hashCode();
        result = 31 * result + vn.hashCode();
        return result;
    }
}
