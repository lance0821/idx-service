package idxsync.idx.service;


import idxsync.domain.ListingResidential;
import idxsync.persistence.repository.ListingResidentialRepository;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.sort.FieldSortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import static org.elasticsearch.index.query.MultiMatchQueryBuilder.Type.CROSS_FIELDS;

@Service
public class ListingResidentialServiceImpl implements ListingResidentialService {

    private static final Logger logger = LoggerFactory.getLogger(ListingResidentialServiceImpl.class);

    @Resource
    private ListingResidentialRepository listingResidentialRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private LookupValuesCache lookupValuesCache;

    @Override
    public ListingResidential getListing(String mlsNumber) {

        logger.debug("getListing mlsNumber: " + mlsNumber);

        ListingResidential listing = listingResidentialRepository.findByMlsNumber(mlsNumber);

        if (listing == null)
            throw new ResourceNotFoundException("No resource with mls number " + mlsNumber);

        return listing;
    }

    @Override
    public ListingResidential getListing(Long matrixUniqueId) {

        logger.debug("getListing matrixUniqueId: " + matrixUniqueId);

        ListingResidential listing = listingResidentialRepository.findByMatrixUniqueId(matrixUniqueId);

        if (listing == null)
            throw new ResourceNotFoundException("No resource with matrix unique id " + matrixUniqueId);

        return listing;
    }

    @Override
    public Page<ListingResidential> getListings(ListingsRequest listingsRequest) {

        logger.debug("getListings: " + listingsRequest.toString());

        BoolQueryBuilder boolQueryBuilder = null;

        if (!listingsRequest.getIncludedFields().isEmpty()) {

            boolQueryBuilder = new BoolQueryBuilder();

            boolQueryBuilder.must(createDisMaxQueryBuilder(listingsRequest.getIncludedFields()));
        }

        if (!listingsRequest.getExcludedFields().isEmpty()) {

            if (boolQueryBuilder == null)
                boolQueryBuilder = new BoolQueryBuilder();

            boolQueryBuilder.mustNot(createDisMaxQueryBuilder(listingsRequest.getExcludedFields()));
        }

        if (!listingsRequest.getRanges().isEmpty()) {

            if (boolQueryBuilder == null)
                boolQueryBuilder = new BoolQueryBuilder();

            List<RangeQuery> ranges = listingsRequest.getRanges();

            for(RangeQuery rangeQuery : ranges) {
                boolQueryBuilder.must(
                        new RangeQueryBuilder(rangeQuery.getField())
                                .gte(rangeQuery.getFrom())
                                .lte(rangeQuery.getTo()));
            }
        }

        if (listingsRequest.getLocationQuery() != null) {

            if (boolQueryBuilder == null)
                boolQueryBuilder = new BoolQueryBuilder();

            MultiMatchQueryBuilder multiMatchQueryBuilder = new MultiMatchQueryBuilder(listingsRequest.getLocationQuery(),
                    "streetNumber",
                    "streetName",
                    "city",
                    "buildingName",
                    "mlsNumber",
                    "stateOrProvince",
                    "postalCode",
                    "neighbourhood",
                    "associationCommunityName")
                    .type(CROSS_FIELDS)
                    .tieBreaker(0.3f)
                    .minimumShouldMatch("30%");

            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        PageConfig pageConfig = listingsRequest.getPageConfig();

        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder();

        SortConfig sortConfig = listingsRequest.getSortConfig();

        searchQueryBuilder
                .withIndices("idx")
                .withTypes("residential")
                .withSort(new FieldSortBuilder(sortConfig.getField()).order(sortConfig.getOrder()))
                .withPageable(new PageRequest(pageConfig.getPage(), pageConfig.getSize()))
                .build();

        if (boolQueryBuilder != null) {
            searchQueryBuilder.withQuery(boolQueryBuilder);
        }

        SearchQuery searchQuery = searchQueryBuilder.build();

        return elasticsearchTemplate.queryForPage(searchQuery, ListingResidential.class);
    }

    private DisMaxQueryBuilder createDisMaxQueryBuilder(List<FieldQuery> fieldQueryList) {
        DisMaxQueryBuilder disMaxQueryBuilder = new DisMaxQueryBuilder();

        fieldQueryList.forEach(fieldQuery -> {
            List<MatchQueryBuilder> matchQueryBuilders =
                    createMatchQueryBuilders(fieldQuery.getField(), fieldQuery.getValues());
            matchQueryBuilders.forEach(disMaxQueryBuilder::add);
        });

        return disMaxQueryBuilder;
    }

    private List<MatchQueryBuilder> createMatchQueryBuilders(String fieldName, List<String> fieldVals) {

        List<MatchQueryBuilder> matchQueryBuilders = new ArrayList<>();

        for(String fieldVal : fieldVals) {
            MatchQueryBuilder matchQueryBuilder = new MatchQueryBuilder(fieldName, fieldVal);
            matchQueryBuilders.add(matchQueryBuilder);
        }

        return matchQueryBuilders;
    }

    public void transformLookupValues(List<ListingResidential> listings) {
        listings.forEach(this::transformLookupValues);
    }

    public void transformLookupValues(ListingResidential listingResidential) {

        List<Field> fields = Arrays.asList(ListingResidential.class.getDeclaredFields());

        fields.forEach(field -> {

            field.setAccessible(true);

            if (field.getType() != String.class ||
                    !lookupValuesCache.containsField(field.getName())) return;

            String lookupValueStr = null;
            try {
                lookupValueStr = (String) field.get(listingResidential);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

            if (lookupValueStr == null)
                return;

            List<String> longValues = new LinkedList<>();

            //multiple lookup values
            if (lookupValueStr.contains(",")) {
                List<String> lookupStrs = Arrays.asList(lookupValueStr.split(","));

                for(String lookup : lookupStrs) {
                    longValues.add(lookupValuesCache.getLookupValue(field.getName(), lookup).getValueLongName());
                }
            }
            else {
                longValues.add(lookupValuesCache.getLookupValue(field.getName(), lookupValueStr).getValueLongName());
            }

            StringBuilder longValue = new StringBuilder();
            if (longValues.size() == 1) {
                longValue.append(longValues.get(0));
            }
            else {
                for(int i = 0; i < longValues.size(); i++) {
                    String val = longValues.get(i);
                    longValue.append(val);

                    if (i < longValues.size()-1) {
                        longValue.append(", ");
                    }
                }
            }

            try {
                field.set(listingResidential, longValue.toString());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
