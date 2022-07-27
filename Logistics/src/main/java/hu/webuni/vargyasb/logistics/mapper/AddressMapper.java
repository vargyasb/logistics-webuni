package hu.webuni.vargyasb.logistics.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import hu.webuni.vargyasb.logistics.dto.AddressDto;
import hu.webuni.vargyasb.logistics.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {
	
	List<AddressDto> addressesToDtos(List<Address> addresses);
	
	List<Address> addressDtosToAddresses(List<AddressDto> addressDtos);

	AddressDto addressToAddressDto(Address address);
	
	Address addressDtoToAddress(AddressDto addressDto);
}
