package com.socialcafe.models;

import java.io.Serializable;
import java.util.List;

public class OrderModel implements Serializable {
    private String id;
    private String reference_no;
    private String user_id;
    private String cash_register_id;
    private String customer_id;
    private String table_id;
    private String membership_number;
    private String warehouse_id;
    private String biller_id;
    private String item;
    private String total_qty;
    private String total_discount;
    private String total_tax;
    private String total_price;
    private String grand_total;
    private String order_tax_rate;
    private String order_tax;
    private String order_discount;
    private String discount_eng;
    private String coupon_id;
    private String coupon_discount;
    private String shipping_cost;
    private String sale_status;
    private String payment_status;
    private String document;
    private String paid_amount;
    private String sale_note;
    private String staff_note;
    private String stored_at;
    private String created_at;
    private String updated_at;
    private List<Detials> details;

    public String getId() {
        return id;
    }

    public String getTable_id() {
        return table_id;
    }

    public String getMembership_number() {
        return membership_number;
    }

    public String getDiscount_eng() {
        return discount_eng;
    }

    public String getStored_at() {
        return stored_at;
    }

    public String getReference_no() {
        return reference_no;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getCash_register_id() {
        return cash_register_id;
    }

    public String getCustomer_id() {
        return customer_id;
    }

    public String getWarehouse_id() {
        return warehouse_id;
    }

    public String getBiller_id() {
        return biller_id;
    }

    public String getItem() {
        return item;
    }

    public String getTotal_qty() {
        return total_qty;
    }

    public String getTotal_discount() {
        return total_discount;
    }

    public String getTotal_tax() {
        return total_tax;
    }

    public String getTotal_price() {
        return total_price;
    }

    public String getGrand_total() {
        return grand_total;
    }

    public String getOrder_tax_rate() {
        return order_tax_rate;
    }

    public String getOrder_tax() {
        return order_tax;
    }

    public String getOrder_discount() {
        return order_discount;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public String getCoupon_discount() {
        return coupon_discount;
    }

    public String getShipping_cost() {
        return shipping_cost;
    }

    public String getSale_status() {
        return sale_status;
    }

    public String getPayment_status() {
        return payment_status;
    }

    public String getDocument() {
        return document;
    }

    public String getPaid_amount() {
        return paid_amount;
    }

    public String getSale_note() {
        return sale_note;
    }

    public String getStaff_note() {
        return staff_note;
    }


    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public List<Detials> getDetails() {
        return details;
    }

    public class Detials implements Serializable {
        private String id;
        private String sale_id;
        private String product_id;
        private String product_batch_id;
        private String variant_id;
        private String qty;
        private String sale_unit_id;
        private String net_unit_price;
        private String discount;
        private String tax_rate;
        private String tax;
        private String total;
        private String created_at;
        private String updated_at;
        private String is_end;

        private ProductModel product;

        public String getId() {
            return id;
        }

        public String getSale_id() {
            return sale_id;
        }

        public String getProduct_id() {
            return product_id;
        }

        public String getProduct_batch_id() {
            return product_batch_id;
        }

        public String getVariant_id() {
            return variant_id;
        }

        public String getQty() {
            return qty;
        }

        public String getSale_unit_id() {
            return sale_unit_id;
        }

        public String getNet_unit_price() {
            return net_unit_price;
        }

        public String getDiscount() {
            return discount;
        }

        public String getTax_rate() {
            return tax_rate;
        }

        public String getTax() {
            return tax;
        }

        public String getTotal() {
            return total;
        }

        public String getCreated_at() {
            return created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public String getIs_end() {
            return is_end;
        }

        public ProductModel getProduct() {
            return product;
        }
    }
}
