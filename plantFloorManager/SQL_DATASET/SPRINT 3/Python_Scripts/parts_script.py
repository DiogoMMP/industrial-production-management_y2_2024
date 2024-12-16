import random
import xml.etree.ElementTree as ET


def generate_inserts(xml_file, table_name1, table_name2, table_name3, table_name4, table_name5,
                     table_name6, table_name7):
    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()

    inserts_parts = []
    inserts_internal_parts = []
    inserts_external_parts = []
    inserts_products = []
    inserts_intermediate_products = []
    inserts_components = []
    inserts_raw_materials = []

    enter = ['\n']

    # Iterate for parts
    for part in root.findall('.//part'):

        # Extract the fields
        part_id = part.find('part_number').text.strip('"')
        part_description = part.find('description').text.strip('"')
        part_type = part.find('part_type').text.strip('"')

        # Hide apostrophes and other special characters
        part_id = part_id.replace("'", "''")
        part_description = part_description.replace("'", "''")
        part_type = part_type.replace("'", "''")

        # Creates the INSERT command
        insert = (
            f"INSERT INTO {table_name1} (Part_ID, Part_Description)"
            f" VALUES ('{part_id}', '{part_description}');"
        )
        inserts_parts.append(insert)

        # Adjusts the country value based on the name
        if part_type.lower() == 'component':
            insert = (
                f"INSERT INTO {table_name5} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_external_parts.append(insert)

            insert = (
                f"INSERT INTO {table_name6} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_components.append(insert)

        elif part_type.lower() == 'raw material':
            insert = (
                f"INSERT INTO {table_name5} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_external_parts.append(insert)

            insert = (
                f"INSERT INTO {table_name7} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_raw_materials.append(insert)

        elif part_type.lower() == 'product':
            insert = (
                f"INSERT INTO {table_name2} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_internal_parts.append(insert)

            name = part.find('name').text.strip('"')
            name = name.replace("'", "''")

            family = part.find('family').text.strip('"')
            factory_plant_id = random.randint(1, 3)

            insert = (
                f"INSERT INTO {table_name4} (Product_ID, Product_Name, Factory_Plant_ID, Family_ID)"
                f" VALUES ('{part_id}', '{name}', {factory_plant_id}, {family});"
            )
            inserts_products.append(insert)

        elif part_type.lower() == 'intermediate product':
            insert = (
                f"INSERT INTO {table_name2} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_internal_parts.append(insert)

            insert = (
                f"INSERT INTO {table_name3} (Part_ID)"
                f" VALUES ('{part_id}');"
            )
            inserts_intermediate_products.append(insert)

    return (inserts_parts + enter + inserts_internal_parts + enter + inserts_intermediate_products + enter +
            inserts_products + enter + inserts_external_parts + enter + inserts_components + enter +
            inserts_raw_materials)


# XML file and table
xml_file_path = 'Dataset_S3_parts_V01.xml'
table_name1 = 'Part'
table_name2 = 'Internal_Part'
table_name3 = 'Intermediate_Product'
table_name4 = 'Product'
table_name5 = 'External_Part'
table_name6 = 'Component'
table_name7 = 'Raw_Material'

# Generates INSERT commands
insert_statements = generate_inserts(xml_file_path, table_name1, table_name2, table_name3, table_name4, table_name5,
                                     table_name6, table_name7)
for statement in insert_statements:
    print(statement)
