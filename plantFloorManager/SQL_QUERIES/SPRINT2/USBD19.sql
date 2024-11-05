// TO BE TESTED

SELECT
    p.Product_ID,
    p.Product_Name,
    COUNT(b.Manufacturing_Operation_ID) AS Operation_Count
FROM
    Product p
JOIN
    Product_Family pf ON p.Family_ID = pf.Family_ID
JOIN
    BOO b ON pf.Family_ID = b.Product_Family_ID
GROUP BY
    p.Product_ID, p.Product_Name
ORDER BY
    Operation_Count DESC
FETCH FIRST 1 ROWS ONLY;
