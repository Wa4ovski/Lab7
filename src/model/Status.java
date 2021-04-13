package model;

/**
 * Status enum class. Contains four values.
 */

public enum Status {
    FIRED,
    RECOMMENDED_FOR_PROMOTION,
    REGULAR,
    PROBATION;

    /**
     * Returns comma separated list with the forms.
     * @return FIRED, RECOMMENDED_FOR_PROMOTION, REGULAR, PROBATION
     */
    public static String nameList() {
        StringBuilder nameList = new StringBuilder();
        for (Status status: values()) {
            nameList.append(status.name()).append(", ");
        }
        return nameList.substring(0, nameList.length() - 2);
    }
}
