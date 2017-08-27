package no.fint.adapter;

import com.google.common.collect.Lists;
import no.fint.dto.Address;
import no.fint.dto.Person;
import no.fint.model.relation.FintResource;
import no.fint.model.relation.Relation;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Resources {

    List<FintResource> createPersonList() {
        Person person1 = new Person("1", "Mari");
        Relation relation1 = new Relation.Builder().with(Person.Relasjonsnavn.ADDRESS).forType(Person.class).field("address").value("1").build();
        FintResource<Person> resource1 = FintResource.with(person1).addRelations(relation1);

        Person person2 = new Person("2", "Per");
        Relation relation2 = new Relation.Builder().with(Person.Relasjonsnavn.ADDRESS).forType(Person.class).field("address").value("2").build();
        FintResource<Person> resource2 = FintResource.with(person2).addRelations(relation2);

        return Lists.newArrayList(resource1, resource2);
    }

    List<FintResource> createPerson(String id) {
        Person person = new Person(id, "Mari");
        Relation relation = new Relation.Builder().with(Person.Relasjonsnavn.ADDRESS).forType(Person.class).field("address").value(id).build();
        FintResource<Person> resource = FintResource.with(person).addRelations(relation);

        return Lists.newArrayList(resource);
    }

    List<FintResource> createAddressList() {
        Address address1 = new Address("1", "veien 1");
        Relation relation1 = new Relation.Builder().with(Address.Relasjonsnavn.PERSON).forType(Address.class).field("person").value("1").build();
        FintResource<Address> resource1 = FintResource.with(address1).addRelations(relation1);

        Address address2 = new Address("2", "veien 2");
        Relation relation2 = new Relation.Builder().with(Address.Relasjonsnavn.PERSON).forType(Address.class).field("person").value("2").build();
        FintResource<Address> resource2 = FintResource.with(address2).addRelations(relation2);

        return Lists.newArrayList(resource1, resource2);
    }

    List<FintResource> createAddress(String id) {
        Address address = new Address(id, "veien " + id);
        Relation relation = new Relation.Builder().with(Address.Relasjonsnavn.PERSON).forType(Address.class).field("person").value(id).build();
        FintResource<Address> resource = FintResource.with(address).addRelations(relation);

        return Lists.newArrayList(resource);
    }
}
