package danskebank.mini_bank_system.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

@Data
public class AddressDTO {
    @JsonIgnore
    private Long id;
    private String street;
    private String city;
    private String postalCode;
}
