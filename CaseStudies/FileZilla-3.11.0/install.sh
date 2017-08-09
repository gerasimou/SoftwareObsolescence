#! /bin/sh
 #http://stackoverflow.com/questions/41898925/compiling-filezilla-on-osx/41899480#41899480
 #run from the main filezilla directory

#delete previous settings
find . \( -name "config" -o -name "autom4te.cache" \) -type d -delete
find . \( -name "Makefile.in" -o -name "configure" \) -type f -delete

#reconfigure
autoreconf -i -m configure.ac 

 #for compiling filezilla source
 export PKG_CONFIG_PATH=/usr/local/Cellar/gnutls/3.5.8/lib/pkgconfig
 export PKG_CONFIG_PATH=/usr/local/Cellar/nettle/3.3/lib/pkgconfig/:$PKG_CONFIG_PATH
 export PKG_CONFIG_PATH=/usr/local/Cellar/libtasn1/4.10/lib/pkgconfig/:$PKG_CONFIG_PATH
 export PKG_CONFIG_PATH=/usr/local/Cellar/p11-kit/0.23.3/lib/pkgconfig/:$PKG_CONFIG_PATH

export LIBGNUTLS_CFLAGS=$(pkg-config --cflags gnutls)
export LIBGNUTLS_LIBS=$(pkg-config --libs gnutls)

if [ -d compile ]; then
  rm -rf compile/*
else
  mkdir compile
fi

cd compile

../configure --with-tinyxml=builtin

make

make install
