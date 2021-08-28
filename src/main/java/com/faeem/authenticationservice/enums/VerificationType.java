package com.faeem.authenticationservice.enums;

public enum VerificationType {
	
	REGISTER("REGISTER"), FORGET_PASSWORD("FORGET_PASSWORD");
	
	
    private final String value;

    private VerificationType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
	
    public Boolean isValid(String value) {
    	for (VerificationType verificationType : VerificationType.values()) {
    		if(verificationType.getValue().equals(value)) {
    			return Boolean.TRUE;
    		}
		}
    	return Boolean.FALSE;
    }

}
