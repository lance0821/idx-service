package idxsync.idx.service;

import idxsync.domain.LookupValue;

import java.util.List;

public interface LookupValueService {

    LookupValue getLookupValue(String fieldName, String shortValue);

    List<LookupValue> getLookupValues();
}
