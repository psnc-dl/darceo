--------------------------------------------------------------------
   please read instructions before starting dArceo installation
--------------------------------------------------------------------

  1) install Java 7.0 SDK from http://www.oracle.com/technetwork/java/javase/downloads/index.html and set JAVA_HOME
  2) install JCE Unlimited Strength from http://www.oracle.com/technetwork/es/java/javase/downloads/jce-7-download-432124.html
  3) add JAVA_HOME/bin to the enviroment variable PATH
  3) install Glassfish in version 3.1.2 (http://dlc.sun.com.edgesuite.net/glassfish/) and make sure that GLASSFISH_HOME variable is properly set
  4) install PostgreSQL Server 9.3 and also PostgreSQL Client (psql - command line tool)
  5) download, unpack and build FFMPEG in version 0.9 (http://ffmpeg.org/):
	5.1) configure --disable-yasm 
	     make
	     make install
	5.2) set FFMPEG_HOME variable
  6) install FITS 0.5.0 (File Information Tool Set)
	6.1) set FITS_HOME
	6.2) TODO - install fits-rmi jar and fits-rmi.sh (bat) cf. SYAE-354
  7) download OWLIM-lite v. 5.2.5 from http://www.ontotext.com/products/ontotext-graphdb/graphdb-lite/ (registration is necessary)
    7.2) unpack and set OWLIM_HOME
	7.1) make directory for semantic repository and set environment variable OWLIM_DATA pointing that directory
  8) check out that you have installed:
	8.1) python in version 2.7
	8.2) wget tool
	8.4) dpkg tool

--------------------------------------------------------------------
   running the installation script
--------------------------------------------------------------------
	
	./darceo-install.sh
	

--------------------------------------------------------------------
   keystores      (probably we replace them by passwords (?)                                       
--------------------------------------------------------------------
