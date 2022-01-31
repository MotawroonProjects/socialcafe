package com.socialcafe.models;

import java.io.Serializable;
import java.util.List;

public class TableDataModel extends StatusResponse implements Serializable {
    private List<TableModel> data;

    public List<TableModel> getData() {
        return data;
    }
}
