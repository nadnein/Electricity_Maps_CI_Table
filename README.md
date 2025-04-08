# Electricity Maps CI Table

Generates a CSV file containing yearly average carbon intensity (CI) values for various locations using data from [Electricity Maps](https://app.electricitymaps.com). Currently, the data is based on values from 2024.

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

## Running the Python Code

### Process CSV Files
To process the yearly carbon intensity data for different locations and generate an output file, run the `createCITable.py` script:

```bash
python python/app/createCITable.py
```

The output will be saved in the `output_files/` directory as `fallbackCIDataTable.csv`.

---

## Running the Groovy Code

### 1. Prerequisites
Ensure you have Java, Groovy, and Gradle installed on your system. You can check their versions using:
```bash
java -version
groovy -version
gradle -version
```

### 2. Set Up Environment Variables
Export your Electricity Maps API key as an environment variable:

```bash
export ELECTRICITYMAP_API_KEY=your-real-api-key
```

### 3. Build the Project
Before running the script, build the project to ensure all dependencies are resolved and the code is compiled:

```bash
./gradlew build
```

### 4. Run the Groovy Script
To determine the carbon intensity for a specific location, use Gradle to execute the `GetCIvalue` class:
```bash
./gradlew run
```
Ensure that the `build.gradle` file is properly configured to include the `GetCIvalue` class as the main class for execution.

The script will fetch real-time carbon intensity data from the Electricity Maps API. 
If the API fails, it will fall back to the `fallbackCIDataTable.csv` file in the `output_files` directory.

---

## Running the Tests

### 1. Python Tests

#### Add the `python_code` Directory to `PYTHONPATH`
Before running the tests, ensure the `python_code` directory is added to your `PYTHONPATH`. You can do this by running:

```bash
export PYTHONPATH=$PYTHONPATH:$(pwd)/python_code
```

#### Run the Tests
Use `pytest` to run the tests:

```bash
pytest python/tests/
```

### 2. Groovy Tests
To run the Groovy tests, use the following command:
```bash
./gradlew test
```

---

## Selecting Your Zone Code

To use the Electricity Maps API, you need a **zone code** for your location. You can find the zone code for your region by visiting the [Electricity Maps Zones Overview](https://portal.electricitymaps.com/docs/getting-started#zonesoverview).

---

## Data Attribution

This project uses data from **Electricity Maps** under the Open Database License (ODbL). 

### License Terms:
- **Share**: You are free to copy, distribute, and use the data.
- **Adapt**: You can modify, transform, and build upon the data.
- **Create**: You can produce works from the data.

### Conditions:
- **Attribute**: You must clearly credit the source of the data and include the license for any public use or redistribution.
- **Share-Alike**: Any adapted versions of the data must be offered under the same ODbL license.
- **Keep Open**: If you use technological restrictions (e.g., DRM), you must also provide an unrestricted version.

---

## Disclaimer

This project is not affiliated with or endorsed by **Electricity Maps**.  
The data is used under the terms of the Open Database License (ODbL).