import random
import xml.etree.ElementTree as ET


def generate_inserts(xml_file, table_name1, table_name2, table_name3, table_name4):
    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()

    # Initialize lists to store INSERT statements
    inserts_boo = []
    inserts_boo_input = []
    inserts_boo_output = []
    inserts_operations = []
    enter = ['\n']

    # Iterate over 'boo' elements in the XML
    for boo in root.findall('.//boo'):
        # Extract the ID attribute
        product_id = boo.get('id')

        # Add INSERT statements for BOO table
        for operation in boo.findall('.//operation'):

            operation_id = operation.get('id')
            operation_type_id = operation.find('optype_id').text.strip('"')
            eet = operation.find('eet').text.strip('"')
            next_operation_id = operation.find('next_op').text.strip('"')

            insert_operation = (
                f"INSERT INTO {table_name4} (Operation_ID, Operation_Type_ID, Expected_Estimated_Time) "
                f"VALUES ({operation_id}, {operation_type_id}, {eet});"
            )
            inserts_operations.append(insert_operation)

            if next_operation_id == 'None':
                insert_boo = (
                    f"INSERT INTO {table_name1} (Operation_ID, Product_ID) "
                    f"VALUES ({operation_id}, '{product_id}');"
                )
                inserts_boo.append(insert_boo)

            else:
                insert_boo = (
                    f"INSERT INTO {table_name1} (Operation_ID, Product_ID, Next_Operation_ID) "
                    f"VALUES ({operation_id}, '{product_id}', {next_operation_id});"
                )
                inserts_boo.append(insert_boo)

            for input_item in operation.findall('.//input'):
                part_id = input_item.find('part').text.strip('"')
                quantity = input_item.find('quantity').text.strip('"')
                unit = input_item.find('unit').text.strip('"')

                insert_input = (
                    f"INSERT INTO {table_name2} (Product_ID, Operation_ID, Part_ID, Quantity, Unit) "
                    f"VALUES ('{product_id}', {operation_id}, '{part_id}', {quantity}, '{unit}');"
                )
                inserts_boo_input.append(insert_input)

            for output_item in operation.findall('.//output'):
                part_id = output_item.find('part').text.strip('"')
                quantity = output_item.find('quantity').text.strip('"')
                unit = output_item.find('unit').text.strip('"')

                insert_output = (
                    f"INSERT INTO {table_name3} (Product_ID, Operation_ID, Part_ID, Quantity, Unit) "
                    f"VALUES ('{product_id}', {operation_id}, '{part_id}', {quantity}, '{unit}');"
                )
                inserts_boo_output.append(insert_output)

    return inserts_operations + enter + inserts_boo + enter + inserts_boo_input + enter + inserts_boo_output


# XML file and table names
xml_file_path = 'Dataset_S3_boo_V02.xml'
table_name1 = 'BOO'
table_name2 = 'BOO_Input'
table_name3 = 'BOO_Output'
table_name4 = 'Operation'

# Generate INSERT commands
insert_statements = generate_inserts(xml_file_path, table_name1, table_name2, table_name3, table_name4)
for statement in insert_statements:
    print(statement)
