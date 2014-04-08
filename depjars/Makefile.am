depjardir = @datadir@/@PACKAGE@/jars
dist_depjar_DATA = $(JARS)


EXTRA_DIST = $(nodist_java_JAVA)
CONFIG_CLEAN_FILES = $(JARS)

AM_JAVACFLAGS= -cp $(top_builddir)/$(subdir)/$(JAVAROOT)

BUILDTMP=$(shell date +%s)

JARS := getopt.jar
BUILT_SOURCES := $(JARS)


if DO_GETOPTJAR

getopt.jar: java-getopt-1.0.13.jar
	cp $< $@

java-getopt-1.0.13.jar:
	test -f $${TMPDIR-/tmp}/$(@F) && cp $${TMPDIR-/tmp}/$(@F) $@ || true
	test -f $@ || wget -O $@ http://download.java.net/maven/2/gnu/getopt/java-getopt/1.0.13/$(@F) || curl -Lo $@ http://download.java.net/maven/2/gnu/getopt/java-getopt/1.0.13/$(@F)
	test -f $${TMPDIR-/tmp}/$(@F) || cp $@ $${TMPDIR-/tmp}/$(@F)
	touch $@

else

getopt.jar: @GETOPTJAR@
	cp $< $@
endif DO_GETOPTJAR_FALSE


check-local: $(JARS)
	for i in $<; do jar tf $$i >/dev/null || exit 1; done

JAVAROOT = bld
#BUILT_SOURCES += javaroot.stamp

