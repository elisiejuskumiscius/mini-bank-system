package danskebank.mini_bank_system.dto;

import danskebank.mini_bank_system.entity.Customer;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CustomerSearchResponse {
    private long totalCount;
    private List<Customer> customers;
}
