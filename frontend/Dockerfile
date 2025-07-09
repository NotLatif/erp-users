# Build stage
FROM node:20-alpine as build

WORKDIR /app

# Copy package files and install dependencies
COPY package.json package-lock.json* ./
RUN npm ci

# Copy all project files
COPY . .

# Build the application
RUN npm run build

# Production stage
FROM node:20-alpine as production

WORKDIR /app

# Copy built application from build stage
COPY --from=build /app/build ./build
COPY --from=build /app/package.json ./package.json
COPY --from=build /app/node_modules ./node_modules

# Expose the port the app will run on
EXPOSE 3000

# Set environment variable
ENV PORT=3000
ENV NODE_ENV=production

# Start the application
CMD ["node", "build"]