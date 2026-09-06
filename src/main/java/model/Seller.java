package model;
/**
 * Representa a un vendedor que atiende clientes y registra ventas.
 * Un vendedor tiene un código de empleado y un turno de trabajo asignado.
 */
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
 /**
 * Construye una representación en texto de la información del vendedor,
 * incluyendo nombre, identificación, teléfono, código de empleado y turno.
 *
 * @return una cadena de texto formateada describiendo al vendedor
 */   
    @Override
    public String display() {
    return "Seller: " + getName() + " | ID: " + getIdNumber() + " | Phone: " 
    + getPhone() + " | Employee code: " + employeeCode + " | Shift: " + shift;
}
}