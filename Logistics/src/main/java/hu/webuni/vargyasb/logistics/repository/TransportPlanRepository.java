package hu.webuni.vargyasb.logistics.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hu.webuni.vargyasb.logistics.model.TransportPlan;

public interface TransportPlanRepository extends JpaRepository<TransportPlan, Long>{

}
