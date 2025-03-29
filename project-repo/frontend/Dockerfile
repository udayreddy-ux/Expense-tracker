#Buildin react app
FROM node:20-alpine AS build

#Setting work directory
WORKDIR /app

#Dependencies
COPY package*.json ./
RUN npm install

COPY . .
RUN npm run build


#Production stage
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]