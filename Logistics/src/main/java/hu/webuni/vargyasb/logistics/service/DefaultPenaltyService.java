package hu.webuni.vargyasb.logistics.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import hu.webuni.vargyasb.logistics.config.LogisticsConfigProperties;

@Service
public class DefaultPenaltyService implements PenaltyService {

	@Autowired
	LogisticsConfigProperties config;
	
	@Override
	public double getPenaltyPercent(int delayInMinutes) {
		double penaltyPercent = 0.0;

		if (delayInMinutes >= config.getIncomePenalty().getLimitHigh()) {
			penaltyPercent = config.getIncomePenalty().getPercentHigh();
		} else if (delayInMinutes >= config.getIncomePenalty().getLimitMid()) {
			penaltyPercent = config.getIncomePenalty().getPercentMid();
		} else if (delayInMinutes >= config.getIncomePenalty().getLimitLow()) {
			penaltyPercent = config.getIncomePenalty().getPercentLow();
		}
		
		return penaltyPercent;
	}

	@Override
	public double getPenaltyAmount(double penaltyPercent, double estimatedIncome) {
		return penaltyPercent == 0 ? estimatedIncome : (estimatedIncome / 100.0 * (100.0 - penaltyPercent));
	}

}
