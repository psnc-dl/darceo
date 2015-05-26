Before building the RMI-FITS project you need edu.harvard.hul:fits:jar:0.8.3 in your maven repository.

Download sources of fits 0.8.3 from GitHub:
	https://github.com/opf-labs/fits/tree/ffmpeg-videomd-aes: 
biuld nad install the lib/fits.jar in your repository:

mvn install:install-file -DgroupId=edu.harvard.hul -DartifactId=fits -Dversion=0.8.3 -Dpackaging=jar -Dfile=fits.jar



Moreover install the lib/ots.jar in the repository:
mvn install:install-file -DgroupId=edu.harvard.hul -DartifactId=ots -Dversion=1.0.12 -Dpackaging=jar -Dfile=ots.jar

