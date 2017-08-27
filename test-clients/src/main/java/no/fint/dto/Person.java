package no.fint.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {
    public enum Relasjonsnavn {
        ADDRESS
    }

    private String id;
    private String name;
}
