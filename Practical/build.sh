#!/bin/bash

# Compile Java files
# javac ./src/*.java
javac ./src/LRChecker.java

# Create wrapper for LRChecker
rm lrchecker
touch lrchecker
echo '#!/bin/sh' > lrchecker
echo 'java -cp src LRChecker "$@"' >> lrchecker
chmod +x lrchecker
