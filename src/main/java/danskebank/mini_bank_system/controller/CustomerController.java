package danskebank.mini_bank_system.controller;

import danskebank.mini_bank_system.dto.AddressDTO;
import danskebank.mini_bank_system.dto.CustomerDTO;
import danskebank.mini_bank_system.dto.CustomerSearchResponse;
import danskebank.mini_bank_system.entity.Address;
import danskebank.mini_bank_system.entity.Customer;
import danskebank.mini_bank_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/create/{accountId}")
    public ResponseEntity<Customer> createCustomer(
            @PathVariable Long accountId,
            @RequestBody CustomerDTO customerDTO) {
        Customer createdCustomer = customerService.createCustomer(accountId, customerDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCustomer);
    }

    @PatchMapping("/update/{customerId}")
    public ResponseEntity<Customer> updateCustomer(@PathVariable Long customerId, @RequestBody CustomerDTO customerDTO) {
        Customer updatedCustomer = customerService.updateCustomer(customerId, customerDTO);
        return ResponseEntity.status(HttpStatus.OK).body(updatedCustomer);
    }

    @GetMapping("/search")
    public ResponseEntity<CustomerSearchResponse> searchCustomers(
            @RequestParam String searchTerm,
            @RequestParam int page,
            @RequestParam int size) {
        Page<Customer> customerPage = customerService.searchCustomers(searchTerm, page, size);
        var response = new CustomerSearchResponse();
        response.setTotalCount(customerPage.getTotalElements());
        response.setCustomers(customerMapper(customerPage.getContent()));
        return ResponseEntity.ok(response);
    }

    static List<CustomerDTO> customerMapper(List<Customer> customers) {
        ArrayList<CustomerDTO> customerDTOS = new ArrayList<>();
        customers.forEach(customer -> {
            CustomerDTO dto = new CustomerDTO();
            dto.setName(customer.getName());
            dto.setLastname(customer.getLastname());
            dto.setEmail(customer.getEmail());
            dto.setPhoneNumber(customer.getPhoneNumber());
            dto.setType(customer.getType().toString());
            dto.setAddresses(addressMapper(customer.getAddresses()));
            customerDTOS.add(dto);
        });
        return customerDTOS;
    }

    static List<AddressDTO> addressMapper(List<Address> addresses) {
        ArrayList<AddressDTO> addressDTOS = new ArrayList<>();
        addresses.forEach(address -> {
            AddressDTO dto = new AddressDTO();
            dto.setCity(address.getCity());
            dto.setStreet(address.getStreet());
            dto.setPostalCode(address.getPostalCode());
            addressDTOS.add(dto);
        });
        return addressDTOS;
    }
}
