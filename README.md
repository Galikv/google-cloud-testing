# Google Cloud Storage CLI Testing

## Overview
This project provides an automated testing infrastructure for the **Google Cloud Storage CLI (gcloud storage)**. The goal is to validate the execution of four CLI commands, including `sign-url`, ensuring signed URLs are accessible and do not trigger phishing warnings.

The test framework is designed for **speed, scalability, and extensibility**, allowing future test additions while ensuring independent test execution.

## Features
✅ Automated tests for four `gcloud storage` commands (cp, rm, rsync, signUrl)
✅ Modular test framework for easy expansion  
✅ Ensures signed URLs do not trigger phishing warnings  
✅ Independent test execution to prevent interference  
✅ Test outcome reporting system  

## Commands Tested
- `gcloud storage sign-url` – Ensures signed URLs are accessible and safe  
- `gcloud storage cp` – Validates file copying between local and cloud storage  
- `gcloud storage rm` – Tests file deletion functionality  
- `gcloud storage rsync` – Verifies synchronization between local and cloud storage  

## Prerequisites
Before running the tests, ensure you have the following installed:

- **Google Cloud SDK** – [Install Guide](https://cloud.google.com/sdk/docs/install)  
- **Java 11+** – [Download Java](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html)  
- **Apache Maven** – [Install Guide](https://maven.apache.org/install.html)  
- **TestNG** – [TestNG Documentation](https://testng.org/doc/)
- 
