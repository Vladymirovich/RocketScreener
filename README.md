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
'''bash
git clone https://github.com/Vladymirovich/RocketScreener.git
cd RocketScreener
'''

2. Set Up Environment Variables
Create a .env file in the root directory of the project. This file will hold all your configuration variables.
'''bash
cp .env.example .env
'''


