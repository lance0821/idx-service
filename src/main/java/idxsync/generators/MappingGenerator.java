package idxsync.generators;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import idxsync.mapping.Mapping;
import idxsync.mapping.MappingItem;
import idxsync.rets.RetsConfig;
import idxsync.rets.RetsConnection;
import idxsync.rets.metadata.Metadata;
import idxsync.rets.metadata.Table;
import org.realtors.rets.client.SearchResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class MappingGenerator {

    public static void generateMappingFiles(RetsConfig retsConfig) throws IOException {

        RetsConnection retsConnection = new RetsConnection(retsConfig);

        retsConnection.establishSession();

        Metadata metadata = retsConnection.getMetadata();
        List<SearchResult> listingResiResults = retsConnection.searchListingResidential("(MatrixModifiedDT=2015-01-01T00:00:00.000+)");
        List<SearchResult> listingCommResults = retsConnection.searchListingCommercial("(MatrixModifiedDT=2015-01-01T00:00:00.000+)");
        List<SearchResult> listingLandResults = retsConnection.searchListingLand("(MatrixModifiedDT=2015-01-01T00:00:00.000+)");
        List<SearchResult> listingMultResults = retsConnection.searchListingMult("(MatrixModifiedDT=2015-01-01T00:00:00.000+)");
        List<SearchResult> openHouseResults = retsConnection.searchOpenHouse("(MatrixModifiedDT=2015-01-01T00:00:00.000+)");

        String listingResidentialMappingsJSON = generateMappingJson(Arrays.asList(listingResiResults.get(0).getColumns()),
                metadata.getResources().get("Property").getClassifications().get("RESI").getTableList());
        String listingCommercialMappingsJSON = generateMappingJson(Arrays.asList(listingCommResults.get(0).getColumns()),
                metadata.getResources().get("Property").getClassifications().get("RESI").getTableList());
        String listingLandMappingsJSON = generateMappingJson(Arrays.asList(listingLandResults.get(0).getColumns()),
                metadata.getResources().get("Property").getClassifications().get("RESI").getTableList());
        String listingMultMappingsJSON = generateMappingJson(Arrays.asList(listingMultResults.get(0).getColumns()),
                metadata.getResources().get("Property").getClassifications().get("RESI").getTableList());
        String openHouseMappingsJSON = generateMappingJson(Arrays.asList(openHouseResults.get(0).getColumns()),
                metadata.getResources().get("OpenHouse").getClassifications().get("OPENHOUSE").getTableList());

        Files.write(Paths.get("./src/main/resources/mappings/listing-residential.json"), listingResidentialMappingsJSON.getBytes());
        Files.write(Paths.get("./src/main/resources/mappings/listing-commercial.json"), listingCommercialMappingsJSON.getBytes());
        Files.write(Paths.get("./src/main/resources/mappings/listing-land.json"), listingLandMappingsJSON.getBytes());
        Files.write(Paths.get("./src/main/resources/mappings/listing-mult.json"), listingMultMappingsJSON.getBytes());
        Files.write(Paths.get("./src/main/resources/mappings/openhouse.json"), openHouseMappingsJSON.getBytes());
    }

    protected static String generateMappingJson(List<String> columns, List<Table> tableList) {

        Map<String, MappingObj> srcMappingObjs = new LinkedHashMap<>();
        columns.forEach(col -> {
            MappingObj mappingObj = new MappingObj();
            mappingObj.setFieldName(col);
            mappingObj.setDataType("String");
            srcMappingObjs.put(col, mappingObj);
        });

        Map<String, MappingObj> standardMappingObjs = new LinkedHashMap<>();
        tableList.forEach(table -> {
            String fieldNameInClass = DomainGenerator.toCamelCase(table.getLongName().replace(" ", ""));
            String dataType = DomainGenerator.getDataType(table.getDataType());

            MappingObj mappingObj = new MappingObj();
            mappingObj.setFieldName(fieldNameInClass);
            mappingObj.setDataType(dataType);

            standardMappingObjs.put(table.getLongName().replace(" ", ""), mappingObj);
        });

        List<MappingItem> mappingItemList = new ArrayList<>();

        srcMappingObjs.forEach((key, srcMappingObj) -> {

            MappingObj standardMappingObj = standardMappingObjs.get(srcMappingObj.getFieldName().replace("_", ""));

            String standardFieldName = null, standardDataType = null;
            if (standardMappingObj != null) {
                standardFieldName = standardMappingObj.getFieldName();
                standardDataType = standardMappingObj.getDataType();
            }

            MappingItem mappingItem = new MappingItem();
            mappingItem.setSrcFieldName(srcMappingObj.getFieldName());
            mappingItem.setSrcDataType(srcMappingObj.getDataType());
            mappingItem.setStandardFieldName(standardFieldName);
            mappingItem.setStandardDataType(standardDataType);

            mappingItemList.add(mappingItem);
        });

        Mapping mapping = new Mapping();
        mapping.setMapping(mappingItemList);

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonResult;
        try {
            jsonResult = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapping);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonResult;
    }

    private static class MappingObj {
        private String fieldName;
        private String dataType;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public String getDataType() {
            return dataType;
        }

        public void setDataType(String dataType) {
            this.dataType = dataType;
        }
    }
}
