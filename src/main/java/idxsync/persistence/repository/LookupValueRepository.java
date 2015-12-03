package idxsync.persistence.repository;

import idxsync.domain.LookupValue;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface LookupValueRepository extends ElasticsearchCrudRepository<LookupValue, String> {

    LookupValue findByStandardFieldNameAndValueShortName(String standardFieldName, String valueShortName);
}
