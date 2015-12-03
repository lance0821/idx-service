package idxsync.idx.service;

import idxsync.domain.SearchTermResidential;
import idxsync.domain.SearchTermSchool;
import org.springframework.data.domain.Page;

public interface SearchTermService {

    public Page<SearchTermResidential> findResidentialSearchTerms(String query, PageConfig pageConfig);

    public Page<SearchTermSchool> findSchoolSearchTerms(String query, PageConfig pageConfig);
}
