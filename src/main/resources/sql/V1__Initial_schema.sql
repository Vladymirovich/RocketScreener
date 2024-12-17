CREATE TABLE IF NOT EXISTS sources (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  type VARCHAR(50) NOT NULL,  -- e.g. "exchange", "analytics", "scanner"
  base_url TEXT NOT NULL,
  api_key TEXT,
  enabled BOOLEAN DEFAULT TRUE,
  priority INT DEFAULT 100
);

CREATE TABLE IF NOT EXISTS filters (
  id SERIAL PRIMARY KEY,
  name VARCHAR(255) NOT NULL,
  metric VARCHAR(100) NOT NULL,  -- e.g. "volume", "price"
  threshold_value DECIMAL NOT NULL,
  threshold_type VARCHAR(50) NOT NULL, -- e.g. "percentage", "absolute"
  time_interval_minutes INT NOT NULL,
  enabled BOOLEAN DEFAULT TRUE,
  is_composite BOOLEAN DEFAULT FALSE,
  composite_expression TEXT -- e.g. "filter1 AND filter2"
);

CREATE TABLE IF NOT EXISTS events (
  id SERIAL PRIMARY KEY,
  event_type VARCHAR(100) NOT NULL, -- e.g. "LARGE_TRANSFER"
  symbol VARCHAR(50),
  source VARCHAR(255),
  timestamp TIMESTAMP DEFAULT now(),
  details JSONB
);

CREATE TABLE IF NOT EXISTS notifications (
  id SERIAL PRIMARY KEY,
  event_id INT REFERENCES events(id),
  template_name VARCHAR(255),
  message TEXT,
  sent_at TIMESTAMP DEFAULT now(),
  chat_id VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS templates (
  id SERIAL PRIMARY KEY,
  template_name VARCHAR(255) UNIQUE NOT NULL,
  template_body TEXT NOT NULL,
  event_type VARCHAR(100),
  language VARCHAR(5) DEFAULT 'en'
);

CREATE TABLE IF NOT EXISTS historical_data (
  id SERIAL PRIMARY KEY,
  symbol VARCHAR(50) NOT NULL,
  metric VARCHAR(100) NOT NULL,
  timestamp TIMESTAMP NOT NULL,
  value DECIMAL NOT NULL
);

-- Indexes
CREATE INDEX idx_historical_data_symbol ON historical_data(symbol);
CREATE INDEX idx_events_event_type ON events(event_type);
