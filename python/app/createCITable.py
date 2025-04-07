import os
import pandas as pd

def process_csv_files(resources_folder, output_file=None):
    """
    Reads all CSV files in the specified folder, validates them, and concatenates them into a single DataFrame.
    """
    # List to store DataFrames for concatenation
    dataframes = []

    # Iterate through all files in the resources folder
    for filename in os.listdir(resources_folder):
        if filename.endswith(".csv"):
            file_path = os.path.join(resources_folder, filename)

            df = pd.read_csv(
                    file_path,
                    usecols=[
                        "Datetime (UTC)",
                        "Country",
                        "Zone name",
                        "Zone id",
                        "Carbon intensity gCO₂eq/kWh (direct)",
                        "Carbon intensity gCO₂eq/kWh (Life cycle)",
                        "Carbon-free energy percentage (CFE%)",
                        "Renewable energy percentage (RE%)",
                        "Data source",
                    ],
                    skip_blank_lines=True,
                    keep_default_na=False  # Ensure "NA" is not treated as a missing value
                )

            row_count = len(df)
            if row_count != 1:  # Check if there is only one row
                raise ValueError(
                    f"Error in file {filename}: It contains more than one row. "
                    f"Number of rows in 'Zone id': {row_count}"
                )
            # Add the DataFrame to the list for concatenation
            dataframes.append(df)

    ci_table_df = pd.concat(dataframes, ignore_index=True)

    if output_file:
        ci_table_df.to_csv(output_file, index=False)

    return ci_table_df

def get_2_letter_country_codes(df):
    """
    Extracts 2-letter country codes from the 'Zone id' column of the DataFrame.
    """
    country_codes = []
    df["Zone id"] = df["Zone id"].astype(str)  # Ensure 'Zone id' is treated as a string
    #print(df.columns)

    for country_code in df["Zone id"]:
        if "-" not in country_code or country_code == "CL-SEN":
             country_codes.append(country_code)  # Append the single value

    return country_codes

def find_missing_country_codes(df, collected_country_codes):
    """
    Finds missing country codes by comparing the collected codes with the codes in the DataFrame.
    """
    # Extract unique country codes from the DataFrame
    df_country_codes = set(df["ISO Code"].astype(str).unique())  # Assuming "Country" column contains 2-letter codes

    # Convert the collected country codes list to a set
    collected_country_codes_set = set(collected_country_codes)

    # Find the missing country codes (codes in df_country_codes but not in collected_country_codes_set)
    missing_country_codes = df_country_codes - collected_country_codes_set

    # Create a dictionary to map ISO codes to country names
    country_code_to_name = dict(zip(df["ISO Code"].astype(str), df["Country Name"]))

    # Get the corresponding country names for the missing codes
    missing_countries = {code: country_code_to_name.get(code, "Unknown") for code in missing_country_codes}

    return missing_countries


def main():
    # Define the resources folder and output file
    resources_folder = "resources"
    output_folder = "output_files"
    output_file = os.path.join(output_folder, "fallbackCIDataTable.csv")

    # Ensure the output folder exists
    os.makedirs(output_folder, exist_ok=True)

    # Process the CSV files
    ci_table_df = process_csv_files(os.path.join(resources_folder, 'yearlyCIData'), output_file)

    # Print Zone name and Zone id 
    print(f"{'Zone Name':35} | {'Zone ID'}")
    print("-" * 40)
    for index, row in ci_table_df.iterrows():
        print(f"{row['Zone name']:35} | {row['Zone id']}")

    # Print the total number of rows
    print("-" * 40)
    print(f"Total number of rows: {len(ci_table_df)}")

    # Get the 2-letter country codes
    country_codes = get_2_letter_country_codes(ci_table_df)

    # Find missing country codes 
    country_codes_df = pd.read_csv(os.path.join(resources_folder, "countryCodes.csv"), 
                                keep_default_na=False)  # Ensure "NA" is not treated as a missing value)

    # Find missing country codes and their corresponding country names
    missing_countries = find_missing_country_codes(country_codes_df, country_codes)

    # Print the missing country codes and names in the same format
    print("\nMissing Country Codes and Names:")
    print("-" * 40)
    if missing_countries:
        print(f"{'Country Code':15} | {'Country Name'}")
        print("-" * 40)
        for code, name in sorted(missing_countries.items()):  # Sort for consistent order
            print(f"{code:15} | {name}")
        # Print the total number of missing countries
        print("-" * 40)
        print(f"Total number of missing countries: {len(missing_countries)}")
    else:
        print("No missing country codes found!")
        
    
    if __name__ == "__main__":
        main()