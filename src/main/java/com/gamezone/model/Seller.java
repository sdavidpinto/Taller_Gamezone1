package com.gamezone.model;
public class Seller extends Person {

    private String employeeCode;
    private String shift;

    public Seller(String name, String idNumber, String phone, String employeeCode, String shift) {
        super(name, idNumber, phone);
        this.employeeCode = employeeCode;
        this.shift = shift;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}