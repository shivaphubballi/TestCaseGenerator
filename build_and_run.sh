#!/bin/bash

# Build the project
echo "Building TestGen Library..."
javac -d bin -cp "bin:lib/*" $(find src/main/java -name "*.java")

# Check if build was successful
if [ $? -eq 0 ]; then
    echo "Build successful! Running demos..."
    
    # Run the main demo
    echo -e "\n\n============== RUNNING MAIN CLASS ==============\n"
    java -cp bin:lib/* com.testgen.Main
    
    # Run the demo runner
    echo -e "\n\n============== RUNNING DEMO RUNNER ==============\n"
    java -cp bin:lib/* com.testgen.DemoRunner
    
    # Run the CLI help
    echo -e "\n\n============== RUNNING CLI HELP ==============\n"
    java -cp bin:lib/* com.testgen.cli.CommandLineRunner help
    
    echo -e "\n\nAll demos completed successfully!"
else
    echo "Build failed! Please check for errors."
fi
