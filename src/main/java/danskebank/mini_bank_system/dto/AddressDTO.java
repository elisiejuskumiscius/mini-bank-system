package danskebank.mini_bank_system.dto;

import lombok.Data;

@Data
public class AddressDTO {
    private Long id;
    private String street;
    private String city;
    private String postalCode;
}
