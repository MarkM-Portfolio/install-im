# each of the directory should be zipped into a ZIP file
BINDIR=../../build

ADD_ONS = defaultadddisk
ADD_ONS += defaultaddnfs

all:
	-mkdir -p $(BINDIR)/ic5base/add_ons
	for d in $(ADD_ONS) ; do zip -r $(BINDIR)/ic5base/add_ons/$$d.zip $$d; done
