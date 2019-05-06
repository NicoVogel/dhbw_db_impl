FROM node:8-alpine
WORKDIR /usr/src/app
RUN mkdir -p /data
COPY [ "package*.json", "server.js","randomGen.js", "./" ]
RUN npm install
EXPOSE 8080
CMD [ "npm", "start" ]