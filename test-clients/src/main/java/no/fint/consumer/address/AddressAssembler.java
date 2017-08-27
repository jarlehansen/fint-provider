package no.fint.consumer.address;

import no.fint.dto.Address;
import no.fint.model.relation.FintResource;
import no.fint.relations.FintResourceAssembler;
import no.fint.relations.FintResourceSupport;
import org.springframework.stereotype.Component;

@Component
public class AddressAssembler extends FintResourceAssembler<Address> {

    public AddressAssembler() {
        super(AddressController.class);
    }

    @Override
    public FintResourceSupport assemble(Address address, FintResource<Address> fintResource) {
        return createResourceWithId(address.getId(), fintResource);
    }
}
