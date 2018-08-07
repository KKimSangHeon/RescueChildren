package kkimsangheon.rescuechildren.DB.VO;

/**
 * Created by SangHeon on 2018-08-02.
 */

public class Student {

    private String id;   // primary key
    private String name;
    private String className;
    private String parentPhoneNumber;
    private int isOut;  //최근 승차했는지 하차했는지 여부

    public Student() {
        this.id = "";
        this.name = "";
        this.className = "";
        this.parentPhoneNumber = "";
        this.isOut = 1;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getParentPhoneNumber() {
        return parentPhoneNumber;
    }

    public void setParentPhoneNumber(String parentPhoneNumber) {
        this.parentPhoneNumber = parentPhoneNumber;
    }

    public int getIsOut() {
        return isOut;
    }

    public void setIsOut(int isOut) {
        this.isOut = isOut;
    }
}
