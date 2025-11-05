-- Scouter Data Schema
-- Database CREATE statements for all Scouter data types

-- ===================================
-- Pack Tables
-- ===================================

-- XLogPack: Transaction log information
CREATE TABLE scouter_xlog (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    end_time BIGINT NOT NULL,
    obj_hash INT NOT NULL,
    service INT NOT NULL,
    txid BIGINT NOT NULL,
    thread_name_hash INT,
    caller BIGINT,
    gxid BIGINT,
    elapsed INT,
    error INT,
    cpu INT,
    sql_count INT,
    sql_time INT,
    ipaddr VARBINARY(16),
    kbytes INT,
    status INT,
    userid BIGINT,
    user_agent INT,
    referer INT,
    `group` INT,
    apicall_count INT,
    apicall_time INT,
    country_code VARCHAR(10),
    city INT,
    x_type TINYINT,
    login INT,
    `desc` INT,
    web_hash INT,
    web_time INT,
    has_dump TINYINT,
    text1 VARCHAR(255),
    text2 VARCHAR(255),
    queuing_host_hash INT,
    queuing_time INT,
    queuing_2nd_host_hash INT,
    queuing_2nd_time INT,
    text3 VARCHAR(255),
    text4 VARCHAR(255),
    text5 VARCHAR(255),
    profile_count INT,
    b3_mode BOOLEAN,
    profile_size INT,
    discard_type TINYINT,
    ignore_global_consequent_sampling BOOLEAN,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_txid (txid),
    INDEX idx_end_time (end_time),
    INDEX idx_obj_hash (obj_hash),
    INDEX idx_service (service)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- AlertPack: Alert information
CREATE TABLE scouter_alert (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time BIGINT NOT NULL,
    obj_type VARCHAR(50),
    obj_hash INT NOT NULL,
    level TINYINT NOT NULL,
    title VARCHAR(255),
    message TEXT,
    tags TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_time (time),
    INDEX idx_obj_hash (obj_hash),
    INDEX idx_level (level)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- PerfCounterPack: Performance counter data
CREATE TABLE scouter_perf_counter (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time BIGINT NOT NULL,
    obj_name VARCHAR(255),
    timetype TINYINT,
    data TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_time (time),
    INDEX idx_obj_name (obj_name),
    INDEX idx_timetype (timetype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ObjectPack: Object information
CREATE TABLE scouter_object (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    obj_type VARCHAR(50),
    obj_hash INT NOT NULL UNIQUE,
    obj_name VARCHAR(255),
    address VARCHAR(255),
    version VARCHAR(100),
    alive BOOLEAN,
    wakeup BIGINT,
    tags TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_obj_type (obj_type),
    INDEX idx_obj_name (obj_name),
    INDEX idx_alive (alive)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SummaryPack: Summary data
CREATE TABLE scouter_summary (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time BIGINT NOT NULL,
    obj_hash INT NOT NULL,
    obj_type VARCHAR(50),
    stype TINYINT,
    `table` TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_time (time),
    INDEX idx_obj_hash (obj_hash),
    INDEX idx_stype (stype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- TextPack: Text dictionary data
CREATE TABLE scouter_text (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    xtype VARCHAR(50),
    hash INT NOT NULL,
    text TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_xtype_hash (xtype, hash),
    INDEX idx_xtype (xtype)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- XLogProfilePack: Profile data
CREATE TABLE scouter_xlog_profile (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    time BIGINT NOT NULL,
    obj_hash INT NOT NULL,
    service INT NOT NULL,
    txid BIGINT NOT NULL,
    elapsed INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_txid (txid),
    INDEX idx_time (time),
    INDEX idx_obj_hash (obj_hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================
-- Profile Step Tables
-- ===================================

-- Base profile steps table
CREATE TABLE scouter_profile_step (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    profile_id BIGINT NOT NULL,
    step_index INT NOT NULL,
    step_type TINYINT NOT NULL,
    step_type_name VARCHAR(50),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (profile_id) REFERENCES scouter_xlog_profile(id) ON DELETE CASCADE,
    INDEX idx_profile_id (profile_id),
    INDEX idx_step_type (step_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- MethodStep: Method execution steps
CREATE TABLE scouter_step_method (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_id BIGINT NOT NULL,
    hash INT,
    elapsed INT,
    cputime INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES scouter_profile_step(id) ON DELETE CASCADE,
    INDEX idx_step_id (step_id),
    INDEX idx_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- SqlStep: SQL execution steps
CREATE TABLE scouter_step_sql (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_id BIGINT NOT NULL,
    hash INT,
    elapsed INT,
    cputime INT,
    param TEXT,
    error INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES scouter_profile_step(id) ON DELETE CASCADE,
    INDEX idx_step_id (step_id),
    INDEX idx_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ApiCallStep: API call steps
CREATE TABLE scouter_step_apicall (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_id BIGINT NOT NULL,
    txid BIGINT,
    hash INT,
    elapsed INT,
    cputime INT,
    error INT,
    address VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES scouter_profile_step(id) ON DELETE CASCADE,
    INDEX idx_step_id (step_id),
    INDEX idx_txid (txid),
    INDEX idx_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- MessageStep: Message steps
CREATE TABLE scouter_step_message (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    step_id BIGINT NOT NULL,
    hash INT,
    time INT,
    value INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (step_id) REFERENCES scouter_profile_step(id) ON DELETE CASCADE,
    INDEX idx_step_id (step_id),
    INDEX idx_hash (hash)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ===================================
-- Summary/Aggregate Tables
-- ===================================

-- Hourly aggregation of XLog data
CREATE TABLE scouter_xlog_hourly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hour_time BIGINT NOT NULL,
    obj_hash INT NOT NULL,
    service INT NOT NULL,
    txn_count INT DEFAULT 0,
    error_count INT DEFAULT 0,
    total_elapsed BIGINT DEFAULT 0,
    avg_elapsed INT DEFAULT 0,
    max_elapsed INT DEFAULT 0,
    min_elapsed INT DEFAULT 0,
    total_cpu BIGINT DEFAULT 0,
    total_sql_count INT DEFAULT 0,
    total_sql_time BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_hour_obj_service (hour_time, obj_hash, service),
    INDEX idx_hour_time (hour_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Daily aggregation of XLog data
CREATE TABLE scouter_xlog_daily (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    day_time BIGINT NOT NULL,
    obj_hash INT NOT NULL,
    service INT NOT NULL,
    txn_count INT DEFAULT 0,
    error_count INT DEFAULT 0,
    total_elapsed BIGINT DEFAULT 0,
    avg_elapsed INT DEFAULT 0,
    max_elapsed INT DEFAULT 0,
    min_elapsed INT DEFAULT 0,
    total_cpu BIGINT DEFAULT 0,
    total_sql_count INT DEFAULT 0,
    total_sql_time BIGINT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_day_obj_service (day_time, obj_hash, service),
    INDEX idx_day_time (day_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Performance counter hourly aggregation
CREATE TABLE scouter_perf_counter_hourly (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    hour_time BIGINT NOT NULL,
    obj_name VARCHAR(255),
    counter_name VARCHAR(100),
    avg_value DECIMAL(20,4),
    max_value DECIMAL(20,4),
    min_value DECIMAL(20,4),
    count INT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_hour_obj_counter (hour_time, obj_name, counter_name),
    INDEX idx_hour_time (hour_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
