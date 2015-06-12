#!/bin/bash

# concatenate args and use eval/exec to preserve spaces in paths, options and args
args=""
for arg in "$@" ; do
	args="$args \"$arg\""
done

JCPATH=${FITS_HOME}/lib
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/droid
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/jhove
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

JCPATH=${FITS_HOME}/lib/nzmetool/adapters
# Add on extra jar files to APPCLASSPATH
for i in "$JCPATH"/*.jar; do
	APPCLASSPATH="$APPCLASSPATH":"$i"
done

cmd="java -Xmx1024m -classpath \"$APPCLASSPATH:${FITS_HOME}/xml/nlnz\" pl.psnc.synat.fits.rmi.FitsRmi $args &"

eval "exec $cmd"
