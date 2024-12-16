package danskebank.mini_bank_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
//@Audited
@Getter
@Setter
public abstract class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @Version
    @JsonIgnore
    private int versionNum;
    @JsonIgnore
    private String createdBy;
    @JsonIgnore
    private LocalDateTime creationDate;
    @JsonIgnore
    private String lastModifiedBy;
    @JsonIgnore
    private LocalDateTime lastModifiedDate;
}
