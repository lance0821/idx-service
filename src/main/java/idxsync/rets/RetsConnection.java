package idxsync.rets;

import idxsync.rets.metadata.Classification;
import idxsync.rets.metadata.Metadata;
import idxsync.rets.metadata.Resource;
import idxsync.rets.metadata.Table;
import org.realtors.rets.client.*;
import org.realtors.rets.common.metadata.types.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Component
public class RetsConnection {

    private final static Logger logger = LoggerFactory.getLogger(RetsConnection.class);

    private final static int SESSION_TIMEOUT_MS = 5 * 60 * 1000; //5 minutes

    @Autowired
    private RetsConfig retsConfig;

    private RetsSession session;

    private Metadata metadata;

    private LocalDateTime lastConnectTime = LocalDateTime.now().minusHours(12);

    public RetsConnection() {

    }

    public RetsConnection(RetsConfig retsConfig) {

        this.retsConfig = retsConfig;
    }

    private synchronized RetsSession getSession() {

        if (isSessionTimeout()) {
            logger.info("Session has timed out. Re-establishing RETS session.");
            establishSession();
        }

        lastConnectTime = LocalDateTime.now();

        return session;
    }

    public boolean isSessionTimeout() {
        return SESSION_TIMEOUT_MS < Duration.between(lastConnectTime, LocalDateTime.now()).toMillis();
    }

    @PostConstruct
    public synchronized LoginResponse establishSession() {

        try {

            //Create a RetsHttpClient (other constructors provide configuration i.e. timeout, gzip capability)
            CommonsHttpClient httpClient = new CommonsHttpClient();

            //force connections to close and not be in the CLOSE_WAIT state
            httpClient.addDefaultHeader("Connection", "Close");
            RetsVersion retsVersion = RetsVersion.RETS_1_7_2;

            //Create a RetesSession with RetsHttpClient
            session = new RetsSession(retsConfig.getUrl(), httpClient, retsVersion);

            session.setMethod("POST");

            logger.info("Logging into RETS server");

            LoginResponse loginResponse = session.login(retsConfig.getUsername(), retsConfig.getPassword());

            lastConnectTime = LocalDateTime.now();

            return loginResponse;

        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }
    }

    public LogoutResponse endSession() {
        try {
            LogoutResponse response = session.logout();

            return response;
        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }
    }

    public Metadata getMetadata() {

        try {
            MSystem system = getSession().getMetadata().getSystem();

            this.metadata = new Metadata();

            this.metadata.setSystemId(system.getSystemID());
            this.metadata.setSystemDescription(system.getSystemDescription());

            //resources
            Arrays.asList(system.getMResources()).forEach(mResource -> {
                Resource resource = mapResources(mResource);
                metadata.getResources().put(resource.getId(), resource);

                //classes
                Arrays.asList(mResource.getMClasses()).forEach(mClass -> {
                    Classification classification = mapClass(mClass);
                    resource.getClassifications().put(classification.getName(), classification);

                    //tables
                    Arrays.asList(mClass.getMTables()).forEach(mTable -> {
                        Table table = mapTable(mTable);
                        classification.getTableList().add(table);
                    });
                });
            });

        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }

        return metadata;
    }

    public MLookup getLookup(String resourceId, String lookupName) {

        try {
            return getSession().getMetadata().getLookup(resourceId, lookupName);
        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }
    }

    private Resource mapResources(MResource mResource) {
        Resource resource = new Resource();

        resource.setId(mResource.getResourceID());
        resource.setDescription(mResource.getDescription());

        return resource;
    }

    private Classification mapClass(MClass mClass) {
        Classification classification = new Classification();

        classification.setName(mClass.getClassName());
        classification.setDescription(mClass.getDescription());

        return classification;
    }

    private Table mapTable(MTable mTable) {
        Table table = new Table();

        table.setDataType(mTable.getDataType());
        table.setDbName(mTable.getDBName());
        table.setInterpretation(mTable.getInterpretation());
        table.setLongName(mTable.getLongName());
        table.setShortName(mTable.getShortName());
        table.setStandardName(mTable.getStandardName());
        table.setMaxLength(mTable.getMaximumLength());
        table.setUnits(mTable.getUnits());
        table.setSearchable(mTable.getSearchable());
        table.setPrecision(mTable.getPrecision());

        return table;
    }

    public SearchResult search(String resourceId, String className, String query, Map... options) {
        SearchRequest searchRequest = new SearchRequest(resourceId, className, query);

        if (options != null && options.length > 0) {
            Map optionsMap = options[0];

            if (optionsMap.containsKey("Limit")) {
                String limit = (String) optionsMap.get("Limit");
                if (limit != null) {
                    if (limit.equalsIgnoreCase("none")) {
                        searchRequest.setLimitNone();
                    } else searchRequest.setLimit(Integer.parseInt(limit));
                }

            }

            if (optionsMap.containsKey("Select")) {
                String select = (String) optionsMap.get("Select");
                if (select != null) searchRequest.setSelect(select);
            }

            if (optionsMap.containsKey("RestrictedIndicator")) {
                String restrictedIndicator = (String) optionsMap.get("RestrictedIndicator");
                if (restrictedIndicator != null) searchRequest.setSelect(restrictedIndicator);
            }

            if (optionsMap.containsKey("Offset")) {
                String offset = (String) optionsMap.get("Offset");
                if (offset != null) {
                    if (offset.equalsIgnoreCase("none")) {
                        searchRequest.setOffsetNone();
                    } else searchRequest.setOffset(Integer.parseInt(offset));
                }

            }
        }

        try {
            return getSession().search(searchRequest);
        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }
    }

    //search helpers
    public List<SearchResult> searchListingResidential(String query, Map... options) {
        return recursiveSearch(new LinkedList<>(), "Property", "RESI", query, 0, options);
    }

    public List<SearchResult> searchListingCommercial(String query, Map... options) {

        return recursiveSearch(new LinkedList<>(), "Property", "COMM", query, 0, options);
    }

    public List<SearchResult> searchListingMult(String query, Map... options) {

        return recursiveSearch(new LinkedList<>(), "Property", "MULT", query, 0, options);
    }

    public List<SearchResult> searchListingLand(String query, Map... options) {

        return recursiveSearch(new LinkedList<>(), "Property", "LAND", query, 0, options);
    }

    public List<SearchResult> searchOpenHouse(String query, Map... options) {

        return recursiveSearch(new LinkedList<>(), "OpenHouse", "OPENHOUSE", query, 0, options);
    }

    private List<SearchResult> recursiveSearch(List<SearchResult> results, String resourceId, String className,
                                               String query, int offset, Map... options) {
        Map<String, String> optionsMap;

        if (options != null && options.length > 0) {
            optionsMap = options[0];
        } else optionsMap = new LinkedHashMap<>();

        if (offset == -1) {
            optionsMap.put("Offset", "None");
        } else optionsMap.put("Offset", "" + offset);

        SearchResult result = search(resourceId, className, query, optionsMap);

        results.add(result);

        if (result.isMaxrows()) {
            recursiveSearch(results, resourceId, className, query, offset + result.getCount(), optionsMap);
        }

        return results;
    }

    public GetObjectResponse getPropertyLargePhotos(List<Long> matrixUniqueIds) {
        return getPropertyPhotos(matrixUniqueIds, "LargePhoto");
    }

    public GetObjectResponse getPropertySmallPhotos(List<Long> matrixUniqueIds) {
        return getPropertyPhotos(matrixUniqueIds, "Photo");
    }

    public GetObjectResponse getPropertyPhotos(List<Long> matrixUniqueIds, String photoType) {
        GetObjectRequest getObjectRequest = new GetObjectRequest("Property", photoType);

        for (Long mui : matrixUniqueIds) {
            getObjectRequest.addObject("" + mui, "*"); //* denotes all objects associated with the listing
        }

        try {
            return getSession().getObject(getObjectRequest);
        } catch (RetsException e) {
            throw new RetsConnectionException(e);
        }
    }
}
