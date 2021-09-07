#!/bin/bash

# NEED to change by project
#SUBMIT_DIR=/engenharia-de-dados/sparkelo/submit-job/crm/clientes/crm/
SUBMIT_DIR=/home/92551995/framework_ingestion/submit-job/ranqueamento/

cd $SUBMIT_DIR

# Typesafe Config
# CONF_DIR=$SUBMIT_DIR
# APP_CONF=_04_upd_fin_grupo_controle_clientes.conf

# Defines the project name
PROJECT_NAME="ranqueamento.ranqueamento"

# Defines the execution mode (always run the project in cluster mode, and for testing choose local mode)
#PROJECT_MASTER="yarn"
#PROJECT_DEPLOY_MODE="cluster"
PROJECT_MASTER="local"
PROJECT_DEPLOY_MODE="client"

# Defines the project directory
#PROJECT_DIR="/engenharia-de-dados/sparkelo/"
PROJECT_DIR="/home/92551995/framework_ingestion/"

# Defines the KeyTabs directory
#KEYTAB_DIR="/etc/security/keytabs/"

# Defines the files needed for execution
#AUTHENTICATION_FILE=${PROJECT_DIR}"authentication.jaas"
COMPILED_PROJECT_FILE=${PROJECT_DIR}"framework_ingestion.jar"
#KEYTAB_FILE=${KEYTAB_DIR}"hdfs.service.keytab"

# Defines the main class
MAIN_CLASS="manager."${PROJECT_NAME}

# Defines the parameters of project
#DATABASE_NAME="" #db_raw_salesforce
#TABLE_NAME="" #casecomment
#PARTITION_BY="" #id
#PARTITION_FIELDS="" #id
#ORDENATION_FIELDS="" #reference_date
#ORDENATION_TYPE="" #desc (keep the most recent record in the table)

# Changing the permission on the project folder (by always
# executing it, we prevent the project files from being
# vulnerable to changes and we also avoid manual errors)
#chmod 755 -R ${PROJECT_DIR}

# Running the application with the above parameters
spark2-submit --class ${MAIN_CLASS} \
--verbose \
--name ${PROJECT_NAME} \
--master ${PROJECT_MASTER} --deploy-mode ${PROJECT_DEPLOY_MODE} \
${COMPILED_PROJECT_FILE}