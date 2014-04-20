CREATE VIEW BILLS_WITH_PRODUCTS AS 
	SELECT 
		S.TYPE, 
		NAME, 
		PRICEVALUE, 
		PRICECURRENCY, 
		BILL_ID,
		BillOpened,
		BillClosed
	FROM 
		OFFERS O, 
		BILL B, 
		BILLITEM BI,
		BILL_BILLITEM BBI,
		SALESITEMS S 
	WHERE 
		B.ID = BBI.BILL_ID AND
		BBI.BILLITEMS_ID = BI.ID AND
		BI.OFFER_ID = O.ID AND
		OFFEREDITEM_ID = S.ID;
		
CREATE VIEW BILLS_SUMMED_UP AS
	SELECT 
		BILL_ID, 
		SUM(PRICEVALUE) as 'total', 
		PRICECURRENCY,
		BillOpened,
		BillClosed
	FROM 
		BILLS_WITH_PRODUCTS
	GROUP BY 
		PRICECURRENCY, -- we need this for the unlikely case we have different currencies on the bill
		BILL_ID;
		
CREATE VIEW BILLS_SUMMED_UP_BY_DAY AS		
	SELECT 
		CURRENT_DATE as date, 
		SUM(total), 
		pricecurrency  
	
	FROM 
		BILLS_SUMMED_UP 
	
	WHERE 
		CAST(billclosed AS DATE) = CURRENT_DATE
		
	GROUP BY 
		pricecurrency;

		