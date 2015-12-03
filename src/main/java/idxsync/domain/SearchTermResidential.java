package idxsync.domain;

import org.springframework.data.elasticsearch.annotations.Document;

@Document(indexName = "idx", type = "search_term_residential")
public class SearchTermResidential extends SearchTerm {

}
