package danskebank.mini_bank_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account extends BaseEntity {
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JsonIgnore
    private Set<Customer> customers = new HashSet<>();

    private int numberOfOwners;

    public void addCustomer(Customer customer) {
        boolean exists = customers.stream().anyMatch(c ->
                c.getName().equalsIgnoreCase(customer.getName()) &&
                        c.getLastname().equalsIgnoreCase(customer.getLastname()) &&
                        c.getPhoneNumber().equals(customer.getPhoneNumber()) &&
                        c.getEmail().equalsIgnoreCase(customer.getEmail())
        );

        if (exists) {
            throw new RuntimeException("Customer is already assigned to this account.");
        }

        customer.setAccount(this);
        customers.add(customer);
        numberOfOwners = customers.size();
    }

    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        numberOfOwners = customers.size();
    }
}
