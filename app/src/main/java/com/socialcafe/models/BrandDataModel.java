package com.socialcafe.models;

import java.io.Serializable;
import java.util.List;

public class BrandDataModel extends StatusResponse implements Serializable {
    private List<BrandModel> data;

    public List<BrandModel> getData() {
        return data;
    }
}
