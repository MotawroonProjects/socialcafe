package com.socialcafe.models;

import java.io.Serializable;
import java.util.List;

public class OrderDataModel extends StatusResponse implements Serializable {


        private List<OrderModel> new_;
        private List<OrderModel> old;

    public List<OrderModel> getNew_() {
        return new_;
    }

    public List<OrderModel> getOld() {
        return old;
    }
}

