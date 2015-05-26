#!/bin/bash
if [ "x$WRDZ_INSTALL_HOME" = "x" ]; then
    WRDZ_INSTALL_HOME=`cd "$DIRNAME"; pwd`
fi
CURRFILE=""
TMPVAR=""
SKIP_READ=false
OLDIFS=$IFS
IFS=$'\n'
declare -a LINES
readarray LINES < $1
#echo $LINES
START_LINE=`grep -n ${2} ${1} | cut -d: -f1`
START_LINE=$((START_LINE-1))
CONTENT="${LINES[@]:${START_LINE}}" 
while read -r line
do
    name=$line
    IFS=$'\t' read -a arrIN <<< "$line"
    SIZEARR=${#arrIN[@]}
    if [ "${arrIN[0]}" == " " ]; then
	break
    fi
    #echo "SIZEARR=${SIZEARR}"
    if [ $SIZEARR -lt 3 ]; then
       if [ $SIZEARR -lt 1 ]; then
          break
       fi
       if [ $SIZEARR == 1 ]; then
          printf " \n%s\n" "${arrIN[0]} "
	  #break
       fi
       if [ $SIZEARR == 2 ]; then
          printf " \n%s\n" "${arrIN[0]} "
          CURRFILE=${arrIN[1]}
	  printf "Would you like to modify template file ${CURRFILE}? (press Enter to read default values, else type NO)"
	  TMPVAR=""    
	  read TMPVAR </dev/tty
          if [ "x$TMPVAR" != "x" ]; then
	     SKIP_READ=true
	     TMPVAR=""
          fi
	  #load and save file in order to remove comments from template
	  #eval "python pyXml.py ${WRDZ_INSTALL_HOME}/lib/wrdz-config/${CURRFILE}"
	  sed -i '/<!--/,/-->/d' ${WRDZ_INSTALL_HOME}/lib/wrdz-config/${CURRFILE}
       fi
       continue
    fi
    VAR_NAME=`echo "${arrIN[0]}" | sed 's/^ *//'`
    printf "%s" "Set variable ["${VAR_NAME}"] ["${arrIN[1]}"] (default: "${arrIN[2]}" ; press ENTER or type new value ): "
    if ! $SKIP_READ; then
    	read TMPVAR </dev/tty
    fi
    export ${VAR_NAME}="${arrIN[2]}"
    if [ "x$TMPVAR" != "x" ]; then
	export ${VAR_NAME}="${TMPVAR}"
    fi
    printf "%s\n" "Assigned: ["${VAR_NAME}"]="${!VAR_NAME}
    IFS=$'\n'
    #if necessary then modify xml config file
    if [ $SIZEARR == 4 ]; then
          eval "python pyXml.py ${WRDZ_INSTALL_HOME}/lib/wrdz-config/${CURRFILE} ${arrIN[3]} ${!VAR_NAME}"
    fi
done <<< "${CONTENT}"

IFS=$OLDIFS

