#!/usr/bin/env bash

# This script opens up the Swagger docs in a browser (serving them from a container)

docker pull swaggerapi/swagger-ui
docker run -d -p 100:8080 -e SWAGGER_JSON=/docs/openapi.yaml -v .:/docs swaggerapi/swagger-ui
sleep 1
open http://localhost:100/
