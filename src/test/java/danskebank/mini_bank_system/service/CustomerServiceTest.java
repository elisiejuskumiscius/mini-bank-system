package danskebank.mini_bank_system.service;

import danskebank.mini_bank_system.dto.AddressDTO;
import danskebank.mini_bank_system.dto.CustomerDTO;
import danskebank.mini_bank_system.entity.Account;
import danskebank.mini_bank_system.entity.Address;
import danskebank.mini_bank_system.entity.Customer;
import danskebank.mini_bank_system.entity.CustomerType;
import danskebank.mini_bank_system.exception.CustomerException;
import danskebank.mini_bank_system.repository.AccountRepository;
import danskebank.mini_bank_system.repository.AddressRepository;
import danskebank.mini_bank_system.repository.CustomerRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;


@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private AddressRepository addressRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createCustomer_ShouldCreateNewCustomer_WhenCustomerDoesNotExist() {
        var accountId = 1L;
        var account = new Account();
        account.setId(accountId);

        var customerDTO = createCustomerDTO();
        var addressDTO = createAddressDTO();

        customerDTO.setAddresses(List.of(addressDTO));

        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Mockito.when(customerRepository.findByNameAndLastnameAndEmailAndPhoneNumber(
                        "John", "Doe", "john.doe@example.com", "1234567890"))
                .thenReturn(Optional.empty());

        var result = customerService.createCustomer(accountId, customerDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("John", result.getName());
        Assertions.assertEquals(1, result.getAddresses().size());
        Mockito.verify(customerRepository).save(Mockito.any(Customer.class));
    }

    @Test
    void createCustomer_ShouldThrowException_WhenCustomerAlreadyAssignedToAccount() {
        var accountId = 1L;
        var account = new Account();
        account.setId(accountId);

        var existingCustomer = new Customer();
        existingCustomer.setId(2L);
        account.setCustomers(Set.of(existingCustomer));

        var customerDTO = createCustomerDTO();

        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Mockito.when(customerRepository.findByNameAndLastnameAndEmailAndPhoneNumber(
                        "John", "Doe", "john.doe@example.com", "1234567890"))
                .thenReturn(Optional.of(existingCustomer));

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> customerService.createCustomer(accountId, customerDTO));
        Assertions.assertEquals("Customer is already assigned to this account.", exception.getMessage());
    }

    @Test
    void createCustomer_ShouldAssignExistingCustomerToAccount_WhenCustomerExistsAndNotAssigned() {
        var accountId = 1L;
        var account = new Account();
        account.setId(accountId);

        var existingCustomer = new Customer();
        existingCustomer.setId(2L);

        var customerDTO = createCustomerDTO();

        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        Mockito.when(customerRepository.findByNameAndLastnameAndEmailAndPhoneNumber(
                        "John", "Doe", "john.doe@example.com", "1234567890"))
                .thenReturn(Optional.of(existingCustomer));

        var result = customerService.createCustomer(accountId, customerDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(account, result.getAccount());
        Mockito.verify(customerRepository).save(existingCustomer);
    }

    @Test
    void createCustomer_ShouldThrowException_WhenAccountNotFound() {
        var accountId = 1L;
        var customerDTO = new CustomerDTO();

        Mockito.when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        RuntimeException exception = Assertions.assertThrows(RuntimeException.class,
                () -> customerService.createCustomer(accountId, customerDTO));
        Assertions.assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void updateCustomer_ShouldUpdateCustomerAndAddresses_WhenCustomerExists() {
        var customerId = 1L;
        var existingCustomer = createCustomer();

        var existingAddress = new Address();
        existingAddress.setId(1L);
        existingAddress.setStreet("Old Street");
        existingAddress.setCity("Old City");
        existingAddress.setPostalCode("00000");
        existingAddress.setCustomer(existingCustomer);
        existingCustomer.setAddresses(List.of(existingAddress));

        var customerDTO = createCustomerDTO();

        var updatedAddressDTO = new AddressDTO();
        updatedAddressDTO.setId(1L);
        updatedAddressDTO.setStreet("Updated Street");
        updatedAddressDTO.setCity("Updated City");
        updatedAddressDTO.setPostalCode("11111");

        var newAddressDTO = createAddressDTO();

        customerDTO.setAddresses(List.of(updatedAddressDTO, newAddressDTO));

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        Mockito.when(addressRepository.findAllByCustomerId(customerId)).thenReturn(Optional.of(List.of(existingAddress)));
        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(addressRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = customerService.updateCustomer(customerId, customerDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals("John", result.getName());
        Assertions.assertEquals("Doe", result.getLastname());
        Assertions.assertEquals(2, result.getAddresses().size());
        Assertions.assertEquals("Updated Street", result.getAddresses().get(0).getStreet());
        Assertions.assertEquals("Updated City", result.getAddresses().get(0).getCity());
        Assertions.assertEquals("123 Main St", result.getAddresses().get(1).getStreet());

        Mockito.verify(customerRepository).save(result);
        Mockito.verify(addressRepository).saveAll(Mockito.anyList());
    }

    @Test
    void updateCustomer_ShouldThrowException_WhenCustomerDoesNotExist() {
        var customerId = 1L;
        var customerDTO = new CustomerDTO();

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.empty());

        var exception = Assertions.assertThrows(CustomerException.class,
                () -> customerService.updateCustomer(customerId, customerDTO));
        Assertions.assertEquals("Customer not found.", exception.getMessage());
    }

    @Test
    void updateCustomer_ShouldAddNewAddress_WhenAddressNotInDatabase() {
        var customerId = 1L;
        var existingCustomer = createCustomer();

        var customerDTO = createCustomerDTO();
        var newAddressDTO = createAddressDTO();

        customerDTO.setAddresses(List.of(newAddressDTO));

        Mockito.when(customerRepository.findById(customerId)).thenReturn(Optional.of(existingCustomer));
        Mockito.when(addressRepository.findAllByCustomerId(customerId)).thenReturn(Optional.of(List.of()));
        Mockito.when(customerRepository.save(Mockito.any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(addressRepository.saveAll(Mockito.anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        var result = customerService.updateCustomer(customerId, customerDTO);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(1, result.getAddresses().size());
        Assertions.assertEquals("123 Main St", result.getAddresses().get(0).getStreet());

        Mockito.verify(customerRepository).save(result);
        Mockito.verify(addressRepository).saveAll(Mockito.anyList());
    }

    @Test
    void searchCustomers_ShouldReturnPagedResults() {
        var searchTerm = "John";
        int page = 0;
        int size = 2;
        var pageable = PageRequest.of(page, size);

        var customer1 = new Customer();
        customer1.setName("John");
        customer1.setLastname("Doe");

        var customer2 = new Customer();
        customer2.setName("John");
        customer2.setLastname("Smith");

        var customerList = List.of(customer1, customer2);
        var customerPage = new PageImpl<>(customerList, pageable, customerList.size());

        Mockito.when(customerRepository.searchCustomers(searchTerm, pageable))
                .thenReturn(customerPage);

        var result = customerService.searchCustomers(searchTerm, page, size);

        Assertions.assertNotNull(result);
        Assertions.assertEquals(2, result.getContent().size());
        Assertions.assertEquals("John", result.getContent().get(0).getName());
        Assertions.assertEquals("Doe", result.getContent().get(0).getLastname());
        Assertions.assertEquals("Smith", result.getContent().get(1).getLastname());

        Mockito.verify(customerRepository).searchCustomers(searchTerm, pageable);
    }

    private CustomerDTO createCustomerDTO() {
        var customerDTO = new CustomerDTO();
        customerDTO.setName("John");
        customerDTO.setLastname("Doe");
        customerDTO.setPhoneNumber("1234567890");
        customerDTO.setEmail("john.doe@example.com");
        customerDTO.setType("PRIVATE");
        return customerDTO;
    }

    private AddressDTO createAddressDTO() {
        var addressDTO = new AddressDTO();
        addressDTO.setStreet("123 Main St");
        addressDTO.setCity("New York");
        addressDTO.setPostalCode("10001");
        return addressDTO;
    }

    private Customer createCustomer() {
        var customerId = 1L;
        var customer = new Customer();
        customer.setId(customerId);
        customer.setName("John");
        customer.setLastname("Doe");
        customer.setPhoneNumber("1234567890");
        customer.setEmail("john.doe@example.com");
        customer.setType(CustomerType.PRIVATE);
        customer.setVersionNum(1);
        return customer;
    }
}
