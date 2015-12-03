package idxsync.persistence.repository;

import idxsync.domain.ListingMult;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ListingMultRepository extends ElasticsearchCrudRepository<ListingMult, String> {
    ListingMult findByMatrixUniqueId(long matrixUniqueId);
    ListingMult findByMlsNumber(String mlsNumber);
}