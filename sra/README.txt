Before building the SRA project you need com.ontotext:owlim-lite:jar:5.2 in your maven repository.

Download OWLIM-LITE 5.2.5 from ontotext: 
	http://download.ontotext.com/owlim/3e4dc2e0-d66c-11e1-b81b-dba586cc0cc6/owlim-lite-5.2.5331.zip
and install the lib/owlim-lite.5-2.jar in your repository:

mvn install:install-file -DgroupId=com.ontotext -DartifactId=owlim-lite -Dversion=5.2 -Dpackaging=jar -Dfile=OWLIM_HOME/lib/owlim-lite.5-2.jar
