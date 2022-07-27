package hu.webuni.vargyasb.logistics.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class TransportPlan {

	@Id
	@GeneratedValue
	private Long id;
	
	private double estimatedIncome;
	
	@OneToMany(mappedBy = "transportPlan")
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
	
	public void addSection(Section section) {
		sections.add(section);
		section.setTransportPlan(this);
		
		long currentSectionIndex = sections.size() - 1;
		section.setNumber(currentSectionIndex);
	}
	
}
