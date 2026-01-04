sbt -v "specs / cucumber"
result=$?
docker compose down
exit $result
