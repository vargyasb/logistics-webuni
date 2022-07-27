package hu.webuni.vargyasb.logistics.dto;

import java.util.ArrayList;
import java.util.List;

import hu.webuni.vargyasb.logistics.model.Section;

public class TransportPlanDto {

	private Long id;
	private double estimatedIncome;
	private List<Section> sections = new ArrayList<>();

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public double getEstimatedIncome() {
		return estimatedIncome;
	}

	public void setEstimatedIncome(double estimatedIncome) {
		this.estimatedIncome = estimatedIncome;
	}

	public List<Section> getSections() {
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
}
