package no.fint.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.fint.model.relation.Identifiable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person implements Identifiable {
    public enum Relasjonsnavn {
        ADDRESS
    }

    private String id;
    private String name;

    @JsonIgnore
    @Override
    public String getId() {
        return id;
    }
}
