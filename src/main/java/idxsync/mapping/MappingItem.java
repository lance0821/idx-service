package idxsync.mapping;


public class MappingItem {
    private String srcFieldName;
    private String srcDataType;
    private String standardFieldName;
    private String standardDataType;

    public String getSrcFieldName() {
        return srcFieldName;
    }

    public void setSrcFieldName(String srcFieldName) {
        this.srcFieldName = srcFieldName;
    }

    public String getSrcDataType() {
        return srcDataType;
    }

    public void setSrcDataType(String srcDataType) {
        this.srcDataType = srcDataType;
    }

    public String getStandardFieldName() {
        return standardFieldName;
    }

    public void setStandardFieldName(String standardFieldName) {
        this.standardFieldName = standardFieldName;
    }

    public String getStandardDataType() {
        return standardDataType;
    }

    public void setStandardDataType(String standardDataType) {
        this.standardDataType = standardDataType;
    }
}
