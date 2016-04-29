FROM node:4
MAINTAINER agao@ifwe.co

#ENV NODE_ENV stage
ENV NODE_ENV production

ADD ./ /usr/local/src/versus-app/
RUN apt-get -y update

WORKDIR /usr/local/src/versus-app/
RUN  rm -rf  /usr/local/src/node_modules && npm install
CMD node --use-strict server.js
