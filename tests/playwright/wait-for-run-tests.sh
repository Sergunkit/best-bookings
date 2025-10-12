#!/bin/bash

set -e

URL="$1"
MAX_RETRIES=2
RETRY_INTERVAL=10

echo "Ожидание доступности $URL..."

for i in $(seq 1 $MAX_RETRIES); do
  status=$(curl -s -o /dev/null -w "%{http_code}" "$URL")
  if [ "$status" -eq 200 ]; then
    echo "Доступен: $URL"
    exit 0
  else
    echo "Лежит (HTTP $status), жду..."
    sleep $RETRY_INTERVAL
  fi
done

echo "Не поднялось за $((MAX_RETRIES * RETRY_INTERVAL)) секунд"
exit 1