package no.fint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {
    public enum Relasjonsnavn {
        PERSON
    }

    private String id;
    private String street;
}
