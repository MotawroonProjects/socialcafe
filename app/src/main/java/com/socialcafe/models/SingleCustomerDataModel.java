package com.socialcafe.models;

import java.io.Serializable;

public class SingleCustomerDataModel extends StatusResponse implements Serializable {
    private CustomerModel data;

    public CustomerModel getData() {
        return data;
    }
}
