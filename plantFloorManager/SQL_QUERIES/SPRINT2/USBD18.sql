CREATE OR REPLACE PROCEDURE Deactivate_Customer (p_Customer_ID NUMBER) IS
    v_Pending_Orders NUMBER;
BEGIN
    -- Check if there are any pending or undelivered orders for the customer
    SELECT COUNT(*)
    INTO v_Pending_Orders
    FROM Customer_Order
    WHERE Customer_ID = p_Customer_ID
      AND Delivery_Date IS NULL;  -- Assuming NULL Delivery_Date indicates pending or undelivered orders

    -- If no pending orders, update the status to "Inactive"
    IF v_Pending_Orders = 0 THEN
        UPDATE Customer
        SET Status = 'Inactive'
        WHERE Customer_ID = p_Customer_ID;
        DBMS_OUTPUT.PUT_LINE('Customer successfully deactivated.');
    ELSE
        DBMS_OUTPUT.PUT_LINE('Error: Customer has pending or undelivered orders and cannot be deactivated.');
    END IF;
END;
/
