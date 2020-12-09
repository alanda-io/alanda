#!/bin/bash

CURRENT_BRANCH_NAME=$(git branch --no-color | grep -E '^\*' | awk '{print $2}' \
        || echo "default_value")

if [ "$CURRENT_BRANCH_NAME" != 'master' ]; then
  echo -e '\e[31mYou can only publish a new version from master!\e[0m'
  exit 1;
fi

if [ -n "$(git status --untracked-files=no --porcelain)" ]; then
  echo -e '\e[31mThere are changes, please commit before you start publishing!\e[0m';
  exit 1;
fi

if [ -n "$(git status -uno)" ]; then
  echo -e '\e[31mYour local branch is not up to date with origin master\e[0m';
  exit 1;
fi

cd ./libs/common/ || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }

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
cd ../../
rm -r -f dist/libs/common
npm run nx run common:build:production || {
  echo -e 'Reverting version to:';
  cd ./libs/common/ || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
  npm version "${CURRENT_PACKAGE_VERSION}"
  exit 1;
}
cd ./dist/libs/common || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
echo 'Login into NPM Nexus'
npm login --registry=https://repo.alanda.io/repository/alanda/ || {
  echo -e 'Reverting version to:';
  cd ../../../libs/common/ || { echo -e '\e[31mPath does not exist!\e[0m'; exit 1; }
  npm version "${CURRENT_PACKAGE_VERSION}"
  exit 1;
}
npm publish
git commit -m "ci(publish): Release new version ${CURRENT_PACKAGE_VERSION}"
git push
echo 'Done!'
