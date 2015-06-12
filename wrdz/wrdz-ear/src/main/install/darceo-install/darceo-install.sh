#!/bin/bash
#set -e
# Setup DARCEO_INSTALL_HOME
if [ "x$DARCEO_INSTALL_HOME" = "x" ]; then
    DARCEO_INSTALL_HOME=`cd "$DIRNAME"; pwd`
fi

#------------------------------------------------------------------------------------------------------------
#check requirements
#------------------------------------------------------------------------------------------------------------
printf "%s \n\n" "---CHECKING REQUIREMENTS---"
CONTINUE=true
BAD_VERSION=false
printf "Checking Java...   "
JAVA_VERSION=`java -version 2>&1`
echo $JAVA_VERSION
if [[ ! "${JAVA_VERSION}" =~ "1.7" ]]; then
	BAD_VERSION=true
	echo "Bad version! JDK 7 is necessary.";
else 
	JCE=`java -jar ${DARCEO_INSTALL_HOME}/lib/jce/unlimitedjce.jar`
	if [[ ! "${JCE}" =~ "TRUE" ]]; then
  		BAD_VERSION=true
  		echo "JDK without JCE Unlimited Strength.";
	else 
  		printf "OK \n"
  	fi
fi

printf "Checking Glassfish (GLASSFISH_HOME variable)...   "
if [ "x$GLASSFISH_HOME" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	GLASSFISH_VERSION=`${GLASSFISH_HOME}/bin/asadmin --user --passwordfile version 2>&1`
	if [[ "${GLASSFISH_VERSION}" =~ "3.2.1" ]]; then
		BAD_VERSION=true
  		echo "Bad version! Glassfish 3.2.1 is necessary.";
	else 
  		printf "OK \n"
	fi
fi

printf "Checking fits (FITS_HOME variable)...   "
if [ "x$FITS_HOME" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	printf "OK \n"
fi

printf "Checking owlim-lite (OWLIM_HOME and OWLIM_DATA variables)...   "
if [ "x$OWLIM_HOME" = "x" ] && [ "x$OWLIM_DATA" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	OWLIM_VERSION=`ls ${OWLIM_HOME}/lib/`
	if [[ ! "${OWLIM_VERSION}" =~ "5.2" ]]; then
		BAD_VERSION=true
  		echo "Bad version! OWLIM (v. 5.2.x) is necessary.";
	else 
  		printf "OK \n"
	fi
fi

DPKG_INSTALLED=`command -v dpkg`
RPM_INSTALLED=`command -v rpm`

IS_DPKG=true
IS_RPM=true
printf "Checking dpkg ...   "
if [ "x$DPKG_INSTALLED" = "x" ]; then
	printf "NOT FOUND \n"
	IS_DPKG=false
else
	printf "OK \n"
fi
printf "Checking rpm ...   "
if [ "x$RPM_INSTALLED" = "x" ]; then
	printf "NOT FOUND \n"
	IS_RPM=false
else
	printf "OK \n"
fi

if [ $IS_DPKG = true ] || [ $IS_RPM = true ] 
then
 echo "Package for checking requirements (dpkg or rpm) is installed."
else
 echo "One package rpm or dpkg should be installed. Installation aborted."
 exit 1
fi

DPKG_CMD="dpkg -l "
if [ $IS_RPM = true ] 
then
  DPKG_CMD="rpm -qa "
fi

PGSQLSERVER=`${DPKG_CMD} | grep postgresql-server 2>&1`
if [ "x$PGSQLSERVER" = "x" ]; then
	PGSQLSERVER=`${DPKG_CMD} | grep postgresql 2>&1`
fi
PGSQLCLIENT=`${DPKG_CMD} | grep postgresql-client 2>&1`
if [ "x$PGSQLCLIENT" = "x" ]; then
	PGSQLCLIENT=`${DPKG_CMD} | grep postgresql 2>&1`
fi
WGETTOOL=`${DPKG_CMD} | grep "wget"`
PYTHONLANG=`${DPKG_CMD} | grep python2.7`
if [ "x$PYTHONLANG" = "x" ]; then
	PYTHONLANG=`${DPKG_CMD} | grep python-2.7`
fi

printf "Checking postgresql ...   "
if [ "x$PGSQLSERVER" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	printf "OK \n"
fi

printf "Checking postgresql-client ...   "
if [ "x$PGSQLCLIENT" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	printf "OK \n"
fi

printf "Checking wget tool ...   "
if [ "x$WGETTOOL" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	printf "OK \n"
fi

printf "Checking python ...   "
if [ "x$PYTHONLANG" = "x" ]; then
	printf "NOT FOUND \n"
	CONTINUE=false
else
	printf "OK \n"
fi

printf "%s \n\n" "---CHECKING REQUIREMENTS FINISHED---"

if [ $CONTINUE == false ]; then
	printf "REQUIREMENTS NOT FULLFILLED \n"
	exit 2
fi

if [ $BAD_VERSION == true ]; then
	printf "One or more tools have bad version - would you like to continue? (if 'yes' press ENTER, else type something) \n"
	read TMP
        if [ "x$TMP" != "x" ]; then
		exit 2
	fi
fi

#------------------------------------------------------------------------------------------------------------
#read base variables
#------------------------------------------------------------------------------------------------------------
source ./readVariables.sh vars.csv "#repositories"
echo "Github location: ${GITHUB_LOC}"
SQL_FILES="wrdz/wrdz-common/entity/src/main/config/common-SQL-CREATE-ALL.sql
wrdz/wrdz-zmd/entity/src/main/config/zmd-SQL-CREATE-ALL.sql
wrdz/wrdz-ru/entity/src/main/config/ru-SQL-CREATE-ALL.sql
wrdz/wrdz-mdz/entity/src/main/config/mdz-SQL-CREATE-ALL.sql
wrdz/wrdz-ms/entity/src/main/config/ms-SQL-CREATE-ALL.sql
wrdz/wrdz-zmkd/entity/src/main/config/zmkd-SQL-CREATE-ALL.sql
wrdz/wrdz-zu/entity/src/main/config/zu-SQL-CREATE-ALL.sql
dsa/sftp/src/main/config/dsa-sftp-SQL-CREATE-ALL.sql"


WRDZ_CONFS="wrdz/wrdz-ear/src/main/config/log4j.xml.template
wrdz/wrdz-ear/src/main/config/wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/zmd-wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/mdz-wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/zmkd-wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/zdt-wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/ms-wrdz-config.xml.template
wrdz/wrdz-ear/src/main/config/zu-wrdz-config.xml.template"

WRDZ_NAMESPACES="wrdz/wrdz-ear/src/main/config/wrdz-namespaces.xml"

#full path to the 'glassfish-wrdz-config.password' file
export PASSWORDFILE=${DARCEO_INSTALL_HOME}/lib/glassfish/glassfish-wrdz-config.password
export PATH=${PATH}:${GLASSFISH_HOME}/../mq/bin/
export PATH=${PATH}:${GLASSFISH_HOME}/bin/

#------------------------------------------------------------------------------------------------------------
#installation process
#------------------------------------------------------------------------------------------------------------
echo "# installation process -------- "
set -e

echo " -------- DATABASE SETTING -------- "
source ./readVariables.sh vars.csv "#postgres"

export PGPASSWORD=${PG_ADMIN_PASS}
set +e
echo "Create postgresDB..."
createdb  -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -e ${PG_DARCEO_DBNAME}
echo "done."

echo "Existing tablespaces: "
psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "SELECT spcname, pg_tablespace_location(oid) FROM pg_tablespace;"
PG_WRDZ_TSPACENAME=""
PG_WRDZ_TSPACELOC=""
printf "\n %s" "If you would like to use one of existing tablespaces plese type tablespace name (or press ENTER to skip and create new tablespace): "
read TMP
if [ "x$TMP" != "x" ]; then
	PG_WRDZ_TSPACENAME=$TMP
fi

PG_WRDZ_TSPACELOC_EXISTS=false

if [ "x$PG_WRDZ_TSPACENAME" != "x" ]; then
        TMP=`psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "select pg_tablespace_location(oid) from pg_tablespace where spcname like '${PG_WRDZ_TSPACENAME}'"`
	PG_WRDZ_TSPACELOC=$TMP
	printf "%s \n" "Selected namespace ${PG_WRDZ_TSPACENAME} located in ${PG_WRDZ_TSPACELOC}. "
	PG_WRDZ_TSPACELOC_EXISTS=true
else 
	printf "New tablespace name: "
	read TMP
	if [ "x$TMP" != "x" ]; then
		PG_WRDZ_TSPACENAME=$TMP
	fi
	printf "New tablespace location: "
	read TMP
	if [ "x$TMP" != "x" ]; then
		PG_WRDZ_TSPACELOC=$TMP
	fi	
fi

if [ "x$PG_WRDZ_TSPACELOC" != "x" ]; then
	echo "Set tablespace..."
	#mkdir -p ${PG_WRDZ_TSPACELOC}
        if [ ! $PG_WRDZ_TSPACELOC_EXISTS ]; then
		echo "Change owner for tablespace directory, please type su password..."
		sudo chown postgres.postgres ${PG_WRDZ_TSPACELOC}
		psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "CREATE TABLESPACE "${PG_WRDZ_TSPACENAME}" LOCATION '"${PG_WRDZ_TSPACELOC}"'"
	fi
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -c "ALTER DATABASE "${PG_DARCEO_DBNAME}" SET TABLESPACE "${PG_WRDZ_TSPACENAME}""
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -c "GRANT CREATE ON TABLESPACE "${PG_WRDZ_TSPACENAME}" TO PUBLIC;"
	echo "done."
fi

printf "Set metadata_store tablespace location"
source ./readVariables.sh vars.csv "#metadatastore"
psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "CREATE TABLESPACE metadata_store LOCATION '""${PG_WRDZ_METASTORELOC}""'"
psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "GRANT CREATE ON TABLESPACE metadata_store TO PUBLIC;"
printf "%s\n" "done."

set -e
printf "%s\n" "Edit postgresql.conf..."
PG_CONF_FILE=`psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -c "select setting from pg_settings where name = 'config_file';" | grep postgresql.conf`
echo "PG_CONF_FILE=[${PG_CONF_FILE}]"
lineNumber=`sudo cat ${PG_CONF_FILE} | grep -n "max_connections =" | cut -d: -f1`
if [ "x$lineNumber" != "x" ]; then
	sudo sed -i "${lineNumber}s/.*/max_connections = 100/" ${PG_CONF_FILE}
fi
lineNumber=`sudo cat ${PG_CONF_FILE} | grep -n "max_prepared_transactions =" | cut -d: -f1`
if [ "x$lineNumber" != "x" ]; then
	sudo sed -i "${lineNumber}s/.*/max_prepared_transactions = 100/" ${PG_CONF_FILE}
fi
echo "file modified."

set +e
echo "Create role..."
psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "CREATE ROLE "${PG_DARCEO_USER}" WITH LOGIN PASSWORD '"${PG_DARCEO_PASS}"'"
echo "done."

echo "Change db owner..."
psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_ADMIN_USER} -d ${PG_DARCEO_DBNAME} -c "ALTER DATABASE "${PG_DARCEO_DBNAME}" OWNER TO "${PG_DARCEO_USER}""
echo "done."

set -e
echo "Downloading sql files..."
SQL_LOC_FILES=""
for file in $SQL_FILES
do
	filename=$(basename "/${file}")
	echo "get "${GITHUB_LOC}/${file}" ..." 
	wget ${GITHUB_LOC}/${file}
	SQL_LOC_FILES=`printf "%s \n" "${SQL_LOC_FILES}${filename}"`
done;
echo "finshed."

export PGPASSWORD=${PG_DARCEO_PASS}
set +e
echo "Execute sql commands..."
for file in $SQL_LOC_FILES
do
	echo ${file}
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_DARCEO_USER} -d ${PG_DARCEO_DBNAME} -f ${file}
	rm ${file}
done;
echo "finshed."
echo " -------- DATABASE SETTING FINISHED -------- "

set -e
echo " -------- GLASSFISH SETTING -------- "
source ./readVariables.sh vars.csv "#glassfish"

echo "Create temporary password file..."
NLINE=$'\n'
PASSFILE_CONTENT="imq.imqcmd.password=admin${NLINE}AS_ADMIN_PASSWORD=${ADMIN_PASS}"
echo "$PASSFILE_CONTENT" > $PASSWORDFILE
echo "done."

set +e
echo "Create domain..."
asadmin delete-domain domain1 --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-domain ${DOMAIN} --user ${ADMIN} --passwordfile ${PASSWORDFILE}
echo "done."

set -e
echo "Copy postgres jdbc driver..."
cp lib/glassfish/postgresql-9.0-801.jdbc4.jar ${GLASSFISH_HOME}/domains/${DOMAIN}/lib/ext
echo "done."

set +e
echo "Remove glassfish modules..."
rm ${GLASSFISH_HOME}/modules/org.eclipse.persistence.antlr.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.asm.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.core.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.jpa.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.jpa.modelgen.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.moxy.jar ${GLASSFISH_HOME}/modules/org.eclipse.persistence.oracle.jar 
echo "done."

echo "Copy webcore.jar module for glassfish..."
cp lib/glassfish/web-core.jar ${GLASSFISH_HOME}/modules
echo "done."

echo "Copy OSGI modules to glassfish..."
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.antlr_3.2.0.v201206041011.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.asm_3.3.1.v201206041142.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.core_2.4.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.jpa.jpql_2.0.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.jpa.modelgen_2.4.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.jpa_2.4.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.moxy_2.4.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
cp lib/glassfish/eclipselink-plugins-2.4.0.v20120608-r11652/org.eclipse.persistence.oracle_2.4.0.v20120608-r11652.jar ${GLASSFISH_HOME}/modules
echo "done."

echo "Running domain..."
asadmin start-domain ${DOMAIN} --user ${ADMIN} --passwordfile ${PASSWORDFILE}
echo "done."

echo "Setting JVM options..."
asadmin create-jvm-options '-Dlog4j.configuration=file\:///${com.sun.aas.instanceRoot}/config/log4j.xml' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-jvm-options '-Dcom.sun.jersey.server.impl.cdi.lookupExtensionInBeanManager=true' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-jvm-options '-Dcom.sun.grizzly.util.buf.UDecoder.ALLOW_ENCODED_SLASH=true' --user ${ADMIN} --passwordfile ${PASSWORDFILE}

asadmin delete-jvm-options '-XX\:PermSize=64m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-jvm-options '-XX\:PermSize=128m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin delete-jvm-options '-XX\:MaxPermSize=192m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-jvm-options '-XX\:MaxPermSize=512m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin delete-jvm-options '-Xmx512m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
asadmin create-jvm-options '-Xmx1536m' --user ${ADMIN} --passwordfile ${PASSWORDFILE}
echo "done."

echo "Setting logging options..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set-log-attributes --target server com.sun.enterprise.server.logging.GFFileHandler.rotationTimelimitInMinutes=480
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set-log-attributes --target server com.sun.enterprise.server.logging.GFFileHandler.rotationLimitInBytes=0
echo "done."

echo "Setting http listeners timeouts..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set server-config.network-config.protocols.protocol.http-listener-1.http.request-timeout-seconds=43200
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set server-config.network-config.protocols.protocol.http-listener-1.http.timeout-seconds=43200
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set server-config.network-config.protocols.protocol.http-listener-2.http.request-timeout-seconds=43200  
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set server-config.network-config.protocols.protocol.http-listener-2.http.timeout-seconds=43200
echo "done."

echo "Create jdbc connection pool..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jdbc-connection-pool --datasourceclassname org.postgresql.xa.PGXADataSource --restype javax.sql.XADataSource --property user=${PG_DARCEO_USER}:password=${PG_DARCEO_PASS}:serverName=${PG_HOST}:portNumber=${PG_PORT}:databaseName=${PG_DARCEO_DBNAME} PostgreSQLXAPool
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jdbc-resource --connectionpoolid PostgreSQLXAPool jdbc/rdb

asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} delete-jdbc-connection-pool --cascade=true DerbyPool
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} delete-jdbc-connection-pool --cascade=true __TimerPool

asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jdbc-connection-pool --datasourceclassname org.postgresql.ds.PGSimpleDataSource --restype javax.sql.DataSource --property user=${PG_DARCEO_USER}:password=${PG_DARCEO_PASS}:serverName=${PG_HOST}:portNumber=${PG_PORT}:databaseName=${PG_DARCEO_DBNAME} TimerPoolPostgres
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jdbc-resource --connectionpoolid TimerPoolPostgres jdbc/__TimerPool
echo "done."

echo "Adding connection factories..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.ConnectionFactory --property transaction-support=XATransaction jms/arp
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.ConnectionFactory --property transaction-support=XATransaction jms/info
echo "done."

set -e
source ./readVariables.sh vars.csv "#mailing"
set +e
echo "Adding mail session..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-javamail-resource --mailhost ${MAIL_HOST} --mailuser ${MAIL_USER} --fromaddress ${MAIL_FROM_ADDRESS}  --property mail.smtp.auth=${MAIL_AUTH}:mail.smtp.password=${MAIL_PASS}:mail.smtp.user=${MAIL_USER}:mail.from.name=${MAIL_FROM_NAME}:mail.from.address=${MAIL_FROM_ADDRESS} mail/ms
echo "done."

echo "Set JMS undelivered messages queue logging..."
imqcmd update bkr -o imq.destination.logDeadMsgs=true -f -u ${ADMIN} -passfile ${PASSWORDFILE}
echo "done."

echo "Adding physical destination points for JMS queues and topics..."
imqcmd create dst -t q -n arp.zmd.object -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o maxNumActiveConsumers=-1 -o maxNumBackupConsumers=0 -o consumerFlowLimit=1000 -o localDeliveryPreferred=true -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
imqcmd create dst -t t -n info.zmd.object -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o consumerFlowLimit=1000 -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
imqcmd create dst -t q -n info.mdz.format -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o maxNumActiveConsumers=-1 -o maxNumBackupConsumers=0 -o consumerFlowLimit=1000 -o localDeliveryPreferred=true -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
imqcmd create dst -t q -n info.mdz.object -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o maxNumActiveConsumers=-1 -o maxNumBackupConsumers=0 -o consumerFlowLimit=1000 -o localDeliveryPreferred=true -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
imqcmd create dst -t t -n info.mdz.plugin -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o consumerFlowLimit=1000 -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
imqcmd create dst -t q -n info.zu.certificate -o maxNumMsgs=100000 -o maxBytesPerMsg=1m -o maxTotalMsgBytes=1000m -o limitBehavior=REJECT_NEWEST -o maxNumProducers=100 -o maxNumActiveConsumers=-1 -o maxNumBackupConsumers=0 -o consumerFlowLimit=1000 -o localDeliveryPreferred=true -o useDMQ=true -o validateXMLSchemaEnabled=false -f -u ${ADMIN} -passfile ${PASSWORDFILE}
echo "done."

echo "Restart domain..."
asadmin restart-domain ${DOMAIN}
echo "done."

echo "Creating JMS resources..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Queue --property Name=arp.zmd.object queue/arp/zmd-object
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Topic --property Name=info.zmd.object topic/info/zmd-object
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Queue --property Name=info.mdz.format queue/info/mdz-format
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Queue --property Name=info.mdz.object queue/info/mdz-object
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Topic --property Name=info.mdz.plugin topic/info/mdz-plugin
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-jms-resource --restype javax.jms.Queue --property Name=info.zu.certificate queue/info/zu-certificate
echo "done."

set -e
source ./readVariables.sh vars.csv "#dsaconf"
set +e
echo "Configure dsa..."
if $USE_DSA_SFTP; then
	set -e
        source ./readVariables.sh vars.csv "#dsasftp"
	echo ${DSA_PUBLICKEY}
	DSA_SFTP_FILENAME=${DSA_SFTP_NAME}-${DSA_SFTP_VERSION}.rar
	DSA_SFTP_LOC=${REPO_LOC}/${DSA_SFTP_NAMESPC}/${DSA_SFTP_NAME}/${DSA_SFTP_VERSION}/${DSA_SFTP_FILENAME}
	wget ${DSA_SFTP_LOC}
	set +e
	JDBC_DATABASE="jdbc:postgresql://"${PG_HOST}":"${PG_PORT}"/"${PG_DARCEO_DBNAME}
	cd ${DARCEO_INSTALL_HOME}/lib/dsa/
	java -jar ${DARCEO_INSTALL_HOME}/lib/dsa/kmdCredential.jar USERS ${DSA_USER} ${DSA_USER_PRIVATEKEYFILE} ${DSA_USER_PUBLICKEYFILE} ${JDBC_DATABASE} ${PG_DARCEO_USER} ${PG_DARCEO_PASS}
	DSA_PUBLICKEY=`cat $DSA_PUBLICKEYFILE`
	cd ${DARCEO_INSTALL_HOME}
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} deploy --name dsa ${DSA_SFTP_FILENAME}
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-connection-pool --connectiondefinition pl.psnc.synat.dsa.DataStorageConnectionFactory --raname dsa --property host=${DSA_HOST}:port=${DSA_PORT}:publicKeyType=${DSA_PUBLICKEYTYPE}:publicKey="'"${DSA_PUBLICKEY}"'" --lazyconnectionenlistment=true dsa-pool
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-resource --poolname dsa-pool jca/dsa
	rm ${DSA_SFTP_FILENAME}
else
	set -e
	source ./readVariables.sh vars.csv "#dsafs"
	DSA_FS_FILENAME=${DSA_FS_NAME}-${DSA_FS_VERSION}.rar
	DSA_FS_LOC=${REPO_LOC}/${DSA_FS_NAMESPC}/${DSA_FS_NAME}/${DSA_FS_VERSION}/${DSA_FS_FILENAME}
	wget ${DSA_FS_LOC}
	set +e
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} deploy --name dsa ${DSA_FS_FILENAME}
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-connection-pool --connectiondefinition pl.psnc.synat.dsa.DataStorageConnectionFactory --raname dsa --property roots=${DSA_FS_ROOTS}:redundancy=${DSA_FS_REDUNDANCY} --lazyconnectionenlistment=true dsa-pool
	asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-resource --poolname dsa-pool jca/dsa
	rm ${DSA_FS_FILENAME}
fi
echo "done."

#sra
echo "Configure owlim lite..."
OWLIM_CONFIG_DIR="wrdz/wrdz-ru/common/src/main/owlim-config"
OWLIM_DST=${OWLIM_HOME}/sesame_owlim/openrdf-console/bin
set -e
wget ${GITHUB_LOC}/${OWLIM_CONFIG_DIR}/owl2-rl-reduced-darceo.pie
set +e
cp owl2-rl-reduced-darceo.pie ${DARCEO_INSTALL_HOME}/lib/owlim/
rm owl2-rl-reduced-darceo.pie
set -e
wget ${GITHUB_LOC}/${OWLIM_CONFIG_DIR}/darceo-owlim-lite.ttl
set +e
cp darceo-owlim-lite.ttl ${DARCEO_INSTALL_HOME}/lib/owlim/
rm darceo-owlim-lite.ttl
set -e
wget ${GITHUB_LOC}/${OWLIM_CONFIG_DIR}/darceo-working-owlim-lite.ttl
set +e
cp darceo-working-owlim-lite.ttl ${DARCEO_INSTALL_HOME}/lib/owlim/
rm darceo-working-owlim-lite.ttl
echo "OWLIM_HOME=${OWLIM_HOME}"
cd ${OWLIM_HOME}/sesame_owlim/
mkdir openrdf-console
unzip openrdf-console.zip -d ./openrdf-console
cp ${DARCEO_INSTALL_HOME}/lib/owlim/createOwlim.txt ${OWLIM_DST}/createOwlim.txt
cp ${DARCEO_INSTALL_HOME}/lib/owlim/owl2-rl-reduced-darceo.pie ${OWLIM_DST}/
chmod +x ${OWLIM_DST}/console.sh
cd ${OWLIM_DST}
mkdir -p ${OWLIM_DATA}/openrdf-sesame-console/templates/
cp ${DARCEO_INSTALL_HOME}/lib/owlim/darceo-owlim-lite.ttl ${OWLIM_DATA}/openrdf-sesame-console/templates/
cp ${DARCEO_INSTALL_HOME}/lib/owlim/darceo-working-owlim-lite.ttl ${OWLIM_DATA}/openrdf-sesame-console/templates/
NEW_LINE='JAVA_OPT="-mx512m -Dinfo.aduna.platform.appdata.basedir=${OWLIM_DATA}" '
LINE_INDEX=`grep -n -m 1 "JAVA_OPT" console.sh | cut -d: -f1`
sed -i "${LINE_INDEX}s/.*/${NEW_LINE}/" console.sh
echo "OWLIM_DATA=${OWLIM_DATA}"
set -e
cat createOwlim.txt | ./console.sh -d $OWLIM_DATA
set +e
cd ${DARCEO_INSTALL_HOME}
echo "done."

set -e
source ./readVariables.sh vars.csv "#sra"
set +e
SRA_OWLIM_FILENAME="${SRA_OWLIM_NAME}-${SRA_VERSION}.rar"
SRA_OWLIM_LOC=${REPO_LOC}/${SRA_NAMESPC}/${SRA_OWLIM_NAME}/${SRA_VERSION}/${SRA_OWLIM_FILENAME}
echo "Configure sra..."
echo "Copy owlim rules... "
cp lib/owlim/owl2-rl-reduced-darceo.pie ${GLASSFISH_HOME}/domains/${DOMAIN}/config/owl2-rl-reduced-darceo.pie
echo "Download sra-owlim..."
set -e
wget ${SRA_OWLIM_LOC}
set +e
echo "Deploy sra-owlim... "
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} deploy --name sra ${SRA_OWLIM_FILENAME}
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-connection-pool --connectiondefinition pl.psnc.synat.sra.SemanticRepositoryConnectionFactory --raname sra --lazyconnectionenlistment=true sra-pool
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-connector-resource --poolname sra-pool jca/sra
rm ${SRA_OWLIM_FILENAME}
echo "done"

#realm
echo "Stop domain..."
asadmin stop-domain ${DOMAIN}
echo "done."

echo "Copy commons-lang-2.4.jar module for glassfish..."
cp lib/glassfish/commons-lang-2.4.jar ${GLASSFISH_HOME}/domains/${DOMAIN}/lib/
echo "done."

echo "Copy commons-codec-1.6.jar module for glassfish..."
cp lib/glassfish/commons-codec-1.6.jar ${GLASSFISH_HOME}/domains/${DOMAIN}/lib/
echo "done."
echo "Preparing realm..."
set -e
source ./readVariables.sh vars.csv "#realm"
REALM_FILENAME="${REALM_NAME}-${REALM_VERSION}.jar"
REALM_LOC=${REPO_LOC}/${REALM_NAMESPC}/${REALM_NAME}/${REALM_VERSION}/${REALM_FILENAME}
wget ${REALM_LOC}
set +e
cp ${REALM_FILENAME} ${GLASSFISH_HOME}/domains/${DOMAIN}/lib/
rm ${REALM_FILENAME} 
echo 'wrdzUserRealm {
     pl.psnc.synat.wrdz.realm.WrdzLoginModule required;
  };' >> ${GLASSFISH_HOME}/domains/${DOMAIN}/config/login.conf
echo "done."
echo "Restart domain..."
asadmin start-domain ${DOMAIN}
echo "done."

echo "Configuring realm..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} create-auth-realm --classname pl.psnc.synat.wrdz.realm.WrdzUserRealm --property jaas-context=wrdzUserRealm:datasource-jndi=jdbc/rdb:user-table=darceo.jaas_users:user-name-column=username:password-column=password:salt-column=salt:certificate-column=certificate:group-table=darceo.jaas_groups:group-name-column=groupname:digest-algorithm=SHA-256:encoding=Base64:charset=utf-8 wrdz-realm
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set server-config.security-service.activate-default-principal-to-role-mapping=true
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} set configs.config.server-config.security-service.auth-realm.certificate.property.jaas-context=wrdzUserRealm
echo "done."

echo "Downloading dArceo config files..."
WRDZ_LOC_FILES=""
for file in $WRDZ_CONFS
do
	echo "get "${GITHUB_LOC}/${file}" ..." 
	wget ${GITHUB_LOC}/${file}
        filename=$(basename "/${file}")
	locpath="lib/wrdz-config"
	newpath=${locpath}/${filename}
	WRDZ_LOC_FILES=`printf "%s \n" "${WRDZ_LOC_FILES}${filename}"`
	mv ${filename} ${newpath}
done;
echo "finshed."

set -e
echo "Configure dArceo variables..."
	source ./readVariables.sh vars.csv  "#wrdzadmin"
	source ./readVariables.sh vars.csv  "#wrdzconf"
	source ./readVariables.sh vars.csv  "#mdzconf"
	source ./readVariables.sh vars.csv  "#zmdconf"
	source ./readVariables.sh vars.csv  "#zmkdconf"
	source ./readVariables.sh vars.csv  "#log4jconf"
	source ./readVariables.sh vars.csv  "#zdtconf"
	source ./readVariables.sh vars.csv  "#msconf"
	source ./readVariables.sh vars.csv  "#zuconf"
echo "done."
set +e

echo "Setting passwords..."
	WRDZ_ADMIN_HASH=$(java -cp ${DARCEO_INSTALL_HOME}/lib/keys/hasher.jar:${DARCEO_INSTALL_HOME}/lib/keys/commons-codec-1.6.jar pl.psnc.darceo.password.Hasher ${WRDZ_ADMIN_PASS})
	WRDZ_CONF_MODULES_HASH=$(java -cp ${DARCEO_INSTALL_HOME}/lib/keys/hasher.jar:${DARCEO_INSTALL_HOME}/lib/keys/commons-codec-1.6.jar pl.psnc.darceo.password.Hasher ${WRDZ_CONF_MODULES_PASS})
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_DARCEO_USER} -d ${PG_DARCEO_DBNAME} -c "UPDATE darceo.zu_auth_user SET ausr_password_hash = '${WRDZ_ADMIN_HASH}' WHERE ausr_id = 33"
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_DARCEO_USER} -d ${PG_DARCEO_DBNAME} -c "UPDATE darceo.zu_auth_user SET ausr_password_hash = '${WRDZ_CONF_MODULES_HASH}' WHERE ausr_id = 11"
	psql -h ${PG_HOST} -p ${PG_PORT} -U ${PG_DARCEO_USER} -d ${PG_DARCEO_DBNAME} -c "UPDATE darceo.zu_auth_user SET ausr_password_hash = '${WRDZ_CONF_MODULES_HASH}' WHERE ausr_id = 13"
	WRDZ_CONF_MODULES_ENCR=$(java -cp ${DARCEO_INSTALL_HOME}/lib/keys/cipher.jar:${DARCEO_INSTALL_HOME}/lib/keys/commons-codec-1.6.jar pl.psnc.darceo.password.Cipher ${WRDZ_CONF_MODULES_PASS})
	eval "python pyXml.py ${WRDZ_INSTALL_HOME}/lib/wrdz-config/wrdz-config.xml.template https/modules-password ${WRDZ_CONF_MODULES_ENCR}"
echo "done."

echo "Copy config files..."
for file in $WRDZ_LOC_FILES
do
	echo "copy "${file}" ..." 
	newFileName=`basename $file .template`
	cp lib/wrdz-config/${file} ${GLASSFISH_HOME}/domains/${DOMAIN}/config/${newFileName}
	rm lib/wrdz-config/${file}
done;
echo "done."

echo "Download wrdz-namespaces.xml..."
echo "wget "${GITHUB_LOC}/${WRDZ_NAMESPACES}" ..."
set -e 
wget ${GITHUB_LOC}/${WRDZ_NAMESPACES}
set +e
filename=$(basename "/${WRDZ_NAMESPACES}")
echo "done."
echo "Copy wrdz-namesapces.xml to domain config directory ..."
cp ${filename} ${GLASSFISH_HOME}/domains/${DOMAIN}/config/
rm ${filename}
echo "done."

#fits
echo "Run fits..."
source ./readVariables.sh vars.csv "#fits"
RMI_FITS_FILENAME=${RMI_FITS_NAME}-${RMI_FITS_VERSION}.jar
RMI_FITS_LOC=${REPO_LOC}/${RMI_FITS_NAMESPC}/${RMI_FITS_NAME}/${RMI_FITS_VERSION}/${RMI_FITS_FILENAME}
set -e
wget ${RMI_FITS_LOC}
set +e
mv ${RMI_FITS_FILENAME} ${FITS_HOME}/lib
WRDZ_COMMON_FILENAME=${WRDZ_COMMON_NAME}-${WRDZ_COMMON_VERSION}.jar
WRDZ_COMMON_LOC=${REPO_LOC}/${WRDZ_COMMON_NAMESPC}/${WRDZ_COMMON_NAME}/${WRDZ_COMMON_VERSION}/${WRDZ_COMMON_FILENAME}
set -e
wget ${WRDZ_COMMON_LOC}
set +e
mv ${WRDZ_COMMON_FILENAME} ${FITS_HOME}/lib
set -e 
wget ${GITHUB_LOC}/rmi-fits/rmi-fits.sh
set +e
mv rmi-fits.sh ${FITS_HOME}
chmod +x ${FITS_HOME}/rmi-fits.sh
mkdir ${FITS_HOME}/logs
${FITS_HOME}/rmi-fits.sh start
echo "done."

echo "Restart domain..."
asadmin restart-domain ${DOMAIN}
echo "done."

set -e
source ./readVariables.sh vars.csv "#wrdzear"
set +e
WRDZ_EAR_FILENAME="${WRDZ_EAR_NAME}-${WRDZ_EAR_VERSION}.ear"
WRDZ_EAR_LOC=${REPO_LOC}/${WRDZ_EAR_NAMESPC}/${WRDZ_EAR_NAME}/${WRDZ_EAR_VERSION}/${WRDZ_EAR_FILENAME}
echo "Download dArceo ear..."
set -e
wget ${WRDZ_EAR_LOC}
set +e
echo "done."

echo "Deploy dArceo ear... "
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} deploy --name wrdz ${WRDZ_EAR_FILENAME}
rm ${WRDZ_EAR_FILENAME}
echo "done."

echo "Enable secure admin console..."
asadmin --user ${ADMIN} --passwordfile ${PASSWORDFILE} enable-secure-admin
asadmin restart-domain ${DOMAIN}
echo "done."

echo "Remove temporary files..."
rm ${PASSWORDFILE}
unset $PGPASSWORD
echo "done."
echo " -------- GLASSFISH SETTING FINISHED -------- "


