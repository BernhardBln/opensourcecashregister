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
		CAST(billclosed AS DATE) AS date, 
		SUM(total) AS total, 
		pricecurrency  
	
	FROM 
		BILLS_SUMMED_UP 
	
	GROUP BY 
		pricecurrency, date;

		
CREATE VIEW STAT_BILLITEMS_PER_HOUR AS
	SELECT
		COUNT(*), 
		SUBSTRING(billopened, 12, 2) AS hour 
	FROM
		BILLS_WITH_PRODUCTS
	GROUP BY 
		hour
	ORDER BY
		hour;
