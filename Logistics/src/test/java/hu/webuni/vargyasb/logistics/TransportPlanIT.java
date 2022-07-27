package hu.webuni.vargyasb.logistics;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.web.reactive.server.WebTestClient;

import hu.webuni.vargyasb.logistics.dto.DelayDto;
import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.model.Milestone;
import hu.webuni.vargyasb.logistics.model.Section;
import hu.webuni.vargyasb.logistics.model.TransportPlan;
import hu.webuni.vargyasb.logistics.repository.AddressRepository;
import hu.webuni.vargyasb.logistics.repository.MilestoneRepository;
import hu.webuni.vargyasb.logistics.repository.SectionRepository;
import hu.webuni.vargyasb.logistics.repository.TransportPlanRepository;
import hu.webuni.vargyasb.logistics.service.TransportPlanService;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
public class TransportPlanIT {

	private static final String BASE_URI = "api/transportPlans";
	
	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	TransportPlanRepository transportPlanRepository;
	
	@Autowired
	AddressRepository addressRepository;
	
	@Autowired
	MilestoneRepository milestoneRepository;
	
	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	TransportPlanService transportPlanService;
	
	@BeforeEach
	public void init() {
		sectionRepository.deleteAll();
		transportPlanRepository.deleteAll();
		milestoneRepository.deleteAll();
		addressRepository.deleteAll();
		
		createTestData();
	}
	
	@Test
	void testThatCorrectPenaltyIsRegisteredToTransportPlan() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		Milestone milestone = milestoneRepository.findAll().get(0);
		long milestoneId = milestone.getId();
		int delayInMinutes = 29;
		
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(milestoneId);
		delayDto.setDelayInMinutes(delayInMinutes);
		
		double estimatedIncome = transportPlan.getEstimatedIncome();
		
		//0%
		registerDelay(transportPlanId, delayDto);

		transportPlan = transportPlanRepository.findById(transportPlanId).get();
		double estimatedIncomeAfterPenalty0 = transportPlan.getEstimatedIncome();
		
		assertThat(estimatedIncomeAfterPenalty0).isEqualTo(estimatedIncome);
		
		// 1%
		delayDto.setDelayInMinutes(30);
		registerDelay(transportPlanId, delayDto);
		
		transportPlan = transportPlanRepository.findById(transportPlanId).get();
		double estimatedIncomeAfterPenalty1 = transportPlan.getEstimatedIncome();
		
		assertThat(estimatedIncomeAfterPenalty1).isEqualTo(estimatedIncome / 100 * (100.0 - 1));
		
		//2.5%
		delayDto.setDelayInMinutes(60);
		registerDelay(transportPlanId, delayDto);
		
		transportPlan = transportPlanRepository.findById(transportPlanId).get();
		double estimatedIncomeAfterPenalty25 = transportPlan.getEstimatedIncome();
		
		assertThat(estimatedIncomeAfterPenalty25).isEqualTo(estimatedIncomeAfterPenalty1 / 100 * (100.0 - 2.5));
		
		//4%
		delayDto.setDelayInMinutes(120);
		registerDelay(transportPlanId, delayDto);
		
		transportPlan = transportPlanRepository.findById(transportPlanId).get();
		double estimatedIncomeAfterPenalty4 = transportPlan.getEstimatedIncome();
		
		assertThat(estimatedIncomeAfterPenalty4).isEqualTo(estimatedIncomeAfterPenalty25 / 100 * (100.0 - 4));
	}
	
	@Test
	void testThatDelayIsRegisteredInBothMilestonesInDifferentSections() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		
		Section section1 = sectionRepository.findAll().get(0);
		Section section2 = sectionRepository.findAll().get(1);

		Milestone section1ToMilestone = section1.getToMilestone();
		long section1ToMilestoneId =  section1ToMilestone.getId();
		LocalDateTime section1ToMilestonePlannedTimeBefore = section1ToMilestone.getPlannedTime();
		Milestone section2FromMilestone = section2.getFromMilestone();
		long section2FromMilestoneId = section2FromMilestone.getId();
		LocalDateTime section2FromMilestonePlannedTimeBefore = section2FromMilestone.getPlannedTime();
		
		int delayInMinutes = 120;
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(section1ToMilestoneId);
		delayDto.setDelayInMinutes(delayInMinutes);
		
		registerDelay(transportPlanId, delayDto);
		section1ToMilestone = milestoneRepository.findById(section1ToMilestoneId).get();
		section2FromMilestone = milestoneRepository.findById(section2FromMilestoneId).get();
		
		assertThat(section1ToMilestonePlannedTimeBefore).isNotEqualTo(section1ToMilestone.getPlannedTime());
		assertThat(section1ToMilestone.getPlannedTime()).isEqualTo(section1ToMilestonePlannedTimeBefore.plusMinutes(delayInMinutes));
		
		assertThat(section2FromMilestonePlannedTimeBefore).isNotEqualTo(section2FromMilestone.getPlannedTime());
		assertThat(section2FromMilestone.getPlannedTime()).isEqualTo(section2FromMilestonePlannedTimeBefore.plusMinutes(delayInMinutes));
	}
	
	@Test
	void testThatDelayIsRegisteredInBothMilestonesInOneSection() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		
		Section section = sectionRepository.findAll().get(0);

		Milestone fromMilestone = section.getFromMilestone();
		long fromMilestoneId =  fromMilestone.getId();
		LocalDateTime fromPlannedTimeBefore = fromMilestone.getPlannedTime();
		Milestone toMilestone = section.getToMilestone();
		long toMilestoneId = toMilestone.getId();
		LocalDateTime toPlannedTimeBefore = toMilestone.getPlannedTime();
		
		int delayInMinutes = 120;
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(fromMilestoneId);
		delayDto.setDelayInMinutes(delayInMinutes);
		
		registerDelay(transportPlanId, delayDto);
		fromMilestone = milestoneRepository.findById(fromMilestoneId).get();
		toMilestone = milestoneRepository.findById(toMilestoneId).get();
		
		assertThat(fromPlannedTimeBefore).isNotEqualTo(fromMilestone.getPlannedTime());
		assertThat(fromMilestone.getPlannedTime()).isEqualTo(fromPlannedTimeBefore.plusMinutes(delayInMinutes));
		
		assertThat(toPlannedTimeBefore).isNotEqualTo(toMilestone.getPlannedTime());
		assertThat(toMilestone.getPlannedTime()).isEqualTo(toPlannedTimeBefore.plusMinutes(delayInMinutes));
	}
	
	@Test
	void testThatMilestonePlannedTimeDelayIsRegistered() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		Milestone milestone = milestoneRepository.findAll().get(0);
		long milestoneId = milestone.getId();
		int delayInMinutes = 120;
		
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(milestoneId);
		delayDto.setDelayInMinutes(delayInMinutes);
		
		LocalDateTime plannedTimeBefore = milestone.getPlannedTime();
		
		registerDelay(transportPlanId, delayDto);
		
		milestone = milestoneRepository.findById(milestoneId).get();
		LocalDateTime plannedTimeAfter = milestone.getPlannedTime();
		
		assertThat(plannedTimeBefore).isNotEqualTo(plannedTimeAfter);
		assertThat(plannedTimeAfter).isEqualTo(plannedTimeBefore.plusMinutes(delayInMinutes));
	}
	
	@Test
	void testThatMilestoneIsNotPresentInAnySection() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		
		Address testAddress = createAndSaveAddress("HU", "7600", "Kaposvar", "Kelemen", 2, 0, 0);
		Milestone testMilestone = createAndSaveMilestone(testAddress, LocalDateTime.of(LocalDate.of(2022, 5, 03), LocalTime.of(10, 10)));
		long testMilestoneId = testMilestone.getId();
		
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(testMilestoneId);
		delayDto.setDelayInMinutes(120);
		
		registerDelayWithMilestoneNotInAnySection(transportPlanId, delayDto);
	}
	
	@Test
	void testThatDelayIsNotRegistered_DueToIdNotFound() throws Exception {
		long transportPlanId = 9999;
		DelayDto delayDto = new DelayDto();
		
		registerDelayWithNonExistentTransportPlanIdOrMilestone(transportPlanId, delayDto);
	}
	
	@Test
	void testThatDelayIsNotRegistered_DueToMilestoneNotFound() throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findAll().get(0);
		long transportPlanId = transportPlan.getId();
		DelayDto delayDto = new DelayDto();
		delayDto.setMilestoneId(9999L);
		delayDto.setDelayInMinutes(120);
		
		registerDelayWithNonExistentTransportPlanIdOrMilestone(transportPlanId, delayDto);
	}
	
	public void createTestData() {
		Address aNagyatad = createAndSaveAddress("HU", "7500", "Nagyatád", "Elso", 10, 0, 0);
		Address aBerzence = createAndSaveAddress("HU", "7516", "Berzence", "Masodik", 2, 0, 0);
		Address aBudapest = createAndSaveAddress("HU", "1024", "Budapest", "Harmadik", 6, 0, 0);
		Address aBerlin = createAndSaveAddress("DE", "10115", "Berlin", "Ritterstrasse", 8, 0, 0);
		Address aBecs = createAndSaveAddress("AT", "1400", "Becs", "Baumgass", 5, 0, 0);
		Address aPecs = createAndSaveAddress("HU", "7330", "Pecs", "Szigeti", 2, 0, 0);
		
		Milestone mBerlin = createAndSaveMilestone(aBerlin, LocalDateTime.of(LocalDate.of(2022, 5, 03), LocalTime.of(10, 10)));
		Milestone mBecs = createAndSaveMilestone(aBecs, LocalDateTime.of(LocalDate.of(2022, 5, 20), LocalTime.of(10, 10)));
		Milestone mBudapest = createAndSaveMilestone(aBudapest, LocalDateTime.of(LocalDate.of(2022, 5, 30), LocalTime.of(10, 10)));
		Milestone mNagyatad = createAndSaveMilestone(aNagyatad, LocalDateTime.of(LocalDate.of(2022, 6, 15), LocalTime.of(10, 10)));
		Milestone mBerzence = createAndSaveMilestone(aBerzence, LocalDateTime.of(LocalDate.of(2022, 8, 02), LocalTime.of(10, 10)));
		Milestone mPecs = createAndSaveMilestone(aPecs, LocalDateTime.of(LocalDate.of(2022, 8, 05), LocalTime.of(10, 10)));
		
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
	
	private void registerDelay(long transportPlanId, DelayDto delayDto) {
		webTestClient
			.post()
			.uri(BASE_URI + "/" + transportPlanId + "/delay")
			.bodyValue(delayDto)
			.exchange()
			.expectStatus()
			.isOk();
	}
	
	private void registerDelayWithNonExistentTransportPlanIdOrMilestone(long transportPlanId, DelayDto delayDto) {
		webTestClient
			.post()
			.uri(BASE_URI + "/" + transportPlanId + "/delay")
			.bodyValue(delayDto)
			.exchange()
			.expectStatus()
			.isNotFound();
	}
	
	private void registerDelayWithMilestoneNotInAnySection(long transportPlanId, DelayDto delayDto) {
		webTestClient
			.post()
			.uri(BASE_URI + "/" + transportPlanId + "/delay")
			.bodyValue(delayDto)
			.exchange()
			.expectStatus()
			.isBadRequest();
	}
}
