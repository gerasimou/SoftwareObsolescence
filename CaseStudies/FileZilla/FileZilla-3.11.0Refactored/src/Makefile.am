if MINGW
  MAYBE_FZSHELLEXT = fzshellext/32 fzshellext/64
endif

#if HAVE_LIBTINYXML
#else
#  MAYBE_TINYXML = tinyxml
#endif
MAYBE_MYNEWLIB = myNewLib

if HAVE_DBUS
  MAYBE_DBUS = dbus
endif

SUBDIRS = include engine $(MAYBE_MYNEWLIB) $(MAYBE_DBUS) interface putty $(MAYBE_FZSHELLEXT) .
DIST_SUBDIRS = include engine myNewLib dbus interface putty fzshellext/64 .

dist_noinst_DATA = FileZilla.sln Dependencies.props.example

dist-hook:
	mkdir -p $(distdir)/fzshellext/32
	mv -f $(distdir)/fzshellext/64/* $(distdir)/fzshellext
