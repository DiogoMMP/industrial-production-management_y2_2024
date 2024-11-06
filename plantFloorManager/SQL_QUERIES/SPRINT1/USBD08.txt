SELECT DISTINCT
    MO.Manufacturing_Operation_ID,
    MO.Operation_Description,
    TI.Type_Industry,
    WT.Workstation_Type_ID,
    WT.Workstation_Type
FROM
    Manufacturing_Operation MO
JOIN
    Type_Industry TI ON MO.Manufacturing_Operation_ID = TI.Manufacturing_Operation_ID
JOIN
    Workstation_Type WT ON TI.Workstation_Type_ID = WT.Workstation_Type_ID
ORDER BY
    MO.Manufacturing_Operation_ID;
