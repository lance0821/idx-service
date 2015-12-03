package idxsync.sync;


import idxsync.domain.ListingResidential;
import idxsync.sync.strategy.SyncUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.MatcherAssert.assertThat;

public class SyncUtilsTest {

    @Test
    public void testRefreshDomain() {

        ListingResidential listing2Refresh = new ListingResidential();
        ListingResidential listingFresh = new ListingResidential();

        listing2Refresh.setCity("Honolulu");
        listing2Refresh.setListPrice(295000.99);

        listingFresh.setCity("Mililani");
        listingFresh.setListPrice(405123.22);

        SyncUtils.refreshDomain(listing2Refresh, listingFresh);

        assertThat(listing2Refresh.getCity(), is(listingFresh.getCity()));
        assertThat(listing2Refresh.getListPrice(), is(listingFresh.getListPrice()));
    }

    @Test
    public void testDomainHash() {

        ListingResidential listing1 = new ListingResidential();
        ListingResidential listing2 = new ListingResidential();

        listing1.setCity("Honolulu");
        listing1.setListPrice(295000.99);

        listing2.setCity("Mililani");
        listing2.setListPrice(405123.22);

        String listingHash1 = SyncUtils.getDomainHash(listing1);
        String listingHash2 = SyncUtils.getDomainHash(listing2);

        assertThat(listingHash1, not(listingHash2));

        listing2.setCity("Honolulu");
        listing2.setListPrice(295000.99);

        listingHash2 = SyncUtils.getDomainHash(listing2);

        assertThat(listingHash1, is(listingHash2));
    }

    @Test
    public void testDomainNeedsRefresh() {
        ListingResidential listing2Refresh = new ListingResidential();
        ListingResidential listingFresh = new ListingResidential();

        listing2Refresh.setCity("Honolulu");
        listing2Refresh.setListPrice(295000.99);

        listingFresh.setCity("Mililani");
        listingFresh.setListPrice(405123.22);

        boolean needsRefresh = SyncUtils.domainNeedsRefresh(listing2Refresh, listingFresh);
        assertThat(needsRefresh, is(true));

        listing2Refresh.setCity("Mililani");
        listing2Refresh.setListPrice(405123.22);

        needsRefresh = SyncUtils.domainNeedsRefresh(listing2Refresh, listingFresh);
        assertThat(needsRefresh, is(false));
    }


}
