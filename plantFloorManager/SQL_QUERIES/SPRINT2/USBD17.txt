CREATE OR REPLACE FUNCTION Register_Customer_Order (
   p_Customer_ID IN NUMBER,
   p_Product_ID IN VARCHAR2,
   p_Order_Date IN DATE,
   p_Delivery_Date IN DATE,
   p_Location IN VARCHAR2,
   p_Quantity IN NUMBER
) RETURN NUMBER IS

   v_Customer_Status VARCHAR2(20);
   v_Product_Exists NUMBER;
   v_Order_ID NUMBER;

BEGIN
   -- Step 1: Validate that the customer is active
BEGIN
SELECT Status INTO v_Customer_Status
FROM Customer
WHERE Customer_ID = p_Customer_ID;

IF v_Customer_Status != 'Active' THEN
         RAISE_APPLICATION_ERROR(-20001, 'Error: Customer is not active.');
END IF;

EXCEPTION
      -- Handle case where customer is not found
      WHEN NO_DATA_FOUND THEN
         RAISE_APPLICATION_ERROR(-20005, 'Error: Customer does not exist.');
END;

   -- Step 2: Validate that the product exists in the current product line-up
BEGIN
SELECT COUNT(*)
INTO v_Product_Exists
FROM Product
WHERE Product_ID = p_Product_ID;

IF v_Product_Exists = 0 THEN
         RAISE_APPLICATION_ERROR(-20002, 'Error: Product is not in the current line-up.');
END IF;

EXCEPTION
      -- Handle case where product is not found
      WHEN NO_DATA_FOUND THEN
         RAISE_APPLICATION_ERROR(-20006, 'Error: Product does not exist.');
END;

   -- Step 3: Insert the new order into the Customer_Order table
SELECT Customer_Order_SEQ.NEXTVAL INTO v_Order_ID FROM dual;

INSERT INTO Customer_Order (
    Customer_Order_ID, Order_Date, Delivery_Date, Location, Customer_ID
) VALUES (
             v_Order_ID, p_Order_Date, p_Delivery_Date, p_Location, p_Customer_ID
         );

-- Step 4: Insert the product into the Customer_Order_Product table
INSERT INTO Customer_Order_Product (
    Customer_Order_ID, Product_ID, Quantity
) VALUES (
             v_Order_ID, p_Product_ID, p_Quantity
         );

-- Step 5: Return the generated Customer_Order_ID
RETURN v_Order_ID;

EXCEPTION
   -- Catch-all for any other unexpected errors
   WHEN OTHERS THEN
      RAISE_APPLICATION_ERROR(-20004, 'Error: Could not process the order due to an unexpected issue: ' || SQLERRM);
END;
/