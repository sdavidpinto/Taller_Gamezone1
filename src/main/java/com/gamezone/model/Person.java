package com.gamezone.model;

public abstract class Person {   
   private String name;
   private String idNumber;
   private String phone;
   
    public Person(String name, String idNumber, String phone) {
        this.name = name;
        this.idNumber = idNumber;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    } 
    
    public abstract String display();
}
