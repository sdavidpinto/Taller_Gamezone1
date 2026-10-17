package services;

import validation.PersonValidator;
import model.Seller;
import persistence.SellerRepository;
import java.util.List;

/**
 * Gestiona las operaciones relacionadas con los vendedores.
 * Utiliza {@link SellerRepository} mediante inyección de dependencias.
 */
public class SellerService {

    private final SellerRepository sellerRepository;

    /**
     * Crea el servicio de vendedores.
     *
     * @param sellerRepository repositorio utilizado para gestionar los vendedores
     */
    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Registra un vendedor después de validar sus datos y verificar que su
     * número de identificación no esté registrado.
     *
     * @param name nombre completo del vendedor
     * @param idNumber número de identificación del vendedor
     * @param phone teléfono de contacto del vendedor
     * @param employeeCode código de empleado del vendedor
     * @param shift turno de trabajo del vendedor
     * @throws exceptions.InvalidDataException si algún dato es inválido
     * @throws exceptions.BusinessRuleException si el vendedor ya existe
     */
    public void registerSeller(String name, String idNumber, String phone,
                               String employeeCode, String shift) {
        PersonValidator.validatePersonData(idNumber, name, phone);
        PersonValidator.validateUniquePerson(sellerRepository.findAll(), idNumber);
        Seller seller = new Seller(name, idNumber, phone, employeeCode, shift);
        sellerRepository.save(seller);
    }

    /**
     * Busca un vendedor por su número de identificación.
     *
     * @param idNumber número de identificación que se desea buscar
     * @return vendedor encontrado
     * @throws exceptions.ResourceNotFoundException si el vendedor no existe
     */
    public Seller findByIdNumber(String idNumber) {
        Seller seller = sellerRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(seller, idNumber);
        return seller;
    }

    /**
     * Obtiene todos los vendedores registrados.
     *
     * @return lista de vendedores registrados
     */
    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    /**
     * Actualiza los datos de un vendedor existente.
     *
     * @param idNumber número de identificación del vendedor
     * @param newName nuevo nombre del vendedor
     * @param newPhone nuevo teléfono del vendedor
     * @param newEmployeeCode nuevo código de empleado del vendedor
     * @param newShift nuevo turno de trabajo del vendedor
     * @return {@code true} si el vendedor fue actualizado
     * @throws exceptions.ResourceNotFoundException si el vendedor no existe
     * @throws exceptions.InvalidDataException si algún dato es inválido
     */
    public boolean updateSeller(String idNumber, String newName, String newPhone,
                                String newEmployeeCode, String newShift) {
        Seller existingSeller = sellerRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(existingSeller, idNumber);
        PersonValidator.validatePersonData(idNumber, newName, newPhone);
        existingSeller.setName(newName);
        existingSeller.setPhone(newPhone);
        existingSeller.setEmployeeCode(newEmployeeCode);
        existingSeller.setShift(newShift);
        return sellerRepository.update(existingSeller);
    }

    /**
     * Elimina un vendedor por su número de identificación.
     *
     * @param idNumber número de identificación del vendedor
     * @return {@code true} si el vendedor fue eliminado
     * @throws exceptions.ResourceNotFoundException si el vendedor no existe
     */
    public boolean deleteSeller(String idNumber) {
        Seller seller = sellerRepository.findByIdNumber(idNumber);
        PersonValidator.validatePersonExists(seller, idNumber);
        return sellerRepository.deleteByIdNumber(idNumber);
    }
}
