package idxsync.persistence.repository;

import idxsync.domain.ListingCommercial;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface ListingCommercialRepository extends ElasticsearchCrudRepository<ListingCommercial, String> {
    ListingCommercial findByMatrixUniqueId(long matrixUniqueId);
    ListingCommercial findByMlsNumber(String mlsNumber);

}