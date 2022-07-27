package hu.webuni.vargyasb.logistics.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "logistics")
@Component
public class LogisticsConfigProperties {
	
	private IncomePenalty incomePenalty;
	
	public IncomePenalty getIncomePenalty() {
		return incomePenalty;
	}

	public void setIncomePenalty(IncomePenalty incomePenalty) {
		this.incomePenalty = incomePenalty;
	}

	public static class IncomePenalty {
		private Double limitLow;
		private Double limitMid;
		private Double limitHigh;
		private Double percentLow;
		private Double percentMid;
		private Double percentHigh;
		
		public Double getLimitLow() {
			return limitLow;
		}
		public void setLimitLow(Double limitLow) {
			this.limitLow = limitLow;
		}
		public Double getLimitMid() {
			return limitMid;
		}
		public void setLimitMid(Double limitMid) {
			this.limitMid = limitMid;
		}
		public Double getLimitHigh() {
			return limitHigh;
		}
		public void setLimitHigh(Double limitHigh) {
			this.limitHigh = limitHigh;
		}
		public Double getPercentLow() {
			return percentLow;
		}
		public void setPercentLow(Double percentLow) {
			this.percentLow = percentLow;
		}
		public Double getPercentMid() {
			return percentMid;
		}
		public void setPercentMid(Double percentMid) {
			this.percentMid = percentMid;
		}
		public Double getPercentHigh() {
			return percentHigh;
		}
		public void setPercentHigh(Double percentHigh) {
			this.percentHigh = percentHigh;
		}
	}
}
