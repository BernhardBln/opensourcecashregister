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
		SUBSTRING(billopened, 12, 2) AS hour,
		COUNT(*) 
	FROM
		BILLS_WITH_PRODUCTS
	GROUP BY 
		hour
	ORDER BY
		hour;

CREATE VIEW STAT_BILLITEMS_BY_NAME_AND_HOUR AS
	SELECT
		NAME,
		SUBSTRING(billopened, 12, 2) AS hour, 
		COUNT(*)
	FROM
		BILLS_WITH_PRODUCTS
	GROUP BY 
		name, 
		hour
	ORDER BY
		name, hour;

CREATE VIEW STAT_BILLITEMS_BY_HOUR_AND_NAME AS
	SELECT
		SUBSTRING(billopened, 12, 2) AS hour, 
		NAME,
		COUNT(*)
	FROM
		BILLS_WITH_PRODUCTS
	GROUP BY 
		hour,
		name 
	ORDER BY
		hour, 
		name;

CREATE VIEW BILLS_SUMMED_UP_BY_DAY_THIS_MONTH AS
        SELECT
                CAST(billclosed AS DATE) AS date,
                SUM(total) AS total,
                pricecurrency

        FROM
                BILLS_SUMMED_UP

        WHERE
		billopened >= formatdatetime(current_timestamp, 'YYYY-MM-01')
        GROUP BY
                pricecurrency, date;

CREATE VIEW BILLS_SUMMED_UP_BY_DAY_LAST_MONTH AS
        SELECT
                CAST(billclosed AS DATE) AS date,
                SUM(total) AS total,
                pricecurrency

        FROM
                BILLS_SUMMED_UP

        WHERE
		billopened >= formatdatetime(dateadd('Month', -1, current_timestamp), 'YYYY-MM-01')
		AND billopened < formatdatetime(current_timestamp, 'YYYY-MM-01')

        GROUP BY
                pricecurrency, date;


CREATE VIEW STAT_BILLITEMS_PER_HOUR_THIS_MONTH AS
        SELECT
                SUBSTRING(billopened, 12, 2) AS hour,
                COUNT(*)
        FROM
                BILLS_WITH_PRODUCTS
	WHERE
		billopened >= formatdatetime(current_timestamp, 'YYYY-MM-01')
        GROUP BY
                hour
        ORDER BY
                hour;

CREATE VIEW STAT_BILLITEMS_PER_HOUR_LAST_MONTH AS
        SELECT
                SUBSTRING(billopened, 12, 2) AS hour,
                COUNT(*)
        FROM
                BILLS_WITH_PRODUCTS
        WHERE
                billopened >= formatdatetime(dateadd('Month', -1, current_timestamp), 'YYYY-MM-01')
                AND billopened < formatdatetime(current_timestamp, 'YYYY-MM-01')
        GROUP BY
                hour
        ORDER BY
                hour;


CREATE VIEW BILLS_TOTAL_THIS_MONTH AS
        SELECT
                formatdatetime(current_timestamp, 'YYYY-MM'),
                SUM(total) AS total,
                pricecurrency

        FROM
                BILLS_SUMMED_UP

        WHERE
                billopened >= formatdatetime(current_timestamp, 'YYYY-MM-01')
        GROUP BY
                pricecurrency;

CREATE VIEW BILLS_TOTAL_LAST_MONTH AS
        SELECT
                formatdatetime(current_timestamp, 'YYYY-MM'),
                SUM(total) AS total,
                pricecurrency

        FROM
                BILLS_SUMMED_UP
        WHERE
                billopened >= formatdatetime(dateadd('Month', -1, current_timestamp), 'YYYY-MM-01')
                AND billopened < formatdatetime(current_timestamp, 'YYYY-MM-01')

        GROUP BY
                pricecurrency;



