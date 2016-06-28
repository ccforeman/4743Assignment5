package model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class States {
	
	private static final Set<String> STATES = new HashSet<String>(Arrays.asList("AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA", "HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA",
			"ME", "MD", "MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ", "NM", "NY", "NC", "ND", "OH", "OK",
			"OR", "PA", "RI", "SC", "SD", "TN", "TX", "UT", "VA", "WA", "WV", "WV", "WI", "WY"));
	
	
	public static Set<String> getStates() {
		return STATES;
	}
	
}
