package idxsync.persistence.repository;

import idxsync.domain.ListingLand;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ListingLandRepository extends ElasticsearchCrudRepository<ListingLand, String> {
    ListingLand findByMatrixUniqueId(long matrixUniqueId);
    ListingLand findByMlsNumber(String mlsNumber);
}