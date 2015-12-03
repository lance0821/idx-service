package idxsync.mapping;

import idxsync.domain.IdxDomain;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public interface Mapper {

    Set<? extends IdxDomain> mapData(List<SearchResult> sourceList);
    Set<? extends IdxDomain> mapData(SearchResult source);
}
