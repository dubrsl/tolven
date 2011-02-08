package org.tolven.core.entity;

/**
 * Types of questions asked when the user logs in for the first time
 * @author Sashikanth Vema
 *
 */
public enum QuestionType {
	
	CHECKBOX("checkbox"),
	RADIO("radio"),
	MULTILIST("multilist"),
	ONELIST("onelist");
	
	private final String value;

    QuestionType(String v) {
        value = v;
    }

    public String value() {
        return value;
    }

    public static QuestionType fromValue(String v) {
        for (QuestionType c: QuestionType.values()) {
            if (c.value.equals(v)) {
                return c;
            }
        }
        throw new IllegalArgumentException(v.toString());
    }

} // enum Question Type
