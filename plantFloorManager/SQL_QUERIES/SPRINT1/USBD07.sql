SELECT
    po.Production_Order_ID,
    bom.Component_ID,
    c.Component_Description,
    SUM(bom.Component_Quantity * po.Quantity_Ordered) AS Quantity_To_Order
FROM
    Product_Production_Order po
JOIN
    BOM bom ON po.Product_ID = bom.Product_ID
JOIN
    Component c ON bom.Component_ID = c.Component_ID
GROUP BY
    po.Production_Order_ID,
    bom.Component_ID,
    c.Component_Description
ORDER BY
    po.Production_Order_ID,
    bom.Component_ID;


    The select is to simplified so it is not right. The correct select needs to have to have a relation with the raw materials as the
    components are made from raw materials. But the select is not wrong, it is just simplified.
    In the relation model the bom needs to be refactored, in order to represent the correlation beetween the raw materials, the components and the product itself wich can be a componenet of another product.