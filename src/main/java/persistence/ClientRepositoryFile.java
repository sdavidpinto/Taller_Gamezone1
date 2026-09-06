package persistence;

import model.Client;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de ClientRepository que guarda y lee los clientes en un
 * archivo de texto, una línea por cliente, en formato clave-valor:
 */
public class ClientRepositoryFile implements ClientRepository {

    private final String filePath;

    public ClientRepositoryFile(String filePath) {
        this.filePath = filePath;
        createFileIfNotExists();
    }

    /**
     * Se asegura de que el archivo de persistencia exista antes de usarlo.
     * Si la carpeta contenedora (por ejemplo "data/") no existe todavia, la crea,
     * para que no falle al intentar crear el archivo dentro de ella.
     */
    private void createFileIfNotExists() {
        File archivo = new File(filePath);
        File parent = archivo.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    @Override
    public void save(Client client) {
        if (client == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo");
        }
        if (findByIdNumber(client.getIdNumber()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ID: " + client.getIdNumber());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(client));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando cliente en: " + filePath, e);
        }
    }

    @Override
    public Client findByIdNumber(String idNumber) {
        for (Client c : findAll()) {
            if (c.getIdNumber() != null && c.getIdNumber().equals(idNumber)) {
                return c;
            }
        }
        return null;
    }

    @Override
    public List<Client> findAll() {
        List<Client> clientes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    clientes.add(parseLine(linea));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo clientes de: " + filePath, e);
        }
        return clientes;
    }

    @Override
    public boolean update(Client client) {
        List<Client> clientes = findAll();
        boolean encontrado = false;
        for (int i = 0; i < clientes.size(); i++) {
            if (clientes.get(i).getIdNumber().equals(client.getIdNumber())) {
                clientes.set(i, client);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            rewriteFile(clientes);
        }
        return encontrado;
    }

    @Override
    public boolean deleteByIdNumber(String idNumber) {
        List<Client> clientes = findAll();
        boolean eliminado = clientes.removeIf(c -> c.getIdNumber() != null && c.getIdNumber().equals(idNumber));
        if (eliminado) {
            rewriteFile(clientes);
        }
        return eliminado;
    }

    private void rewriteFile(List<Client> clientes) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Client c : clientes) {
                bw.write(toLine(c));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reescribiendo clientes en: " + filePath, e);
        }
    }

    private String toLine(Client c) {
        return "idNumber: " + c.getIdNumber()
                + "; name: " + c.getName()
                + "; phone: " + c.getPhone()
                + "; email: " + c.getEmail();
    }

    private Client parseLine(String linea) {
        String[] partes = linea.split(";");
        String idNumber = partes[0].split(":", 2)[1].trim();
        String name = partes[1].split(":", 2)[1].trim();
        String phone = partes[2].split(":", 2)[1].trim();
        String email = partes[3].split(":", 2)[1].trim();
        return new Client(name, idNumber, phone, email);
    }
}