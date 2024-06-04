#!/bin/bash
docker run --name postgres -e POSTGRES_PASSWORD=postgres -e POSTGRES_DB=bitbucket-db -d -p 5432:5432 postgres:alpine