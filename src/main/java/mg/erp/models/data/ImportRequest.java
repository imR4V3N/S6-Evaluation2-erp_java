package mg.erp.models.data;

public class ImportRequest {
    /*
    * @file1 Employee
    * @file2 Salary Structure & Salary Component
    * @file3 Salary Structure Assignement & Salary Slip
    * */
    private String file1;
    private String file2;
    private String file3;

    public String getFile1() {
        return file1;
    }

    public void setFile1(String file1) {
        this.file1 = file1;
    }

    public String getFile2() {
        return file2;
    }

    public void setFile2(String file2) {
        this.file2 = file2;
    }

    public String getFile3() {
        return file3;
    }

    public void setFile3(String file3) {
        this.file3 = file3;
    }
}
