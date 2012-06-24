#!/bin/sh
# convert all .bat files to .sh files

for B in *.bat
do
	S="`echo $B | sed -e 's/\.bat$/.sh/'`"
	echo $B -\> $S
	rm -f $S
	echo '#!/bin/sh' > $S
	sed < $B >> $S \
		-e 's,^@echo off,,' \
		-e 's/%~dp0//' \
		-e 's/set \([^=]*\)=\(.*\)/\1="\2"/' \
		-e 's/-cp/-classpath/' \
		-e 's/%\([^%]*\)%/\${\1}/g' \
		-e 's/%\*/\$\*/g' \
		-e 's/;/:/g' -e 's,\\,/,g' \
		-e 's/^REM/#/'
	chmod 755 $S
done
