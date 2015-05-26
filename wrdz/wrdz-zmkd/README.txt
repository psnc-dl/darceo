Before building the WRDZ-ZMKD project you need install Apache DeviceMap:
	http://incubator.apache.org/devicemap/getting-started.html
ie:	 
Install Device Description Repository API into your local Maven repository:

wget http://www.w3.org/TR/2008/WD-DDR-Simple-API-20080404/DDR-Simple-API.jar
mvn install:install-file -Dfile=DDR-Simple-API.jar -DgroupId=org.w3c.ddr.simple -DartifactId=DDR-Simple-API -Dversion=2008-04-04 -Dpackaging=jar

Check out DeviceMap from subversion:

svn co http://svn.apache.org/repos/asf/incubator/devicemap/trunk/ devicemap

Build OpenDDR:

cd devicemap && mvn clean install
