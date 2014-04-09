DIST_SUBDIRS =	depjars java convjars tests-autotest 
SUBDIRS =	depjars 

if DO_JAVA
SUBDIRS += java

# always must be after any java dirs -- acts as a collector
SUBDIRS += convjars
endif

# always after any builds and after conv(enience)jars: need to test that the convjars work in isolation
# this, plus no local derived content, means that all builds are tested (local $top_srcdir content would rebuild after tests)
if DO_AUTOTEST
SUBDIRS +=	tests-autotest
endif

# if missing mkinstalldirs install-sh trigger errors, ignore the result code on the "system (cp ...)" command in automake
CONFIG_CLEAN_FILES = aclocal.m4 config.guess config.sub config.h config.h.in ltconfig ltmain.sh missing mkinstalldirs install-sh Doxyfile @PACKAGE@.spec @PACKAGE@.jar changes.atom
EXTRA_DIST = @PACKAGE@.spec

ACLOCAL_AMFLAGS = -I m4


if DO_DOXYGEN
doc: htdocs/README.dox Doxyfile
	$(DOXYGEN)
	if test -d $(<D)/.git; then (cd $(<D) && git add -A); if test -n "$(DOXYMESSAGE)" ; then (cd $(<D) && git commit -a -m "$(DOXYMESSAGE)" && git push); fi; else echo "$(<D) subdir not set as a subrepo; see http:// ..."; exit 1; fi

htdocs/README.dox: README.md Makefile
	if test -d $(@D)/.git; then :; else \
		if grep $(@D) .gitignore; then echo found gitignore; else echo $(@D) >> .gitignore && git add .gitignore; fi ; \
		if git branch --list --remotes|grep gh-pages; then echo found gh-pages; else echo you need to create the gh-pages branch on github; exit 1; fi ; \
		git clone $$(git config --get remote.origin.url) -b gh-pages $(@D) || { echo failed; exit 1; } ; \
		find $(@D) -name $(@D) -o -name .git -prune -o -name xml -prune -o -name latex -prune -o -exec rm -fr {} \; 2>/dev/null || { echo failed; exit 1; } ; \
		echo "checked out" ; \
	fi
if DO_DOXYGEN_MD
	awk 'BEGIN { print "@file "; print "@mainpage JavaDoc API Markup for @PACKAGE@"; } { printf "%s\n",$$0; } END {print "" }' $< > $@
else
	awk 'BEGIN { print "/** @file "; print "@mainpage JavaDoc API Markup for @PACKAGE@"; } /@PACKAGE@$$/ {next} /^=+$$/ {next} { printf " * %s\n",$$0; } END {print " */" }' $< > $@
endif DO_DOXYGEN_MD_FALSE
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
