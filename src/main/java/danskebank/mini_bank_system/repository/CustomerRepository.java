package danskebank.mini_bank_system.repository;

import danskebank.mini_bank_system.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    Optional<Customer> findByNameAndLastnameAndEmailAndPhoneNumber(String name, String lastname, String email, String phoneNumber);

    @Query("""
        SELECT c
        FROM Customer c
        WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(c.lastname) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(c.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(c.phoneNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
           OR LOWER(c.type) LIKE LOWER(CONCAT('%', :searchTerm, '%'))
    """)
    Page<Customer> searchCustomers(@Param("searchTerm") String searchTerm, Pageable pageable);
}
