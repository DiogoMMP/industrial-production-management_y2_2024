import xml.etree.ElementTree as ET


def generate_inserts(xml_file, table_name):
    # Parse the XML file
    tree = ET.parse(xml_file)
    root = tree.getroot()

    inserts = []

    # Iterate for customers
    for client in root.findall('client'):
        # Extracts the ID of the attribute
        client_id = client.get('ID')
        # Extract the fields
        name = client.find('name').text.strip('"')
        nif = client.find('vatin').text.strip('"')
        address = client.find('adress').text.strip('"')
        zip_code = client.find('zip').text.strip('"')
        town = client.find('town').text.strip('"')
        country = client.find('country').text.strip('"')
        email = client.find('email').text.strip('"')
        mobile_number = client.find('phone').text.strip()

        # Hide apostrophes and other special characters
        name = name.replace("'", "''")
        adress = address.replace("'", "''")
        town = town.replace("'", "''")

        country_id = 0

        # Adjusts the country value based on the name
        if country.lower() == 'portugal':
            country_id = 620
        elif country.lower() == 'czechia':
            country_id = 203

        # Creates the INSERT command
        insert = (
            f"INSERT INTO {table_name} (Customer_ID, NIF, Name, Address, Mobile_Number, Email, type_ID, Country_ID, "
            f"Status)"
            f" VALUES ({client_id}, '{nif}', '{name}', '{adress}, {zip_code} {town}, {country}', {mobile_number}, '{email}',"
            f"'type_id', {country_id}, 'Activated');"
        )
        inserts.append(insert)

    return inserts


# XML file and table
xml_file_path = '../Dataset_S3_clients_V01.xml'
table_name = 'Customer'

# Generates INSERT commands
insert_statements = generate_inserts(xml_file_path, table_name)
for statement in insert_statements:
    print(statement)
