package com.test.country.client.api;

import java.io.Serializable;
import java.util.Objects;

public class Flag implements Serializable {
    private static final long serialVersionUID = 2019390137621531554L;

    public Flag() {
        //Jackson need an empty constructor
    }

    private String png;

    public String getPng() {
        return png;
    }

    public void setPng(String png) {
        this.png = png;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Flag)) {
            return false;
        }
        Flag flag = (Flag) o;
        return Objects.equals(png, flag.png);
    }

    @Override
    public int hashCode() {
        return Objects.hash(png);
    }
}
