package idxsync.persistence.repository;

import idxsync.domain.SearchTermResidential;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface SearchTermResidentialRepository extends ElasticsearchCrudRepository<SearchTermResidential, String> {
}
