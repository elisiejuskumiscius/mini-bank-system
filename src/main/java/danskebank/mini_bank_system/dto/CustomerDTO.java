package danskebank.mini_bank_system.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerDTO {
    private String name;
    private String lastname;
    private String phoneNumber;
    private String email;
    private String type;
    private List<AddressDTO> addresses;
}