package hu.webuni.vargyasb.logistics.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import hu.webuni.vargyasb.logistics.config.LogisticsConfigProperties;
import hu.webuni.vargyasb.logistics.dto.DelayDto;
import hu.webuni.vargyasb.logistics.model.Address;
import hu.webuni.vargyasb.logistics.model.Milestone;
import hu.webuni.vargyasb.logistics.model.Section;
import hu.webuni.vargyasb.logistics.model.TransportPlan;
import hu.webuni.vargyasb.logistics.repository.MilestoneRepository;
import hu.webuni.vargyasb.logistics.repository.SectionRepository;
import hu.webuni.vargyasb.logistics.repository.TransportPlanRepository;

@Service
public class TransportPlanService {

	@Autowired
	TransportPlanRepository transportPlanRepository;

	@Autowired
	MilestoneRepository milestoneRepository;

	@Autowired
	SectionRepository sectionRepository;
	
	@Autowired
	PenaltyService penaltyService;

	@Transactional
	public void registerDelay(long id, DelayDto delayDto) throws Exception {
		TransportPlan transportPlan = transportPlanRepository.findById(id).get();
		long milestoneId = delayDto.getMilestoneId();
		Milestone milestone = milestoneRepository.findById(milestoneId).get();
		
		if (transportPlan == null || milestone == null)
			throw new NoSuchElementException();

		int delayInMinutes = delayDto.getDelayInMinutes();
		List<Section> unfilteredSections = transportPlan.getSections();
		
		List<Section> filteredSections = checkIfSectionsContainMilestone(unfilteredSections, milestone);

		if (filteredSections == null || filteredSections.isEmpty())
			throw new Exception();
		
		saveDelayToMilestones(unfilteredSections, filteredSections, delayInMinutes, milestone);
		applyIncomePenalty(transportPlan, delayInMinutes);
	}
	
	@Transactional
	public void applyIncomePenalty(TransportPlan transportPlan, int delayInMinutes) {
		double estimatedIncome = transportPlan.getEstimatedIncome();
		double penaltyPercent = penaltyService.getPenaltyPercent(delayInMinutes);
		estimatedIncome = penaltyService.getPenaltyAmount(penaltyPercent, estimatedIncome);
		
		transportPlan.setEstimatedIncome(estimatedIncome);
		transportPlanRepository.save(transportPlan);
	}

	@Transactional
	public TransportPlan addSectionToTransportPlan(long transportPlanId, Section section) {
		Optional<TransportPlan> transportPlanOptional = transportPlanRepository.findById(transportPlanId);
		if (transportPlanOptional.isPresent()) {
			TransportPlan transportPlan = transportPlanOptional.get();
			transportPlan.addSection(section);
			section.setTransportPlan(transportPlan);
			sectionRepository.save(section);
			return transportPlan;
		} else {
			throw new NoSuchElementException();
		}
	}
	
	@Transactional
	public List<Section> checkIfSectionsContainMilestone(List<Section> sections, Milestone milestone) {
		if (sections != null) {
			return sections.stream()
					.filter(s -> s.getFromMilestone().equals(milestone) || s.getToMilestone().equals(milestone))
					.collect(Collectors.toList());
		}
		return null;
	}
	
	public void saveDelayToMilestones(List<Section> sections, List<Section> filteredSections, int delayInMinutes, Milestone milestone) {
		if (!filteredSections.isEmpty()) {
			Milestone filteredFromMilestone = filteredSections.get(0).getFromMilestone();
			Milestone filteredToMilestone = filteredSections.get(0).getToMilestone();
			if (filteredFromMilestone.equals(milestone)) {
				filteredFromMilestone.setPlannedTime(filteredFromMilestone.getPlannedTime().plusMinutes(delayInMinutes));
				filteredToMilestone.setPlannedTime(filteredToMilestone.getPlannedTime().plusMinutes(delayInMinutes));
			} else {
				filteredToMilestone.setPlannedTime(filteredToMilestone.getPlannedTime().plusMinutes(delayInMinutes));
				long nextSectionNumber = filteredSections.get(0).getNumber() + 1;
				if (nextSectionNumber < sections.size()) {
					Section nextSection = sections.get((int) nextSectionNumber);
					Milestone nextSectionFromMilestone = nextSection.getFromMilestone();
					nextSectionFromMilestone.setPlannedTime(nextSectionFromMilestone.getPlannedTime().plusMinutes(delayInMinutes));
					milestoneRepository.save(nextSectionFromMilestone);
				}
			}
			milestoneRepository.save(filteredFromMilestone);
			milestoneRepository.save(filteredToMilestone);
		}
	}

}
