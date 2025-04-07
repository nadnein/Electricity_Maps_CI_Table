import os
import pandas as pd
import pytest
from app.createCITable import process_csv_files, get_2_letter_country_codes, find_missing_country_codes

# add ../electricity-maps-ci-table/python_code to your PYTHONPATH with: export PYTHONPATH=$PYTHONPATH:../electricity-maps-ci-table/python_code
# Run with: pytest python/tests/ 

# mock data for testing
@pytest.fixture
def mock_yearly_data(tmp_path):
    # Create a temporary directory for resources
    resources_folder = tmp_path / "resources"
    resources_folder.mkdir()

    # Create mock CSV files
    yearly_data_folder = resources_folder / "yearlyCIData"
    yearly_data_folder.mkdir()

    # Create the first mock CSV file
    yearly_data_file_1 = yearly_data_folder / "mock_yearly_data_1.csv"
    yearly_data_file_1.write_text(
        "Datetime (UTC),Country,Zone name,Zone id,Carbon intensity gCO₂eq/kWh (direct),"
        "Carbon intensity gCO₂eq/kWh (Life cycle),Carbon-free energy percentage (CFE%),"
        "Renewable energy percentage (RE%),Data source\n"
        "2024-01-01 00:00:00,France,France,FR,100,120,50,60,MockSource\n"
    )

    # Create the second mock CSV file
    yearly_data_file_2 = yearly_data_folder / "mock_yearly_data_2.csv"
    yearly_data_file_2.write_text(
        "Datetime (UTC),Country,Zone name,Zone id,Carbon intensity gCO₂eq/kWh (direct),"
        "Carbon intensity gCO₂eq/kWh (Life cycle),Carbon-free energy percentage (CFE%),"
        "Renewable energy percentage (RE%),Data source\n"
        "2024-01-01 01:00:00,Germany,Germany,DE,110,130,55,65,MockSource\n"
    )

    return str(resources_folder)


# Test process_csv_files
def test_process_csv_files(mock_yearly_data):
    output_file = os.path.join(mock_yearly_data, "output.csv")
    df = process_csv_files(os.path.join(mock_yearly_data, "yearlyCIData"), output_file)

    # Assert the DataFrame has the correct structure and data
    assert len(df) == 2
    assert list(df.columns) == [
        "Datetime (UTC)",
        "Country",
        "Zone name",
        "Zone id",
        "Carbon intensity gCO₂eq/kWh (direct)",
        "Carbon intensity gCO₂eq/kWh (Life cycle)",
        "Carbon-free energy percentage (CFE%)",
        "Renewable energy percentage (RE%)",
        "Data source",
    ]
    assert df.iloc[0]["Zone id"] == "FR"
    assert df.iloc[1]["Zone id"] == "DE"

# Test get_2_letter_country_codes
def test_get_2_letter_country_codes():
    # Create a mock DataFrame
    data = {
        "Zone id": ["FR", "DE", "IT", "CL-SEN", "US-CA"],
    }
    df = pd.DataFrame(data)

    # Get the 2-letter country codes
    country_codes = get_2_letter_country_codes(df)

    # Assert the correct codes are returned
    assert country_codes == ["FR", "DE", "IT", "CL-SEN"]

# Test find_missing_country_codes
def test_find_missing_country_codes():
    # Create a mock DataFrame for country codes
    data = {
        "ISO Code": ["FR", "DE", "IT"],
        "Country Name": ["France", "Germany", "Italy"],
    }
    df = pd.DataFrame(data)

    # Collected country codes
    collected_country_codes = ["FR", "IT"]

    # Find missing country codes
    missing_countries = find_missing_country_codes(df, collected_country_codes)

    # Assert the correct missing countries are returned
    assert missing_countries == {"DE": "Germany"}