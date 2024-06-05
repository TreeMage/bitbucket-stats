#!/bin/bash

# This script installs the pre-commit hook in the .git/hooks directory

# Get the directory of the script
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"


# Link the pre-commit hook to the .git/hooks directory
ln -sf $DIR/pre-commit-hook.sh $DIR/../.git/hooks/pre-commit