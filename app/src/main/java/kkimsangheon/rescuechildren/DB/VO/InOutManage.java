package kkimsangheon.rescuechildren.DB.VO;

/**
 * Created by SangHeon on 2018-08-02.
 */

public class InOutManage {
    private String studentId;   // foreign key
    private String longitude;
    private String latitude;
    private String inOutTime;
    private int isManual;       //태깅이 아닌 수동으로 처리했을경우

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getInOutTime() {
        return inOutTime;
    }

    public void setInOutTime(String inOutTime) {
        this.inOutTime = inOutTime;
    }

    public int getIsManual() {
        return isManual;
    }

    public void setIsManual(int isManual) {
        this.isManual = isManual;
    }
}
