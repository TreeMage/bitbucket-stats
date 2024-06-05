#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )/../.." && pwd )"

function info {
   echo -e "\033[0;33m$1\033[0m"
}
function error {
   echo -e "\033[0;31m$1\033[0m"
}

function success {
   echo -e "\033[0;32m$1\033[0m"
}

# Stash unstaged changes
git diff --quiet
hadNoNonStagedChanges=$?
if ! [ $hadNoNonStagedChanges -eq 0 ]
then
   info "* Stashing non-staged changes"
   git stash --keep-index -u > /dev/null
fi

info "* Compiles?"

# Check if the project compiles
(cd $DIR/; sbt test:compile > /dev/null)
compiles=$?

if [ $compiles -eq 0 ]
then
   success "  > Yes"
else
   error "  > No"
fi

# Check for formatting

info "* Formatted?"

(cd $DIR/; scalafmt --test > /dev/null)
formatted=$?

if [ $formatted -eq 0 ]
then
   success "  > Yes"
else
   error "  > No "
   # Undo formatting
   git stash --keep-index > /dev/null
   git stash drop > /dev/null
fi

# Undo stashing
if ! [ $hadNoNonStagedChanges -eq 0 ]
then
   info "* Unstashing non-staged changes"
   git stash pop > /dev/null
fi

# Check if compilation and formatting passed
if [ $compiles -eq 0 ] && [ $formatted -eq 0 ]
then
   success "* All checks passed"
   exit 0
fi

error "* Some checks failed"
if ! [ $compiles -eq 0 ]
then
   error "  > Compilation failed"
fi

if ! [ $formatted -eq 0 ]
then
   error "  > Formatting failed. Please run 'scalafmt' to fix the formatting issues."
fi
exit 1





