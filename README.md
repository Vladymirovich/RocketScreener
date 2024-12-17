RocketScreener

RocketScreener is a comprehensive system designed to monitor, aggregate, analyze, and notify users about the state of the cryptocurrency market with maximum flexibility and configurability. Whether you're a seasoned trader or just starting, RocketScreener provides the tools you need to stay informed and make data-driven decisions.

Table of Contents
Features
Prerequisites
Installation
Configuration
Running the Project
Using the Bots
Public Bot
Admin Bot
Monitoring and Metrics
Backup and Archiving
Troubleshooting
Contributing
License
Features
Data Collection: Gathers data from multiple cryptocurrency exchanges and analytics platforms.
Data Analysis & Filtering: Applies customizable filters and composite rules to analyze market data.
Real-Time Notifications: Sends instant alerts about significant market events via Telegram bots.
Admin Interface: Manage settings, filters, data sources, and templates through an intuitive Telegram admin bot.
Historical Data Storage: Maintains up to 5 years of historical data with regular backups and archiving.
Monitoring & Metrics: Integrates with Prometheus and Grafana for real-time monitoring and visualization.
Smart Money Analysis: Utilizes OpenAI to provide insightful analyses of market movements.
User-Friendly: Designed for both beginners and experienced users to effortlessly monitor the crypto market.
Prerequisites
Before you begin, ensure you have the following installed on your server:

Java:

Version: Java 17 (Latest LTS recommended)
Installation: Download Java
Maven:

Version: 3.6.0 or higher
Installation: Install Maven
Docker & Docker Compose:

Installation: Install Docker and Install Docker Compose
PostgreSQL:

Installation: Managed via Docker Compose in the setup steps below.
Prometheus & Grafana:

Installation: Managed via Docker Compose in the setup steps below.
Telegram Accounts:

Admin Bot: Boss_Rocket_Screener_bot
Public Bot: Rocket_Screener_bot
Bot Tokens: Provided by Telegram when you create bots.
Installation
Follow these steps to set up and run RocketScreener on your server.

1. Clone the Repository
Open your terminal and run:

bash
Копировать код
git clone https://github.com/Vladymirovich/RocketScreener.git
cd RocketScreener
2. Set Up Environment Variables
Create a .env file in the root directory of the project. This file will hold all your configuration variables.

bash
Копировать код
cp .env.example .env
Note: Replace the placeholder values with your actual API keys and tokens.

3. Configure Docker Services
RocketScreener uses Docker to manage PostgreSQL, Prometheus, and Grafana. Ensure Docker and Docker Compose are installed on your system.

Navigate to the docker/ directory:

bash
Копировать код
cd docker
a. Docker Compose Setup
Create a docker-compose.yml file with the following content:

yaml
Копировать код
version: '3.8'

services:
  postgres:
    image: postgres:13
    restart: always
    environment:
      POSTGRES_USER: ${DB_USERNAME}
      POSTGRES_PASSWORD: ${DB_PASSWORD}
      POSTGRES_DB: rocketscreener
    volumes:
      - postgres-data:/var/lib/postgresql/data
    ports:
      - "5432:5432"

  prometheus:
    image: prom/prometheus:latest
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
    ports:
      - "9090:9090"
    restart: always

  grafana:
    image: grafana/grafana:latest
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    ports:
      - "3000:3000"
    volumes:
      - grafana-data:/var/lib/grafana
    depends_on:
      - prometheus
    restart: always

volumes:
  postgres-data:
  grafana-data:
b. Prometheus Configuration
Create a prometheus.yml file inside the docker/ directory:

yaml
Копировать код
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
  
  - job_name: 'rocket_screener'
    static_configs:
      - targets: ['host.docker.internal:8080']
Note: Adjust targets as needed based on your network setup.

4. Launch Docker Containers
From the docker/ directory, run:

bash
Копировать код
docker-compose up -d
This command will start PostgreSQL, Prometheus, and Grafana in detached mode.

5. Initialize the Database
RocketScreener uses Flyway for database migrations. Ensure your .env file has the correct PostgreSQL credentials.

From the root directory, run:

bash
Копировать код
mvn flyway:migrate
6. Build and Run the Application
Return to the root directory:

bash
Копировать код
cd ..
Build the project using Maven:

bash
Копировать код
mvn clean install
Run the application:

bash
Копировать код
mvn spring-boot:run
The application should now be running on http://localhost:8080.

Configuration
Ensure all required environment variables are set in the .env file. Here's a breakdown of the key variables:

bash
Копировать код
# Telegram Bots
ADMIN_BOT_TOKEN=8158495456:AAEWAoA4raZIruSFSV0uaoGH1Aw0xfqci-g
ADMIN_BOT_USERNAME=Boss_Rocket_Screener_bot
PUBLIC_BOT_TOKEN=7613015195:AAFrWktn8tbqBiz_1BXxxk9XSZOe5Y3T6Kg
PUBLIC_BOT_USERNAME=Rocket_Screener_bot
PUBLIC_CHAT_ID=987654321

# Whitelist for admin users (Telegram user IDs separated by commas)
ADMIN_WHITELIST=123456789,987654321

# Database
DB_URL=jdbc:postgresql://localhost:5432/rocketscreener
DB_USERNAME=postgres
DB_PASSWORD=yourpassword
DB_SCHEMA=public

# Historical Data
HISTORICAL_DATA_MAX_PERIOD_YEARS=5
ARCHIVE_THRESHOLD_YEARS=1

# Default filters
DEFAULT_VOLUME_CHANGE_THRESHOLD=10
DEFAULT_TIME_INTERVAL_MINUTES=5

# API Keys (Replace FAKE_..._KEY with your actual keys)
COINMARKETCAP_API_KEY=FAKE_CMC_KEY
BINANCE_API_KEY=FAKE_BINANCE_KEY
BYBIT_API_KEY=FAKE_BYBIT_KEY
COINBASE_API_KEY=FAKE_COINBASE_KEY
OKX_API_KEY=FAKE_OKX_KEY
KRAKEN_API_KEY=FAKE_KRAKEN_KEY
BITFINEX_API_KEY=FAKE_BITFINEX_KEY
GATEIO_API_KEY=FAKE_GATEIO_KEY
ARKHAM_API_KEY=FAKE_ARKHAM_KEY
ETHERSCAN_API_KEY=FAKE_ETHERSCAN_KEY
OPENAI_API_KEY=FAKE_OPENAI_KEY
EXCHANGE_API_KEY=FAKE_EXCHANGE_RATE_KEY

# Localization
DEFAULT_LANGUAGE=en

# Prometheus/Grafana
GRAFANA_URL=http://localhost:3000
PROMETHEUS_URL=http://localhost:9090

# Monthly backups
MONTHLY_BACKUPS_ENABLED=true

# Real-time notifications
REALTIME_ENABLED=true
Important:
Replace all FAKE_..._KEY values with your actual API keys.
Ensure ADMIN_WHITELIST contains the Telegram user IDs of administrators.
Update PUBLIC_CHAT_ID with the actual chat ID where public notifications should be sent.
Running the Project
Start Docker Services:

bash
Копировать код
cd docker
docker-compose up -d
cd ..
Build and Run the Application:

bash
Копировать код
mvn clean install
mvn spring-boot:run
Access Services:

Prometheus: http://localhost:9090
Grafana: http://localhost:3000
Default Login:
Username: admin
Password: admin
Using the Bots
Public Bot
The Public Bot sends notifications to all subscribed users about significant market events.

Bot Link: Rocket_Screener_bot
Features:
Receive real-time alerts on market changes.
View daily and weekly overviews.
Note: Users can only receive notifications and cannot modify any settings.

Admin Bot
The Admin Bot allows administrators to configure and manage the RocketScreener system.

Bot Link: Boss_Rocket_Screener_bot
Bot Token: Provided in the .env file (ADMIN_BOT_TOKEN).
Whitelist: Only users with Telegram IDs listed in ADMIN_WHITELIST can access admin functionalities.
Features:

Manage Data Sources:

Add or remove cryptocurrency exchanges and analytics platforms.
Set priority levels for data sources.
Configure Filters:

Define thresholds for price changes, volume shifts, etc.
Create composite filters using logical operators (AND/OR).
Edit Notification Templates:

Customize the format and content of notifications.
Manage templates for different event types.
Set Data Retention Policies:

Configure how long historical data is stored.
Set up archiving for old data.
Monitor System Metrics:

View performance and technical metrics via Prometheus/Grafana integration.
Backup Management:

Initiate database backups.
Access archived data.
Localization Settings:

Switch between supported languages (e.g., English, Russian).
How to Use:

Start the Admin Bot:

Open Telegram and navigate to Boss_Rocket_Screener_bot.

Authenticate:

Ensure your Telegram user ID is listed in ADMIN_WHITELIST. If not, contact the system administrator.

Navigate the Menu:

Use the inline buttons and submenus to access different configuration options.

Make Changes:

Follow the prompts to add sources, set filters, edit templates, and manage other settings.

Monitoring and Metrics
RocketScreener integrates with Prometheus and Grafana to provide real-time monitoring and visualization of system performance and market metrics.

Accessing Grafana Dashboards
Open Grafana:

Visit http://localhost:3000 in your web browser.

Login:

Username: admin
Password: admin
Explore Dashboards:

View pre-configured dashboards that display various metrics related to RocketScreener's performance and the cryptocurrency market.

Prometheus Configuration
Prometheus scrapes metrics from RocketScreener and stores them for Grafana to visualize.

Access Prometheus: http://localhost:9090
Backup and Archiving
RocketScreener ensures data integrity and availability through regular backups and archiving.

Database Backups
Automated Monthly Backups:
The system performs monthly backups of the PostgreSQL database. These backups are accessible via the admin bot.
Data Archiving
Historical Data Storage:
RocketScreener stores historical market data for up to 5 years. Data older than one year is archived to cold storage, which can be accessed through the admin bot.
Troubleshooting
If you encounter issues while setting up or running RocketScreener, follow these steps:

Check Logs:

Review application logs located in the logs/ directory or accessible via the admin bot.

Verify Environment Variables:

Ensure all necessary variables are correctly set in the .env file.

Ensure Docker Services are Running:

bash
Копировать код
cd docker
docker-compose ps
All services should show a status of Up.

Database Connectivity:

Confirm that PostgreSQL is running and accessible using the credentials in the .env file.

API Keys:

Ensure all API keys and tokens are valid and have the necessary permissions.

Network Configuration:

Make sure the server's firewall allows necessary ports (e.g., 5432 for PostgreSQL, 9090 for Prometheus, 3000 for Grafana).

Contributing
Contributions are welcome! If you'd like to contribute to RocketScreener, please follow these steps:

Fork the Repository

Create a New Branch

bash
Копировать код
git checkout -b feature/YourFeatureName
Make Your Changes

Commit Your Changes

bash
Копировать код
git commit -m "Add Your Feature"
Push to the Branch

bash
Копировать код
git push origin feature/YourFeatureName
Open a Pull Request
