install:
	npm ci

lint:
	npm run lint

lint-fix:
	npm run lint:fix

typecheck:
	npm run typecheck

build:
	npm run build

start:
	npm run start

docker-dependencies-fix:
	docker run --rm -it -v .:/app -w /app node:22-alpine sh -c "npm i"

start-mock-server:
	../api/node_modules/.bin/prism mock ../api/tsp-output/openapi.yaml

dev: 
	npm run dev
