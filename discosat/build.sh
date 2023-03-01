#!/bin/bash

# Define the HAWKBIT_VERSION if its not set
: "${HAWKBIT_VERSION:=0.3.0-SNAPSHOT}"
: "${DOCKER_REGISTRY_URL:=http://localhost:5000/}"

# Link the latest release
ln -f ../hawkbit-runtime/hawkbit-update-server/target/hawkbit-update-server-"${HAWKBIT_VERSION}".jar \
    hawkbit-satos/hawkbit-update-server.jar || { echo "Make sure to build hawkbit first: cd .. && mvn clean install" ; exit 1; }

docker compose up -d --build

# Check if a local registry is running
registry_status=$(curl -I -k -s "${DOCKER_REGISTRY_URL}" | head -n 1 | cut -d ' ' -f 2)
# If it is, push the container
if [[ $registry_status -eq "200" ]]; then
    registry_tag_url="${DOCKER_REGISTRY_URL#*://}"
    registry_tag_url="${registry_tag_url%/}"
    registry_target="${registry_tag_url}"/hawkbit-satos
    echo "Found a working docker registry, pushing image to ${registry_target}"

    docker tag hawkbit-satos "${registry_target}"
    docker push "${registry_target}"
fi

