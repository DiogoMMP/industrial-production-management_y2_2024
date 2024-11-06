-- Insert a new product family for testing
INSERT INTO Product_Family (Family_ID, Family_Description) VALUES (999, 'Test Family for All Machines');

-- Insert a new product that will use all machine types
-- New product family for Universal Product
INSERT INTO Product_Family (Family_ID, Family_Description)
VALUES (150, 'Universal Family');


INSERT INTO Product (Product_ID, Product_Name, Product_Description, Factory_Plant_ID, Family_ID)
VALUES ('ALL_WS_PRODUCT', 'Universal Product', 'Product using all workstations', 1, 150);


-- Mapping all Manufacturing Operations for Universal Product Family in BOO
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5647, 1);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5649, 2);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5651, 3);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5653, 4);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5655, 5);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5657, 6);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5659, 7);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5661, 8);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5663, 9);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5665, 10);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5667, 11);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5669, 12);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5671, 13);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5681, 14);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5682, 15);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5683, 16);
INSERT INTO BOO (Product_Family_ID, Manufacturing_Operation_ID, Operation_Order) VALUES (150, 5688, 17);

-- Linking all Manufacturing Operations for Universal Product Family to each Workstation Type
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2001, 'Universal Operation', 5647, 'A4578');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2002, 'Universal Operation', 5649, 'A4588');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2003, 'Universal Operation', 5651, 'A4598');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2004, 'Universal Operation', 5653, 'C5637');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2005, 'Universal Operation', 5655, 'S3271');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2006, 'Universal Operation', 5657, 'K3675');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2007, 'Universal Operation', 5659, 'K3676');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2008, 'Universal Operation', 5661, 'D9123');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2009, 'Universal Operation', 5663, 'Q5478');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2010, 'Universal Operation', 5665, 'Q3547');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2011, 'Universal Operation', 5667, 'T3452');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2012, 'Universal Operation', 5669, 'G9273');
INSERT INTO Type_Industry (Type_Industry_ID, Type_Industry, Manufacturing_Operation_ID, Workstation_Type_ID) VALUES (2013, 'Universal Operation', 5671, 'G9274');


SELECT p.Product_ID, p.Product_Name
FROM Product p
JOIN BOO boo ON p.Family_ID = boo.Product_Family_ID
JOIN Type_Industry ti ON boo.Manufacturing_Operation_ID = ti.Manufacturing_Operation_ID
JOIN Workstation w ON ti.Workstation_Type_ID = w.Workstation_Type_ID
GROUP BY p.Product_ID, p.Product_Name
HAVING COUNT(DISTINCT w.Workstation_Type_ID) = (
    SELECT COUNT(DISTINCT Workstation_Type_ID) FROM Workstation
);