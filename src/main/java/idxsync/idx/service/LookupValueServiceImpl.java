package idxsync.idx.service;

import idxsync.domain.LookupValue;
import idxsync.persistence.repository.LookupValueRepository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.LinkedList;
import java.util.List;

@Service
public class LookupValueServiceImpl implements LookupValueService {

    @Resource
    private LookupValueRepository repository;

    @Override
    public LookupValue getLookupValue(String fieldName, String shortValue) {
        LookupValue lookupValue = repository.findByStandardFieldNameAndValueShortName(fieldName, shortValue);

        if (lookupValue == null) {
            throw new ResourceNotFoundException(
                    String.format("Could not find lookup value with fieldName %s and shortValue %s.",
                            fieldName, shortValue));
        }

        return lookupValue;
    }

    @Override
    public List<LookupValue> getLookupValues() {
        Iterable<LookupValue> iterable = repository.findAll();

        if (iterable == null || !iterable.iterator().hasNext()) {
            throw new ResourceNotFoundException("No lookup values exist.");
        }

        List<LookupValue> lookupValues = new LinkedList<>();

        iterable.forEach(lookupValues::add);

        return lookupValues;
    }
}
