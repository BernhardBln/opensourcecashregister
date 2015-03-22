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
		billopened >= FORMATDATETIME(DATEADD('Month', -1, CURRENT_TIMESTAMP), 'YYYY-MM-01') AND
		billopened < FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')
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
		FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM'),
		SUM(total) AS total,
		pricecurrency

	FROM
		BILLS_SUMMED_UP
	WHERE
		billopened >= FORMATDATETIME(DATEADD('Month', -1, CURRENT_TIMESTAMP), 'YYYY-MM-01') AND
		billopened < FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')

	GROUP BY
		pricecurrency;


CREATE VIEW PIVOTSTAT_BILLITEMS_PER_HOUR_THIS_MONTH AS
	SELECT
		REPLACE(
			REPLACE(
				REPLACE(
					REPLACE(
						REPLACE(
							REPLACE(
								REPLACE(
									FORMATDATETIME(billopened, 'E'), 'Mo', '1 Mo'), 
								'Di', '2 Di'), 
							'Mi', '3 Mi'), 
						'Do', '4 Do'), 
					'Fr', '5 Fr'), 
				'Sa', '6 Sa'), 
			'So', '7 So') AS weekday,	
		SUBSTRING(billopened, 12, 2) AS hour,
		COUNT(*)
	FROM
		BILLS_WITH_PRODUCTS
	WHERE
		billopened >= FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')
	GROUP BY
		weekday, hour
	ORDER BY
		hour;
 

CREATE VIEW PIVOTSTAT_BILLITEMS_PER_HOUR_LAST_MONTH AS
SELECT
		REPLACE(
			REPLACE(
				REPLACE(
					REPLACE(
						REPLACE(
							REPLACE(
								REPLACE(
									FORMATDATETIME(billopened, 'E'), 'Mo', '1 Mo'), 
								'Di', '2 Di'), 
							'Mi', '3 Mi'), 
						'Do', '4 Do'), 
					'Fr', '5 Fr'), 
				'Sa', '6 Sa'), 
			'So', '7 So') AS weekday,
			SUBSTRING(billopened, 12, 2) AS hour,
			COUNT(*)
	FROM
		BILLS_WITH_PRODUCTS
	WHERE
		billopened >= FORMATDATETIME(DATEADD('Month', -1, CURRENT_TIMESTAMP), 'YYYY-MM-01') AND
		billopened < FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')
	GROUP BY
		weekday, hour
	ORDER BY
		hour;

CREATE VIEW AVG_TOTALS_THIS_MONTH AS
	SELECT
		REPLACE(
			REPLACE(
				REPLACE(
					REPLACE(
						REPLACE(
							REPLACE(
								REPLACE(
									FORMATDATETIME(billopened, 'E'), 'Mo', '1 Mo'), 
								'Di', '2 Di'), 
							'Mi', '3 Mi'), 
						'Do', '4 Do'), 
					'Fr', '5 Fr'), 
				'Sa', '6 Sa'), 
			'So', '7 So') AS weekday,		
		SUM(total) AS total,
		AVG(total) as avg_total,
		MIN(total) as min_total,
		MAX(total) as max_total,
		pricecurrency
		
	FROM
		(
			select
				SUBSTR(billopened, 0, 10) billopened, 
				SUM(total) total, 
				pricecurrency
			from
				BILLS_SUMMED_UP
			WHERE
				billopened >= FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')
			GROUP BY 
				pricecurrency, billopened
		)

	GROUP BY
		pricecurrency, 
		weekday

	ORDER BY 
		weekday;

create view avg_totals_last_month as 
	SELECT
		REPLACE(
			REPLACE(
				REPLACE(
					REPLACE(
						REPLACE(
							REPLACE(
								REPLACE(
									FORMATDATETIME(billopened, 'E'), 'Mo', '1 Mo'), 
								'Di', '2 Di'), 
							'Mi', '3 Mi'), 
						'Do', '4 Do'), 
					'Fr', '5 Fr'), 
				'Sa', '6 Sa'), 
			'So', '7 So') AS weekday,	
		SUM(total) AS total,
		AVG(total) as avg_total,
		MIN(total) as min_total,
		MAX(total) as max_total,
		pricecurrency
		
	FROM
		(
			SELECT
				SUBSTR(billopened, 0, 10) billopened, 
				SUM(total) total, 
				pricecurrency
			FROM
				BILLS_SUMMED_UP
			WHERE
				billopened >= FORMATDATETIME(DATEADD('Month', -1, CURRENT_TIMESTAMP), 'YYYY-MM-01') AND
				billopened < FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')
			GROUP BY 
				pricecurrency, billopened
		)

	GROUP BY
		pricecurrency, 
		weekday

	order by 
		weekday;
		
CREATE VIEW avg_totals_stats AS
	SELECT 
		T.weekday, 
	
		T.TOTAL as Total_THIS_month,
		L.TOTAL as Total_last_month,
		
		T.avg_total as Avg_Total_THIS_month,
		L.avg_total as Avg_Total_last_month,
		
		T.min_total as MinTotal_THIS_month,
		L.min_total as MinTotal_last_month,
		
		T.max_total as MaxTotal_THIS_month,
		L.max_total as MaxTotal_last_month
	
	FROM   
		AVG_TOTALS_THIS_MONTH T,  
		avg_totals_last_month L 
	WHERE 
		T.weekday = L.weekday;

CREATE VIEW compare_month_performance_with_prev AS
	SELECT 
		SUM(total), 
		CURRENT_TIMESTAMP 
		FROM 
		BILLS_SUMMED_UP_BY_DAY_THIS_MONTH 
	UNION
	SELECT 
	SUM(total), 
		DATEADD('month', -1, CURRENT_TIMESTAMP)  
	FROM 
		BILLS_SUMMED_UP_BY_DAY_last_MONTH 
	WHERE 
		date < DATEADD('month', -1, CURRENT_TIMESTAMP);
	
create or replace view google_stat as 
	SELECT

	FORMATDATETIME(billopened, 'E'), 
	replace(  total, '.', ',')
		
	FROM
		(
			SELECT
				SUBSTR(billopened, 0, 10) billopened, 
				SUM(total) total, 
				pricecurrency
			FROM
				BILLS_SUMMED_UP
			WHERE				
billopened >= FORMATDATETIME(CURRENT_TIMESTAMP, 'YYYY-MM-01')

			GROUP BY 
				pricecurrency, billopened
order by billopened
		);
 
	
create or replace view BillItemsWithTax as
	SELECT B.id as BillId, billopened, billclosed, freeprOMOTIONOFFER offer_id, o.type, o.validFrom as offerValidFrom, o.valIDTO  as offerValidTo, pricevalue, pricECURRENCY , cosTSNETVALUE , cosTSNETCURRENCY , s.valiDFROM  as salesItemValidFrom, s.valIDTO  as salesItemValidTo, name, denotation as taxInfoDenotation, desigNATION  as vatClassDesignation, taxrate, globALTAXINFO_ID as appliedTaxInfo, round(pricevalue / ((100+taxrate) / 100),2) as priceNet, round(pricevalue * (1 - 1/(1+ taxrate / 100)), 2) as vat 
	FROM BILL B, BiLL_BILLITEM BB, BILLITEM  BI , offers o, salesitems s, taxinfo t, vatclass v
	where
	B.id=BB.bill_id and bb.billitems_id = bi.id and offer_id = o.id and offereditem_id = s.id and t.vatclass_id=v.id
	 and globaltaxinfo_id = t.id and OVERRIDINGTAXINFO_ID is null

	union

	SELECT    B.id as BillId, billopened, billclosed, freeprOMOTIONOFFER offer_id, o.type, o.validFrom as offerValidFrom, o.valIDTO  as offerValidTo, pricevalue, pricECURRENCY , cosTSNETVALUE , cosTSNETCURRENCY , s.valiDFROM  as salesItemValidFrom, s.valIDTO  as salesItemValidTo, name, denotation as taxInfoDenotation, desigNATION  as vatClassDesignation, taxrate,  overridingtaxinfo_id as appliedTaxInfo, round(pricevalue / ((100+taxrate) / 100),2) as priceNet, round( pricevalue * (1 - 1/(1+ taxrate / 100)), 2) as vat 
	FROM BILL B, BiLL_BILLITEM BB, BILLITEM  BI , offers o, salesitems s, taxinfo t, vatclass v
	where
	B.id=BB.bill_id and bb.billitems_id = bi.id and offer_id = o.id and offereditem_id = s.id and t.vatclass_id=v.id
	 and OVERRIDINGTAXINFO_ID = t.id and OVERRIDINGTAXINFO_ID is not null;
	 

create or replace view BillItemsWithTax_noInternal_noPromo as
	SELECT B.id as BillId, billopened, billclosed, freeprOMOTIONOFFER offer_id, o.type, o.validFrom as offerValidFrom, o.valIDTO  as offerValidTo, pricevalue, pricECURRENCY , cosTSNETVALUE , cosTSNETCURRENCY , s.valiDFROM  as salesItemValidFrom, s.valIDTO  as salesItemValidTo, name, denotation as taxInfoDenotation, desigNATION  as vatClassDesignation, taxrate, globALTAXINFO_ID as appliedTaxInfo, round(pricevalue / ((100+taxrate) / 100),2) as priceNet, round(pricevalue * (1 - 1/(1+ taxrate / 100)), 2) as vat 
	FROM BILL B, BiLL_BILLITEM BB, BILLITEM  BI , offers o, salesitems s, taxinfo t, vatclass v
	where
	B.id=BB.bill_id and bb.billitems_id = bi.id and offer_id = o.id and offereditem_id = s.id and t.vatclass_id=v.id
	 and globaltaxinfo_id = t.id and OVERRIDINGTAXINFO_ID is null and interNALCONSUMER_ID is null and freePROMOTIONOFFER =false

	union

	SELECT    B.id as BillId, billopened, billclosed, freeprOMOTIONOFFER offer_id, o.type, o.validFrom as offerValidFrom, o.valIDTO  as offerValidTo, pricevalue, pricECURRENCY , cosTSNETVALUE , cosTSNETCURRENCY , s.valiDFROM  as salesItemValidFrom, s.valIDTO  as salesItemValidTo, name, denotation as taxInfoDenotation, desigNATION  as vatClassDesignation, taxrate,  overridingtaxinfo_id as appliedTaxInfo, round(pricevalue / ((100+taxrate) / 100),2) as priceNet, round( pricevalue * (1 - 1/(1+ taxrate / 100)), 2) as vat 
	FROM BILL B, BiLL_BILLITEM BB, BILLITEM  BI , offers o, salesitems s, taxinfo t, vatclass v
	where
	B.id=BB.bill_id and bb.billitems_id = bi.id and offer_id = o.id and offereditem_id = s.id and t.vatclass_id=v.id
	 and OVERRIDINGTAXINFO_ID = t.id and OVERRIDINGTAXINFO_ID is not null and interNALCONSUMER_ID is null and freePROMOTIONOFFER =false;
	 

create or replace view withTaxPerDay as
SELECT 
		CAST(billopened AS DATE) as billOpened, sum(pricevalue) as gross, sum(pricenet) as net
FROM  BILLITEMSWITHTAX_NOINTERNAL_NOPROMO 
group by billOpened 
order by billopened desc;


create or replace view DrinksByHoursWithoutInternalconsumersTwoMonthsAgo
  SELECT 
replace(
                REPLACE(
                        REPLACE(
                                REPLACE(
                                        REPLACE(
                                                REPLACE(
                                                        REPLACE(
                                                                REPLACE(
                                                                        FORMATDATETIME(billopened, 'E'), 'Mo', '1 Mo-Fr'), 
                                                                'Di', '1 Mo-Fr'), 
                                                        'Mi', '1 Mo-Fr'), 
                                                'Do', '1 Mo-Fr'), 
                                        'Fr', '1 Mo-Fr'), 
                                'Sa', '2 Sa'), 
                        'So', '3 So'),
                   '1 Mo-1 Mo-Fr', '1 Mo-Fr') AS weekday,
                        SUBSTRING(billopened, 12, 2) AS hour,

count(*)
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
                OFFEREDITEM_ID = S.ID

and
                        billopened >= formatdatetime(dateadd('Month', -2, current_timestamp), 'YYYY-MM-01')
                        AND billopened < formatdatetime(dateadd('Month', -1, current_timestamp), 'YYYY-MM-01')

and internalconsumer_id is null
--and extract(hour from billopened) > 15
group by weekday, hour
order by weekday, hour;

CREATE VIEW compare_month_performance_with_prev_details AS
SELECT  day(L.date), L.total as LastMonthTotal, T.total as ThisMonthTotal
FROM BILLS_SUMMED_UP_BY_DAY_LAST_MONTH L left join 
           BILLS_SUMMED_UP_BY_DAY_THIS_MONTH  T
on day(L.date) = day(T.date)



