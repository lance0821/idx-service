package idxsync.idx.service;

import idxsync.domain.SearchTermResidential;
import idxsync.domain.SearchTermSchool;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.stereotype.Service;

import static org.elasticsearch.index.query.MatchQueryBuilder.Type.PHRASE_PREFIX;

@Service
public class SearchTermServiceImpl implements SearchTermService {

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Page<SearchTermResidential> findResidentialSearchTerms(String query, PageConfig pageConfig) {
        SearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(new MatchQueryBuilder("searchTerm", query)
                                .type(PHRASE_PREFIX)
                                .maxExpansions(100))
                        .withIndices("idx")
                        .withTypes("search_term_residential")

                        .withPageable(new PageRequest(pageConfig.getPage(), pageConfig.getSize()))
                        .build();

        return elasticsearchTemplate.queryForPage(searchQuery, SearchTermResidential.class);
    }

    @Override
    public Page<SearchTermSchool> findSchoolSearchTerms(String query, PageConfig pageConfig) {
        SearchQuery searchQuery =
                new NativeSearchQueryBuilder()
                        .withQuery(new MatchQueryBuilder("searchTerm", query)
                                .type(PHRASE_PREFIX)
                                .maxExpansions(100))
                        .withIndices("idx")
                        .withTypes("search_term_school")
                        .withPageable(new PageRequest(pageConfig.getPage(), pageConfig.getSize()))
                        .build();

        return elasticsearchTemplate.queryForPage(searchQuery, SearchTermSchool.class);
    }
}
