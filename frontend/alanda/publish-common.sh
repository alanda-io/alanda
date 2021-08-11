#!/bin/bash

function echoError {
  echo -e "\e[31m${1}\e[0m"
}

function checkIfGitIsClean {
  CURRENT_BRANCH_NAME=$(git branch --no-color | grep -E '^\*' | awk '{print $2}' \
        || echo "default_value")

  if [ "$CURRENT_BRANCH_NAME" != 'master' ]; then
    echoError "You can only publish a new version from master!"
    exit 1;
  fi

  if [ -n "$(git status --untracked-files=no --porcelain)" ]; then
    echoError "There are changes, please commit before you start publishing!"
    exit 1;
  fi

  git fetch
  HEAD_HASH=$(git rev-parse HEAD)
  UPSTREAM_HASH=$(git rev-parse master@{upstream})
  if [ "$HEAD_HASH" != "$UPSTREAM_HASH" ]; then
    echoError "Your local branch is not up to date with origin master!"
   exit 1;
  fi
}

function publish {
  cd ./libs/common/ || { echoError "Path does not exist!"; exit 1; }

  CURRENT_PACKAGE_VERSION=$(cat package.json \
    | grep version \
    | head -1 \
    | awk -F: '{ print $2 }' \
    | sed 's/[",]//g' \
    | tr -d '[[:space:]]')

  VERSION_UPGRADE=${1}
  if [ -z "${1}" ]; then VERSION_UPGRADE=$"patch"; fi

  echo 'Updating package version:'
  npm version ${VERSION_UPGRADE}
  NEW_PACKAGE_VERSION=$(cat package.json \
    | grep version \
    | head -1 \
    | awk -F: '{ print $2 }' \
    | sed 's/[",]//g' \
    | tr -d '[[:space:]]')

  cd ../../
  rm -r -f dist/libs/common
  npm run nx run common:build:production || {
    echo -e 'Reverting version to:';
    cd ./libs/common/ || { echoError "Path does not exist!"; exit 1; }
    npm version "${CURRENT_PACKAGE_VERSION}"
    exit 1;
  }
  cd ./dist/libs/common || { echoError "Path does not exist!"; exit 1; }
  echo 'Login into NPM Nexus'
  npm login --registry=https://repo.alanda.io/repository/alanda/ || {
    echo -e 'Reverting version to:';
    cd ../../../libs/common/ || { echoError "Path does not exist!"; exit 1; }
    npm version "${CURRENT_PACKAGE_VERSION}"
    exit 1;
  }
  npm publish

  cd ../../../
}

function commitNewVersion {
  git add ./libs/common/package.json || { echoError "Path does not exist!"; exit 1; }
  git add ./libs/common/package-lock.json || { echoError "Path does not exist!"; exit 1; }
  git commit -m "ci(publish): Release new version ${NEW_PACKAGE_VERSION}"
  git push
}

if [ "${1}" == 'force' ]; then
  publish "patch"
elif [ "${2}" == 'force' ]; then
  publish "${1}"
else
  checkIfGitIsClean
  publish "${1}"
fi

commitNewVersion

echo -e "\e[32mDone! Released version: ${NEW_PACKAGE_VERSION}\e[0m"
