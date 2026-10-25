package ui;

import model.Client;
import model.Sale;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import services.ClientService;
import services.SaleService;

/**
 * Submenú de clientes. Recibe solo los servicios que necesita.
 */
public class ClientMenu extends SubMenu {

    private final ClientService clientService;
    private final SaleService saleService;

    public ClientMenu(ClientService clientService, SaleService saleService) {
        this.clientService = clientService;
        this.saleService = saleService;
    }

    @Override
    public void show() {
        String menu = "=== Clientes ===\n"
                + "1. Registrar\n"
                + "2. Buscar por ID\n"
                + "3. Listar todos\n"
                + "4. Actualizar\n"
                + "5. Eliminar\n"
                + "0. Volver";
        String entrada = JOptionPane.showInputDialog(null, menu, "Clientes", JOptionPane.PLAIN_MESSAGE);
        if (entrada == null) return;

        switch (parseOption(entrada)) {
            case 1 -> registerClient();
            case 2 -> searchClient();
            case 3 -> listClients();
            case 4 -> updateClient();
            case 5 -> deleteClient();
            case 0 -> { /* volver */ }
            default -> JOptionPane.showMessageDialog(null, "Opción inválida.");
        }
    }

    private void registerClient() {
        String name = askRequiredText("Nombre:");
        if (name == null) return;
        String phone = askRequiredText("Teléfono:");
        if (phone == null) return;
        String email = askRequiredText("Email:");
        if (email == null) return;

        // Este do-while es distinto: no valida formato, valida una regla
        // de negocio (ID duplicado) que solo el service conoce. Por eso
        // reintenta atrapando la excepción que lanza clientService.
        boolean registrado = false;
        do {
            String idNumber = askRequiredText("Identificación:");
            if (idNumber == null) return;
            try {
                clientService.registerClient(name, idNumber, phone, email);
                registrado = true;
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(null, e.getMessage() + "\nIntenta con otra identificación.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (!registrado);

        JOptionPane.showMessageDialog(null, "Cliente registrado con éxito.");
    }

    private void searchClient() {
        String idNumber = JOptionPane.showInputDialog("ID del cliente a buscar:");
        Client c = clientService.findByIdNumber(idNumber);

        if (c == null) {
            JOptionPane.showMessageDialog(null, "No se encontró el cliente.");
            return;
        }

        StringBuilder mensaje = new StringBuilder(c.display());

        List<Sale> ventasDelCliente = saleService.findAll().stream()
                .filter(venta -> venta.getClient().getIdNumber().equals(idNumber))
                .toList();

        if (ventasDelCliente.isEmpty()) {
            mensaje.append("\n\nEste cliente no tiene ventas registradas.");
        } else {
            mensaje.append("\n\nVentas del cliente:\n");
            for (Sale venta : ventasDelCliente) {
                mensaje.append(venta.Display()).append("\n");
            }
        }

        JOptionPane.showMessageDialog(null, mensaje.toString());
    }

    private void listClients() {
        List<Client> clientes = clientService.findAll();
        JTextArea salida = new JTextArea(20, 50);
        JScrollPane tabla = new JScrollPane(salida);
        salida.setText("idNumber\tname\tphone\temail\n");
        for (Client c : clientes) {
            salida.append(c.getIdNumber() + "\t" + c.getName() + "\t" + c.getPhone() + "\t" + c.getEmail() + "\n");
        }
        JOptionPane.showMessageDialog(null, tabla);
    }

    private void updateClient() {
        String idNumber;
        do {
            idNumber = askRequiredText("ID del cliente a actualizar:");
            if (idNumber == null) return;
            if (clientService.findByIdNumber(idNumber) == null) {
                JOptionPane.showMessageDialog(null, "No existe un cliente con ese ID.\nIntenta con otro.", "Dato inválido", JOptionPane.WARNING_MESSAGE);
            }
        } while (clientService.findByIdNumber(idNumber) == null);

        String name = askRequiredText("Nuevo nombre:");
        if (name == null) return;
        String phone = askRequiredText("Nuevo teléfono:");
        if (phone == null) return;
        String email = askRequiredText("Nuevo email:");
        if (email == null) return;

        clientService.updateClient(idNumber, name, phone, email);
        JOptionPane.showMessageDialog(null, "Cliente actualizado.");
    }

    private void deleteClient() {
        String idNumber = JOptionPane.showInputDialog("ID del cliente a eliminar:");
        boolean ok = clientService.deleteClient(idNumber);
        JOptionPane.showMessageDialog(null, ok ? "Cliente eliminado." : "No se encontró el cliente.");
    }
}