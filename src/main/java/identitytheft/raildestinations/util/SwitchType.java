package identitytheft.raildestinations.util;

import org.apache.commons.lang3.StringUtils;

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
            if (StringUtils.equalsIgnoreCase(tag, type.tag)) {
                return type;
            }
        }

        return null;
    }

}