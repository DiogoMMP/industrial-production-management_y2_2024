CREATE OR REPLACE FUNCTION Register_Workstation(
    p_Workstation_ID IN NUMBER,
    p_Workstation_Name IN VARCHAR2,
    p_Workstation_Description IN VARCHAR2,
    p_Plant_Floor_ID IN NUMBER,
    p_Workstation_Type_ID IN VARCHAR2
) RETURN VARCHAR2 AS
BEGIN
    INSERT INTO Workstation (Workstation_ID, Workstation_Name, Workstation_Description, Plant_Floor_ID, Workstation_Type_ID)
    VALUES (p_Workstation_ID, p_Workstation_Name, p_Workstation_Description, p_Plant_Floor_ID, p_Workstation_Type_ID);

    RETURN 'Success';
EXCEPTION
    WHEN DUP_VAL_ON_INDEX THEN
        RETURN 'Error: Workstation ID already exists.';
    WHEN OTHERS THEN
        RETURN 'Error: ' || SQLERRM;
END Register_Workstation;


DECLARE
    result_message VARCHAR2(100);
BEGIN
    result_message := Register_Workstation(
        p_Workstation_ID => 9999,
        p_Workstation_Name => 'Test Station',
        p_Workstation_Description => 'Testing workstation entry',
        p_Plant_Floor_ID => 1,
        p_Workstation_Type_ID => 'A4578'
    );

    DBMS_OUTPUT.PUT_LINE('Function Result: ' || result_message);
END;