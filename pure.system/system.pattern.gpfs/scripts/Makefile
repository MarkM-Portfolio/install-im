# each of the directory should be zipped into a ZIP file
BINDIR=../../build
PATTERN_NAME = Connections5

PACKAGES += ic5jvmAdjustDM
PACKAGES += ic5jvmAdjust
PACKAGES += ic5jvmAdjustNode2
PACKAGES += ic5copyDB2driver
PACKAGES += ic5customizeTDI
PACKAGES += ic5fixInstallWAS
PACKAGES += ic5installIC5
PACKAGES += ic5installTDI
PACKAGES += ic5optionalTasksIC5
PACKAGES += ic5postInstallIC5
PACKAGES += ic5prepareDB2
PACKAGES += ic5prepareHTTP
PACKAGES += ic5searchConTool
PACKAGES += ic5setEnvProperties
PACKAGES += ic5prepareDB2CR1

all:
	-mkdir -p $(BINDIR)/$(PATTERN_NAME)/scripts
	for d in $(PACKAGES) ; do cd $$d ; zip -r ../$(BINDIR)/$(PATTERN_NAME)/scripts/$$d.zip . ; cd - ; done
