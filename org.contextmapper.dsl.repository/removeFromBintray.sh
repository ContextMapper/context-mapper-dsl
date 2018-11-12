#!/bin/bash
# remove p2 metadata artifacts from bintray remote path
#Sample Usage: removeFromBintray.sh apikey remotePath
API=https://api.bintray.com

BINTRAY_API_KEY=$1
PATH_TO_REPOSITORY=$2

BINTRAY_USER=contextmapper
BINTRAY_REPO=context-mapping-dsl

function main() {
remove_p2_metadata
}

function remove_p2_metadata() {
echo "${BINTRAY_USER}"
echo "${BINTRAY_API_KEY}"
echo "${BINTRAY_REPO}"
echo "${PCK_NAME}"
echo "${PCK_VERSION}"
echo "${PATH_TO_REPOSITORY}"


echo "Removing metadata content.jar..."
curl -X DELETE -u${BINTRAY_USER}:${BINTRAY_API_KEY} "https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${PATH_TO_REPOSITORY}/content.jar"
echo ""
echo "Removing metadata artifacts.jar..."
curl -X DELETE -u${BINTRAY_USER}:${BINTRAY_API_KEY} "https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${PATH_TO_REPOSITORY}/artifacts.jar"
echo ""
echo "Removing metadata compositeContent.xml..."
curl -X DELETE -u${BINTRAY_USER}:${BINTRAY_API_KEY} "https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${PATH_TO_REPOSITORY}/compositeContent.xml"
echo ""
echo "Removing metadata compositeArtifacts.xml..."
curl -X DELETE -u${BINTRAY_USER}:${BINTRAY_API_KEY} "https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${PATH_TO_REPOSITORY}/compositeArtifacts.xml"
echo ""
echo "Removing metadata p2.index..."
curl -X DELETE -u${BINTRAY_USER}:${BINTRAY_API_KEY} "https://api.bintray.com/content/${BINTRAY_USER}/${BINTRAY_REPO}/${PATH_TO_REPOSITORY}/p2.index"
echo ""

}



main "$@"
