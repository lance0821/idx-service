package idxsync.persistence.repository;

import idxsync.domain.SearchTermSchool;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface SearchTermSchoolRepository extends ElasticsearchCrudRepository<SearchTermSchool, String> {

}
