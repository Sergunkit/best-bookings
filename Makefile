OPENAPI_GENERATOR_IMAGE := openapitools/openapi-generator-cli:latest-release
SCHEMATHESIS_IMAGE := schemathesis/schemathesis:latest
API_URL = http://server:8080
ENDPOINTS = "/hotels$$|/hotels/\\d+/rooms$$|/hotels/\\d+$$|/amenities$$|/bookings$$|/bookings/\\d+$$|/favorites$$|/favorites/\\d+$$|/rooms/\\d+$$|/tokens$$|/users$$|/users/\\d+$$|/users/\\d+/favorites$$"

gen-api:
	docker run --rm -v ${PWD}/api:/api node:22-alpine sh -c "cd /api && npm i && npx tsp compile ."

gen-server:
	docker run --rm -v ${PWD}/api:/local -v ${PWD}/backend:/backend $(OPENAPI_GENERATOR_IMAGE) generate \
		-i /local/tsp-output/openapi.yaml \
		-g spring \
		-o /backend/generated \
   		--additional-properties=useSpringBoot3=true,hibernateMode=true,jpa=true \
   		--additional-properties=useBeanValidation=true,useLombok=true,delegatePattern=true

gen-client:
	docker run --rm -v ${PWD}/api:/local -v ${PWD}/client:/client $(OPENAPI_GENERATOR_IMAGE) generate \
		-i /local/tsp-output/openapi.yaml \
		-g typescript-fetch \
		-o /client/generated_client \
		--additional-properties=npmName="@api/client",supportsES6=true

test-api:
		rm -f $(CURDIR)/tests/schemathesis_reports/junit.xml
		mkdir -p ${PWD}/tests/schemathesis_reports
		chmod -R u+w ${PWD}/tests/schemathesis_reports
		docker run --rm \
				--network=hotel-booking_default \
				-v ${PWD}/api:/api \
				-v ${PWD}/tests/schemathesis_reports:/reports \
				$(SCHEMATHESIS_IMAGE) run \
				--url=$(API_URL) \
				/api/tsp-output/openapi.yaml \
				--include-path-regex=$(ENDPOINTS) \
				--checks=all \
				--max-examples=50 \
				--report=junit \
				--report-dir=/reports
			ls -l /reports	

start-mock:
	./api/node_modules/.bin/prism mock api/tsp-output/openapi.yaml

gen-all: gen-api gen-server gen-client

dev:
	COMPOSE_BAKE=true docker compose up --build

start:
	COMPOSE_BAKE=true docker-compose -f docker-compose.yml up --build -d

stop:
	docker-compose down

drop-database:
	docker-compose down -v

seed:
	docker-compose exec db psql -U postgres -d mydb -f /docker-entrypoint-initdb.d/data.sql
