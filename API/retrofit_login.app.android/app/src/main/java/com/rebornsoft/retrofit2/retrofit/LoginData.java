package com.rebornsoft.retrofit2.retrofit;

public class LoginData {
    String empNum;
    String password;
    String companyCode;
    String empSeq;
    String companySeq;
    boolean status;
    String mode;
    String message;


    public LoginData(String empNum, String password, String companyCode) {
        this.empNum = empNum;
        this.password = password;
        this.companyCode = companyCode;
    }

    public String getEmpSeq() { return empSeq; }
    public void setEmpSeq(String empSeq){
        this.empSeq = empSeq;
    }

    public String getCompanySeq() { return companySeq; }
    public void setCompanySeq(String companySeq){
        this.companySeq = companySeq;
    }

    public boolean getStatus() {
        return status;
    }
    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMode() { return mode; }
    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }

}
