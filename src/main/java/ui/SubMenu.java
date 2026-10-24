package ui;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import javax.swing.JOptionPane;

/**
 * Base de todos los submenús de la consola. Define el contrato común
 * (show) y concentra los helpers de entrada de datos que antes estaban
 * repetidos en una sola clase gigante. Cada submenú concreto solo conoce
 * los servicios que realmente necesita.
 */
public abstract class SubMenu {

    /** Muestra el submenú y atiende la opción elegida por el usuario. */
    public abstract void show();

    protected int parseOption(String texto) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Pide una fecha en formato ISO (yyyy-MM-dd) repitiendo la pregunta
     * mientras el texto no se pueda convertir a LocalDate. Devuelve null
     * si el usuario cancela.
     */
    protected LocalDate askDate(String mensaje) {
        while (true) {
            String texto = JOptionPane.showInputDialog(mensaje + " (formato: yyyy-MM-dd)");
            if (texto == null) {
                return null;
            }
            try {
                return LocalDate.parse(texto.trim());
            } catch (DateTimeParseException e) {
                JOptionPane.showMessageDialog(null, "Ingresa una fecha válida (ej. 2026-09-25).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
    
     /**
     * Pide la confirmacion para el tipo de control usado en los accessorios.
     */
    protected Boolean askYesNo(String mensaje) {
        int opcion = JOptionPane.showConfirmDialog(null, mensaje, "Confirmación", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.CLOSED_OPTION) return null;
        return opcion == JOptionPane.YES_OPTION;
    }
    /**
     * Pide un texto obligatorio repitiendo la pregunta mientras venga vacío
     * o en blanco. Si el usuario cancela el diálogo, se interrumpe todo el
     * registro devolviendo null (se revisa en cada método que llama esto).
     */
    protected String askRequiredText(String mensaje) {
        String valor;
        do {
            valor = JOptionPane.showInputDialog(mensaje);
            if (valor == null) {
                return null; // el usuario canceló el diálogo
            }
            if (valor.isBlank()) {
                JOptionPane.showMessageDialog(null, "Este campo no puede quedar vacío.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (valor.isBlank());
        return valor;
    }

    /**
     * Pide un número decimal repitiendo la pregunta mientras el texto
     * ingresado no se pueda convertir a double. Devuelve null si el
     * usuario cancela.
     */
    protected Double askDouble(String mensaje) {
        while (true) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                return null;
            }
            try {
                return Double.parseDouble(texto.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa un número válido (ej. 59.99).", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }

    /**
     * Pide un número entero repitiendo la pregunta mientras el texto
     * ingresado no se pueda convertir a int. Devuelve null si el usuario
     * cancela.
     */
    protected Integer askInt(String mensaje) {
        while (true) {
            String texto = JOptionPane.showInputDialog(mensaje);
            if (texto == null) {
                return null;
            }
            try {
                return Integer.parseInt(texto.trim());
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null, "Ingresa un número entero válido.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        }
    }
}