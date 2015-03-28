drop view bills_with_products cascade;
drop view avg_totals_stats;
drop view avg_totals_last_month;
drop view avg_totals_this_month;

-- convert data
create table bill_item_extra_and_variation_offers
    (bill_item_id bigint not null, extra_and_variation_offers_id bigint not null);

alter table bill_item_extra_and_variation_offers add constraint FK_33cm8hm6c0pj9s0njgdkhfkbl foreign key (extra_and_variation_offers_id) references offers;
alter table bill_item_extra_and_variation_offers add constraint FK_bqu88egv9k103h1imaife39gk foreign key (bill_item_id) references billitem;

insert into bill_item_extra_and_variation_offers 
    select id, variationoffer_id  from billitem where variationoffer_id  is not null;
insert into bill_item_extra_and_variation_offers 
    select billitem_id, EXTRAANDVARIATIONOFFERS_ID   from billitem_offers ;

alter table billitem drop column variationoffer_id;
drop table billitem_offers;

-- rename


alter table bill 
     alter column  billclosed rename to BILL_CLOSED;
alter table bill 
     alter column  BILLOPENED rename to BILL_OPENED;
alter table bill 
     alter column  FREEPROMOTIONOFFER rename to FREE_PROMOTION_OFFER;
alter table bill 
     alter column  GLOBALTAXINFO_ID rename to GLOBAL_TAX_INFO_ID;
alter table bill 
     alter column  INTERNALCONSUMER_ID rename to INTERNAL_CONSUMER_ID;

alter table bill_billitem rename to bill_bill_items;
alter table bill_bill_items 
     alter column BILLITEMS_ID rename to BILL_ITEMS_ID; 

alter table billitem rename to bill_item;

alter table containersize rename to container_size;
alter table container_size 
     alter column VALIDFROM rename to VALID_FROM; 
alter table container_size 
     alter column VALIDTO rename to VALID_TO; 

alter table OFFERS 
     alter column VALIDFROM rename to VALID_FROM; 
alter table OFFERS 
     alter column VALIDTO rename to VALID_TO; 
alter table OFFERS 
     alter column COSTSNETVALUE 
           rename to COSTS_NET_VALUE; 
alter table OFFERS 
     alter column COSTSNETCURRENCY 
           rename to COSTS_NET_CURRENCY; 
alter table OFFERS 
     alter column PRICEVALUE 
           rename to PRICE_VALUE; 
alter table OFFERS 
     alter column PRICECURRENCY 
           rename to PRICE_CURRENCY; 
alter table OFFERS 
     alter column OFFEREDITEM_ID 
           rename to OFFERED_ITEM_ID; 

alter table PRODUCTCATEGORY  rename to PRODUCT_CATEGORY;
alter table PRODUCT_CATEGORY 
     alter column VALIDFROM rename to VALID_FROM; 
alter table PRODUCT_CATEGORY 
     alter column VALIDTO rename to VALID_TO; 

alter table SALESITEMS   rename to SALES_ITEMS;
alter table SALES_ITEMS 
     alter column VALIDFROM rename to VALID_FROM; 
alter table SALES_ITEMS 
     alter column VALIDTO rename to VALID_TO; 
alter table SALES_ITEMS 
     alter column PRODUCTCATEGORY_ID  rename to PRODUCT_CATEGORY_ID; 
alter table SALES_ITEMS 
     alter column OVERRIDINGTAXINFO_ID   rename to OVERRIDING_TAX_INFO_ID; 
alter table SALES_ITEMS 
     alter column CONTAINERSIZE_ID  rename to CONTAINER_SIZE_ID; 

alter table TAXINFO    rename to TAX_INFO;
alter table TAX_INFO 
     alter column VALIDFROM rename to VALID_FROM; 
alter table TAX_INFO 
     alter column VALIDTO rename to VALID_TO; 
alter table TAX_INFO 
     alter column TAXUSAGE   rename to TAX_USAGE; 
alter table TAX_INFO 
     alter column VATCLASS_ID    rename to VAT_CLASS_ID;  

alter table USER  
     alter column VALIDFROM rename to VALID_FROM; 
alter table USER 
     alter column VALIDTO rename to VALID_TO; 

 
alter table vatclass   
     alter column VALIDFROM rename to VALID_FROM; 
alter table vatclass 
     alter column VALIDTO rename to VALID_TO; 
alter table vatclass 
     alter column TAXRATE  rename to tax_rate; 

