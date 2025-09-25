# Java compiler and runtime
JAVAC = javac
JAVA  = java

# Package name
PACKAGE = thread_per_request

# Main class to run
MAIN_CLASS = $(PACKAGE).RawClient

# Where to put compiled .class files
BUILD_DIR = ../../build

# Java sources in this folder
SOURCES = $(wildcard *.java)

# Corresponding .class files
CLASSES = $(SOURCES:%.java=$(BUILD_DIR)/$(PACKAGE)/%.class)

# Default target
all: run

# Compile rule
$(BUILD_DIR)/$(PACKAGE)/%.class: %.java
	@mkdir -p $(BUILD_DIR)/$(PACKAGE)
	$(JAVAC) -d $(BUILD_DIR) $<

compile: $(CLASSES)

# Run server
run: compile
	$(JAVA) -cp $(BUILD_DIR) $(MAIN_CLASS)

# Clean
clean:
	rm -rf $(BUILD_DIR)
