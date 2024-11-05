// TO BE TESTED

SELECT
    p.Product_ID AS Product,
    bp.PartsParts_ID AS Part_ID,
    bp.Parts_Quantity,
    mo.Manufacturing_Operation_ID AS Operation_ID,
    mo.Operation_Description,
    ti.Workstation_Type_ID AS Workstation_Type,
    ws.Workstation_Name,
    ws.Workstation_Description,
    cr.Raw_Material_ID,
    cr.Raw_Material_Quantity
FROM
    Product p
JOIN
    BOM b ON p.Product_ID = b.ProductProduct_ID
JOIN
    BOM_Parts bp ON b.ProductProduct_ID = bp.BOMProductProduct_ID
LEFT JOIN
    BOO boo ON p.Family_ID = boo.Product_Family_ID
LEFT JOIN
    Manufacturing_Operation mo ON boo.Manufacturing_Operation_ID = mo.Manufacturing_Operation_ID
LEFT JOIN
    Type_Industry ti ON ti.Manufacturing_Operation_ID = mo.Manufacturing_Operation_ID
LEFT JOIN
    Workstation_Type wt ON ti.Workstation_Type_ID = wt.Workstation_Type_ID
LEFT JOIN
    Workstation ws ON wt.Workstation_Type_ID = ws.Workstation_Type_ID
LEFT JOIN
    Component_Raw_Material cr ON cr.Component_ID = bp.PartsParts_ID
WHERE
    p.Product_ID = :specified_product_id
ORDER BY
    bp.PartsParts_ID, boo.Operation_Order;
