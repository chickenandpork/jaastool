nodist_java_JAVA = JaasNoFile.java
nodist_java_DATA = @PACKAGE@.jar

javadir = $(pkgdir)


EXTRA_DIST = $(nodist_java_JAVA)
CONFIG_CLEAN_FILES = @PACKAGE@.jar

AM_JAVACFLAGS= @JAVAC_FLAGS@ -cp $(JAVAROOT):$(top_builddir)/depjars/getopt.jar

BUILDTMP=$(shell date +%s)
BUILT_SOURCES =


JAVAROOT = bld
BUILT_SOURCES += javaroot.stamp

javaroot.stamp: $(nodist_java_JAVA)
	mkdir -p $(JAVAROOT) && touch $@

@PACKAGE@.jar: classjava.stamp
	awk -v BUILDTOP="bld" '/^package / {gsub (";$$",""); gsub ("\\.","/",$$2); SUBS[$$2]=$$2 } END { FIRST=0; for (n in SUBS) { if (0 == FIRST++) printf "jar cvf $@"; printf " -C %s %s",BUILDTOP,n }; printf "\n"; }' $(nodist_java_JAVA) | $(SHELL)
	touch @PACKAGE@.class

clean-local:
	CLASSES=`for i in $(nodist_java_JAVA); do awk -v FILE="$$i" '/^package / {gsub (";$$",""); gsub (".java",".class",FILE); gsub ("\\\\.","/",$$2); printf "%s/%s\n",$$2,FILE}' $(srcdir)/$$i; done`; (cd $(top_builddir)/ && rm -f $$CLASSES)
	rm -f @PACKAGE@.jar $(notdir $(subdir)).jar $(BUILT_SOURCES)

