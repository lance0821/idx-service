package idxsync.domain;

public enum SearchTermType {

    STREET("STREET"),
    CITY("CITY"),
    ZIPCODE("ZIPCODE"),
    MLS_NUMBER("MLS_NUMBER"),
    NEIGHBOURHOOD("NEIGHBOURHOOD"),
    BUILDING_NAME("BUILDING_NAME"),
    ELEMENTARY_SCHOOL("ELEMENTARY_SCHOOL"),
    MIDDLE_SCHOOL("MIDDLE_SCHOOL"),
    HIGH_SCHOOL("HIGH_SCHOOL");

    private String type;

    SearchTermType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
