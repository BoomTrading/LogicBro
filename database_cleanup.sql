-- LogicBro Database Cleanup Script
-- This script will drop all tables and recreate them in the correct order

USE logic_bro;

-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Drop all tables
DROP TABLE IF EXISTS audio_analysis;
DROP TABLE IF EXISTS analysis_results;
DROP TABLE IF EXISTS audio_files;
DROP TABLE IF EXISTS projects;
DROP TABLE IF EXISTS users;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;
