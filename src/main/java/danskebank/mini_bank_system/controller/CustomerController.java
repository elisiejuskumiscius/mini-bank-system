package danskebank.mini_bank_system.controller;

import danskebank.mini_bank_system.dto.CustomerDTO;
import danskebank.mini_bank_system.dto.CustomerSearchResponse;
import danskebank.mini_bank_system.entity.Customer;
import danskebank.mini_bank_system.service.CustomerService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        response.setCustomers(customerPage.getContent());
        return ResponseEntity.ok(response);
    }
}
