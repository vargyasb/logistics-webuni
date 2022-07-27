package hu.webuni.vargyasb.logistics.service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import hu.webuni.vargyasb.logistics.dto.AddressDto;
import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.repository.AddressRepository;

@Service
public class AddressService {

	@Autowired
	AddressRepository addressRepository;
	
	@Transactional
	public Address createAddress(Address address) {
		return addressRepository.save(address);
	}
	
	public List<Address> findAll() {
		return addressRepository.findAll();
	}
	
	public Optional<Address> findById(Long id) {
		return addressRepository.findById(id);
	}
	
	@Transactional
	public void deleteById(Long id) {
		if (addressRepository.existsById(id)) {
			addressRepository.deleteById(id);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	@Transactional
	public Address modifyAddress(Long id, Address address) {
		if (addressRepository.existsById(id)) {
			address.setId(id);
			return addressRepository.save(address);
		} else {
			throw new NoSuchElementException();
		}
	}
	
	public Page<Address> findAddressesByExample(AddressDto example, Pageable pageable) {
		String countryCode = example.getCountryCode();
		String city = example.getCity();
		String zipCode = example.getZipCode();
		String street = example.getStreet();
		
		Specification<Address> spec = Specification.where(null);
		
		if (StringUtils.hasText(countryCode))
			spec = spec.and(AddressSpecifications.hasCountryCode(countryCode));
		if (StringUtils.hasText(city))
			spec = spec.and(AddressSpecifications.hasCity(city));
		if (StringUtils.hasText(zipCode))
			spec = spec.and(AddressSpecifications.hasZipCode(zipCode));
		if (StringUtils.hasText(street))
			spec = spec.and(AddressSpecifications.hasStreet(street));
		
		return addressRepository.findAll(spec, pageable);
	}
}
