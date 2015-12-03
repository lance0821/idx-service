package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;

import java.util.UUID;

@Document(indexName = "idx", type = "lookup_value")
public class LookupValue {

    private String id;

    private String mlsFieldName;
    private String standardFieldName;

    private String valueShortName;
    private String valueLongName;

    public LookupValue() {
        id = UUID.randomUUID().toString();
    }

    public String getId() {
        return id;
    }

    public String getMlsFieldName() {
        return mlsFieldName;
    }

    public void setMlsFieldName(String mlsFieldName) {
        this.mlsFieldName = mlsFieldName;
    }

    public String getStandardFieldName() {
        return standardFieldName;
    }

    public void setStandardFieldName(String standardFieldName) {
        this.standardFieldName = standardFieldName;
    }

    public String getValueShortName() {
        return valueShortName;
    }

    public void setValueShortName(String valueShortName) {
        this.valueShortName = valueShortName;
    }

    public String getValueLongName() {
        return valueLongName;
    }

    public void setValueLongName(String valueLongName) {
        this.valueLongName = valueLongName;
    }
}
