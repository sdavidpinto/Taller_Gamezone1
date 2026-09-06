package com.gamezone.model;
/**
 * Representa a una persona genérica que interactúa con la tienda.
 * Esta clase es abstracta porque toda persona debe especializarse
 * como Cliente o Vendedor.
 */
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
    
 /**
 * Construye una representación en texto de la información de esta persona.
 * Cada subclase debe implementarlo según sus propios atributos.
 *
 * @return una cadena de texto formateada describiendo a la persona
 */
    public abstract String display();
}
