package danskebank.mini_bank_system.repository;

import danskebank.mini_bank_system.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {
    Optional<List<Address>> findAllByCustomerId(Long customerId);
}
