package danskebank.mini_bank_system.service;

import danskebank.mini_bank_system.dto.CustomerDTO;
import danskebank.mini_bank_system.entity.Account;
import danskebank.mini_bank_system.entity.Address;
import danskebank.mini_bank_system.entity.Customer;
import danskebank.mini_bank_system.entity.CustomerType;
import danskebank.mini_bank_system.exception.CustomerException;
import danskebank.mini_bank_system.repository.AccountRepository;
import danskebank.mini_bank_system.repository.AddressRepository;
import danskebank.mini_bank_system.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final AccountRepository accountRepository;
    private final AddressRepository addressRepository;

    @Transactional
    public Customer createCustomer(Long accountId, CustomerDTO customerDTO) {

        var account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        var existingCustomer = customerRepository.findByNameAndLastnameAndEmailAndPhoneNumber(
                customerDTO.getName(),
                customerDTO.getLastname(),
                customerDTO.getEmail(),
                customerDTO.getPhoneNumber());

        if (existingCustomer.isPresent()) {
            var customer = existingCustomer.get();
            if (account.getCustomers().contains(customer)) {
                throw new RuntimeException("Customer is already assigned to this account.");
            }
            customer.setAccount(account);
            customerRepository.save(customer);
            return customer;

        } else {
            List<Address> addresses = customerDTO.getAddresses().stream()
                    .map(addressDTO -> Address.builder()
                            .street(addressDTO.getStreet())
                            .city(addressDTO.getCity())
                            .postalCode(addressDTO.getPostalCode())
                            .build())
                    .collect(Collectors.toList());

            var newCustomer = new Customer();
            newCustomer.setName(customerDTO.getName());
            newCustomer.setLastname(customerDTO.getLastname());
            newCustomer.setPhoneNumber(customerDTO.getPhoneNumber());
            newCustomer.setEmail(customerDTO.getEmail());
            newCustomer.setType(CustomerType.valueOf(customerDTO.getType()));
            newCustomer.setAccount(account);
            addresses.forEach(address -> {
                address.setCustomer(newCustomer);
                address.setCreatedBy("User");
                address.setCreationDate(LocalDateTime.now());
                address.setLastModifiedDate(LocalDateTime.now());
                address.setLastModifiedBy("User");
                address.setVersionNum(1);
            });
            newCustomer.setAddresses(addresses);
            newCustomer.setCreatedBy("User");
            newCustomer.setCreationDate(LocalDateTime.now());
            newCustomer.setLastModifiedDate(LocalDateTime.now());
            newCustomer.setLastModifiedBy("User");
            newCustomer.setVersionNum(1);

            customerRepository.save(newCustomer);
            return newCustomer;
        }
    }

    @Transactional
    public Customer updateCustomer(Long id, CustomerDTO customerDTO) {
        var customer = new Customer();
        try {
            var existingCustomer = customerRepository.findById(id);
            if (existingCustomer.isPresent()) {
                customer = existingCustomer.get();
                customer.setName(customerDTO.getName());
                customer.setLastname(customerDTO.getLastname());
                customer.setPhoneNumber(customerDTO.getPhoneNumber());
                customer.setEmail(customerDTO.getEmail());
                customer.setType(CustomerType.valueOf(customerDTO.getType()));
                customer.setVersionNum(customer.getVersionNum() + 1);
                customer.setLastModifiedBy("User");
                customer.setLastModifiedDate(LocalDateTime.now());

                if (customerDTO.getAddresses() != null) {
                    List<Address> existingAddresses = addressRepository.findAllByCustomerId(customer.getId());

                    Map<Long, Address> existingAddressMap = existingAddresses.stream()
                            .collect(Collectors.toMap(Address::getId, address -> address));

                    List<Address> updatedAddresses = new ArrayList<>();

                    for (var addressUpdate : customerDTO.getAddresses()) {
                        if (addressUpdate.getId() != null && existingAddressMap.containsKey(addressUpdate.getId())) {
                            var existingAddress = existingAddressMap.get(addressUpdate.getId());
                            existingAddress.setStreet(addressUpdate.getStreet());
                            existingAddress.setCity(addressUpdate.getCity());
                            existingAddress.setPostalCode(addressUpdate.getPostalCode());
                            existingAddress.setLastModifiedDate(LocalDateTime.now());
                            existingAddress.setVersionNum(existingAddress.getVersionNum() + 1);
                            existingAddress.setLastModifiedBy("Updated User");
                            updatedAddresses.add(existingAddress);
                        } else {
                            var newAddress = new Address();
                            newAddress.setCustomer(customer);
                            newAddress.setStreet(addressUpdate.getStreet());
                            newAddress.setCity(addressUpdate.getCity());
                            newAddress.setPostalCode(addressUpdate.getPostalCode());
                            newAddress.setVersionNum(1);
                            newAddress.setCreatedBy("User");
                            newAddress.setCreationDate(LocalDateTime.now());
                            newAddress.setLastModifiedBy("User");
                            newAddress.setLastModifiedDate(LocalDateTime.now());
                            updatedAddresses.add(newAddress);
                        }
                    }
                    addressRepository.saveAll(updatedAddresses);
                    customer.setAddresses(updatedAddresses);
                }
                customerRepository.save(customer);
                return customer;
            }
        } catch (Exception e) {
            throw new CustomerException("Customer not found.", e.getCause());
        }
        return customer;
    }

    public Page<Customer> searchCustomers(String searchTerm, int page, int size) {
        var pageable = PageRequest.of(page, size);
        return customerRepository.searchCustomers(searchTerm, pageable);
    }
}
