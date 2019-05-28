FROM node:8-alpine
WORKDIR /usr/src/app
RUN mkdir -p /data
COPY [ "package*.json", "bench.js", "./" ]
RUN npm install
EXPOSE 8080
CMD [ "npm", "install", "bench-rest" ]
ENTRYPOINT [ "node", "bench.js" ]