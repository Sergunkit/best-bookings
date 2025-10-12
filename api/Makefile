install:
	npm ci

types-to-openapi:
	npx tsp compile .

start-mock-server:
	./node_modules/.bin/prism mock tsp-output/openapi.yaml

dev:
	npx tsp compile . --watch