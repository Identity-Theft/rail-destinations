package identitytheft.raildestinations.util;

public enum SwitchType {
	NORMAL("[destination]"),
	INVERTED("[!destination]");

	private final String tag;

	SwitchType(String tag) {
		this.tag = tag;
	}

	public static SwitchType find(String tag) {
		if (tag == null || tag.isEmpty()) {
			return null;
		}
		for (SwitchType type : values()) {
			if (tag.equalsIgnoreCase(type.tag)) {
				return type;
			}
		}
		return null;
	}
}
