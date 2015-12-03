package idxsync.mapping;


import com.fasterxml.jackson.databind.ObjectMapper;
import idxsync.domain.IdxDomain;
import org.apache.commons.lang.StringUtils;
import org.realtors.rets.client.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.*;

import static idxsync.AppUtils.getDeclaredField;
import static idxsync.AppUtils.toDate;

public abstract class AbstractMapper {

    private static Logger logger = LoggerFactory.getLogger(AbstractMapper.class);

    private String domainClassName;
    private Map<String, MappingItem> mappingHash;

    public AbstractMapper(String mappingFile, String domainClassName) {
        InputStream mappingFileIS = AbstractMapper.class.getResourceAsStream(mappingFile);
        mappingHash = toMappingHashSrcFields(mappingFileIS);
        this.domainClassName = domainClassName;
    }

    protected List<IdxDomain> mapDomainData(List<SearchResult> sourceList) {

        List<IdxDomain> domainList = new ArrayList<>();

        sourceList.forEach(searchResult -> {
            domainList.addAll(
                    mapDomainData(searchResult));
        });

        return domainList;
    }

    protected List<IdxDomain> mapDomainData(SearchResult source) {

        List<IdxDomain> domainList = new ArrayList<>();

        //return empty list if source is empty
        if (source.getCount() < 1) return domainList;

        List<String> columns = Arrays.asList(source.getColumns());
        Iterator itr = source.iterator();
        //each source listing
        while(itr.hasNext()) {

            List<String> result = Arrays.asList((String[])itr.next());

            Object domain;

            try {
                Class<?> cls = Class.forName(domainClassName);
                domain = cls.newInstance();
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                throw new MapperException(e);
            }

            //loop through and map each source listing field into ListingResidential instance
            for(int i = 0; i < result.size(); i++) {
                String srcFieldValue = result.get(i);

                if (StringUtils.isBlank(srcFieldValue)) continue;

                String fieldName = columns.get(i);
                MappingItem mappingItem = mappingHash.get(fieldName);

                //if no mapping for field name is found
                if (mappingItem == null) {
                    //log it and skip it
                    logger.info("No mapping item found for field name '{}', Domain: {}", fieldName, domainClassName);
                    continue;
                }

                // Use reflection to find standard field from class
                // and set field it to the incoming field value.
                setStandardField(domain, mappingItem, srcFieldValue);
            }

            domainList.add((IdxDomain) domain);
        }

        return domainList;
    }

    protected Map<String, MappingItem> toMappingHashSrcFields(InputStream mappingData) {
        ObjectMapper objectMapper = new ObjectMapper();

        Mapping mapping;

        try {
            mapping = objectMapper.readValue(mappingData, Mapping.class);
        } catch (IOException e) {
            throw new MapperException(e);
        }

        return mapping2HashSrcField(mapping);
    }

    /**
     * Use reflection to find standard field from class and set field it to the incoming field value.
     * @param domain
     * @param mappingItem
     * @param srcFieldValue
     */
    protected void setStandardField(Object domain,
                                  MappingItem mappingItem, String srcFieldValue) {
        try {

            Field clsField = getDeclaredField(mappingItem.getStandardFieldName(), domain.getClass());
            clsField.setAccessible(true);

            switch(mappingItem.getStandardDataType()) {
                case "boolean":
                    clsField.set(domain, Boolean.parseBoolean(srcFieldValue));
                    break;
                case "short":
                    clsField.set(domain, Short.parseShort(srcFieldValue));
                    break;
                case "int":
                    clsField.set(domain, Integer.parseInt(srcFieldValue));
                    break;
                case "long":
                    clsField.set(domain, Long.parseLong(srcFieldValue));
                    break;
                case "double":
                    clsField.set(domain, Double.parseDouble(srcFieldValue));
                    break;
                case "Date":
                    clsField.set(domain, toDate(LocalDateTime.parse(srcFieldValue)));
                    break;
                case "String":
                default:
                    clsField.set(domain, srcFieldValue);
                    break;
            }


        } catch (IllegalAccessException | NullPointerException e) {
            throw new MapperException(e);
        }
    }

    private static Map<String, MappingItem> mapping2HashSrcField(Mapping mapping) {
        Map<String, MappingItem> mappingHash = new LinkedHashMap<>();

        mapping.getMapping().forEach(mappingItem -> {
            mappingHash.put(mappingItem.getSrcFieldName(), mappingItem);
        });

        return mappingHash;
    }

    private static Map<String, MappingItem> mapping2HashStandardField(Mapping mapping) {
        Map<String, MappingItem> mappingHash = new LinkedHashMap<>();

        mapping.getMapping().forEach(mappingItem -> {
            mappingHash.put(mappingItem.getStandardFieldName(), mappingItem);
        });

        return mappingHash;
    }
}
