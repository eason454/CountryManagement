package com.zy.country.client.api;

import java.io.Serializable;
import java.util.Objects;

public class Name implements Serializable {
    private static final long serialVersionUID = -7977680913524678136L;

    public Name() {
        //Jackson need an empty constructor
    }

    private String common;

    public void setCommon(String common) {
        this.common = common;
    }

    public String getCommon() {
        return common;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Name)) {
            return false;
        }
        Name name = (Name) o;
        return Objects.equals(common, name.common);
    }

    @Override
    public int hashCode() {
        return Objects.hash(common);
    }
}