dnl ***********************************************************************
dnl Process this file with autoconf to produce a configure script.
dnl ***********************************************************************
#AC_INIT(install-sh)
AC_INIT(jaastool, 1.0)
AC_CONFIG_MACRO_DIR([m4])

AC_CANONICAL_HOST

AM_INIT_AUTOMAKE([foreign dist-bzip2])
AC_CONFIG_TESTDIR([tests-autotest])

dnl ***********************************************************************
dnl Checks for programs.
dnl ***********************************************************************
AC_PROG_CC
AC_PROG_CXX
AC_PROG_INSTALL
AC_PROG_MAKE_SET

AC_SUBST(AUTOM4TE, autom4te)

AC_ARG_WITH(doxygen,
[AS_HELP_STRING([--with-doxygen],[Indicate Doxygen allowing a without-; (default=search)])],
[
    case "$withval" in
	""|"yes") 
		AC_PATH_PROGS(DOXYGEN, [doxygen], missing, $PATH:/Applications/Doxygen.app/Contents/Resources)
		AC_PATH_PROGS(FOP, [fop], missing)
		;;
	"no")  DOXYGEN=missing ;;
	*) DOXYGEN=$withval ;;
    esac
],
[
	DOXYGEN=missing 
])
AC_PATH_PROGS(DOT, [dot], missing, $PATH:/usr/local/bin:/usr/local/graph*/bin)
AC_SUBST(PATHDOT, [`dirname "${DOT}"`])
AM_CONDITIONAL(DO_DOT, test "x$DOT" != "xmissing")
AC_PATH_PROGS(ASTYLE, [astyle], missing, $PATH:$HOME/bin)
AM_CONDITIONAL(DO_ASTYLE, test "x$ASTYLE" != "xmissing")
AM_CONDITIONAL(DO_DOXYGEN, test "x$DOXYGEN" != "xmissing" -a "x$DOT" != "xmissing")
if test "x$DOT" != "xmissing"; then HAVE_DOT=yes; else HAVE_DOT=no; fi
AC_SUBST(HAVE_DOT)

BUILDID=0
AC_SUBST(BUILDID)

AC_PATH_PROGS(RPMBUILD, [rpmbuild], [missing rpmbuild])
AM_CONDITIONAL(HAVE_RPMBUILD, [test "x$RPMBUILD" != "xmissing rpmbuild"])

SVN_CHECK_JDK([1.6.0])
AM_CONDITIONAL(DO_JAVA, test "$JAVAC" != none)

AC_ARG_ENABLE(autotest,
[AS_HELP_STRING([--enable-autotest],[Enable autotest testcases; breaks: make distcheck; default=disabled])],
[
    case "$enableval" in
	"yes") ENAB=yes ;;
	"no")  ENAB=no  ;;
	*) ENAB=no ;;
    esac
],
[
    ENAB=no
])
AM_CONDITIONAL(DO_AUTOTEST, test "$ENAB" = yes)

AC_ARG_WITH(getopt,
[AS_HELP_STRING([--with-getopt],[Enable autoincorporation of a built getopt.jar; default=search])],
[
    case "$withval" in
	"yes") WITH_GETOPTJAR=$((locate getopt.jar) | head -1) ;;
	"no")  WITH_GETOPTJAR=no  ;;
	*) WITH_GETOPTJAR="$withval" ;;
    esac
],
[
AC_MSG_CHECKING([for getopt.jar])
    WITH_GETOPTJAR=$((locate getopt.jar) | head -1) ;
AC_MSG_RESULT([${WITH_GETOPTJAR}])
])
AM_CONDITIONAL(DO_GETOPTJAR, test "$WITH_GETOPTJAR" == no)
AC_SUBST([GETOPTJAR], ${WITH_GETOPTJAR})


AC_CONFIG_FILES(Makefile
  depjars/Makefile
  java/Makefile
  tests-autotest/Makefile tests-autotest/atlocal tests-autotest/testsuite.at
  convjars/Makefile
)

if test "x$RPMBUILD" != "xmissing rpmbuild"
then
  AC_CONFIG_FILES(jaastool.spec:pkg/rpm/jaastool.spec.in)
fi

if test "x$DOXYGEN" != "xmissing"
then
  AC_CONFIG_FILES(Doxyfile:pkg/Doxyfile.in)
fi

AC_OUTPUT
