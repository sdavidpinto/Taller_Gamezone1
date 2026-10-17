package persistence;

import exceptions.PersistenceException;
import exceptions.InvalidDataException;
import exceptions.BusinessRuleException;

import model.Seller;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementación de SellerRepository que guarda y lee los vendedores en un
 * archivo de texto, una línea por vendedor, en formato clave-valor:
 */
public class SellerRepositoryFile implements SellerRepository {

    private final String filePath;

    /**
     * Crea un repositorio de vendedores respaldado por el archivo indicado.
     *
     * @param filePath ruta del archivo de persistencia
     * @throws PersistenceException si el archivo no puede crearse
     */
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
                throw new PersistenceException("No se pudo crear el archivo: " + filePath, e);
            }
        }
    }

    /**
     * Guarda un vendedor en el archivo de persistencia.
     *
     * @param seller vendedor que se desea guardar
     * @throws InvalidDataException si el vendedor es nulo
     * @throws BusinessRuleException si el vendedor ya existe
     * @throws PersistenceException si ocurre un error de escritura
     */
    @Override
    public void save(Seller seller) {
        if (seller == null) {
            throw new InvalidDataException("vendedor", "no puede ser nulo.");
        }
        if (findByIdNumber(seller.getIdNumber()) != null) {
            throw new BusinessRuleException("Ya existe un vendedor con ID: " + seller.getIdNumber());
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, true))) {
            bw.write(toLine(seller));
            bw.newLine();
        } catch (IOException e) {
            throw new PersistenceException("Error guardando vendedor en: " + filePath, e);
        }
    }

    /**
     * Busca un vendedor por su número de identificación.
     *
     * @param idNumber número de identificación del vendedor
     * @return vendedor encontrado o {@code null} si no existe
     * @throws PersistenceException si ocurre un error de lectura
     */
    @Override
    public Seller findByIdNumber(String idNumber) {
        for (Seller s : findAll()) {
            if (s.getIdNumber() != null && s.getIdNumber().equals(idNumber)) {
                return s;
            }
        }
        return null;
    }

    /**
     * Obtiene todos los vendedores almacenados.
     *
     * @return lista de vendedores almacenados
     * @throws PersistenceException si ocurre un error de lectura
     */
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
            throw new PersistenceException("Error leyendo vendedores de: " + filePath, e);
        }
        return vendedores;
    }

    /**
     * Actualiza un vendedor existente.
     *
     * @param seller vendedor con los datos actualizados
     * @return {@code true} si fue actualizado; {@code false} si no existe
     * @throws PersistenceException si ocurre un error de escritura
     */
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

    /**
     * Elimina un vendedor por su número de identificación.
     *
     * @param idNumber número de identificación del vendedor
     * @return {@code true} si fue eliminado; {@code false} si no existe
     * @throws PersistenceException si ocurre un error de escritura
     */
    @Override
    public boolean deleteByIdNumber(String idNumber) {
        List<Seller> vendedores = findAll();
        boolean eliminado = vendedores.removeIf(s -> s.getIdNumber() != null && s.getIdNumber().equals(idNumber));
        if (eliminado) {
            rewriteFile(vendedores);
        }
        return eliminado;
    }

    /**
     * Reescribe el archivo con la lista de vendedores indicada.
     *
     * @param vendedores vendedores que se escribirán
     * @throws PersistenceException si ocurre un error de escritura
     */
    private void rewriteFile(List<Seller> vendedores) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath, false))) {
            for (Seller s : vendedores) {
                bw.write(toLine(s));
                bw.newLine();
            }
        } catch (IOException e) {
            throw new PersistenceException("Error reescribiendo vendedores en: " + filePath, e);
        }
    }

    /**
     * Convierte un vendedor al formato de almacenamiento.
     *
     * @param s vendedor que se desea convertir
     * @return representación del vendedor en una línea
     */
    private String toLine(Seller s) {
        return "idNumber: " + s.getIdNumber()
                + "; name: " + s.getName()
                + "; phone: " + s.getPhone()
                + "; employeeCode: " + s.getEmployeeCode()
                + "; shift: " + s.getShift();
    }

    /**
     * Reconstruye un vendedor desde una línea del archivo.
     *
     * @param linea línea con los datos del vendedor
     * @return vendedor reconstruido
     */
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
