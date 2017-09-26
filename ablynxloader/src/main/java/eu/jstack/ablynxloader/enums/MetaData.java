package eu.jstack.ablynxloader.enums;

import java.util.Date;

public enum  MetaData {
    SIZE("size"),
    TITLE("title"),
    TAGS("tags"),
    COMMENTS("comments"),
    TEMPLATE("template"),
    STATUS("status"),
    CATEGORIES("categories"),
    SUBJECT("subject"),
    HYPERLINKBASE("hyperlinkbase"),
    COMPANY("company"),
    LASTMODIFIED("lastmodified"),
    CREATED("created"),
    LASTPRINTED("lastprinted"),
    MANAGER("manager"),
    AUTHOR("author"),
    LASTMODIFIEDBY("lastmodifiedby");

    private String name;

    MetaData(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
