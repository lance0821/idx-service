package idxsync.idx.service;

import idxsync.domain.LookupValue;
import idxsync.persistence.repository.LookupValueRepository;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class LookupValuesCache {

    @Resource
    private LookupValueRepository lookupValueRepository;

    private Map<String, Map<String, LookupValue>> lookupValuesMap;

    public Boolean containsField(String field) {
        return getLookupValuesMap().containsKey(field);
    }

    public LookupValue getLookupValue(String field, String value) {
        Map<String, LookupValue> lookupValueMap = getLookupValuesMap().get(field);

        if (lookupValueMap == null) {
            return null;
        }

        return lookupValueMap.get(getLookupValueKey(field, value));
    }

    private Map<String, Map<String, LookupValue>> getLookupValuesMap() {
        if (lookupValuesMap == null) {
            synchronized (this) {

                if (lookupValuesMap != null)
                    return lookupValuesMap;

                lookupValuesMap = new LinkedHashMap<>();

                Iterable<LookupValue> lookupValues = lookupValueRepository.findAll();
                Iterator<LookupValue> iter = lookupValues.iterator();
                while(iter.hasNext()) {

                    LookupValue lookupValue = iter.next();

                    Map<String, LookupValue> lookupValueMap = this.lookupValuesMap.get(lookupValue.getStandardFieldName());

                    if (lookupValueMap == null) {
                        lookupValueMap = new LinkedHashMap<>();

                        this.lookupValuesMap.put(lookupValue.getStandardFieldName(), lookupValueMap);
                    }

                    lookupValueMap.put(getLookupValueKey(lookupValue), lookupValue);
                }
            }
        }

        return lookupValuesMap;
    }

    private String getLookupValueKey(LookupValue lookupValue) {
        return getLookupValueKey(lookupValue.getStandardFieldName(), lookupValue.getValueShortName());
    }

    private String getLookupValueKey(String field, String value) {
        return field.toLowerCase() + "_" + value.toLowerCase();
    }
}
