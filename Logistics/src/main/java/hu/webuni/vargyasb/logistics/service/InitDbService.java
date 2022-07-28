package hu.webuni.vargyasb.logistics.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.model.Milestone;
import hu.webuni.vargyasb.logistics.model.Section;
import hu.webuni.vargyasb.logistics.model.TransportPlan;
import hu.webuni.vargyasb.logistics.repository.AddressRepository;
import hu.webuni.vargyasb.logistics.repository.MilestoneRepository;
import hu.webuni.vargyasb.logistics.repository.SectionRepository;
import hu.webuni.vargyasb.logistics.repository.TransportPlanRepository;

@Service
public class InitDbService {

	@Autowired
	TransportPlanService transportPlanService;
	
	@Autowired
	TransportPlanRepository transportPlanRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	MilestoneRepository milestoneRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	public void clearDB() {
		transportPlanRepository.deleteAll();
		sectionRepository.deleteAll();
		milestoneRepository.deleteAll();
		addressRepository.deleteAll();
	}
	
	public void insertTestData() {
		Address aNagyatad = createAndSaveAddress("HU", "7500", "Nagyatád", "Elso", 10, 0, 0);
		Address aBerzence = createAndSaveAddress("HU", "7516", "Berzence", "Masodik", 2, 0, 0);
		Address aBudapest = createAndSaveAddress("HU", "1024", "Budapest", "Harmadik", 6, 0, 0);
		Address aBerlin = createAndSaveAddress("DE", "10115", "Berlin", "Ritterstrasse", 8, 0, 0);
		Address aBecs = createAndSaveAddress("AT", "1400", "Becs", "Baumgass", 5, 0, 0);
		Address aPecs = createAndSaveAddress("HU", "7624", "Pecs", "Szigeti", 2, 0, 0);
		Address aKaposvar = createAndSaveAddress("HU", "7400", "Kaposvar", "Kelemen", 22, 0, 0);
		
		Milestone mBerlin = createAndSaveMilestone(aBerlin, LocalDateTime.of(LocalDate.of(2022, 5, 03), LocalTime.of(10, 10)));
		Milestone mBecs = createAndSaveMilestone(aBecs, LocalDateTime.of(LocalDate.of(2022, 5, 20), LocalTime.of(10, 10)));
		Milestone mBudapest = createAndSaveMilestone(aBudapest, LocalDateTime.of(LocalDate.of(2022, 5, 30), LocalTime.of(10, 10)));
		Milestone mNagyatad = createAndSaveMilestone(aNagyatad, LocalDateTime.of(LocalDate.of(2022, 6, 15), LocalTime.of(10, 10)));
		Milestone mBerzence = createAndSaveMilestone(aBerzence, LocalDateTime.of(LocalDate.of(2022, 8, 02), LocalTime.of(10, 10)));
		Milestone mPecs = createAndSaveMilestone(aPecs, LocalDateTime.of(LocalDate.of(2022, 8, 05), LocalTime.of(10, 10)));
		Milestone mKaposvar = createAndSaveMilestone(aKaposvar, LocalDateTime.of(LocalDate.of(2023, 8, 05), LocalTime.of(10, 10)));
		
		Section sBerlinToBecs = createandSaveSection(mBerlin, mBecs);
		Section sBudapestToNagyatad = createandSaveSection(mBudapest, mNagyatad);
		Section sBerzenceToPecs = createandSaveSection(mBerzence, mPecs);
		
		TransportPlan pTrip = new TransportPlan();
		pTrip.setEstimatedIncome(150000);
		pTrip = transportPlanRepository.save(pTrip);
		long pTripId = pTrip.getId();
		transportPlanService.addSectionToTransportPlan(pTripId, sBerlinToBecs);
		transportPlanService.addSectionToTransportPlan(pTripId, sBudapestToNagyatad);
		transportPlanService.addSectionToTransportPlan(pTripId, sBerzenceToPecs);
		
	}
	
	private Address createAndSaveAddress(String countryCode, String zipCode, String city, String street,
			long number, double longitude, double latitude) {
		Address address = new Address();
		address.setCountryCode(countryCode);
		address.setZipCode(zipCode);
		address.setCity(city);
		address.setStreet(street);
		address.setNumber(number);
		address.setLongitude(longitude);
		address.setLatitude(latitude);
		
		return addressRepository.save(address);
	}
	
	private Milestone createAndSaveMilestone(Address address, LocalDateTime plannedTime) {
		Milestone milestone = new Milestone();
		milestone.setAddress(address);
		milestone.setPlannedTime(plannedTime);
		
		return milestoneRepository.save(milestone);
	}
	
	private Section createandSaveSection(Milestone fromMilestone, Milestone toMilestone) {
		Section section = new Section();
		section.setFromMilestone(fromMilestone);
		section.setToMilestone(toMilestone);
		
		return sectionRepository.save(section);
	}
	
}
