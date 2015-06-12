--------------------------------------------------------------------
   please read instructions before starting dArceo installation
--------------------------------------------------------------------

  1) install Java 7.0 SDK from http://www.oracle.com/technetwork/java/javase/downloads/index.html
  2) install JCE Unlimited Strength from http://www.oracle.com/technetwork/es/java/javase/downloads/jce-7-download-432124.html
  3) install Glassfish in version 3.1.2 (http://dlc.sun.com.edgesuite.net/glassfish/) and make sure that GLASSFISH_HOME variable is properly set
  4) install PostgreSQL Server 9.3 and also PostgreSQL Client (psql - command line tool)
  5) download, unpack and build FFMPEG in version 2.6.1 (http://ffmpeg.org/):
	5.1) configure --disable-yasm 
	     make
	     make install
	5.2) make sure that ffprobe is in PATH
  6) install FITS 0.8.3 with VideoMD extraction support
	6.1) set FITS_HOME
  7) download OWLIM-lite v. 5.2.5 from http://www.ontotext.com/products/ontotext-graphdb/graphdb-lite/ (registration is necessary)
	7.1) unpack and set OWLIM_HOME
	7.2) create directory for semantic repository and set environment variable OWLIM_DATA pointing that directory
  8) check out that you have installed:
	8.1) python in version 2.7
	8.2) wget tool
	8.4) dpkg tool

--------------------------------------------------------------------
   running the installation script
--------------------------------------------------------------------
	
	./darceo-install.sh
