package idxsync.persistence.repository;

import idxsync.domain.OpenHouse;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface OpenHouseRepository extends ElasticsearchCrudRepository<OpenHouse, String> {

    Iterable<OpenHouse> findAllByMatrixModifiedDateGreaterThanEqual(Long matrixModifiedDate);
}