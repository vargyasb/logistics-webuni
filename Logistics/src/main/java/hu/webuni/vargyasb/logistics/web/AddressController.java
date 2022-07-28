package hu.webuni.vargyasb.logistics.web;

import java.util.List;
import java.util.NoSuchElementException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import hu.webuni.vargyasb.logistics.dto.AddressDto;
import hu.webuni.vargyasb.logistics.mapper.AddressMapper;
import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.service.AddressService;

@RestController
@RequestMapping("/api/addresses")
public class AddressController {

	@Autowired
	AddressService addressService;
	
	@Autowired
	AddressMapper addressMapper;
	
	@PostMapping
	public AddressDto addNewAddress(@RequestBody @Valid AddressDto addressDto) {
		if (addressDto.getId() != null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		}
		Address address = addressService.createAddress(addressMapper.addressDtoToAddress(addressDto));
		return addressMapper.addressToAddressDto(address);
	}
	
	@GetMapping
	public List<AddressDto> getAll() {
		return addressMapper.addressesToDtos(addressService.findAll());
	}
	
	@GetMapping("/{id}")
	public AddressDto getById(@PathVariable long id) {
		Address address = addressService.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return addressMapper.addressToAddressDto(address);
	}
	
	@DeleteMapping("/{id}")
	public void deleteById(@PathVariable long id) {
		try {
			addressService.deleteById(id);
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	@PutMapping("/{id}")
	public AddressDto modifyAddress(@PathVariable long id, @RequestBody @Valid AddressDto addressDto) {
		if (addressDto.getId() != null && addressDto.getId() != id) 
				throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
		
		Address address;
		try {
			address = addressService.modifyAddress(id, addressMapper.addressDtoToAddress(addressDto));
		} catch (NoSuchElementException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
		return addressMapper.addressToAddressDto(address);
	}
	
	@PostMapping("/search")
	public ResponseEntity<Object> findAddressesByExample(@RequestBody AddressDto example, 
			@PageableDefault(sort = {"id"}, page = 0, value = Integer.MAX_VALUE) Pageable pageable) {
		Page<Address> page = addressService.findAddressesByExample(example, pageable);
		Long totalCount = page.getTotalElements();
		
		HttpHeaders headers = new HttpHeaders();
		headers.add("X-Total-Count", totalCount.toString());

		return ResponseEntity.ok()
				.headers(headers)
				.body(page.getContent());
	}
}
