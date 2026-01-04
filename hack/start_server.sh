  docker compose up -d server
  result=$?
  if [[ $result -ne 0 ]]; then
    docker compose logs
  fi
  exit $result
