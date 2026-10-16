package exceptions;

public class BusinessRuleException extends GameZoneException {

    public BusinessRuleException(String message) {
        super(message, "BUSINESS_RULE_VIOLATION");
    }
}