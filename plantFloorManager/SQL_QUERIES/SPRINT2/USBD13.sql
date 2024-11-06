CREATE OR REPLACE FUNCTION Get_Product_Operations(
    p_product_id IN VARCHAR2
) RETURN SYS_REFCURSOR IS
    c_operations SYS_REFCURSOR;
BEGIN
    OPEN c_operations FOR
        -- Retrieve operations for the product and any subproducts that are required to produce it
        WITH Product_Operations AS (
            -- Get operations for the main product
            SELECT
                p.Product_ID AS Main_Product_ID,
                p.Product_ID AS Product_ID,
                boo.Manufacturing_Operation_ID,
                mo.Operation_Description,
                boo.Operation_Order
            FROM Product p
                     JOIN BOO boo ON boo.Product_Family_ID = p.Family_ID
                     JOIN Manufacturing_Operation mo ON mo.Manufacturing_Operation_ID = boo.Manufacturing_Operation_ID
            WHERE p.Product_ID = p_product_id

            UNION ALL

            -- Get operations for each part if it is a subproduct
            SELECT
                p.Product_ID AS Main_Product_ID,
                sp.Product_ID AS Product_ID,
                boo.Manufacturing_Operation_ID,
                mo.Operation_Description,
                boo.Operation_Order
            FROM Product p
                     JOIN BOM b ON b.ProductProduct_ID = p.Product_ID
                     JOIN BOM_Parts bp ON bp.BOMProductProduct_ID = b.ProductProduct_ID
                     JOIN Product sp ON sp.PartsParts_ID = bp.PartsParts_ID
                     JOIN BOO boo ON boo.Product_Family_ID = sp.Family_ID
                     JOIN Manufacturing_Operation mo ON mo.Manufacturing_Operation_ID = boo.Manufacturing_Operation_ID
            WHERE p.Product_ID = p_product_id
        ),
             Workstation_Types AS (
                 -- Retrieve workstation types associated with each operation
                 SELECT
                     po.Main_Product_ID,
                     po.Product_ID,
                     po.Manufacturing_Operation_ID,
                     po.Operation_Description,
                     po.Operation_Order,
                     wt.Workstation_Type
                 FROM Product_Operations po
                          JOIN Type_Industry ti ON ti.Manufacturing_Operation_ID = po.Manufacturing_Operation_ID
                          JOIN Workstation_Type wt ON wt.Workstation_Type_ID = ti.Workstation_Type_ID
             ),
             Component_Inputs_Outputs AS (
                 -- Gather input and output components for each operation
                 SELECT
                     wt.Main_Product_ID,
                     wt.Product_ID,
                     wt.Manufacturing_Operation_ID,
                     wt.Operation_Description,
                     wt.Operation_Order,
                     wt.Workstation_Type,
                     c.Component_ID AS Input_Component,
                     cr.Raw_Material_ID AS Output_Component
                 FROM Workstation_Types wt
                          LEFT JOIN Component c ON c.PartsParts_ID = wt.Product_ID
                          LEFT JOIN Component_Raw_Material cr ON cr.Component_ID = c.Component_ID
             )
        -- Final query to return operations with workstation types and components
        SELECT
            Main_Product_ID,
            Product_ID,
            Manufacturing_Operation_ID,
            Operation_Description,
            Operation_Order,
            Workstation_Type,
            Input_Component,
            Output_Component
        FROM Component_Inputs_Outputs
        ORDER BY Main_Product_ID, Product_ID, Operation_Order;

    RETURN c_operations;
END Get_Product_Operations;
