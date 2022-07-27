package hu.webuni.vargyasb.logistics.service;

import org.springframework.data.jpa.domain.Specification;

import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.model.Address_;

public class AddressSpecifications {

	public static Specification<Address> hasCountryCode(String countryCode) {
		return (root, cq, cb) -> cb.equal(root.get(Address_.countryCode), countryCode); 
	}

	public static Specification<Address> hasCity(String city) {
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.city)), city.toLowerCase() + "%");
	}

	public static Specification<Address> hasZipCode(String zipCode) {
		return (root, cq, cb) -> cb.equal(root.get(Address_.zipCode), zipCode);
	}

	public static Specification<Address> hasStreet(String street) {
		return (root, cq, cb) -> cb.like(cb.lower(root.get(Address_.street)), street.toLowerCase() + "%");
	}

	
}
