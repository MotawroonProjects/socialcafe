package com.socialcafe.models;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.List;

public class ProductModel implements Serializable {

        private int id;
        private String name;
        private double price;
        private int brand_id;
        private int featured;


        private int count;
        
        private String image;
        private int can_make;
        private String code;
        

         private Unit unit;

        private int category_id;
        

         private FirstStock first_stock;
        

         private Tax tax;
private CategoryModel category;
       private List<OfferProducts> offer_products;

        public void setCount(int count) {
            this.count = count;
        }

        public void setId(int id) {
            this.id = id;
        }


        public void setName(String name) {
            this.name = name;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public void setUnit(Unit unit) {
            this.unit = unit;
        }

        public void setCategory_id(int category_id) {
            this.category_id = category_id;
        }

        public void setFirst_stock(FirstStock first_stock) {
            this.first_stock = first_stock;
        }

        public void setTax(Tax tax) {
            this.tax = tax;
        }

        public void setOffer_products(List<OfferProducts> offer_products) {
            this.offer_products = offer_products;
        }

        public int getBrand_id() {
            return brand_id;
        }

        public void setBrand_id(int brand_id) {
            this.brand_id = brand_id;
        }

        public int getFeatured() {
            return featured;
        }

        public void setFeatured(int featured) {
            this.featured = featured;
        }

        public int getCan_make() {
            return can_make;
        }

        public void setCan_make(int can_make) {
            this.can_make = can_make;
        }



        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }

        public int getCount() {
            return count;
        }

        public String getImage() {
            return image;
        }

        public String getCode() {
            return code;
        }

        public FirstStock getFirst_stock() {
            return first_stock;
        }

        public Tax getTax() {
            return tax;
        }

        public Unit getUnit() {
            return unit;
        }

        public int getCategory_id() {
            return category_id;
        }

        public List<OfferProducts> getOffer_products() {
            return offer_products;
        }

    public CategoryModel getCategory() {
        return category;
    }

    public static class FirstStock implements Serializable {
            private int localid;
            private int id;
            private double qty;
            private int product_id;

            public int getId() {
                return id;
            }

            public double getQty() {
                return qty;
            }

            public int getProduct_id() {
                return product_id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setQty(double qty) {
                this.qty = qty;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public int getLocalid() {
                return localid;
            }

            public void setLocalid(int localid) {
                this.localid = localid;
            }
        }

        public static class Tax implements Serializable {
            private int localid;
            private int id;

            private String name;
            private int rate;
            private int product_id;

            public void setId(int id) {
                this.id = id;
            }

            public void setName(String name) {
                this.name = name;
            }

            public void setRate(int rate) {
                this.rate = rate;
            }

            public int getId() {
                return id;
            }

            public String getName() {
                return name;
            }

            public int getRate() {
                return rate;
            }

            public int getProduct_id() {
                return product_id;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public int getLocalid() {
                return localid;
            }

            public void setLocalid(int localid) {
                this.localid = localid;
            }
        }

     
        public static class Unit implements Serializable {
            private int localid;
            private int id;
            private String unit_name;
            private int product_id;

            public int getId() {
                return id;
            }

            public String getUnit_name() {
                return unit_name;
            }

            public int getProduct_id() {
                return product_id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public void setUnit_name(String unit_name) {
                this.unit_name = unit_name;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public int getLocalid() {
                return localid;
            }

            public void setLocalid(int localid) {
                this.localid = localid;
            }
        }

  
        public class OfferProducts implements Serializable {
            private int localid;
            private int product_id;
            private int offer_id;
            private int id;

            public int getId() {
                return id;
            }

            public void setId(int id) {
                this.id = id;
            }

            public int getProduct_id() {
                return product_id;
            }

            public void setProduct_id(int product_id) {
                this.product_id = product_id;
            }

            public int getLocalid() {
                return localid;
            }

            public void setLocalid(int localid) {
                this.localid = localid;
            }

            public int getOffer_id() {
                return offer_id;
            }

            public void setOffer_id(int offer_id) {
                this.offer_id = offer_id;
            }
        }

}


