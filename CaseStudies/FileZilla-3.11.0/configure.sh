#! /bin/sh
 #http://stackoverflow.com/questions/41898925/compiling-filezilla-on-osx/41899480#41899480
 #run from the main filezilla directory

#delete previous settings
find . \( -name "config" -o -name "autom4te.cache" \) -type d -delete
find . \( -name "Makefile.in" -o -name "configure" \) -type f -delete

#reconfigure
autoreconf -i -m configure.ac
