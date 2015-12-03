package idxsync.mapping;


import idxsync.AppUtils;
import idxsync.domain.ListingLand;
import org.realtors.rets.client.SearchResult;

import java.util.List;
import java.util.Set;

public class ListingLandMapper extends AbstractMapper implements Mapper {

    public ListingLandMapper(String mappingFile) {
        super(mappingFile, ListingLand.class.getName());
    }

    @Override
    public Set<ListingLand> mapData(List<SearchResult> sourceList) {

        return AppUtils.castCollectionSet(mapDomainData(sourceList), ListingLand.class);
    }

    @Override
    public Set<ListingLand> mapData(SearchResult source) {

        return AppUtils.castCollectionSet(mapDomainData(source), ListingLand.class);
    }
}
