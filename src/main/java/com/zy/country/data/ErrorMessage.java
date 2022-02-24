package com.zy.country.data;

import java.io.Serializable;
import java.util.Objects;

/**
 * DTO class contains status and error message that client knows failing reason
 */
public class ErrorMessage implements Serializable {
    private static final long serialVersionUID = -1151625748340225278L;
    private Integer status;
    private String message;

    public ErrorMessage() {
        //Jackson need an empty constructor
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ErrorMessage)) {
            return false;
        }
        ErrorMessage that = (ErrorMessage) o;
        return Objects.equals(status, that.status) && Objects.equals(message, that.message);
    }

    @Override
    public int hashCode() {
        return Objects.hash(status, message);
    }
}
