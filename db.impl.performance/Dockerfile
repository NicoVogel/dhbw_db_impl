FROM node:8-alpine
WORKDIR /usr/src/app
RUN mkdir -p /data
COPY [ "package*.json", "bench.js", "./" ]
RUN npm install bench-rest
EXPOSE 8080
ENTRYPOINT [ "node", "bench.js" ]