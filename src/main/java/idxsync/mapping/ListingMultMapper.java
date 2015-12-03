package idxsync.mapping;


import idxsync.AppUtils;
import idxsync.domain.ListingMult;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public class ListingMultMapper extends AbstractMapper implements Mapper {

    public ListingMultMapper(String mappingFile) {
        super(mappingFile, ListingMult.class.getName());
    }

    @Override
    public Set<ListingMult> mapData(List<SearchResult> sourceList) {

        return AppUtils.castCollectionSet(mapDomainData(sourceList), ListingMult.class);
    }

    @Override
    public Set<ListingMult> mapData(SearchResult source) {

        return AppUtils.castCollectionSet(mapDomainData(source), ListingMult.class);
    }
}
