package idxsync.persistence.repository;

import idxsync.domain.SyncFullStat;
import idxsync.domain.SyncStatus;
import org.springframework.data.elasticsearch.repository.ElasticsearchCrudRepository;

public interface SyncFullStatRepository extends ElasticsearchCrudRepository<SyncFullStat, String> {

    Iterable<SyncFullStat> findAllByStatusOrderByUpdatedDesc(SyncStatus status);
    SyncFullStat findBySyncToken(String syncToken);
}
