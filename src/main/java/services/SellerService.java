package services;

import model.Seller;
import persistence.SellerRepository;

import java.util.List;

/**
 * Capa de servicios para Seller. Recibe la interfaz SellerRepository
 * por inyección de dependencias (constructor).
 */
public class SellerService {

    private final SellerRepository sellerRepository;

    public SellerService(SellerRepository sellerRepository) {
        this.sellerRepository = sellerRepository;
    }

    /**
     * Registers a new seller after validating that no other seller
     * is already registered with the same identification number.
     *
     * @param name the full name of the seller
     * @param idNumber the identification number of the seller
     * @param phone the contact phone number of the seller
     * @param employeeCode the employee code assigned to the seller
     * @param shift the work shift assigned to the seller
     * @throws IllegalArgumentException if a seller with the same idNumber already exists
     */
    public void registerSeller(String name, String idNumber, String phone, String employeeCode, String shift) {
        if (sellerRepository.findByIdNumber(idNumber) != null) {
            throw new IllegalArgumentException("Ya existe un vendedor con ID: " + idNumber);
        }
        Seller seller = new Seller(name, idNumber, phone, employeeCode, shift);
        sellerRepository.save(seller);
    }

    /**
     * Finds a seller by their identification number.
     *
     * @param idNumber the identification number to search for
     * @return the seller with the given ID, or null if not found
     */
    public Seller findByIdNumber(String idNumber) {
        return sellerRepository.findByIdNumber(idNumber);
    }

    /**
     * Returns all registered sellers.
     *
     * @return a list containing all sellers
     */
    public List<Seller> findAll() {
        return sellerRepository.findAll();
    }

    /**
     * Updates the information of an existing seller.
     *
     * @param idNumber the identification number of the seller to update
     * @param newName the new name of the seller
     * @param newPhone the new phone number of the seller
     * @param newEmployeeCode the new employee code of the seller
     * @param newShift the new work shift of the seller
     * @return true if the seller was found and updated, false otherwise
     */
    public boolean updateSeller(String idNumber, String newName, String newPhone,
                                 String newEmployeeCode, String newShift) {
        Seller existingSeller = sellerRepository.findByIdNumber(idNumber);
        if (existingSeller == null) {
            return false;
        }
        existingSeller.setName(newName);
        existingSeller.setPhone(newPhone);
        existingSeller.setEmployeeCode(newEmployeeCode);
        existingSeller.setShift(newShift);
        return sellerRepository.update(existingSeller);
    }

    /**
     * Deletes a seller by their identification number.
     *
     * @param idNumber the identification number of the seller to delete
     * @return true if the seller was found and deleted, false otherwise
     */
    public boolean deleteSeller(String idNumber) {
        return sellerRepository.deleteByIdNumber(idNumber);
    }
}