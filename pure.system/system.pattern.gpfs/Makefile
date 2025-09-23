BINDIR=../build
PATTERN_NAME=Connections5

all: $(BINDIR)/$(PATTERN_NAME).tgz

$(BINDIR)/$(PATTERN_NAME).tgz: meta_files scripts_zip \
    $(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tgz \
    $(BINDIR)/$(PATTERN_NAME)/applications/Connections-5.0.zip
	#cd $(BINDIR)/$(PATTERN_NAME) && zip -r ../$(PATTERN_NAME).zip .
	tar cv -C $(BINDIR)/$(PATTERN_NAME) -f $(BINDIR)/$(PATTERN_NAME).tar \
	    add-ons files im plugins scripts patterntypes applications
	gzip -1 $(BINDIR)/$(PATTERN_NAME).tar
	mv $(BINDIR)/$(PATTERN_NAME).tar.gz $(BINDIR)/$(PATTERN_NAME).tgz 


meta_files:
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/add-ons
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/files
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/im
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/plugins

scripts_zip:
	$(MAKE) -C scripts

$(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tgz:
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/patterntypes
	tar cv -C patterntypes/Connections-5.0 \
        -f $(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tar licenses locales patterntype.json
	gzip -1 $(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tar
	mv $(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tar.gz $(BINDIR)/$(PATTERN_NAME)/patterntypes/Connections-5.0.tgz

$(BINDIR)/$(PATTERN_NAME)/applications/Connections-5.0.zip:
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/applications
	cd applications/Connections-5.0 && zip -r ../../$(BINDIR)/$(PATTERN_NAME)/applications/Connections-5.0.zip .

clean:
	rm -rf $(BINDIR)

