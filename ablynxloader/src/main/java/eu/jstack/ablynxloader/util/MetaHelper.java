package eu.jstack.ablynxloader.util;

import eu.jstack.ablynxloader.enums.MetaData;
import org.apache.poi.POIXMLProperties;

import java.util.Date;
import java.util.LinkedHashMap;

public class MetaHelper {
    public static LinkedHashMap<String, Object> getMetaData(POIXMLProperties.CoreProperties coreProperties, POIXMLProperties.ExtendedProperties extendedProperties) {
        LinkedHashMap<String, Object> metaData = new LinkedHashMap<>();
        //metaData.put(MetaData.SIZE.toString(), coreProperties.getUnderlyingProperties().getSize());
        metaData.put(MetaData.TITLE.toString(), coreProperties.getTitle());
        metaData.put(MetaData.TAGS.toString(), coreProperties.getKeywords());
        metaData.put(MetaData.COMMENTS.toString(), coreProperties.getDescription());
        metaData.put(MetaData.TEMPLATE.toString(), extendedProperties.getTemplate());
        metaData.put(MetaData.STATUS.toString(), coreProperties.getContentStatus());
        metaData.put(MetaData.CATEGORIES.toString(), coreProperties.getCategory());
        metaData.put(MetaData.SUBJECT.toString(), coreProperties.getSubject());
        metaData.put(MetaData.HYPERLINKBASE.toString(), extendedProperties.getHyperlinkBase());
        metaData.put(MetaData.COMPANY.toString(), extendedProperties.getCompany());
        metaData.put(MetaData.LASTMODIFIED.toString(), coreProperties.getModified());
        metaData.put(MetaData.CREATED.toString(), coreProperties.getCreated());
        metaData.put(MetaData.LASTPRINTED.toString(), coreProperties.getLastPrinted());
        metaData.put(MetaData.MANAGER.toString(), extendedProperties.getManager());
        metaData.put(MetaData.AUTHOR.toString(), coreProperties.getCreator());
        metaData.put(MetaData.LASTMODIFIEDBY.toString(),  coreProperties.getLastModifiedByUser());

        return metaData;
    }
}
