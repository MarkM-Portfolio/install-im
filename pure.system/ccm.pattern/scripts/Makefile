# each of the directory should be zipped into a ZIP file
BINDIR=../../build
PATTERN_NAME = CCM-5.0

PACKAGES += ic5federateNode
PACKAGES += ic5installCCM
PACKAGES += ic5jvmAdjustCCM

all:
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/scripts
	for d in $(PACKAGES) ; do cd $$d ; zip -r ../$(BINDIR)/$(PATTERN_NAME)/scripts/$$d.zip . ; cd - ; done
