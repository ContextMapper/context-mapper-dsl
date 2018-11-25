#!/bin/sh -e

echo "This will trigger a release job on Travis. Are you sure you want to continue? (Y/n)"

read CONTINUE_RELEASE

if [ "$CONTINUE_RELEASE" = "Y" ]; then
  TRAVIS_TOKEN=$(travis token)
  TRAVIS_REQUEST='{
   "request": {
   "message": "Trigger Release",
   "branch":"master",
   "config": {
     "script": "xvfb-run ./.travis/prepareReleaseTravis.sh"
    }
  }}'

  curl -s -X POST \
   -H "Content-Type: application/json" \
   -H "Accept: application/json" \
   -H "Travis-API-Version: 3" \
   -H "Authorization: token $TRAVIS_TOKEN" \
   -d "$TRAVIS_REQUEST" \
   https://api.travis-ci.com/repo/ContextMapper%2Fcontext-mapper-dsl/requests
else
  echo "Aborted."
fi
