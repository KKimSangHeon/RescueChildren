package kkimsangheon.rescuechildren.DB.VO;

/**
 * Created by SangHeon on 2018-08-02.
 */

public class Student {

    private String id;   // primary key
    private String name;
    private String className;
    private String parentPhoneNumber;
    private String currentTagTime;
    private int isOut;  //최근 승차했는지 하차했는지 여부
    private int isOrderByRegisteredTime;
    private int isOrderByCuttentTagTime;

    public Student() {
        this.id = "";
        this.name = "";
        this.className = "";
        this.parentPhoneNumber = "";
        this.currentTagTime = "";
        this.isOut = -1;
        this.isOrderByRegisteredTime = 0;
        this.isOrderByCuttentTagTime = 0;
    }

    public int getIsOrderByCuttentTagTime() {
        return isOrderByCuttentTagTime;
    }

    public void setIsOrderByCuttentTagTime(int isOrderByCuttentTagTime) {
        this.isOrderByCuttentTagTime = isOrderByCuttentTagTime;
    }

    public int getIsOrderByRegisteredTime() {
        return isOrderByRegisteredTime;
    }

    public void setIsOrderByRegisteredTime(int isOrderByRegisteredTime) {
        this.isOrderByRegisteredTime = isOrderByRegisteredTime;
    }

    public String getCurrentTagTime() {
        return currentTagTime;
    }

    public void setCurrentTagTime(String currentTagTime) {
        this.currentTagTime = currentTagTime;
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
