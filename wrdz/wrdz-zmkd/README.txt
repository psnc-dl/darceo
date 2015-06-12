Before building the WRDZ-ZMKD project you need install Apache DeviceMap:
	http://devicemap.apache.org/getting-started.html

Check out DeviceMap from subversion:

svn co http://svn.apache.org/repos/asf/devicemap/trunk devicemap


Change the version of devicemap-data dependency (released) and build the W3C DDR client simpleddr (not released yet):

edit devicemap/clients/w3c-ddr/pom.xml
	- set devicemap.data.version to 1.0.2
	
cd devicemap/clients/w3c-ddr
mvn clean install
