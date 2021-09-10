#!/bin/bash

# NEED to change by project
SUBMIT_DIR=/data/ingestion/blip/framework_ingestion/submit-job/ranqueamento/

cd $SUBMIT_DIR

# Defines the project name
PROJECT_NAME="ranqueamento.ranqueamento"

# Defines the execution mode (always run the project in cluster mode, and for testing choose local mode)
#PROJECT_MASTER="yarn"
#PROJECT_DEPLOY_MODE="cluster"
PROJECT_MASTER="local"
PROJECT_DEPLOY_MODE="client"

# Defines the project directory
PROJECT_DIR="/data/ingestion/blip/framework_ingestion/"

# Defines the files needed for execution
COMPILED_PROJECT_FILE=${PROJECT_DIR}"framework_ingestion.jar"

# Defines the main class
MAIN_CLASS="manager."${PROJECT_NAME}

# Defines the parameters of project
DATABASE_NAME="db_raw_salesforce" #db_raw_salesforce
TABLE_NAME="casecomment" #casecomment
PARTITION_TABLE="id" #id
PARTITION_FIELDS="id,cpf" #id
SORTING_FIELDS="reference_date" #reference_date
SORTING_TYPE="desc" #desc (keep the most recent record in the table)

# Changing the permission on the project folder (by always
# executing it, we prevent the project files from being
# vulnerable to changes and we also avoid manual errors)
chmod 755 -R ${PROJECT_DIR}

# Running the application with the above parameters
spark-submit --class ${MAIN_CLASS} \
--verbose \
--name ${PROJECT_NAME} \
--master ${PROJECT_MASTER} --deploy-mode ${PROJECT_DEPLOY_MODE} \
${COMPILED_PROJECT_FILE}
DATABASE_NAME=${DATABASE_NAME}
TABLE_NAME=${TABLE_NAME}
PARTITION_TABLE=${PARTITION_TABLE}
PARTITION_FIELDS=${PARTITION_FIELDS}
SORTING_FIELDS=${SORTING_FIELDS}
SORTING_TYPE=${SORTING_TYPE}