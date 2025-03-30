# Stage 1: Build React App
FROM node:20-alpine AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

# Build-time argument
ARG REACT_APP_API_BASE_URL
RUN echo "REACT_APP_API_BASE_URL=$REACT_APP_API_BASE_URL" > .env

COPY . .
RUN npm run build

# Stage 2: Serve with NGINX
FROM nginx:alpine
COPY --from=build /app/build /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
