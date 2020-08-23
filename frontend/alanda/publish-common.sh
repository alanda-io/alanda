#!/bin/bash

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
echo 'Done!'
