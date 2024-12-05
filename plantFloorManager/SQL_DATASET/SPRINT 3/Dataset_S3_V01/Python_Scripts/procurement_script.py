import random
import xml.etree.ElementTree as ET


def generate_inserts(xml_file, table_name1, table_name2):
    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()

    inserts_suppliers = []
    inserts_procurements = []

    procurement_id = 10

    enter = ['\n']

    # Iterate for suppliers
    for supplier in root.findall('.//supplier'):

        # Extract the fields
        supplier_id = supplier.get('id')

        # Creates the INSERT command
        insert = (
            f"INSERT INTO {table_name1} (Supplier_ID)"
            f" VALUES ({supplier_id});"
        )
        inserts_suppliers.append(insert)

        for part in supplier.findall('.//part'):

            part_id = part.get('id')

            for offer in part.findall('.//offer'):

                start_date = offer.find('start_date').text.strip('"')
                end_date = offer.find('end_date').text.strip('"')
                min_qnt = offer.find('min_quantity').text.strip('"')
                price = offer.find('price').text.strip('"')
                procurement_id = procurement_id + 1

                if end_date is None or end_date.strip().lower() == 'null':
                    # Creates the INSERT command
                    insert = (
                        f"INSERT INTO {table_name2} (Procurement_ID, Part_ID, Supplier_ID, Price, Min_Qnt, Start_Date, "
                        f"End_Date)"
                        f" VALUES ({procurement_id}, '{part_id}', {supplier_id}, {price}, {min_qnt}, "
                        f"TO_DATE('{start_date}', 'YYYY/MM/DD'), NULL);"
                    )
                    inserts_procurements.append(insert)

                else:
                    # Creates the INSERT command
                    insert = (
                        f"INSERT INTO {table_name2} (Procurement_ID, Part_ID, Supplier_ID, Price, Min_Qnt, Start_Date, "
                        f"End_Date)"
                        f" VALUES ({procurement_id}, '{part_id}', {supplier_id}, {price}, {min_qnt}, TO_DATE('{start_date}', "
                        f"'YYYY/MM/DD'), TO_DATE('{end_date}', 'YYYY/MM/DD'));"
                    )
                    inserts_procurements.append(insert)

    return inserts_suppliers + enter + inserts_procurements


# XML file and table
xml_file_path = '../Dataset_S3_procurement_V01.xml'
table_name1 = 'Supplier'
table_name2 = 'Procurement'

# Generates INSERT commands
insert_statements = generate_inserts(xml_file_path, table_name1, table_name2)
for statement in insert_statements:
    print(statement)
