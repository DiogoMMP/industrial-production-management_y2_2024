import random
import xml.etree.ElementTree as ET


def generate_inserts(xml_file, table_name1, table_name2):
    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()

    inserts_operation_type = []
    inserts_workstation_type_operation_type = []
    enter = ['\n']

    # Initialise a dictionary to store the results
    operations_workstations = {}

    # Iterate for operation types
    for operation_type in root.findall('.//operation_type'):
        # Extracts the ID of the attribute
        operation_type_id = operation_type.get('id')

        # Extract the fields
        operation_description = operation_type.find('operation_desc').text.strip('"')
        workstation_types = [
            ws.text.strip('"') for ws in operation_type.findall(".//workstation_type")
        ]  # Get all associated workstations

        operations_workstations[operation_type_id] = workstation_types  # Add to dictionary

        # Hide apostrophes and other special characters
        operation_description = operation_description.replace("'", "''")

        # Creates the INSERT command
        insert = (
            f"INSERT INTO {table_name1} (Operation_Type_ID, Operation_Description)"
            f" VALUES ({operation_type_id}, '{operation_description}');"
        )
        inserts_operation_type.append(insert)

        for Workstation_Type_Operation_Type_ID in workstation_types:

            # Generate random times
            maximum_execution_time = random.randint(100, 500)  # Time between 100 and 300
            setup_time = random.randint(10, 50)  # Setup time between 10 and 50

            # Creates the INSERT command
            insert_ws = (
                f"INSERT INTO {table_name2} (Workstation_Type_ID, Operation_Type_ID, Maximum_Execution_Time, Setup_Time)"
                f" VALUES ('{Workstation_Type_Operation_Type_ID}', {operation_type_id}, {maximum_execution_time}, "
                f"{setup_time});"
            )
            inserts_workstation_type_operation_type.append(insert_ws)

    return inserts_operation_type + enter + inserts_workstation_type_operation_type


# XML file and table
xml_file_path = 'Dataset_S3_operations_V01.xml'
table_name1 = 'Operation_Type'
table_name2 = 'Workstation_Type_Operation_Type'

# Generates INSERT commands
insert_statements = generate_inserts(xml_file_path, table_name1, table_name2)
for statement in insert_statements:
    print(statement)
