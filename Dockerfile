# ============================================================================
# Angular 16 Frontend - Production Dockerfile with Nginx Reverse Proxy
# ============================================================================
FROM node:18-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
RUN npm run build -- --configuration production

FROM nginx:alpine
# Copy custom Nginx configuration
COPY nginx.conf /etc/nginx/nginx.conf

# Copy compiled Angular production bundle
COPY --from=build /app/dist/salary-management-system /usr/share/nginx/html

EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
