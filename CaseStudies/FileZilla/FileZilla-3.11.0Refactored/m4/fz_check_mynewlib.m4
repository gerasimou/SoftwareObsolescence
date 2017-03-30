dnl Checks whether system's myNewLib library exists

AC_DEFUN([FZ_CHECK_MYNEWLIB], [
  AC_ARG_WITH(mynewlib, AC_HELP_STRING([--with-mynewlib=type], [Selects which version of mynewlib to use. Type has to be either system or builtin]),
    [
      if test "x$with_mynewlib" != "xbuiltin"; then
        AC_MSG_ERROR([--with-mynewlib has to be set to either system (the default), builtin or auto])
      fi
    ])

  if test "x$with_mynewlib" = "xsystem"; then
    AC_MSG_NOTICE([Using system mynewlib])
    AC_DEFINE(HAVE_LIBMYNEWLIB, 1, [Define to 1 if your system has the `mynewlib' library (-lmynewlib).])
    MYNEWLIB_LIBS="-lmynewlib"
  else
    AC_MSG_NOTICE([Using builtin mynewlib])
    MYNEWLIB_LIBS="../myNewLib/libmynewlib.a"
  fi

  AC_SUBST(MYNEWLIB_LIBS)
])
