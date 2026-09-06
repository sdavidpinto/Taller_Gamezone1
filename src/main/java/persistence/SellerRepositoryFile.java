package persistence;

import model.Seller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de SellerRepository que guarda y lee los vendedores en un
 * archivo de texto, una línea por vendedor, en formato clave-valor:
 *
 *   idNumber: 1065555555; name: Carla Ruiz; phone: 3009999999; employeeCode: E01; shift: mañana
 */
public class SellerRepositoryFile implements SellerRepository {

    private final String filePath;

    public SellerRepositoryFile(String filePath) {
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
    public void save(Seller seller) {
        if (seller == null) {
            throw new IllegalArgumentException("El vendedor no puede ser nulo");
        }
        if (findByIdNumber(seller.getIdNumber()) != null) {
            throw new IllegalArgumentException("Ya existe un vendedor con ID: " + seller.getIdNumber());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(seller));
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando vendedor en: " + filePath, e);
        }
    }

    @Override
    public Seller findByIdNumber(String idNumber) {
        for (Seller s : findAll()) {
            if (s.getIdNumber() != null && s.getIdNumber().equals(idNumber)) {
                return s;
            }
        }
        return null;
    }

    @Override
    public List<Seller> findAll() {
        List<Seller> vendedores = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    vendedores.add(parseLine(linea));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error leyendo vendedores de: " + filePath, e);
        }
        return vendedores;
    }

    @Override
    public boolean update(Seller seller) {
        List<Seller> vendedores = findAll();
        boolean encontrado = false;
        for (int i = 0; i < vendedores.size(); i++) {
            if (vendedores.get(i).getIdNumber().equals(seller.getIdNumber())) {
                vendedores.set(i, seller);
                encontrado = true;
                break;
            }
        }
        if (encontrado) {
            rewriteFile(vendedores);
        }
        return encontrado;
    }

    @Override
    public boolean deleteByIdNumber(String idNumber) {
        List<Seller> vendedores = findAll();
        boolean eliminado = vendedores.removeIf(s -> s.getIdNumber() != null && s.getIdNumber().equals(idNumber));
        if (eliminado) {
            rewriteFile(vendedores);
        }
        return eliminado;
    }

    private void rewriteFile(List<Seller> vendedores) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Seller s : vendedores) {
                bw.write(toLine(s));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reescribiendo vendedores en: " + filePath, e);
        }
    }

    private String toLine(Seller s) {
        return "idNumber: " + s.getIdNumber()
                + "; name: " + s.getName()
                + "; phone: " + s.getPhone()
                + "; employeeCode: " + s.getEmployeeCode()
                + "; shift: " + s.getShift();
    }

    private Seller parseLine(String linea) {
        String[] partes = linea.split(";");
        String idNumber = partes[0].split(":", 2)[1].trim();
        String name = partes[1].split(":", 2)[1].trim();
        String phone = partes[2].split(":", 2)[1].trim();
        String employeeCode = partes[3].split(":", 2)[1].trim();
        String shift = partes[4].split(":", 2)[1].trim();
        return new Seller(name, idNumber, phone, employeeCode, shift);
    }
}