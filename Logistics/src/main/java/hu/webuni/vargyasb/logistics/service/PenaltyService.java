package hu.webuni.vargyasb.logistics.service;

public interface PenaltyService {

	double getPenaltyPercent(int delayInMinutes);
	double getPenaltyAmount(double penaltyPercent, double estimatedIncome);
}
