package idxsync.idx.service;

import idxsync.Application;
import idxsync.domain.ListingResidential;
import idxsync.idx.strategy.PhotoData;
import idxsync.persistence.repository.ListingResidentialRepository;
import idxsync.rets.RetsConnection;
import idxsync.sync.service.SyncServiceFacade;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.FileSystemUtils;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.util.Iterator;

import static idxsync.AppUtils.getTime;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public class IdxServiceITest {

    @Autowired
    private SyncServiceFacade syncService;

    @Autowired
    private PhotoService idxService;

    @Autowired
    private RetsConnection retsConnection;

    @Resource
    private ListingResidentialRepository listingResidentialRepository;

    @Value("${photos.storage.path}")
    private String photosStoragePath;

    @Before
    public void setup() {
        syncService.syncIdxData(System.currentTimeMillis() + "", LocalDateTime.now().minusDays(1));
    }

    @After
    public void tearDown() {

        listingResidentialRepository.deleteAll();

        FileSystemUtils.deleteRecursively(new File(photosStoragePath));

        retsConnection.endSession();
    }

    @Test
    public void testGetListingResidentialPhoto() {
        Iterable<ListingResidential> listings =
                listingResidentialRepository.findAllByMatrixModifiedDateGreaterThanEqual(getTime(LocalDateTime.now().minusDays(1)));

        Iterator<ListingResidential> iterator = listings.iterator();
        ListingResidential listing = null;
        while(iterator.hasNext()) {
            listing = iterator.next();

            if (listing.getPhotoCount() > 0) break;
        }

        PhotoData output = idxService.getListingResidentialPhoto(listing.getMatrixUniqueId(), 1, PhotoSize.LARGE);
        assertThat(output.getMimeType(), is("image/jpg"));
    }
}
