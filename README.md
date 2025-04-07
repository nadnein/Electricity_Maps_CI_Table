# Electricity Maps CI Table

Generates a CSV file containing yearly average carbon intensity (CI) values for various locations using data from Electricity Maps. Currently, the data is based on values from 2024.
---

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/your-username/electricity-maps-ci-table.git
cd electricity-maps-ci-table
```

### 2. Set Up a Virtual Environment
It is recommended to use a virtual environment to manage dependencies.

```bash
python3 -m venv venv
source venv/bin/activate  # On macOS/Linux
venv\Scripts\activate     # On Windows
```

### 3. Install Dependencies
Install the required Python packages using `pip`:

```bash
pip install -r requirements.txt
```

---

## Running the Code

### 1. Process CSV Files
To process the yearly carbon intensity data and generate an output file, run the `createCITable.py` script:

```bash
python python/app/createCITable.py
```

The output will be saved in the `output_files/` directory as `fallbackCIDataTable.csv`.

---

## Running the Tests

### 1. Add the `python_code` Directory to `PYTHONPATH`
Before running the tests, ensure the `python_code` directory is added to your `PYTHONPATH`. You can do this by running:

```bash
export PYTHONPATH=$PYTHONPATH:$(pwd)/python_code
```

### 2. Run the Tests
Use `pytest` to run the tests:

```bash
pytest python/tests/
```