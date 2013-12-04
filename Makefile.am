DIST_SUBDIRS =	depjars java convjars tests-autotest 
SUBDIRS =	depjars 

if DO_JAVA
SUBDIRS += java
endif

# always must be after any java dirs -- acts as a collector
if DO_JAVA
SUBDIRS += convjars
endif

# always after any testa and afver conv(enience)jars: need to test that the convjars work in isolation
if DO_AUTOTEST
SUBDIRS +=	tests-autotest
endif

# if missing mkinstalldirs install-sh trigger errors, ignore the result code on the "system (cp ...)" command in automake
CONFIG_CLEAN_FILES = aclocal.m4 config.guess config.sub config.h config.h.in ltconfig ltmain.sh missing mkinstalldirs install-sh Doxyfile @PACKAGE@.spec @PACKAGE@.jar changes.atom
EXTRA_DIST = @PACKAGE@.spec

ACLOCAL_AMFLAGS = -I m4


if DO_DOXYGEN
doc: Doxyfile
	$(DOXYGEN)
endif

if DO_ASTYLE
formatting: java/JaasNoFile.java
	$(MAKE) check
	for i in $^; do $(ASTYLE) -A1 $$i; done
	$(MAKE) check

git-formatting:
	$(MAKE) formatting
	git commit -a -m 'formatting and whitespace'
	echo git push
	echo git dcommit
endif


if HAVE_RPMBUILD
rpm: dist-bzip2
	@RPMBUILD@ --define 'skipcheck 1' -ta $(PACKAGE)-$(VERSION).tar.bz2

endif
